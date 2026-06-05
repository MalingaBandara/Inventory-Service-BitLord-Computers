package com.bitlord.inventoryservice.controller;

import com.bitlord.inventoryservice.dto.StockValidationRequest;
import com.bitlord.inventoryservice.dto.StockValidationResponse;
import com.bitlord.inventoryservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for synchronous stock validation.
 */
@RestController
@RequestMapping("/api/inventory") // All endpoints in this controller are prefixed with /api/inventory
public class StockValidationController {

    // Service layer that handles the stock validation business logic
    private final InventoryService inventoryService;

    // Constructor injection — wires in the InventoryService dependency
    public StockValidationController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // POST /api/inventory/validate — checks whether the requested stock quantity is available
    // Used by other services (e.g. Order Service via Feign Client) to verify stock before placing an order
    @PostMapping("/validate")
    public ResponseEntity<StockValidationResponse> validateStock(@RequestBody StockValidationRequest request) {
        return ResponseEntity.ok(inventoryService.validateStock(request));
    }
}