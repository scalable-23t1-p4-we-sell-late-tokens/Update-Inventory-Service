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
            String itemName = json.getItem_name();
            long amount = json.getAmount();
            String messageFlag = json.getMessage_flag();

            inventoryService.orderItem(itemName, amount);
            messageProcessed = true;

            JSONBuilder response = new JSONBuilder();
            response.addField("username", username)
                    .addField("amount", amount)
                    .addField("message_response", messageFlag);

            if(messageFlag.equals("inventory")) {
                throw new ForceRollbackException(response.buildAsClass(RollbackJSON.class));
            }

            inventoryService.sendProgressSignal(response.buildAsString());
            messageProcessed = false;

        } catch (ForceRollbackException forceRollbackException) {
            System.err.println("The message was flagged for rollback demonstration, you can chill out (unless unintented) "
                    + forceRollbackException.getMessage());
            inventoryService.rollback(forceRollbackException.getUsername(),
                    forceRollbackException.getAmount());
        } catch (InsufficientFundException insufficientFundException) {
            System.err.println("You don't got the cash: " + insufficientFundException.getMessage());
            inventoryService.rollback(insufficientFundException.getUsername(),
                    insufficientFundException.getAmount());
        } catch (ItemNotFoundException itemNotFoundException) {
            System.err.println("Item doesn't exist: " + itemNotFoundException.getMessage());
        } catch (TimeOutException timeOutException) {
            System.err.println("The service has reached timeout period: " + timeOutException.getMessage());
        } catch (UnknownException unknownException) {
            System.err.println("Exception occurred, read the error message: " + unknownException.getMessage());
            if (messageProcessed) {
                inventoryService.rollback(unknownException.getUsername(),
                        unknownException.getAmount());
            }
        }
    }
}
