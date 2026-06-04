package com.bitlord.inventoryservice.controller;

import com.bitlord.inventoryservice.model.Product;
import com.bitlord.inventoryservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Product> getProduct(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getProductBySku(sku));
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(inventoryService.addProduct(product));
    }

    @PatchMapping("/{sku}/adjust")
    public ResponseEntity<Product> adjustStock(@PathVariable String sku, @RequestParam Integer quantityChange, @RequestParam String reason) {
        return ResponseEntity.ok(inventoryService.adjustStock(sku, quantityChange, reason));
    }
}
