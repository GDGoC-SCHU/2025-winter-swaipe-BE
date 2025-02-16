package com.donut.swaipe.global.exception.kakao;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.Getter;

@Getter
public class KakaoApiException extends CustomException {
    public KakaoApiException() {
        super(MessageCode.KAKAO_API_ERROR);
    }
}