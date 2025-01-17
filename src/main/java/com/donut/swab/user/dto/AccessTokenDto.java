package com.donut.swab.user.dto;

import static com.donut.swab.security.jwt.JwtProvider.BEARER_PREFIX;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenDto {
    private String token;

    public AccessTokenDto(String token) {
        this.token = token.substring(BEARER_PREFIX.length());
    }
}
