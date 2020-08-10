package com.redis.drauggy2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@ComponentScan("com.redis.drauggy2")

public class RedisConfig {


    @Bean
    StringRedisConnection stringRedisConnection(RedisConnectionFactory connectionFactory) {
        var connection = RedisConnectionUtils.getConnection(connectionFactory);
        return new DefaultStringRedisConnection(connection);
    }

    @Bean
    ChannelTopic topicSend(@Value("${receiveTopic}") String topic) {
        return new ChannelTopic(topic);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        var listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        return listenerContainer;
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
