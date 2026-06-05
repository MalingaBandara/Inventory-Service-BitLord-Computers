package com.bitlord.inventoryservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing an order event in the system.
 *
 * This is typically used in event-driven architecture (Kafka / messaging)
 * to transfer order information from the Order Service to Inventory Service.
 */
@Data
public class OrderEvent {

    // Type of event (e.g., ORDER_CREATED, ORDER_CANCELLED)
    private String eventType;

    // Unique identifier of the order
    private String orderId;

    // Unique identifier of the customer who placed the order
    private String customerId;

    // Email address of the customer for notifications
    private String customerEmail;

    // List of items included in the order
    private List<OrderItemDto> items;

    // Total monetary value of the order
    private BigDecimal totalAmount;

    // Timestamp when the order event was created
    private LocalDateTime timestamp;

    /**
     * DTO representing each individual item inside an order.
     */
    @Data
    public static class OrderItemDto {

        // Stock keeping unit (unique product identifier)
        private String sku;

        // Human-readable product name
        private String productName;

        // Quantity of the product ordered
        private Integer quantity;

        // Price per single unit of the product
        private BigDecimal unitPrice;
    }
}