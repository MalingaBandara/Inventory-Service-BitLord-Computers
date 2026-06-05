package com.bitlord.inventoryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


// Lombok annotation — auto-generates getters, setters, equals, hashCode, and toString
@Data
// Marks this class as a JPA entity — maps to a database table named "product"
@Entity
public class Product {

    // Primary key of the product table
    @Id
    // Auto-increments the ID value on each new insert
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SKU (Stock Keeping Unit) — must be unique across all products
    @jakarta.persistence.Column(unique = true)
    private String sku;

    private String productName;      // Display name of the product
    private String category;         // Category the product belongs to (e.g. "GPU", "RAM")
    private Integer stockQuantity;   // Current number of units available in stock
    private Integer lowStockThreshold = 5; // Minimum stock level before a low-stock alert is triggered — defaults to 5
    private BigDecimal unitPrice;    // Price per single unit of the product

    // Automatically updates this field with the current timestamp whenever the record is modified
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}