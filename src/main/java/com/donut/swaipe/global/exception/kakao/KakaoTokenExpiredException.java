package com.donut.swaipe.global.exception.kakao;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class KakaoTokenExpiredException extends CustomException {
    public KakaoTokenExpiredException() {
        super(MessageCode.KAKAO_TOKEN_EXPIRED);
    }
}