package com.scalable.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scalable.inventory.exception.ItemNotFoundException;
import com.scalable.inventory.model.Inventory;
import com.scalable.inventory.repository.InventoryRepository;

import java.util.Optional;


@Service
public class InventoryService {
    @Autowired
    private InventoryRepository createInventoryRepository;

    public void createDefaultItem(String itemName) {
        Optional<Inventory> entity = createInventoryRepository.findByItemName(itemName);
        if(!entity.isPresent()) {
            Inventory newPayment = new Inventory(itemName);
            createInventoryRepository.save(newPayment);
        }
    }

    public void createNewItem(String itemName, long stock) {
        Optional<Inventory> entity = createInventoryRepository.findByItemName(itemName);
        if(!entity.isPresent()) {
            Inventory newPayment = new Inventory(itemName, stock);
            createInventoryRepository.save(newPayment);
        }
    }

    public Optional<Inventory> getItem(String itemName) {
        return createInventoryRepository.findByItemName(itemName);
    }


    public void orderItem(String itemName, long amount) throws Exception{
        Inventory item = createInventoryRepository.findByItemName(itemName).orElse(null);

        if (item != null) {
            item.setStock(item.getStock() - 1);
            createInventoryRepository.save(item);
        } else {
            throw new ItemNotFoundException("Item: " + itemName + " not found");
        }
    }

}
