package com.bitlord.inventoryservice.dto;

import lombok.Data;
import java.time.LocalDateTime;


/**
 * DTO representing the result of an inventory reservation event.
 *
 * This object is typically used in event-driven communication
 * to notify other services about the outcome of inventory reservation
 * (e.g., success or failure of reserving stock for an order).
 */
@Data
public class InventoryReservationResult {

    // Type of event (e.g., RESERVATION_SUCCESS, RESERVATION_FAILED)
    private String eventType;

    // Unique identifier of the order related to this reservation event
    private String orderId;

    // Status of the reservation (e.g., SUCCESS, FAILED, PENDING)
    private String status;

    // Reason for failure (if reservation was not successful)
    private String reason;

    // Timestamp when the reservation event was created/processed
    private LocalDateTime timestamp;
}