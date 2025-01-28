package com.donut.swaipe.global.exception.user;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class SignUpFailedException extends CustomException {
    public SignUpFailedException() {
        super(MessageCode.SIGNUP_FAILED);
    }
}