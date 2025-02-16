package com.donut.swaipe.domain.stdCard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationCodeRequest {
    private String email;
    private String code;
    private String univName;
} 