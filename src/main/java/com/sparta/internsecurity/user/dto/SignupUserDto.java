package com.sparta.internsecurity.user.dto;

import com.sparta.internsecurity.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class SignupUserDto {

    @NotBlank(message = "Required Username")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])[a-z0-9]{8,20}$", message = "사용자 ID는 최소 8글자 이상, 최대 20글자 이하여야 합니다.")
    private String username;
    @NotBlank(message = "Required Password")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()]).{10,}$", message = "대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함합니다. \n비밀번호는 최소 10글자 이상이어야 합니다.")
    private String password;
    @NotBlank(message = "Required Name")
    @Pattern(regexp = "^[가-힣]{1,10}$", message = "별명은 한글로만 최대 10글자까지 입력할 수 있습니다.")
    private String nickname;

    public User signup(String password){
        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
