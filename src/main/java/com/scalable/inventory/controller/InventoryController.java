package com.scalable.inventory.controller;
import com.scalable.inventory.exception.ItemNotFoundException;
import com.scalable.inventory.model.Inventory;
import com.scalable.inventory.service.InventoryService;

import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("inventory")
public class InventoryController {
    @Autowired
    InventoryService inventoryService;

    private final MeterRegistry registry;

    public InventoryController(MeterRegistry registry) {
        this.registry = registry;
    }

    private final Logger LOG = LoggerFactory.getLogger(InventoryController.class);

    @PostMapping("/create-default/{itemName}")
    public ResponseEntity<String> createNewDefaultPayment(@PathVariable String itemName)
    {
        inventoryService.createDefaultItem(itemName);

        registry.counter("stockItem.total", "username", itemName).increment();
        LOG.info("Adding stock " + itemName + " to the inventory");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/create/{itemName}/{stock}")
    public ResponseEntity<String> createNewPayment(@PathVariable String itemName, @PathVariable long stock)
    {
        inventoryService.createNewItem(itemName, stock);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/{itemName}/{amount}")
    public ResponseEntity<String> orderItem(@PathVariable String itemName, @PathVariable long amount)
    {
        try {
            inventoryService.orderItem(itemName, amount);
        } catch (ItemNotFoundException itemNotFoundException) {
            return ResponseEntity.internalServerError().body("Item not found");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        inventoryService.createNewItem(itemName, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stock/{itemName}")
    public ResponseEntity<Long> getStock(@PathVariable String itemName) {
        Inventory retrievedUsername = inventoryService.getItem(itemName).orElse(null);
        if (retrievedUsername != null) {
            return ResponseEntity.ok(retrievedUsername.getStock());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}