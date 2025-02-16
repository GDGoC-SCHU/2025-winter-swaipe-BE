package com.donut.swaipe.domain.mms.controller;

import com.donut.swaipe.domain.kakao.entity.KakaoUser;
import com.donut.swaipe.domain.mms.service.SmsService;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsAuthController {
    private final SmsService smsService;

    /**
     * SMS 인증번호를 발송합니다.
     * 개발 환경에서는 로그로 인증번호를 출력합니다.
     */
    @PostMapping("/send")
    public ApiResponse<Void> sendSms(
            @RequestParam String phoneNumber,
            @AuthenticationPrincipal KakaoUser kakaoUser
    ) {
        if (kakaoUser == null) {
            throw new CustomException(MessageCode.UNAUTHORIZED_ACCESS);
        }
        smsService.sendVerificationSms(phoneNumber, kakaoUser.getKakaoId());
        return ApiResponse.success(MessageCode.SMS_SENT);
    }

    /**
     * SMS 인증번호를 검증합니다.
     */
    @PostMapping("/verify")
    public ApiResponse<Boolean> verifySms(
            @RequestParam String phoneNumber,
            @RequestParam String code,
            @AuthenticationPrincipal KakaoUser kakaoUser
    ) {
        boolean isValid = smsService.verifyCode(phoneNumber, code, kakaoUser.getKakaoId());
        return ApiResponse.success(
            isValid ? MessageCode.SMS_VERIFIED : MessageCode.SMS_VERIFICATION_FAILED,
            isValid
        );
    }
} 