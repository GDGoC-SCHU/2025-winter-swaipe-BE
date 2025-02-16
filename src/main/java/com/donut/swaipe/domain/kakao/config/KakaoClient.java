package com.donut.swaipe.domain.kakao.config;

import com.donut.swaipe.domain.kakao.dto.KakaoTokenDto;
import com.donut.swaipe.domain.kakao.dto.KakaoTokenResponse;
import com.donut.swaipe.domain.kakao.dto.KakaoUserInfo;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import com.donut.swaipe.global.exception.kakao.KakaoApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoClient {

	private final WebClient webClient;

	@Value("${kakao.client.id}")
	private String clientId;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	public KakaoTokenDto getToken(String code) {
		log.info("Requesting Kakao token with code: {}", code);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		log.debug("Token request params: client_id={}, redirect_uri={}",
				clientId, redirectUri);

		return webClient.post()
				.uri("https://kauth.kakao.com/oauth/token")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(params))
				.retrieve()
				.bodyToMono(KakaoTokenResponse.class)
				.map(this::convertToTokenDto)
				.block();
	}

	public KakaoTokenDto refreshToken(String refreshToken) {
		log.info("Refreshing Kakao token");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", clientId);
		params.add("refresh_token", refreshToken);

		return webClient.post()
				.uri("https://kauth.kakao.com/oauth/token")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(params))
				.retrieve()
				.bodyToMono(KakaoTokenResponse.class)
				.map(this::convertToTokenDto)
				.block();
	}

	public KakaoUserInfo getUserInfo(String accessToken) {
		log.info("Requesting Kakao user info");

		return webClient.get()
				.uri("https://kapi.kakao.com/v2/user/me")
				.header("Authorization", "Bearer " + accessToken)
				.retrieve()
				.bodyToMono(KakaoUserInfo.class)
				.block();
	}

	private KakaoTokenDto convertToTokenDto(KakaoTokenResponse response) {
		return KakaoTokenDto.builder()
				.accessToken(response.getAccess_token())
				.refreshToken(response.getRefresh_token())
				.expiresIn(response.getExpires_in())
				.refreshTokenExpiresIn(response.getRefresh_token_expires_in())
				.build();
	}
}