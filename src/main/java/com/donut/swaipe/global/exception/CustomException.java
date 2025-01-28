package com.donut.swaipe.global.exception;

import com.donut.swaipe.global.common.MessageCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final MessageCode messageCode;

    public CustomException(MessageCode messageCode) {
        super(messageCode.getMessage());
        this.messageCode = messageCode;
    }
}