package com.redis.drauggy.service;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

@Service
@RequiredArgsConstructor
public class DataGenerator {
    @Value("${service.data-generator.size}")
    private Integer dataSize;


    String createData() {
        byte[] bytes = new byte[(dataSize)];
        ThreadLocalRandom.current().nextBytes(bytes);
        return Base64Utils.encodeToString(bytes);
    }

    String createUUID() {

        return java.util.UUID.randomUUID().toString();

    }

}
