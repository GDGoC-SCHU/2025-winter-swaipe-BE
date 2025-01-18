package com.donut.swab.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponseDto {
    private Long id;
    private String username;
    private String nickname;
}


