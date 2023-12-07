package com.scalable.inventory.component.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.inventory.exception.*;
import com.scalable.inventory.service.InventoryService;
import com.scalable.inventory.type.json.JSONBuilder;
import com.scalable.inventory.type.json.JSONMessageTypeFactory;
import com.scalable.inventory.type.json.ProgressJSON;
import com.scalable.inventory.type.json.RollbackJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

// TODO: Figure out and implement the timeout mechanism
// TODO: Check whether message_response is appropriate
@Component
public class RedisProgressChannel implements MessageListener {

    private final ObjectMapper objectMapper;

    @Autowired
    InventoryService inventoryService;

    public RedisProgressChannel(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        boolean messageProcessed = false;
        String payload = new String(message.getBody());
        JSONMessageTypeFactory jsonMessageTypeFactory = new JSONMessageTypeFactory();
        try {
            ProgressJSON json = (ProgressJSON) jsonMessageTypeFactory
                    .createMessage(payload, ProgressJSON.class);

            String username = json.getUsername();
            String orderID = json.getOrder_id();
            String itemName = json.getItem_name();
            long amount = json.getAmount();
            String messageFlag = json.getMessage_flag();

            inventoryService.createNewItem(itemName, 100);
            inventoryService.orderItem(itemName, amount);
            messageProcessed = true;

            if(messageFlag.equals("inventory")) {
                JSONBuilder response = new JSONBuilder();
                response.addField("username", username)
                        .addField("order_id", orderID)
                        .addField("item_name", itemName)
                        .addField("amount", amount)
                        .addField("message_response", null);
                throw new ForceRollbackException(response.buildAsClass(RollbackJSON.class));
            }

            JSONBuilder response = new JSONBuilder();
                response.addField("username", username)
                        .addField("order_id", orderID)
                        .addField("item_name", itemName)
                        .addField("amount", amount)
                        .addField("message_flag", messageFlag);

            inventoryService.sendProgressSignal(response.buildAsClass(ProgressJSON.class));
            messageProcessed = false;

        } catch (ForceRollbackException forceRollbackException) {
            System.err.println("The message was flagged for rollback demonstration, you can chill out (unless unintented): "
                    + forceRollbackException.getMessage());
            System.out.println("Beginning rollback process");
            inventoryService.rollback(forceRollbackException.getItem_name(),
                    forceRollbackException.getAmount());
            JSONBuilder response = new JSONBuilder();
            response.addField("username", forceRollbackException.getUsername())
                    .addField("order_id", forceRollbackException.getOrder_id())
                    .addField("amount", forceRollbackException.getAmount())
                    .addField("message_response", forceRollbackException.getMessage_response());
            try {
                inventoryService.sendRollbackSignal(response.buildAsClass(RollbackJSON.class));
                System.out.println("Rollback complete");
            }
            catch (UnknownException unknownException) {
                System.err.println("Failed to rollback: " + unknownException.getMessage());
            }

        } catch (OutOfStockException outOfStockException) {
            System.err.println("Not enough item on the stock to satisfy your need: " + outOfStockException.getMessage());

        } catch (ItemNotFoundException itemNotFoundException) {
            System.err.println("Item doesn't exist: " + itemNotFoundException.getMessage());

        } catch (TimeOutException timeOutException) {
            System.err.println("The service has reached timeout period: " + timeOutException.getMessage());
            if (messageProcessed) {
                System.out.println("Beginning rollback process");
                inventoryService.rollback(timeOutException.getItem_name(),
                        timeOutException.getAmount());
                JSONBuilder response = new JSONBuilder();
                response.addField("username", timeOutException.getUsername())
                    .addField("order_id", timeOutException.getOrder_id())
                    .addField("amount", timeOutException.getAmount())
                    .addField("message_response", timeOutException.getMessage_response());
                try {
                    inventoryService.sendRollbackSignal(response.buildAsClass(RollbackJSON.class));
                    System.out.println("Rollback complete");
                }
                catch (UnknownException unknownException) {
                    System.err.println("Failed to rollback " + unknownException.getMessage());
                }

            }
        } catch (UnknownException unknownException) {
            System.err.println("Exception occurred, read the error message: " + unknownException.getMessage());
            if (messageProcessed) {
                System.out.println("Beginning rollback process");
                inventoryService.rollback(unknownException.getItem_name(),
                        unknownException.getAmount());
                JSONBuilder response = new JSONBuilder();
                response.addField("username", unknownException.getUsername())
                    .addField("order_id", unknownException.getOrder_id())
                    .addField("amount", unknownException.getAmount())
                    .addField("message_response", unknownException.getMessage_response());
                try {
                    inventoryService.sendRollbackSignal(response.buildAsClass(RollbackJSON.class));
                    System.out.println("Rollback complete");
                }
                catch (UnknownException unknownUnknownException) {
                    System.err.println("Failed to rollback " + unknownUnknownException.getMessage());
                }
            }
        }
    }
}
