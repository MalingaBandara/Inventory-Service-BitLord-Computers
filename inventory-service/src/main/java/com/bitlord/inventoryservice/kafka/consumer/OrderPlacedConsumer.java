package com.bitlord.inventoryservice.kafka.consumer;

import com.bitlord.inventoryservice.dto.OrderEvent;
import com.bitlord.inventoryservice.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Primary point of contact for external actions into Inventory rules engine.
 */
@Service
public class OrderPlacedConsumer {
    private final InventoryService inventoryService;

    public OrderPlacedConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Constantly listens to incoming "order-placed" topics to guarantee
     * realtime inventory reservation checking seamlessly.
     */
    @KafkaListener(topics = "order-placed", groupId = "inventory-group")
    public void consumeOrderPlacedEvent(OrderEvent orderEvent) {
        // Dispatch to internal business processes
        inventoryService.processOrderPlacement(orderEvent);
    }
}
