package com.donut.swaipe.global.exception.kakao;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;


@Getter
public class KakaoTokenNotFoundException extends CustomException {
    public KakaoTokenNotFoundException() {
        super(MessageCode.KAKAO_TOKEN_NOT_FOUND);
    }
}