package com.donut.swab.global.exception.user;

import com.donut.swab.global.exception.CustomException;
import lombok.Getter;

@Getter
public class SignUpFailedException extends CustomException {
    public SignUpFailedException(String message) {
        super(message);
    }
}