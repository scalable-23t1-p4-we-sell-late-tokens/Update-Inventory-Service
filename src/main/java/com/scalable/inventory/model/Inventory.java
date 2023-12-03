package com.scalable.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String itemName;
    private long stock;

    public Inventory() { }

    public Inventory(String itemName, long stock) {
        this.itemName = itemName;
        this.stock = stock;
    }

    // Let's say that starting stock is 10 unit
    public Inventory(String itemName) {
        this.itemName = itemName;
        this.stock = 10;
    }
}
