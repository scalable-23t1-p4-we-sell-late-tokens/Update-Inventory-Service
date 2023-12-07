package com.scalable.inventory.service;

import com.scalable.inventory.exception.ItemNotFoundException;
import com.scalable.inventory.exception.OutOfStockException;
import com.scalable.inventory.exception.UnknownException;
import com.scalable.inventory.service.redis.RedisService;
import com.scalable.inventory.type.json.JSONBuilder;
import com.scalable.inventory.type.json.JSONMessageTypeFactory;
import com.scalable.inventory.type.json.ProgressJSON;
import com.scalable.inventory.type.json.RollbackJSON;
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


    public void orderItem(String itemName, long amount) throws OutOfStockException, ItemNotFoundException {
        Inventory item = createInventoryRepository.findByItemName(itemName).orElse(null);

        if (item != null) {
            if (amount > item.getStock()) {
                throw new OutOfStockException("Out of stock: { current stock: " + item.getStock() + ", amount requested: " + amount + " }\n");
            }
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

    public void sendRollbackSignal(RollbackJSON json) throws UnknownException{
        try {
            JSONBuilder response = new JSONBuilder();
            response.addField("username", json.getUsername())
                    .addField("order_id", json.getOrder_id())
                    .addField("amount", json.getAmount())
                    .addField("item_name", json.getItem_name())
                    .addField("message_response", json.getMessage_response());
            redisService.sendMessageToChannel("inventoryToPayment", response.buildAsString());
        } catch (Exception e) {
            throw new UnknownException(json);
        }
    }

    public void sendProgressSignal(ProgressJSON json) throws UnknownException{
        try {
            JSONBuilder response = new JSONBuilder();
            response.addField("username", json.getUsername())
                    .addField("order_id", json.getOrder_id())
                    .addField("amount", json.getAmount())
                    .addField("item_name", json.getItem_name())
                    .addField("message_flag", json.getMessage_flag());
            redisService.sendMessageToChannel("inventoryToDelivery", response.buildAsString());
        } catch (Exception e) {
            throw new UnknownException(json);
        }
    }

}
