package com.donut.swaipe.domain.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
//	CHAT("채팅"),
	LIKE("좋아요"),
//	COMMENT("댓글"),
	FOLLOW("팔로우"),
	SYSTEM("시스템");

	private final String description;

	NotificationType(String description) {
		this.description = description;
	}
}