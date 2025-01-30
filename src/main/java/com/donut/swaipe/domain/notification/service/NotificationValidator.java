package com.donut.swaipe.domain.notification.service;

import com.donut.swaipe.domain.notification.enums.NotificationType;
import com.donut.swaipe.domain.user.entity.User;
import com.donut.swaipe.domain.user.repository.UserRepository;
import com.donut.swaipe.global.exception.noti.InvalidNotificationRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 알림 관련 유효성 검사를 수행하는 Validator 클래스입니다.
 *
 * @author donut
 * @version 1.0
 * @since 2024-01-28
 */
@Component
@RequiredArgsConstructor
public class NotificationValidator {

	private final UserRepository userRepository;

	/**
	 * FCM 토큰의 유효성을 검사합니다.
	 *
	 * @param fcmToken 검사할 FCM 토큰
	 * @throws InvalidNotificationRequestException 토큰이 유효하지 않은 경우
	 */
	public void validateFcmToken(String fcmToken) {
		if (fcmToken == null || fcmToken.isBlank()) {
			throw new InvalidNotificationRequestException();
		}
	}

	/**
	 * 알림 제목의 유효성을 검사합니다.
	 *
	 * @param title 검사할 알림 제목
	 * @throws InvalidNotificationRequestException 제목이 유효하지 않은 경우
	 */
	public void validateTitle(String title) {
		if (title == null || title.isBlank()) {
			throw new InvalidNotificationRequestException();
		}
		if (title.length() > 50) {
			throw new InvalidNotificationRequestException();
		}
	}

	/**
	 * 알림 내용의 유효성을 검사합니다.
	 *
	 * @param message 검사할 알림 내용
	 * @throws InvalidNotificationRequestException 내용이 유효하지 않은 경우
	 */
	public void validateMessage(String message) {
		if (message == null || message.isBlank()) {
			throw new InvalidNotificationRequestException();
		}
		if (message.length() > 500) {
			throw new InvalidNotificationRequestException();
		}
	}

	/**
	 * 사용자의 알림 수신 설정을 검사합니다.
	 *
	 * @param user 검사할 사용자
	 * @throws InvalidNotificationRequestException 알림 수신이 비활성화된 경우
	 */
	public void validateNotificationEnabled(User user, NotificationType notificationType) {
		if (!user.isNotificationEnabled()) {
			throw new InvalidNotificationRequestException();
		}

		if (!user.isNotificationTypeEnabled(notificationType)) {
			throw new InvalidNotificationRequestException();
		}
	}
}