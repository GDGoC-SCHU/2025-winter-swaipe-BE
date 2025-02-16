package com.donut.swaipe.domain.kakao.controller;

import com.donut.swaipe.domain.kakao.dto.AuthResponseDto;
import com.donut.swaipe.domain.kakao.entity.KakaoUser;
import com.donut.swaipe.domain.kakao.service.KakaoAuthService;
import com.donut.swaipe.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

	private final KakaoAuthService kakaoAuthService;

	/**
	 * 카카오 로그인 콜백을 처리합니다.
	 *
	 * @param code 카카오 인증 코드
	 * @return 인증 결과 및 토큰 정보
	 */
	@GetMapping("/callback")
	public ResponseEntity<ApiResponse<AuthResponseDto>> kakaoCallback(@RequestParam String code) {
		log.info("카카오 인증 코드 수신: {}", code);
		return ResponseEntity.ok(kakaoAuthService.authenticateUser(code));
	}

	/**
	 * 카카오 토큰을 갱신합니다.
	 *
	 * @param kakaoUser 인증된 카카오 사용자
	 * @return 갱신된 토큰 정보
	 */
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthResponseDto>> refreshToken(
			@AuthenticationPrincipal KakaoUser kakaoUser) {
		return ResponseEntity.ok(kakaoAuthService.refreshUserToken(kakaoUser));
	}
}
