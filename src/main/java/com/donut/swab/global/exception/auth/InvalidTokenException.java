package com.donut.swab.global.exception.auth;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
