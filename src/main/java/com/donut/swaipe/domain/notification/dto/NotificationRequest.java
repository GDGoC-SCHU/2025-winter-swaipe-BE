package com.donut.swaipe.domain.notification.dto;

import com.donut.swaipe.domain.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationRequest {

	@NotNull
	private Long userId;

	@NotBlank(message = "제목은 필수입니다")
	private String title;

	@NotNull(message = "알림 타입은 필수입니다")
	private NotificationType type;

	@NotBlank(message = "메시지는 필수입니다")
	private String message;

	private String fcmToken;

	private Map<String, String> data;
}


