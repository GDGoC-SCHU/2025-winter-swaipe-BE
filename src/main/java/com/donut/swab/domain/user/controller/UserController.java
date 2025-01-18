package com.donut.swab.domain.user.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.donut.swab.domain.Auth.dto.TokenDto;
import com.donut.swab.domain.Auth.dto.TokenRequestDto;
import com.donut.swab.domain.Auth.service.AuthService;
import com.donut.swab.domain.user.dto.SignOutRequestDto;
import com.donut.swab.domain.user.dto.SignUpRequestDto;
import com.donut.swab.domain.user.dto.SignupResponseDto;
import com.donut.swab.domain.user.service.UserService;
import com.donut.swab.global.common.ApiResponse;
import com.donut.swab.global.security.details.UserDetailsImpl;
import com.donut.swab.global.security.jwt.JwtProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Response Estimate", description = "Response Estimate API")
public class UserController {

	private final UserService userService;
	private final AuthService authService;
	private final JwtProvider jwtProvider;

	@PostMapping
	@Operation(summary = "signup", description = "sign up api")
	public ApiResponse<SignupResponseDto> signup(@RequestBody SignUpRequestDto signupRequestDto) {
		return userService.signup(signupRequestDto);
	}

	@PostMapping("/refresh")
	@Operation(summary = "refresh", description = "refresh access token")
	public ApiResponse<TokenDto> refresh(@RequestBody @Valid TokenRequestDto requestDto) {
		return authService.refresh(requestDto);
	}

	@PostMapping("/logout")
	@Operation(summary = "logout", description = "logout api")
	public ApiResponse<Void> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return authService.logout(userDetails);
	}

	@DeleteMapping("/signout")
	@Operation(summary = "signout", description = "sign out api")
	public ApiResponse<Void> signOut(@AuthenticationPrincipal UserDetailsImpl userDetails, 
			@RequestBody SignOutRequestDto requestDto) {
		return authService.signOut(userDetails, requestDto);
	}
}
