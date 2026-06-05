package com.bitlord.inventoryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;


// Lombok annotation — auto-generates getters, setters, toString, equals, and hashCode
@Data
// Marks this class as a JPA entity — mapped to a database table
@Entity
public class StockMovement {

    // Primary key of the StockMovement table
    @Id
    // Auto-increments the ID value for each new record
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SKU of the product whose stock was moved
    private String sku;

    // The amount of stock changed — positive for increase, negative for decrease
    private Integer quantityChange;

    // Describes why the stock movement occurred (e.g. "restock", "order", "damage")
    private String reason;

    // The order ID linked to this stock movement, if triggered by an order
    private String referenceOrderId;

    // The date and time when this stock movement was recorded
    private LocalDateTime timestamp;
}