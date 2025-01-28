package com.donut.swaipe.global.exception.user;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class LogoutFailedException extends CustomException {
    public LogoutFailedException() {
        super(MessageCode.LOGOUT_FAILED);
    }
}
