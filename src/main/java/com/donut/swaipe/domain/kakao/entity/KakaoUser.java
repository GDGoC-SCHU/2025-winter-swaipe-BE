package com.donut.swaipe.domain.kakao.entity;

import com.donut.swaipe.global.common.TimeStamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kakao_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUser extends TimeStamp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String kakaoId;

	@Column
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Builder
	private KakaoUser(String kakaoId, String email, String nickname) {
		this.kakaoId = kakaoId;
		this.email = email;
		this.nickname = nickname;
	}

	public static KakaoUser createUser(String kakaoId, String email, String nickname) {
		return KakaoUser.builder()
				.kakaoId(kakaoId)
				.email(email)
				.nickname(nickname)
				.build();
	}

	public void updateUserInfo(String nickname) {
		this.nickname = nickname;
	}
}