package com.donut.swaipe.domain.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {

	private String accessToken;
	private String refreshToken;
	private String tokenType;
	private Long expiresIn;
	private Long refreshTokenExpiresIn;

	public static AuthResponseDto from(KakaoTokenDto tokenDto) {
		return AuthResponseDto.builder()
			.accessToken(tokenDto.getAccessToken())
			.refreshToken(tokenDto.getRefreshToken())
			.tokenType("Bearer")
			.expiresIn(tokenDto.getExpiresIn())
			.refreshTokenExpiresIn(tokenDto.getRefreshTokenExpiresIn())
			.build();
	}
}