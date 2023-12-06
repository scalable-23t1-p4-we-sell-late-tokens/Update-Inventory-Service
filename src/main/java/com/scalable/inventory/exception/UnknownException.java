package com.scalable.inventory.exception;

import com.scalable.inventory.type.json.RollbackJSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnknownException extends Exception{
    private String username;
    private String order_id;
    private String item_name;
    private long amount;
    private String message_response;

    public UnknownException() {
        super();
    }

    public UnknownException(RollbackJSON parameters) {
        super();
        this.username = parameters.getUsername();
        this.order_id = parameters.getOrder_id();
        this.item_name = parameters.getItem_name();
        this.amount = parameters.getAmount();
        this.message_response = parameters.getMessage_response();
    }

    public UnknownException(String message) {
        super(message);
    }

    public UnknownException(String message, Throwable cause) {
        super(message, cause);
    }
}
