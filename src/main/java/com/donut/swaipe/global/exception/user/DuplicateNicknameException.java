package com.donut.swaipe.global.exception.user;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class DuplicateNicknameException extends CustomException {
    public DuplicateNicknameException() {
        super(MessageCode.DUPLICATE_NICKNAME);
    }
}
