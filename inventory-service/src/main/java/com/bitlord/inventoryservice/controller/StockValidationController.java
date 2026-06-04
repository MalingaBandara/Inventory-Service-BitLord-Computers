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
@RequestMapping("/api/inventory")
public class StockValidationController {

    private final InventoryService inventoryService;

    public StockValidationController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/validate")
    public ResponseEntity<StockValidationResponse> validateStock(@RequestBody StockValidationRequest request) {
        return ResponseEntity.ok(inventoryService.validateStock(request));
    }
}
