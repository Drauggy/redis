package com.redis.drauggy.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

@Service
@Slf4j
@NoArgsConstructor
public class SignValidation {
    @Value("${algorithm}")
    private String algorithm;


    boolean validation(String dataBody, String signature) {
        try {

            Signature ecdsaVerify = Signature.getInstance(algorithm);

            File file = new File(Objects.requireNonNull(getClass()
                    .getClassLoader().getResource("pub.key")).getFile());
            String pubKey;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
            ) {
                pubKey = reader.readLine();
            }
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64Utils.decodeFromString(pubKey));

            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update((dataBody.getBytes("UTF-8")));

            return ecdsaVerify.verify(Base64Utils.decodeFromString(signature));
        } catch (Exception e) {
            log.error("Message:  {} \n", e.getMessage(), e.getCause());
            throw new RuntimeException("Ошибка, ключ не прошел проверку");
        }
    }
}
