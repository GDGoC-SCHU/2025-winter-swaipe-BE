package com.donut.swab.global.exception.auth;

import com.donut.swab.global.exception.CustomException;


public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super(message);
    }
}