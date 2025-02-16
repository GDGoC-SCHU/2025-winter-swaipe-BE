package com.donut.swaipe.global.exception;

import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.auth.AuthenticationFailedException;
import com.donut.swaipe.global.exception.auth.InvalidTokenException;
import com.donut.swaipe.global.exception.auth.UnauthorizedAccessException;
import com.donut.swaipe.global.exception.kakao.KakaoApiException;
import com.donut.swaipe.global.exception.kakao.KakaoAuthException;
import com.donut.swaipe.global.exception.kakao.KakaoTokenExpiredException;
import com.donut.swaipe.global.exception.kakao.KakaoTokenNotFoundException;
import com.donut.swaipe.global.exception.user.DuplicateNicknameException;
import com.donut.swaipe.global.exception.user.DuplicateUsernameException;
import com.donut.swaipe.global.exception.user.LogoutFailedException;
import com.donut.swaipe.global.exception.user.SignOutFailedException;
import com.donut.swaipe.global.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import com.donut.swaipe.global.exception.NoResourceFoundException;


@Slf4j
@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {

	//	enum으로 정의된 고정 메시지만 사용
	//	예외 클래스의 존재 자체가 예외 상황을 나타내는 역할
	//	예외의 동적 메시지나 상세 정보는 활용 불가

	/**
	 * 사용자를 찾을 수 없을 때 발생하는 예외를 처리합니다.
	 *
	 * @param e 사용자를 찾을 수 없을 때 발생하는 예외
	 * @return 404 NOT_FOUND 응답
	 */
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(MessageCode.USER_NOT_FOUND));
	}

	/**
	 * 중복된 username으로 가입 시도할 때 발생하는 예외를 처리합니다.
	 *
	 * @param e 중복된 username으로 인한 예외
	 * @return 409 CONFLICT 응답
	 */
	@ExceptionHandler(DuplicateUsernameException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateUsernameException(
			DuplicateUsernameException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(MessageCode.DUPLICATE_USERNAME));
	}

	/**
	 * 중복된 nickname으로 가입/수정 시도할 때 발생하는 예외를 처리합니다.
	 *
	 * @param e 중복된 nickname으로 인한 예외
	 * @return 409 CONFLICT 응답
	 */
	@ExceptionHandler(DuplicateNicknameException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateNicknameException(
			DuplicateNicknameException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(MessageCode.DUPLICATE_NICKNAME));
	}

	/**
	 * 권한이 없는 작업 시도 시 발생하는 예외를 처리합니다.
	 *
	 * @param e 권한 없는 접근으로 인한 예외
	 * @return 403 FORBIDDEN 응답
	 */
	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAccessException(
			UnauthorizedAccessException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiResponse.error(MessageCode.UNAUTHORIZED_ACCESS));
	}

	/**
	 * 인증 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @param e 인증 실패로 인한 예외
	 * @return 401 UNAUTHORIZED 응답
	 */
	@ExceptionHandler(AuthenticationFailedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthenticationFailedException(
			AuthenticationFailedException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(MessageCode.AUTHENTICATION_FAILED));
	}

	/**
	 * 유효하지 않은 토큰 사용 시 발생하는 예외를 처리합니다.
	 *
	 * @param e 유효하지 않은 토큰으로 인한 예외
	 * @return 401 UNAUTHORIZED 응답
	 */
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(MessageCode.INVALID_TOKEN));
	}

	/**
	 * 로그아웃 처리 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @param e 로그아웃 실패로 인한 예외
	 * @return 400 BAD_REQUEST 응답
	 */
	@ExceptionHandler(LogoutFailedException.class)
	public ResponseEntity<ApiResponse<Void>> handleLogoutFailedException(LogoutFailedException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(MessageCode.LOGOUT_FAILED));
	}

	/**
	 * 회원탈퇴 처리 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @param e 회원탈퇴 실패로 인한 예외
	 * @return 400 BAD_REQUEST 응답
	 */
	@ExceptionHandler(SignOutFailedException.class)
	public ResponseEntity<ApiResponse<Void>> handleSignOutFailedException(
			SignOutFailedException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(MessageCode.SIGNOUT_FAILED));
	}

	/**
	 * 요청 데이터 검증 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @param e 입력값 검증 실패로 인한 예외
	 * @return 400 BAD_REQUEST 응답
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(
			MethodArgumentNotValidException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(MessageCode.INVALID_INPUT));
	}

	/**
	 * 기타 예상하지 못한 예외를 처리합니다.
	 *
	 * @param e 예상치 못한 예외
	 * @return 500 INTERNAL_SERVER_ERROR 응답
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("Unexpected error occurred: ", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error(MessageCode.FAILED));
	}

	/**
	 * 카카오 API 호출 중 발생하는 예외를 처리합니다.
	 *
	 * @param e 카카오 API 호출 실패로 인한 예외
	 * @return 400 BAD_REQUEST 응답
	 */
	@ExceptionHandler(KakaoApiException.class)
	public ResponseEntity<ApiResponse<Void>> handleKakaoApiException(KakaoApiException e) {
		log.error("Kakao API error occurred: ", e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(MessageCode.KAKAO_API_ERROR));
	}

	/**
	 * 카카오 토큰을 찾을 수 없을 때 발생하는 예외를 처리합니다.
	 *
	 * @param e 카카오 토큰을 찾을 수 없을 때 발생하는 예외
	 * @return 404 NOT_FOUND 응답
	 */
	@ExceptionHandler(KakaoTokenNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleKakaoTokenNotFoundException(
			KakaoTokenNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(MessageCode.KAKAO_TOKEN_NOT_FOUND));
	}

	/**
	 * 카카오 토큰이 만료되었을 때 발생하는 예외를 처리합니다.
	 *
	 * @param e 카카오 토큰 만료로 인한 예외
	 * @return 401 UNAUTHORIZED 응답
	 */
	@ExceptionHandler(KakaoTokenExpiredException.class)
	public ResponseEntity<ApiResponse<Void>> handleKakaoTokenExpiredException(
			KakaoTokenExpiredException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(MessageCode.KAKAO_TOKEN_EXPIRED));
	}

	/**
	 * 카카오 인증 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @param e 카카오 인증 실패로 인한 예외
	 * @return 401 UNAUTHORIZED 응답
	 */
	@ExceptionHandler(KakaoAuthException.class)
	public ResponseEntity<ApiResponse<Void>> handleKakaoAuthException(KakaoAuthException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(MessageCode.KAKAO_AUTH_FAILED));
	}

	/**
	 * WebClient 응답 처리 중 발생하는 예외를 처리합니다.
	 *
	 * @param e WebClient 응답 처리 실패로 인한 예외
	 * @return 400 BAD_REQUEST 응답
	 */
	@ExceptionHandler(WebClientResponseException.class)
	public ResponseEntity<ApiResponse<Void>> handleWebClientResponseException(
			WebClientResponseException e) {
		log.error("WebClient error occurred: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(MessageCode.KAKAO_API_ERROR));

	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleNoResourceFoundException(NoResourceFoundException ex) {
		// favicon.ico 요청인 경우 204 응답
		if (ex.getMessage().contains("favicon.ico")) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(MessageCode.RESOURCE_NOT_FOUND));
	}
}