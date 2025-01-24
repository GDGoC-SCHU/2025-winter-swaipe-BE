package com.donut.swaipe.global.exception.user;

import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotFoundException extends CustomException {
    public UserNotFoundException(String message) {
        super(message);
    }
}