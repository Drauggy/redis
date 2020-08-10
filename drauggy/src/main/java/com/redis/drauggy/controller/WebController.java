package com.redis.drauggy.controller;

import com.redis.drauggy.service.SuperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

@RestController
@RequiredArgsConstructor


public class WebController {
    private final SuperService superService;


    @GetMapping("/send")
    public Future<String> createAndSendRandomByteMassive() {

        return superService.sign();
    }


}
