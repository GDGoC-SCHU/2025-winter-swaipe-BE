package com.donut.swaipe.global.exception.auth;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class InvalidTokenException extends CustomException {
    public InvalidTokenException() {
        super(MessageCode.INVALID_TOKEN);
    }
}
