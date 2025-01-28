package com.donut.swaipe.global.exception.user;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class SignOutFailedException extends CustomException {
    public SignOutFailedException() {
        super(MessageCode.SIGNOUT_FAILED);
    }
}