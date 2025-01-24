package com.donut.swaipe.global.exception.user;

import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class LogoutFailedException extends CustomException {
    public LogoutFailedException(String message) {
        super(message);
    }
}
