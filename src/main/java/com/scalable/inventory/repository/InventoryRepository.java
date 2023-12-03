package com.scalable.inventory.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.scalable.inventory.model.Inventory;

import java.util.Optional;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, String> {
    Optional<Inventory> findByItemName(String itemName);
}
