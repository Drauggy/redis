package com.drauggy.signature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode

public class SignatureRequest {

    private final String id;
    private final String data;


}

