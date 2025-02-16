package com.donut.swaipe.domain.stdCard.controller;

import com.donut.swaipe.global.common.MessageCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.donut.swaipe.domain.stdCard.dto.EmailVerificationRequest;
import com.donut.swaipe.domain.stdCard.dto.EmailVerificationResponse;
import com.donut.swaipe.domain.stdCard.service.UniversityVerificationService;
import com.donut.swaipe.global.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class VerificationController {

	private final UniversityVerificationService verificationService;

	@PostMapping("/university-email")
	public ApiResponse<EmailVerificationResponse> verifyUniversityEmail(
			@RequestBody @Valid EmailVerificationRequest request) {
		EmailVerificationResponse response = verificationService.verifyUniversityEmail(request);
		return ApiResponse.success(MessageCode.UNIVERSITY_EMAIL_VERIFIED, response);
	}
}