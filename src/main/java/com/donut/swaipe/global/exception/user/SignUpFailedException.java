package com.donut.swaipe.global.exception.user;

import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class SignUpFailedException extends CustomException {
    public SignUpFailedException(String message) {
        super(message);
    }
}