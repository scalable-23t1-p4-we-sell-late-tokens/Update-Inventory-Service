package com.scalable.inventory.exception;


public class ItemAlreadyExists extends RuntimeException{
    public ItemAlreadyExists() {
        super();
    }

    public ItemAlreadyExists(String message) {
        super(message);
    }

    public ItemAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }
}
