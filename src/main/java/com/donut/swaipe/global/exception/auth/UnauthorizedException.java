package com.donut.swaipe.global.exception.auth;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;


public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super(MessageCode.AUTHENTICATION_FAILED);
    }
}