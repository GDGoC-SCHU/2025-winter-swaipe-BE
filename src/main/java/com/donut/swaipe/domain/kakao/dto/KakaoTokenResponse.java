package com.donut.swaipe.domain.kakao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokenResponse {

	private String access_token;
	private String token_type;
	private String refresh_token;
	private Long expires_in;
	private Long refresh_token_expires_in;
}