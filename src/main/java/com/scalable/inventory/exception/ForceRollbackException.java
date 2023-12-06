package com.scalable.inventory.exception;

import com.scalable.inventory.type.json.RollbackJSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForceRollbackException extends RuntimeException {
    private String username;
    private String order_id;
    private String item_name;
    private long amount;
    private String message_response;

    public ForceRollbackException() {
        super();
    }

    public ForceRollbackException(RollbackJSON parameters) {
        super();
        this.username = parameters.getUsername();
        this.item_name = parameters.getItem_name();
        this.amount = parameters.getAmount();
        this.message_response = parameters.getMessage_response();
    }

    public ForceRollbackException(String message) {
        super(message);
    }

    public ForceRollbackException(String message, Throwable cause) {
        super(message, cause);
    }
}
