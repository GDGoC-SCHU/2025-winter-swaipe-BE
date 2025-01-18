package com.donut.swab.global.exception.user;

import com.donut.swab.global.exception.CustomException;
import lombok.Getter;

@Getter
public class SignOutFailedException extends CustomException {
    public SignOutFailedException(String message) {
        super(message);
    }
}