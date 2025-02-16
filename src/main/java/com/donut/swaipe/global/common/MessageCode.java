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
	SIGNIN_SUCCESS("U000", "로그인을 완료했습니다." ),
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
	SIGNIN_FAILED("U014", "로그인에 실패했습니다." ),

	// Auth
	UNAUTHORIZED_ACCESS("A001", "권한이 없습니다."),
	AUTHENTICATION_FAILED("A002", "인증에 실패했습니다."),
	INVALID_TOKEN("A003", "유효하지 않은 토큰입니다.\n다시한번 토큰을 확인해주세요."),
	GENERATE_TOKEN("A004", "토큰 발급 완료했습니다."),
	AUTHORIZED_ERROR("A005", "인가에 실패했습니다."),
	REGENERATE_TOKEN("A006", "토큰 재발급을 완료했습니다."),

	// Validation
	INVALID_INPUT("V001", "입력값이 올바르지 않습니다."),

	// Kakao Auth
	KAKAO_API_ERROR("K001", "카카오 API 호출 중 오류가 발생했습니다."),
	KAKAO_TOKEN_NOT_FOUND("K002", "카카오 토큰을 찾을 수 없습니다."),
	KAKAO_TOKEN_EXPIRED("K003", "카카오 토큰이 만료되었습니다."),
	KAKAO_AUTH_FAILED("K004", "카카오 인증에 실패했습니다."),
	KAKAO_USER_INFO_FAILED("K005", "카카오 사용자 정보 조회에 실패했습니다."),
	KAKAO_TOKEN_REFRESH_FAILED("K006", "카카오 토큰 갱신에 실패했습니다."),

	// Resource
	RESOURCE_NOT_FOUND("R001", "요청한 리소스를 찾을 수 없습니다."),

	// SMS 관련
	SMS_SENT("S001", "인증번호가 발송되었습니다."),
	SMS_VERIFIED("S002", "인증이 완료되었습니다."),
	SMS_SEND_FAILED("S003", "인증번호 발송에 실패했습니다."),
	SMS_CODE_EXPIRED("S004", "인증번호가 만료되었습니다."),
	SMS_VERIFICATION_FAILED("S005", "잘못된 인증번호입니다."),
	SMS_VERIFICATION_IN_PROGRESS("S006", "이미 진행 중인 인증이 있습니다."),
	SMS_VERIFICATION_NOT_FOUND("S007", "진행 중인 인증을 찾을 수 없습니다."),

	// 학생증 관련
	STUDENT_CARD_SCAN_SUCCESS("SC001", "학생증 스캔이 완료되었습니다."),
	STUDENT_CARD_SCAN_FAILED("SC002", "학생증 스캔에 실패했습니다."),
	STUDENT_CARD_NOT_FOUND("SC003", "학생증을 찾을 수 없습니다."),
	STUDENT_CARD_INVALID("SC004", "유효하지 않은 학생증입니다."),
	BARCODE_READ_FAILED("SC005", "바코드 인식에 실패했습니다."),
	OCR_READ_FAILED("SC006", "텍스트 인식에 실패했습니다."),
	INVALID_UNIVERSITY_EMAIL("SC007", "유효하지 않은 대학교 이메일입니다."),
	UNIVERSITY_EMAIL_VERIFIED("SC008", "대학 인증 완료했습니다.");

	private final String code;
	private final String message;
}