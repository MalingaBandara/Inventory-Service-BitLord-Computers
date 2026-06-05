package com.bitlord.inventoryservice.kafka.producer;

import com.bitlord.inventoryservice.dto.InventoryReservationResult;
import com.bitlord.inventoryservice.dto.LowStockAlert;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


// Kafka producer service responsible for publishing inventory-related events to Kafka topics
@Service
public class InventoryProducer {

    // KafkaTemplate used to send messages to Kafka topics
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Constructor injection — wires in the KafkaTemplate dependency
    public InventoryProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Publishes the inventory reservation result to the "inventory-reservation-result" topic
    // Called after processing a stock reservation request from the order service
    public void sendReservationResult(InventoryReservationResult result) {
        kafkaTemplate.send("inventory-reservation-result", result);
    }

    // Publishes a low stock alert to the "low-stock-alert" topic
    // Called when a product's stock level drops below the defined threshold
    public void sendLowStockAlert(LowStockAlert alert) {
        kafkaTemplate.send("low-stock-alert", alert);
    }
}