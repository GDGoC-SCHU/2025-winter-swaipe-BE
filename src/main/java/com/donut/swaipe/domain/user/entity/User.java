package com.donut.swaipe.domain.user.entity;

import com.donut.swaipe.domain.notification.enums.NotificationType;
import com.donut.swaipe.domain.user.enums.UserRole;
import com.donut.swaipe.global.common.TimeStamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user",
		indexes = {
				@Index(name = "idx_username", columnList = "username")
		})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimeStamp {

	/**
	 * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	private String fcmToken;

	@Column(nullable = false)
	private UserRole userRole = UserRole.USER;

	@Column(nullable = false)
	private boolean notificationEnabled = true;

	@Column(nullable = false)
	private int notificationFlags = DEFAULT_NOTIFICATION_FLAGS;

	private static final int DEFAULT_NOTIFICATION_FLAGS = 0b11111; // 모든 알림 활성화

	/**
	 * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
	 */

	public void updatePassword(String newPassword) {
		this.password = newPassword;
	}

	public void updateNickname(String newNickname) {
		this.nickname = newNickname;
	}



	// 관리자만 호출 가능하도록 서비스 레이어에서 검증
	public void updateRole(UserRole newRole) {
		this.userRole = newRole;
	}

	public void updateFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public void updateNotificationEnabled(boolean enabled) {
		this.notificationEnabled = enabled;
	}

	public boolean isNotificationTypeEnabled(NotificationType type) {
		return (notificationFlags & (1 << type.ordinal())) != 0;
	}

	public void updateNotificationFlag(NotificationType type, boolean enabled) {
		if (enabled) {
			notificationFlags |= (1 << type.ordinal());
		} else {
			notificationFlags &= ~(1 << type.ordinal());
		}
	}


	/**
	 * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
	 */

	@Builder
	public User(String username, String password, String nickname) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
	}
}
