package com.donut.swaipe.domain.notification.service;

import com.donut.swaipe.domain.notification.dto.NotificationHistoryResponse;
import com.donut.swaipe.domain.notification.dto.NotificationRequest;
import com.donut.swaipe.domain.notification.dto.NotificationResponse;
import com.donut.swaipe.domain.notification.entity.Notification;
import com.donut.swaipe.domain.notification.mapper.NotificationMapper;
import com.donut.swaipe.domain.notification.repository.NotificationRepository;
import com.donut.swaipe.domain.user.entity.User;
import com.donut.swaipe.domain.user.repository.UserRepository;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.noti.NotificationAccessDeniedException;
import com.donut.swaipe.global.exception.noti.NotificationFailedException;
import com.donut.swaipe.global.exception.noti.NotificationNotFoundException;
import com.donut.swaipe.global.exception.user.UserNotFoundException;
import com.donut.swaipe.global.security.details.UserDetailsImpl;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author donut
 * @version 1.0
 * @since 2024-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final FirebaseMessaging firebaseMessaging;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final NotificationMapper notificationMapper;
	private final NotificationValidator notificationValidator;

	/**
	 * 새로운 알림을 전송합니다.
	 *
	 * @param request 알림 전송 요청 정보
	 * @return 알림 전송 결과
	 * @throws NotificationFailedException 알림 전송 중 오류가 발생한 경우
	 */
	@Transactional
	public ApiResponse<NotificationResponse> sendNotification(NotificationRequest request) {
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException());

		// FCM 토큰이 요청에 포함되어 있다면 업데이트
		if (request.getFcmToken() != null && !request.getFcmToken().isBlank()) {
			user.updateFcmToken(request.getFcmToken());
		}

		try {
			// 알림 유효성 검증
			notificationValidator.validateTitle(request.getTitle());
			notificationValidator.validateMessage(request.getMessage());

			// 알림 설정 검증
			if (!user.isNotificationEnabled()) {
				log.info("알림이 비활성화된 사용자: userId={}", user.getId());
				return saveNotification(request, user, false, MessageCode.NOTIFICATION_DISABLED);
			}

			if (!user.isNotificationTypeEnabled(request.getType())) {
				log.info("해당 알림 타입이 비활성화된 사용자: userId={}, type={}", user.getId(), request.getType());
				return saveNotification(request, user, false, MessageCode.NOTIFICATION_TYPE_DISABLED);
			}

			// FCM 토큰 검증
			if (user.getFcmToken() == null || user.getFcmToken().isBlank()) {
				log.warn("FCM 토큰이 없는 사용자: userId={}", user.getId());
				return saveNotification(request, user, false, MessageCode.NOTIFICATION_NO_TOKEN);
			}

			// 알림 발송
			Message message = notificationMapper.toFcmMessage(request, user.getFcmToken());
			firebaseMessaging.send(message);

			log.info("알림 발송 완료: userId={}, title={}", user.getId(), request.getTitle());
			return saveNotification(request, user, true, MessageCode.NOTIFICATION_SEND_SUCCESS);

		} catch (Exception e) {
			log.error("알림 발송 실패: {}", e.getMessage());
			return saveNotification(request, user, false, MessageCode.NOTIFICATION_SEND_FAIL);
		}
	}


	/**
	 * 현재 사용자의 알림 목록을 조회합니다.
	 *
	 * @param userDetails 현재 인증된 사용자 정보
	 * @return 사용자의 알림 목록
	 */
	@Transactional(readOnly = true)
	public ApiResponse<List<NotificationHistoryResponse>> getUserNotifications(
			UserDetailsImpl userDetails) {
		User user = userRepository.findById(userDetails.getUser().getId())
				.orElseThrow(() -> new UserNotFoundException());

		List<NotificationHistoryResponse> notifications = notificationRepository
				.findByUserOrderByCreatedAtDesc(user)
				.stream()
				.map(notificationMapper::toHistoryResponse)
				.toList();

		return ApiResponse.success(
				MessageCode.NOTIFICATION_LIST_SUCCESS,
				notifications
		);
	}

	/**
	 * 특정 알림의 상세 정보를 조회합니다.
	 *
	 * @param notificationId 조회할 알림 ID
	 * @param userDetails    현재 인증된 사용자 정보
	 * @return 알림 상세 정보
	 * @throws NotificationNotFoundException     알림을 찾을 수 없는 경우
	 * @throws NotificationAccessDeniedException 알림에 대한 접근 권한이 없는 경우
	 */
	@Transactional(readOnly = true)
	public ApiResponse<NotificationHistoryResponse> getNotification(Long notificationId,
			UserDetailsImpl userDetails) {
		Notification notification = findNotificationById(notificationId);
		validateNotificationAccess(notification, userDetails);

		return ApiResponse.success(
				MessageCode.NOTIFICATION_DETAIL_SUCCESS,
				notificationMapper.toHistoryResponse(notification)
		);
	}

	/**
	 * 알림을 읽음 처리합니다.
	 *
	 * @param notificationId 읽음 처리할 알림 ID
	 * @param userDetails    현재 인증된 사용자 정보
	 * @throws NotificationNotFoundException     알림을 찾을 수 없는 경우
	 * @throws NotificationAccessDeniedException 알림에 대한 접근 권한이 없는 경우
	 */
	@Transactional
	public ApiResponse<Boolean> markAsRead(Long notificationId, UserDetailsImpl userDetails) {
		Notification notification = findNotificationById(notificationId);
		validateNotificationAccess(notification, userDetails);

		notification.markAsRead();
		log.info("알림 읽음 처리 완료: notificationId={}, userId={}", notificationId,
				userDetails.getUser().getId());

		return ApiResponse.success(MessageCode.NOTIFICATION_READ_SUCCESS, true);
	}

	/**
	 * 알림을 삭제합니다.
	 *
	 * @param notificationId 삭제할 알림 ID
	 * @param userDetails    현재 인증된 사용자 정보
	 * @throws NotificationNotFoundException     알림을 찾을 수 없는 경우
	 * @throws NotificationAccessDeniedException 알림에 대한 접근 권한이 없는 경우
	 */
	@Transactional
	public ApiResponse<Boolean> deleteNotification(Long notificationId,
			UserDetailsImpl userDetails) {
		Notification notification = findNotificationById(notificationId);
		validateNotificationAccess(notification, userDetails);

		notificationRepository.delete(notification);
		log.info("알림 삭제 완료: notificationId={}, userId={}", notificationId,
				userDetails.getUser().getId());

		return ApiResponse.success(MessageCode.NOTIFICATION_DELETE_SUCCESS, true);
	}

	/**
	 * ID로 알림을 조회합니다.
	 */
	private Notification findNotificationById(Long notificationId) {
		return notificationRepository.findById(notificationId)
				.orElseThrow(() -> new NotificationNotFoundException());
	}

	/**
	 * 사용자의 알림 접근 권한을 검증합니다.
	 */
	private void validateNotificationAccess(Notification notification,
			UserDetailsImpl userDetails) {
		if (!notification.getUser().getId().equals(userDetails.getUser().getId())) {
			throw new NotificationAccessDeniedException();
		}
	}

	private ApiResponse<NotificationResponse> saveNotification(
			NotificationRequest request,
			User user,
			boolean success,
			MessageCode messageCode) {
		Notification notification = notificationMapper.toEntity(request, user, success);
		notificationRepository.save(notification);
		return ApiResponse.success(messageCode, notificationMapper.toResponse(notification));
	}
}