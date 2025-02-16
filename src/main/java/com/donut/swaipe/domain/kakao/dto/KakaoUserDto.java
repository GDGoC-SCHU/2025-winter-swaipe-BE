package com.donut.swaipe.domain.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaoUserDto {

	private final String kakaoId;
	private final String email;
	private final String nickname;
}