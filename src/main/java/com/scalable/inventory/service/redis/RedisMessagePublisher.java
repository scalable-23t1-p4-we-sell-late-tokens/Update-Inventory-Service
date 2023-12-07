package com.scalable.inventory.service.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisMessagePublisher {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisMessagePublisher(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishMessage(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }
}

