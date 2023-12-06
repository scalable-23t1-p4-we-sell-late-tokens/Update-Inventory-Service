package com.scalable.inventory.service;

import com.scalable.inventory.exception.ItemNotFoundException;
import com.scalable.inventory.exception.UnknownException;
import com.scalable.inventory.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scalable.inventory.exception.ItemAlreadyExists;
import com.scalable.inventory.model.Inventory;
import com.scalable.inventory.repository.InventoryRepository;

import java.util.Optional;


@Service
public class InventoryService {
    @Autowired
    private InventoryRepository createInventoryRepository;

    @Autowired
    private RedisService redisService;

    public void createDefaultItem(String itemName) throws ItemAlreadyExists{
        Optional<Inventory> entity = createInventoryRepository.findByItemName(itemName);
        if(!entity.isPresent()) {
            Inventory newPayment = new Inventory(itemName);
            createInventoryRepository.save(newPayment);
        }
        else {
            throw new ItemAlreadyExists();
        }
    }

    public void createNewItem(String itemName, long stock) throws ItemAlreadyExists{
        Optional<Inventory> entity = createInventoryRepository.findByItemName(itemName);
        if(!entity.isPresent()) {
            Inventory newPayment = new Inventory(itemName, stock);
            createInventoryRepository.save(newPayment);
        }
        else {
            throw new ItemAlreadyExists();
        }
    }

    public Optional<Inventory> getItem(String itemName) {
        return createInventoryRepository.findByItemName(itemName);
    }


    public void orderItem(String itemName, long amount) throws ItemNotFoundException {
        Inventory item = createInventoryRepository.findByItemName(itemName).orElse(null);

        if (item != null) {
            item.setStock(item.getStock() - amount);
            createInventoryRepository.save(item);
        } else {
            throw new ItemNotFoundException("Item: " + itemName + " not found");
        }
    }

    public void rollback(String itemName, long amount) throws ItemNotFoundException {
        Inventory item = createInventoryRepository.findByItemName(itemName).orElse(null);

        if (item != null) {
            item.setStock(item.getStock() + amount);
            createInventoryRepository.save(item);
        } else {
            throw new ItemNotFoundException("Item: " + itemName + " not found");
        }
    }

    public void sendRollbackSignal(String jsonString) throws UnknownException{
        try {
            redisService.sendMessageToChannel("inventoryToPayment", jsonString);
        } catch (Exception e) {
            throw new UnknownException(e.getMessage());
        }
    }

    public void sendProgressSignal(String jsonString) throws UnknownException{
        try {
            redisService.sendMessageToChannel("inventoryToDelivery", jsonString);
        } catch (Exception e) {
            throw new UnknownException(e.getMessage());
        }
    }

}
