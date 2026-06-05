package com.bitlord.inventoryservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO representing a low stock alert event.
 *
 * This is typically used in event-driven systems to notify
 * other services or monitoring systems when a product's
 * inventory level falls below a defined threshold.
 */
@Data
public class LowStockAlert {

    // Type of event (e.g., LOW_STOCK_WARNING, LOW_STOCK_ALERT)
    private String eventType;

    // Stock Keeping Unit (unique product identifier)
    private String sku;

    // Human-readable name of the product
    private String productName;

    // Current available stock quantity at the time of alert
    private Integer currentStock;

    // Minimum threshold value that triggers this alert
    private Integer threshold;

    // Timestamp when this low stock event was generated
    private LocalDateTime timestamp;
}