package com.scalable.inventory.type.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.inventory.exception.UnknownException;
import lombok.*;



public class JSONMessageTypeFactory {
    private ObjectMapper objectMapper;

    public JSONMessageTypeFactory() {
        this.objectMapper = new ObjectMapper();
    }

    public BaseJSON createMessage(String jsonString, Class<? extends BaseJSON> messageType) throws UnknownException{
        try {
            return objectMapper.readValue(jsonString, messageType);
        } catch (Exception e) {
            throw new UnknownException(e.getMessage());
        }
    }
}
