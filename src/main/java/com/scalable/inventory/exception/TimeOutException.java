package com.scalable.inventory.exception;

public class TimeOutException extends RuntimeException {

    private String username;
    private long amount;

    public TimeOutException() {
        super();
    }

    public TimeOutException(String message) {
        super(message);
    }

    public TimeOutException(String message, Throwable cause) {
        super(message, cause);
    }
}