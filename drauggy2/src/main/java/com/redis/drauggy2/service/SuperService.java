package com.redis.drauggy2.service;

import com.drauggy.signature.SignatureResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

public class SuperService implements InitializingBean, MessageListener {

    private final StringRedisTemplate redisTemplate;
    private final SignUp signUp;
    private final ObjectMapper mapper;
    @Value("${sendTopic}")
    private String topic;
    private BoundHashOperations bho;

    @SneakyThrows

    private void publish(final String message) {
        redisTemplate.convertAndSend(topic, message);
    }


    @Override
    public void afterPropertiesSet() {
        bho = redisTemplate.boundHashOps("signature.request");
    }

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] bytes) {


        String UUID = new String(message.getBody());
        String data = (String) bho.get(UUID);

        String signature = signUp.makeSignature(data);
        SignatureResponse response = new SignatureResponse(UUID, signature);

        publish(mapper.writeValueAsString(response));
    }
}
