package com.scalable.inventory.component.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.inventory.exception.UnknownException;
import com.scalable.inventory.service.InventoryService;
import com.scalable.inventory.type.json.JSONBuilder;
import com.scalable.inventory.type.json.JSONMessageTypeFactory;
import com.scalable.inventory.type.json.RollbackJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisRollbackChannel implements MessageListener {
    private final ObjectMapper objectMapper;

    @Autowired
    InventoryService inventoryService;

    public RedisRollbackChannel(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody());
        JSONMessageTypeFactory jsonMessageTypeFactory = new JSONMessageTypeFactory();
        try {
            RollbackJSON json = (RollbackJSON) jsonMessageTypeFactory
                    .createMessage(payload, RollbackJSON.class);


            String username = json.getUsername();
            String orderID = json.getOrder_id();
            String itemName = json.getItem_name();
            long amount = json.getAmount();
            String messageResponse = json.getMessage_response();

            System.out.println("Beginning rollback process");
            inventoryService.rollback(itemName, amount);

            JSONBuilder response = new JSONBuilder();
            response.addField("username", username)
                    .addField("order_id", orderID)
                    .addField("amount", amount)
                    .addField("message_response", messageResponse);

            inventoryService.sendRollbackSignal(response.buildAsClass(RollbackJSON.class));
            System.out.println("Rollback complete");


        } catch (UnknownException unknownException) {
            System.err.println("Unknown Error Occurred: " + unknownException.getMessage());
        }
    }
}
