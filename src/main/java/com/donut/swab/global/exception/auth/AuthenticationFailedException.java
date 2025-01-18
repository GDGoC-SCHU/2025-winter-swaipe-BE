package com.donut.swab.global.exception.auth;

import lombok.Getter;

@Getter
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
