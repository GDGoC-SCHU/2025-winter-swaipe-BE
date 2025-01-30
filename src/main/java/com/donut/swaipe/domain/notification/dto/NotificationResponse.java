package com.donut.swaipe.domain.notification.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private LocalDateTime sentAt;
}