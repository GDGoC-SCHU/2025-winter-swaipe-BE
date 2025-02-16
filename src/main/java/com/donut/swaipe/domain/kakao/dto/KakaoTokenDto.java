package com.donut.swaipe.domain.kakao.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoTokenDto {
	private String accessToken;
	private String refreshToken;
	private Long expiresIn;
	private Long refreshTokenExpiresIn;

	public static KakaoTokenDto from(KakaoTokenResponse response) {
		return KakaoTokenDto.builder()
			.accessToken(response.getAccess_token())
			.refreshToken(response.getRefresh_token())
			.expiresIn(response.getExpires_in())
			.refreshTokenExpiresIn(response.getRefresh_token_expires_in())
			.build();
	}
}