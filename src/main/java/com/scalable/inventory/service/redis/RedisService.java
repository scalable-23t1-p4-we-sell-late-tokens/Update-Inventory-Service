package com.scalable.inventory.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisMessagePublisher messagePublisher;

    @Autowired
    public RedisService(RedisMessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    public void sendMessageToChannel(String channel, String message) {
        messagePublisher.publishMessage(channel, message);
    }
}

