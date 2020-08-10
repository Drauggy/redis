package com.redis.drauggy.service;

import com.drauggy.signature.SignatureRequest;
import com.drauggy.signature.SignatureResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.drauggy.exception.ResponseException;
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

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Slf4j
@Service
public class SuperService implements InitializingBean, MessageListener {


    private final Map<String, CompletableFuture<String>> map = new ConcurrentHashMap<>();
    private final StringRedisTemplate redisTemplate;
    private final DataGenerator generator;
    private final ObjectMapper mapper;
    private BoundHashOperations bho;
    private final SignValidation validation;
    @Value("${sendTopic}")
    private String topic;

    @Override
    public void afterPropertiesSet() {
        bho = redisTemplate.boundHashOps("signature.request");
    }


    private void publish(final String message) {
        redisTemplate.convertAndSend(topic, message);
    }

    @SneakyThrows
    public Future<String> sign() {
        SignatureRequest request = new SignatureRequest(generator.createUUID(), generator.createData());


        payloadSave(request.getId(), request.getData());
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        map.put(request.getId(), completableFuture);
        publish(request.getId());
        return completableFuture;
    }

    private void payloadSave(String UUID, String payload) {
        bho.put(UUID, payload);
    }


    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] bytes) {
        StringReader reader = new StringReader(new String(message.getBody()));
        SignatureResponse response = mapper.readValue(reader, SignatureResponse.class);
        String data = (String) bho.get(response.getId());
        if (map.containsKey(response.getId())) {
            if (validation.validation(data, response.getSignature())) {
                map.get(response.getId()).complete("Key is correct");
            } else {
                map.get(response.getId())
                        .completeExceptionally(new ResponseException("Data is corrupted or compromised"));
            }

        } else {
            throw new ResponseException("id не найден");
        }
    }
}
