package com.donut.swaipe.global.exception.auth;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class AuthenticationFailedException extends CustomException {
    public AuthenticationFailedException() {
        super(MessageCode.DUPLICATE_NICKNAME);
    }
}