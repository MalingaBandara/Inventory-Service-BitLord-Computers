package com.bitlord.inventoryservice.kafka.producer;

import com.bitlord.inventoryservice.dto.InventoryReservationResult;
import com.bitlord.inventoryservice.dto.LowStockAlert;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReservationResult(InventoryReservationResult result) {
        kafkaTemplate.send("inventory-reservation-result", result);
    }

    public void sendLowStockAlert(LowStockAlert alert) {
        kafkaTemplate.send("low-stock-alert", alert);
    }
}
