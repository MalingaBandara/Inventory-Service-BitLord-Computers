package com.bitlord.inventoryservice.repository;

import com.bitlord.inventoryservice.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository interface for StockMovement entity
// Extends JpaRepository to get built-in CRUD operations (save, findById, findAll, delete, etc.)
// Long is the data type of the StockMovement primary key (id)
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}