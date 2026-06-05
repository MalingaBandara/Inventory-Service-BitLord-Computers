package com.bitlord.inventoryservice.controller;

import com.bitlord.inventoryservice.model.Product;
import com.bitlord.inventoryservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// REST controller that exposes inventory-related API endpoints
@RestController
@RequestMapping("/api/inventory") // All endpoints in this controller are prefixed with /api/inventory
public class InventoryController {

    // Service layer that contains the business logic for inventory operations
    private final InventoryService inventoryService;

    // Constructor injection — wires in the InventoryService dependency
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // GET /api/inventory — returns a list of all products in the inventory
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }

    // GET /api/inventory/{sku} — returns a single product matched by its SKU
    @GetMapping("/{sku}")
    public ResponseEntity<Product> getProduct(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getProductBySku(sku));
    }

    // POST /api/inventory — adds a new product to the inventory
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(inventoryService.addProduct(product));
    }

    // PATCH /api/inventory/{sku}/adjust — adjusts the stock quantity of a product by SKU
    // quantityChange: positive value increases stock, negative value decreases stock
    // reason: describes why the stock adjustment was made (e.g. "restock", "damage")
    @PatchMapping("/{sku}/adjust")
    public ResponseEntity<Product> adjustStock(@PathVariable String sku, @RequestParam Integer quantityChange, @RequestParam String reason) {
        return ResponseEntity.ok(inventoryService.adjustStock(sku, quantityChange, reason));
    }
}