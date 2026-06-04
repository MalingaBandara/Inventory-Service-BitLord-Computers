package com.bitlord.inventoryservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryReservationResult {
    private String eventType;
    private String orderId;
    private String status;
    private String reason;
    private LocalDateTime timestamp;
}
