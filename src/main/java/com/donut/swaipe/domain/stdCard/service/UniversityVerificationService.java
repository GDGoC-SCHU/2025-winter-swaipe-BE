package com.donut.swaipe.domain.stdCard.service;

import com.donut.swaipe.global.exception.student.InvalidUniversityEmailException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.donut.swaipe.domain.stdCard.dto.EmailVerificationRequest;
import com.donut.swaipe.domain.stdCard.dto.EmailVerificationResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UniversityVerificationService {

	@Value("${univCert.api-key}")
	private String apiKey;

	private final WebClient webClient;

	public EmailVerificationResponse verifyUniversityEmail(EmailVerificationRequest request) {
		String email = request.getEmail();
		try {
			String domain = extractDomain(email);

			if (!isDomainValid(domain)) {
				throw new InvalidUniversityEmailException();
			}

			// 이미 인증된 이메일인지 확인
			Map<String, Object> statusResponse = checkEmailStatus(email);
			if (Boolean.TRUE.equals(statusResponse.get("success"))) {
				return EmailVerificationResponse.success(email, domain);
			}

			// 대학교 이름 추출
			String univName = extractUnivName(domain);
			univName = "순천향대학교";
			
			// 인증 메일 발송 요청
			Map<String, Object> certifyResponse = requestEmailVerification(email, univName);
			log.debug("UnivCert API Certify Response: {}", certifyResponse);
			
			if (!Boolean.TRUE.equals(certifyResponse.get("success"))) {
				String errorMessage = (String) certifyResponse.get("message");
				log.error("대학 인증 메일 발송 실패: {}", errorMessage);
				throw new InvalidUniversityEmailException();
			}

			return EmailVerificationResponse.awaitingVerification(email, domain);

		} catch (InvalidUniversityEmailException e) {
			throw e;
		} catch (Exception e) {
			log.error("대학 이메일 검증 중 오류 발생. email={}", email, e);
			throw new InvalidUniversityEmailException();
		}
	}

	private String extractDomain(String email) {
		return email.substring(email.indexOf("@") + 1);
	}

	private boolean isDomainValid(String domain) {
		return domain.endsWith("ac.kr");
	}

	private String extractUnivName(String domain) {
		// 도메인에서 대학교 이름 추출 로직
		// 예: sch.ac.kr -> 순천향대학교
		return domain.split("\\.")[0].toUpperCase() + "대학교";
	}

	private Map<String, Object> checkEmailStatus(String email) {
		return webClient.post()
				.uri("https://univcert.com/api/v1/status")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of(
						"key", apiKey,
						"email", email
				))
				.retrieve()
				.bodyToMono(Map.class)
				.doOnError(error -> log.error("UnivCert API Status 확인 실패: {}", error.getMessage()))
				.onErrorReturn(Map.of("success", false))
				.block();
	}

	private Map<String, Object> requestEmailVerification(String email, String univName) {
		return webClient.post()
				.uri("https://univcert.com/api/v1/certify")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(Map.of(
						"key", apiKey,
						"email", email,
						"univName", univName,
						"univ_check", false
				))
				.retrieve()
				.bodyToMono(Map.class)
				.doOnError(error -> log.error("UnivCert API Certify 요청 실패: {}", error.getMessage()))
				.onErrorReturn(Map.of("success", false, "message", "인증 메일 발송 실패"))
				.block();
	}
}