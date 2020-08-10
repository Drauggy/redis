package com.redis.drauggy.config;

import com.redis.drauggy.service.SuperService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@RequiredArgsConstructor
@Configuration
public class RedisMessageListenerRegistrar implements ApplicationRunner {
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final SuperService superService;
    private final ChannelTopic topic;

    @Override
    public void run(ApplicationArguments args) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(superService);
        redisMessageListenerContainer.addMessageListener(listenerAdapter, topic);

    }
}
