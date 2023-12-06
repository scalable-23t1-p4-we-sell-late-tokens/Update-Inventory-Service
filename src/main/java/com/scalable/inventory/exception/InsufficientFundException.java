package com.scalable.inventory.exception;

import com.scalable.inventory.type.json.RollbackJSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsufficientFundException extends RuntimeException {
    private String username;
    private String order_id;
    private String item_name;
    private long amount;
    private String message_response;

    public InsufficientFundException() {
        super();
    }

    public InsufficientFundException(RollbackJSON parameters) {
        super();
        this.username = parameters.getUsername();
        this.order_id = parameters.getOrder_id();
        this.item_name = parameters.getItem_name();
        this.amount = parameters.getAmount();
        this.message_response = parameters.getMessage_response();
    }

    public InsufficientFundException(String message) {
        super(message);
    }

    public InsufficientFundException(String message, Throwable cause) {
        super(message, cause);
    }
}
