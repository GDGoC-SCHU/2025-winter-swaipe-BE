package com.sparta.internsecurity.user.controller;


import com.sparta.internsecurity.user.dto.SignupResponseDto;
import com.sparta.internsecurity.user.dto.SignupUserDto;
import com.sparta.internsecurity.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public SignupResponseDto signup(
            @RequestBody SignupUserDto userDto
    ) {
        return userService.signup(userDto);
    }
}
