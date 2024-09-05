package com.sparta.internsecurity.user.controller;


import com.sparta.internsecurity.Auth.service.AuthService;
import com.sparta.internsecurity.user.dto.AccessTokenDto;
import com.sparta.internsecurity.user.dto.LoginRequestDto;
import com.sparta.internsecurity.user.dto.SignupResponseDto;
import com.sparta.internsecurity.user.dto.SignupUserDto;
import com.sparta.internsecurity.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Response Estimate", description = "Response Estimate API")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "signup", description = "sign up api")
    @Parameters({
            @Parameter(name = "username", description = "사용자 ID는 최소 8글자 이상, 최대 20글자 이하", example = "jakelee0808"),
            @Parameter(name = "password", description = "대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함, 비밀번호는 최소 10글자 이상", example = "rejw32904!%*#AB"),
            @Parameter(name = "nickname", description = "별명은 한글로만 최대 10글자까지 입력", example = "jakeLee")
    })
    public SignupResponseDto signup(
            @RequestBody SignupUserDto userDto
    ) {
        return userService.signup(userDto);
    }

    @PostMapping("/login")
    @Operation(summary = "login", description = "login api")
    public ResponseEntity<AccessTokenDto> login(
            @RequestBody LoginRequestDto requestDto, HttpServletResponse res
    ){
        return ResponseEntity.ok().body(authService.login(requestDto, res));
    }
}
