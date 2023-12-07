package com.scalable.inventory.config;

import com.scalable.inventory.component.redis.RedisProgressChannel;
import com.scalable.inventory.component.redis.RedisRollbackChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisListenerConfig {

    @Bean
    public RedisMessageListenerContainer messageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisProgressChannel progressChannel,
            RedisRollbackChannel rollbackChannel) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Listen to payment to get progressive message
        // Listen to delivery to get rollback message
        container.addMessageListener(progressChannel, new ChannelTopic("paymentToInventory"));
        container.addMessageListener(rollbackChannel, new ChannelTopic("deliveryToInventory"));

        return container;
    }
}
