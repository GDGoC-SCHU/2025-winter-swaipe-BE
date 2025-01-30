package com.donut.swaipe.domain.notification.dto;

import com.donut.swaipe.domain.notification.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationHistoryResponse {

	private Long id;
	private String title;
	private String message;
	private NotificationType type;
	private LocalDateTime createdAt;
	private boolean read;
	private Map<String, String> data;
}