package com.scalable.inventory.exception;

import com.scalable.inventory.type.json.ProgressJSON;
import com.scalable.inventory.type.json.RollbackJSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutOfStockException extends RuntimeException {
    private String username;
    private String order_id;
    private String item_name;
    private long amount;
    private String message_response;

    public OutOfStockException() {
        super();
    }

    public OutOfStockException(ProgressJSON parameters) {
        super();
        this.username = parameters.getUsername();
        this.order_id = parameters.getOrder_id();
        this.item_name = parameters.getItem_name();
        this.amount = parameters.getAmount();
        this.message_response = null;
    }

    public OutOfStockException(String message) {
        super(message);
    }

    public OutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
