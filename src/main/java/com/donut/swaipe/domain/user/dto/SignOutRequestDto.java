package com.donut.swaipe.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignOutRequestDto {
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}