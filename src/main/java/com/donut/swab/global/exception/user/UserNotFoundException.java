package com.donut.swab.global.exception.user;

import com.donut.swab.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotFoundException extends CustomException {
    public UserNotFoundException(String message) {
        super(message);
    }
}