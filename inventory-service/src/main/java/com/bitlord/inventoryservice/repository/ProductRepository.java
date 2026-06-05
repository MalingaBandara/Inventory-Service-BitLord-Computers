package com.bitlord.inventoryservice.repository;

import com.bitlord.inventoryservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repository interface for Product entity — provides standard CRUD operations via JpaRepository
// JpaRepository<Product, Long> — Product is the entity type, Long is the primary key type
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query method — finds a product by its SKU field
    // Returns Optional to safely handle the case where no matching product is found
    Optional<Product> findBySku(String sku);
}