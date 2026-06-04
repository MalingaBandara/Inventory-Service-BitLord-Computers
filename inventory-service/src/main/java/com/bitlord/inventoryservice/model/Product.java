package com.bitlord.inventoryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(unique = true)
    private String sku;
    private String productName;
    private String category;
    private Integer stockQuantity;
    private Integer lowStockThreshold = 5;
    private BigDecimal unitPrice;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
