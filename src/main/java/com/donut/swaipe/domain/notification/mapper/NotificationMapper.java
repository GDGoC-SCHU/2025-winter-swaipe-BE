package com.donut.swaipe.domain.notification.mapper;

import com.donut.swaipe.domain.notification.dto.NotificationHistoryResponse;
import com.donut.swaipe.domain.notification.dto.NotificationRequest;
import com.donut.swaipe.domain.notification.dto.NotificationResponse;
import com.donut.swaipe.domain.notification.entity.Notification;
import com.donut.swaipe.domain.notification.entity.NotificationData;
import com.donut.swaipe.domain.user.entity.User;
import com.google.firebase.messaging.Message;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 알림 관련 객체들의 변환을 담당하는 매퍼 클래스입니다.
 *
 * @author donut
 * @version 1.0
 * @since 2024-01-28
 */
@Component
public class NotificationMapper {

	public Notification toEntity(NotificationRequest request, User user, boolean success) {
		Notification notification = Notification.builder()
				.user(user)
				.title(request.getTitle())
				.message(request.getMessage())
				.type(request.getType())
				.success(success)
				.build();

		if (request.getData() != null) {
			request.getData().forEach(notification::addNotificationData);
		}

		return notification;
	}

	public NotificationResponse toResponse(Notification notification) {
		return new NotificationResponse(
				notification.getId(),
				notification.getCreatedAt()
		);
	}

	public NotificationHistoryResponse toHistoryResponse(Notification notification) {
		Map<String, String> dataMap = notification.getNotificationData().stream()
				.collect(Collectors.toMap(
						NotificationData::getDataKey,
						NotificationData::getDataValue
				));

		return new NotificationHistoryResponse(
				notification.getId(),
				notification.getTitle(),
				notification.getMessage(),
				notification.getType(),
				notification.getCreatedAt(),
				notification.is_read(),
				dataMap
		);
	}

	public Message toFcmMessage(NotificationRequest request, String fcmToken) {
		return Message.builder()
				.setToken(fcmToken)
				.setNotification(com.google.firebase.messaging.Notification.builder()
						.setTitle(request.getTitle())
						.setBody(request.getMessage())
						.build())
				.putAllData(request.getData())
				.build();
	}
}
