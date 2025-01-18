package com.donut.swab.global.exception.user;

import com.donut.swab.global.exception.CustomException;
import lombok.Getter;

@Getter
public class LogoutFailedException extends CustomException {
    public LogoutFailedException(String message) {
        super(message);
    }
}
