package com.bitlord.inventoryservice.service;

import com.bitlord.inventoryservice.dto.InventoryReservationResult;
import com.bitlord.inventoryservice.dto.LowStockAlert;
import com.bitlord.inventoryservice.dto.OrderEvent;
import com.bitlord.inventoryservice.kafka.producer.InventoryProducer;
import com.bitlord.inventoryservice.model.Product;
import com.bitlord.inventoryservice.model.StockMovement;
import com.bitlord.inventoryservice.repository.ProductRepository;
import com.bitlord.inventoryservice.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Core business orchestrator for Inventory operations.
 * Processes automated deduction based on remote events and manages product listings.
 */
@Service
public class InventoryService {

    // Repository for querying and saving Product records from the database
    private final ProductRepository productRepository;

    // Repository for logging every stock change as a StockMovement audit record
    private final StockMovementRepository stockMovementRepository;

    // Kafka producer for sending inventory events (reservation results, low stock alerts)
    private final InventoryProducer inventoryProducer;

    // Constructor injection — wires in all required dependencies
    public InventoryService(ProductRepository productRepository, StockMovementRepository stockMovementRepository, InventoryProducer inventoryProducer) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.inventoryProducer = inventoryProducer;
    }

    /**
     * The heart of the Inventory processing flow.
     * Invoked when an Order is placed. Evaluates stock dynamically, applies deductions if viable,
     * and broadcasts findings downstream using Kafka topics.
     */
    @Transactional
    public void processOrderPlacement(OrderEvent orderEvent) {

        // Flag to track whether all items in the order have sufficient stock
        boolean sufficientStock = true;

        // Accumulates failure reasons if any item has insufficient stock
        StringBuilder reason = new StringBuilder();

        // 1. Validating step: First pass to verify that ALL items have sufficient stock.
        for (OrderEvent.OrderItemDto item : orderEvent.getItems()) {

            // Look up the product by SKU — returns null if not found
            Product product = productRepository.findBySku(item.getSku()).orElse(null);

            // Fail if product doesn't exist, has no stock value, or stock is less than requested quantity
            if (product == null || product.getStockQuantity() == null || product.getStockQuantity() < item.getQuantity()) {
                sufficientStock = false; // We can't fulfill the entire order!
                reason.append("Insufficient stock for SKU: ").append(item.getSku()).append(". ");
            }
        }

        // Prepare Kafka response object
        InventoryReservationResult result = new InventoryReservationResult();
        result.setEventType("INVENTORY_RESERVATION_RESULT");
        result.setOrderId(orderEvent.getOrderId());
        result.setTimestamp(LocalDateTime.now());

        if (sufficientStock) {
            // 2. Deduction step: if validation passed, reduce quantities and log audit movements
            for (OrderEvent.OrderItemDto item : orderEvent.getItems()) {

                // Safe to use .get() here since stock was already validated in step 1
                Product product = productRepository.findBySku(item.getSku()).get();

                // Deduct the ordered quantity from the current stock level
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                productRepository.save(product);

                // Audit logging - required for observability
                StockMovement movement = new StockMovement();
                movement.setSku(product.getSku());
                movement.setQuantityChange(-item.getQuantity()); // Notice negative logic for deduction
                movement.setReason("ORDER_CONFIRMED");
                movement.setReferenceOrderId(orderEvent.getOrderId());
                movement.setTimestamp(LocalDateTime.now());
                stockMovementRepository.save(movement);

                // Check Threshold: Automatically issue a warning if restock is necessary
                if (product.getStockQuantity() < product.getLowStockThreshold()) {

                    // Build a low stock alert event to notify relevant parties via Kafka
                    LowStockAlert alert = new LowStockAlert();
                    alert.setEventType("LOW_STOCK_ALERT");
                    alert.setSku(product.getSku());
                    alert.setProductName(product.getProductName());
                    alert.setCurrentStock(product.getStockQuantity());
                    alert.setThreshold(product.getLowStockThreshold());
                    alert.setTimestamp(LocalDateTime.now());

                    // Publish the low stock alert to the Kafka topic
                    inventoryProducer.sendLowStockAlert(alert);
                }
            }

            // Mark reservation as successful since all items were deducted
            result.setStatus("SUCCESS");
        } else {
            // Mark reservation as failed and attach the reason(s) for the failure
            result.setStatus("FAILURE");
            result.setReason(reason.toString());
        }

        // 3. Dispatch result so the Order Service knows whether to confirm or discard the transaction
        inventoryProducer.sendReservationResult(result);
    }

    // Returns all products currently stored in the inventory
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Finds a product by its SKU — throws an exception if no matching product is found
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Saves a new product to the inventory and returns the persisted entity
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Admin functionality to fix stock gaps.
     */
    @Transactional
    public Product adjustStock(String sku, Integer adjustQuantity, String reason) {

        // Fetch the product by SKU — throws if not found
        Product product = getProductBySku(sku);

        // Apply the quantity adjustment — positive value adds stock, negative reduces it
        product.setStockQuantity(product.getStockQuantity() + adjustQuantity);
        productRepository.save(product);

        // Log the manual stock adjustment as an audit record
        StockMovement movement = new StockMovement();
        movement.setSku(sku);
        movement.setQuantityChange(adjustQuantity);
        movement.setReason(reason);
        movement.setTimestamp(LocalDateTime.now());
        stockMovementRepository.save(movement);

        return product;
    }

    /**
     * Synchronous stock validation used by Order Service (Phase 2).
     */
    public com.bitlord.inventoryservice.dto.StockValidationResponse validateStock(com.bitlord.inventoryservice.dto.StockValidationRequest request) {

        // Assume all items are available until proven otherwise
        boolean allAvailable = true;

        // Holds per-SKU validation results to be returned in the response
        java.util.List<com.bitlord.inventoryservice.dto.StockValidationResponse.Result> results = new java.util.ArrayList<>();

        // Guard clause — return early with failure if request or items list is null
        if (request == null || request.getItems() == null) {
            return new com.bitlord.inventoryservice.dto.StockValidationResponse(false, results);
        }

        for (com.bitlord.inventoryservice.dto.StockValidationRequest.Item item : request.getItems()) {

            // Skip any null items or items with a missing SKU
            if (item == null || item.getSku() == null) continue;

            // Look up the product — treat missing product as 0 available stock
            Product product = productRepository.findBySku(item.getSku()).orElse(null);
            int available = (product != null && product.getStockQuantity() != null) ? product.getStockQuantity() : 0;

            // Check if available stock meets or exceeds the requested quantity
            boolean sufficient = available >= item.getRequestedQuantity();

            // If any single item fails, the entire validation is marked as unavailable
            if (!sufficient) {
                allAvailable = false;
            }

            // Add the per-SKU result (requested qty, available qty, and sufficiency flag) to the list
            results.add(new com.bitlord.inventoryservice.dto.StockValidationResponse.Result(
                    item.getSku(), item.getRequestedQuantity(), available, sufficient
            ));
        }

        // Return the overall availability flag alongside the detailed per-SKU results
        return new com.bitlord.inventoryservice.dto.StockValidationResponse(allAvailable, results);
    }
}