package com.donut.swaipe.domain.notification.entity;

import com.donut.swaipe.domain.notification.enums.NotificationType;
import com.donut.swaipe.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private String title;
	private String message;
	private boolean success;
	private boolean is_read;

	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NotificationData> notificationData = new ArrayList<>();

	@CreatedDate
	private LocalDateTime createdAt;

	@Builder
	public Notification(User user, String title, String message, NotificationType type, boolean success) {
		this.user = user;
		this.title = title;
		this.message = message;
		this.type = type;
		this.success = success;
		this.is_read = false;
	}

	public void markAsRead() {
		this.is_read = true;
	}

	public void addNotificationData(String key, String value) {
		this.notificationData.add(
				NotificationData.builder()
						.notification(this)
						.dataKey(key)
						.dataValue(value)
						.build()
		);
	}
}