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

    // Service layer that handles inventory business logic for incoming order events
    private final InventoryService inventoryService;

    // Constructor injection — wires in the InventoryService dependency
    public OrderPlacedConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Constantly listens to incoming "order-placed" topics to guarantee
     * realtime inventory reservation checking seamlessly.
     */
    // Listens to the "order-placed" Kafka topic as part of the "inventory-group" consumer group
    // Triggered automatically whenever a new order event is published to the topic
    @KafkaListener(topics = "order-placed", groupId = "inventory-group")
    public void consumeOrderPlacedEvent(OrderEvent orderEvent) {
        // Dispatch to internal business processes
        // Passes the received order event to the service layer for stock validation and reservation
        inventoryService.processOrderPlacement(orderEvent);
    }
}