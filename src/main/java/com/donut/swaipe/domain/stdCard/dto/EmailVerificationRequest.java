package com.donut.swaipe.domain.stdCard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationRequest {

	@NotBlank(message = "이메일은 필수입니다")
	@Email(message = "올바른 이메일 형식이 아닙니다")
	@Pattern(regexp = "^[0-9]{8,10}@.*$", message = "이메일 아이디는 8-10자리의 숫자여야 합니다")
	private String email;

	@Builder
	public EmailVerificationRequest(String email) {
		this.email = email;
	}

	public String getStudentId() {
		return email.substring(0, email.indexOf("@"));
	}
}
