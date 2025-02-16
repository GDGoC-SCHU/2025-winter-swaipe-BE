package com.donut.swaipe.domain.mms.service;

import com.donut.swaipe.domain.mms.redis.SmsRedisService;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

	private final SmsRedisService redisService;
	private static final String SMS_PREFIX = "SMS:";
	private static final String USER_SMS_PREFIX = "USER_SMS:";
	private static final long EXPIRE_TIME = 120L;

	@Value("${twilio.account-sid}")
	private String ACCOUNT_SID;

	@Value("${twilio.auth-token}")
	private String AUTH_TOKEN;

	@Value("${twilio.phone-number}")
	private String FROM_NUMBER;

	@Value("${spring.profiles.active:local}")  // 기본값은 local
	private String activeProfile;

	public void sendVerificationSms(String phoneNumber, String userId) {
		try {
			if (redisService.hasKey(USER_SMS_PREFIX + userId)) {
				throw new CustomException(MessageCode.SMS_VERIFICATION_IN_PROGRESS);
			}

			String verificationCode = generateVerificationCode();

			if ("local".equals(activeProfile) || "test".equals(activeProfile)) {
				// 개발/테스트 환경에서는 로그로만 출력
				log.info("=== Development/Test Environment SMS ===");
				log.info("Phone Number: {}", phoneNumber);
				log.info("Verification Code: {}", verificationCode);
				log.info("=====================================");
			} else {
				// 운영 환경에서는 실제 SMS 발송
				Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
				String formattedPhoneNumber = "+82" + phoneNumber.substring(1);
				Message message = Message.creator(
						new PhoneNumber(formattedPhoneNumber),
						new PhoneNumber(FROM_NUMBER),
						"인증번호: " + verificationCode + "\n2분 내에 입력해주세요.")
					.create();
				log.info("SMS sent successfully. SID: {}, Status: {}", message.getSid(), message.getStatus());
			}

			// Redis에 인증번호 저장
			redisService.setDataExpire(SMS_PREFIX + phoneNumber, verificationCode, EXPIRE_TIME);
			redisService.setDataExpire(USER_SMS_PREFIX + userId, phoneNumber, EXPIRE_TIME);

		} catch (Exception e) {
			log.error("Failed to send SMS for user {}: {}", userId, e.getMessage());
			throw new CustomException(MessageCode.SMS_SEND_FAILED);
		}
	}

	public boolean verifyCode(String phoneNumber, String code, String userId) {
		// 사용자의 인증 진행 여부 확인
		String userPhone = redisService.getData(USER_SMS_PREFIX + userId);
		if (userPhone == null || !userPhone.equals(phoneNumber)) {
			throw new CustomException(MessageCode.SMS_VERIFICATION_NOT_FOUND);
		}

		String savedCode = redisService.getData(SMS_PREFIX + phoneNumber);
		if (savedCode == null) {
			throw new CustomException(MessageCode.SMS_CODE_EXPIRED);
		}

		boolean isValid = savedCode.equals(code);
		if (isValid) {
			redisService.deleteData(SMS_PREFIX + phoneNumber);
			redisService.deleteData(USER_SMS_PREFIX + userId);
		}

		return isValid;
	}

	private String generateVerificationCode() {
		Random random = new Random();
		return String.format("%06d", random.nextInt(1000000));
	}
} 