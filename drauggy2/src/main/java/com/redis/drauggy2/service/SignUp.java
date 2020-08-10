package com.redis.drauggy2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUp {
    @Value("${algorithm}")
    private String algorithm;

    String makeSignature(String object) throws JSONException {

/*        paired keys (public&private) generation
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(ecSpec, new SecureRandom());
        KeyPair keypair = g.generateKeyPair();
        PublicKey publicKey = keypair.getPublic();
        PrivateKey privateKey = keypair.getPrivate();
       String bytes = Base64.getEncoder().encodeToString(privateKey.getEncoded()) ;
        System.out.println(bytes);
        bytes = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println(bytes);
*/

        try {
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("priv.key")).getFile());
            String pKey;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))

            ) {
                pKey = reader.readLine();
            }
            byte[] privateKey = Base64Utils.decodeFromString(pKey);
            PKCS8EncodedKeySpec formatted_private = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory kf = KeyFactory.getInstance("EC");
            PrivateKey pk = kf.generatePrivate(formatted_private);

            //...... sign
            Signature ecdsaSign = Signature.getInstance(algorithm);
            ecdsaSign.initSign(pk);
            //

            ecdsaSign.update(object.getBytes("UTF-8"));

            byte[] signature = ecdsaSign.sign();


            return Base64Utils.encodeToString(signature);
        } catch (Exception e) {
            log.error("Ошибка: {}", e.getMessage(), e.getCause());
            throw new RuntimeException("Ошибка");
        }

    }
}
