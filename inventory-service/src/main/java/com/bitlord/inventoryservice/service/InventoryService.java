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
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryProducer inventoryProducer;

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
        boolean sufficientStock = true;
        StringBuilder reason = new StringBuilder();

        // 1. Validating step: First pass to verify that ALL items have sufficient stock.
        for (OrderEvent.OrderItemDto item : orderEvent.getItems()) {
            Product product = productRepository.findBySku(item.getSku()).orElse(null);
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
                Product product = productRepository.findBySku(item.getSku()).get();
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
                    LowStockAlert alert = new LowStockAlert();
                    alert.setEventType("LOW_STOCK_ALERT");
                    alert.setSku(product.getSku());
                    alert.setProductName(product.getProductName());
                    alert.setCurrentStock(product.getStockQuantity());
                    alert.setThreshold(product.getLowStockThreshold());
                    alert.setTimestamp(LocalDateTime.now());
                    inventoryProducer.sendLowStockAlert(alert);
                }
            }
            result.setStatus("SUCCESS");
        } else {
            result.setStatus("FAILURE");
            result.setReason(reason.toString());
        }

        // 3. Dispatch result so the Order Service knows whether to confirm or discard the transaction
        inventoryProducer.sendReservationResult(result);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Admin functionality to fix stock gaps.
     */
    @Transactional
    public Product adjustStock(String sku, Integer adjustQuantity, String reason) {
        Product product = getProductBySku(sku);
        product.setStockQuantity(product.getStockQuantity() + adjustQuantity);
        productRepository.save(product);

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
        boolean allAvailable = true;
        java.util.List<com.bitlord.inventoryservice.dto.StockValidationResponse.Result> results = new java.util.ArrayList<>();

        if (request == null || request.getItems() == null) {
            return new com.bitlord.inventoryservice.dto.StockValidationResponse(false, results);
        }

        for (com.bitlord.inventoryservice.dto.StockValidationRequest.Item item : request.getItems()) {
            if (item == null || item.getSku() == null) continue;
            
            Product product = productRepository.findBySku(item.getSku()).orElse(null);
            int available = (product != null && product.getStockQuantity() != null) ? product.getStockQuantity() : 0;
            boolean sufficient = available >= item.getRequestedQuantity();
            if (!sufficient) {
                allAvailable = false;
            }
            results.add(new com.bitlord.inventoryservice.dto.StockValidationResponse.Result(
                    item.getSku(), item.getRequestedQuantity(), available, sufficient
            ));
        }

        return new com.bitlord.inventoryservice.dto.StockValidationResponse(allAvailable, results);
    }
}
