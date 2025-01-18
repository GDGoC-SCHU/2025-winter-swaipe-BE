package com.donut.swab.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRequestDto {
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$",
            message = "닉네임은 2~10자의 한글, 영문, 숫자만 사용 가능합니다.")
    private String nickname;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
            message = "비밀번호는 8~15자의 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password;
}