package com.donut.swaipe.domain.mms.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsVerifyRequestDto {
    private String phoneNumber;
    
    @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자입니다")
    private String code;
} 