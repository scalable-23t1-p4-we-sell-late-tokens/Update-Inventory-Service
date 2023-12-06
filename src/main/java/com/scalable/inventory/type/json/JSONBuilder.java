package com.scalable.inventory.type.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.inventory.exception.UnknownException;

import java.util.HashMap;
import java.util.Map;

public class JSONBuilder {

    private final Map<String, Object> jsonMap;

    public JSONBuilder() {
        this.jsonMap = new HashMap<>();
    }

    public JSONBuilder addField(String key, Object value) {
        jsonMap.put(key, value);
        return this;
    }

    public JSONBuilder addObject(String key, JSONBuilder nestedBuilder) throws UnknownException{
        jsonMap.put(key, nestedBuilder.buildAsString());
        return this;
    }

    public <T extends BaseJSON> T buildAsClass(Class<T> messageType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(buildAsString(), messageType);
        } catch (Exception e) {
            // Handle exception (e.g., JSON deserialization error)
            e.printStackTrace();
            return null;
        }
    }

    public String buildAsString() throws UnknownException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new UnknownException(jsonProcessingException.getMessage());
        } catch (Exception e) {
            throw new UnknownException(e.getMessage());
        }
    }
}
