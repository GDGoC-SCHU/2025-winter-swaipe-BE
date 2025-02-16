package com.donut.swaipe.domain.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfo {
	private String id;  // 카카오 회원번호
	
	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;

	@Getter
	@NoArgsConstructor
	public static class KakaoAccount {
		private String email;
		private Profile profile;
		
		@JsonProperty("email_needs_agreement")
		private boolean emailNeedsAgreement;
		
		@JsonProperty("is_email_valid")
		private boolean isEmailValid;
		
		@JsonProperty("is_email_verified")
		private boolean isEmailVerified;
	}

	@Getter
	@NoArgsConstructor
	public static class Profile {
		private String nickname;
		
		@JsonProperty("profile_image_url")
		private String profileImageUrl;
		
		@JsonProperty("thumbnail_image_url")
		private String thumbnailImageUrl;
	}

	public String getEmail() {
		return kakaoAccount != null ? kakaoAccount.getEmail() : null;
	}

	public String getNickname() {
		return kakaoAccount != null && kakaoAccount.getProfile() != null ?
				kakaoAccount.getProfile().getNickname() : null;
	}
}