package com.donut.swaipe.domain.mms.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsAuthRequestDto {
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$",
            message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String phoneNumber;
} 