package com.donut.swaipe.domain.notification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "notification_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_id")
	private Notification notification;

	private String dataKey;
	private String dataValue;

	@Builder
	public NotificationData(Notification notification, String dataKey, String dataValue) {
		this.notification = notification;
		this.dataKey = dataKey;
		this.dataValue = dataValue;
	}
}