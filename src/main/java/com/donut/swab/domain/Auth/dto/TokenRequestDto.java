package com.donut.swab.domain.Auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDto {
    @NotBlank(message = "Token is required")
    private String token;
}