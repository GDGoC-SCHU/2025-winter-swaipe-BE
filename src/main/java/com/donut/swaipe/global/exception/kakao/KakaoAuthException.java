package com.donut.swaipe.global.exception.kakao;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class KakaoAuthException extends CustomException {
    public KakaoAuthException() {
        super(MessageCode.KAKAO_AUTH_FAILED);
    }
}