package com.donut.swaipe.domain.stdCard.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailVerificationResponse {
	private final String email;
	private final String domain;
	private final VerificationStatus status;

	public static EmailVerificationResponse success(String email, String domain) {
		return new EmailVerificationResponse(email, domain, VerificationStatus.VERIFIED);
	}

	public static EmailVerificationResponse awaitingVerification(String email, String domain) {
		return new EmailVerificationResponse(email, domain, VerificationStatus.AWAITING_VERIFICATION);
	}

	public enum VerificationStatus {
		VERIFIED,           // 인증 완료
		AWAITING_VERIFICATION  // 인증번호 입력 대기 중
	}
}
