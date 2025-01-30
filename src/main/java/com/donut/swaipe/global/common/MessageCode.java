package com.donut.swaipe.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageCode {
	// 공통
	SUCCESS("200", "성공적으로 처리되었습니다."),
	FAILED("400", "처리에 실패했습니다."),

	// 결제
	PAYMENT_SUCCESS("P001", "결제가 완료되었습니다."),
	PAYMENT_FAILED("P002", "결제에 실패했습니다."),
	PAYMENT_CANCELLED("P003", "결제가 취소되었습니다."),

	// User
	USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
	SIGNUP_FAILED("U006", "회원가입에 실패 했습니다."),
	DUPLICATE_USERNAME("U002", "이미 사용 중인 아이디입니다."),
	DUPLICATE_NICKNAME("U003", "이미 사용 중인 닉네임입니다."),
	LOGOUT_FAILED("U004", "로그아웃 처리에 실패했습니다."),
	SIGNOUT_FAILED("U005", "회원탈퇴 처리에 실패했습니다."),
	INSUFFICIENT_BALANCE("U007", "잔액이 부족합니다."),
	SIGNUP_SUCCESS("U008", "회원가입이 완료되었습니다."),
	USER_UPDATE_SUCCESS("U009", "사용자 정보가 수정되었습니다."),
	USER_LOGOUT("U010", "로그아웃된 사용자 입니다."),
	USER_LOGIN("U011", "로그인을 완료했습니다."),
	LOGOUT_SUCCESS("U012", "로그아웃을 완료했습니다."),
	SIGNOUT_SUCCESS("U013", "회원탈퇴에 성공했습니다."),

	// Auth
	UNAUTHORIZED_ACCESS("A001", "권한이 없습니다."),
	AUTHENTICATION_FAILED("A002", "인증에 실패했습니다."),
	INVALID_TOKEN("A003", "유효하지 않은 토큰입니다.\n다시한번 토큰을 확인해주세요."),
	GENERATE_TOKEN("A004", "토큰 발급 완료했습니다."),
	AUTHORIZED_ERROR("A005", "인가에 실패했습니다."),
	REGENERATE_TOKEN("A006", "토큰 재발급을 완료했습니다."),

	// Validation
	INVALID_INPUT("V001", "입력값이 올바르지 않습니다."),

	// Notification
	NOTIFICATION_NOT_FOUND("N001", "알림을 찾을 수 없습니다."),
	NOTIFICATION_SEND_FAILED("N002", "알림 발송에 실패했습니다."),
	NOTIFICATION_ACCESS_DENIED("N003", "알림에 대한 접근 권한이 없습니다."),
	INVALID_NOTIFICATION_REQUEST("N004", "잘못된 알림 요청입니다."),
	INVALID_FCM_TOKEN("N005", "유효하지 않은 FCM 토큰입니다."),
	NOTIFICATION_SEND_SUCCESS("N006", "알림 전송 완료했습니다."),
	NOTIFICATION_LIST_SUCCESS("N007", "알림 목록 조회 완료했습니다."),
	NOTIFICATION_DETAIL_SUCCESS("N008", "특정 알림 조회 완료했습니다."),
	NOTIFICATION_READ_SUCCESS("N009", "알림을 읽으셨습니다."),
	NOTIFICATION_DELETE_SUCCESS("N010", "알림 삭제 완료했습니다."),
	NOTIFICATION_DISABLED("N011", "알림이 비활성화되어 있습니다."),
	NOTIFICATION_TYPE_DISABLED("N012", "해당 유형의 알림이 비활성화되어 있습니다."),
	NOTIFICATION_NO_TOKEN("N013", "FCM 토큰이 없습니다."),
	NOTIFICATION_SEND_FAIL("N014", "알림 발송에 실패했습니다.");

	private final String code;
	private final String message;
}