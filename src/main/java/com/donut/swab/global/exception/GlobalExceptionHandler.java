package com.donut.swab.global.exception;

import com.donut.swab.global.common.ApiResponse;
import com.donut.swab.global.exception.auth.AuthenticationFailedException;
import com.donut.swab.global.exception.auth.InvalidTokenException;
import com.donut.swab.global.exception.auth.UnauthorizedAccessException;
import com.donut.swab.global.exception.user.DuplicateNicknameException;
import com.donut.swab.global.exception.user.DuplicateUsernameException;
import com.donut.swab.global.exception.user.LogoutFailedException;
import com.donut.swab.global.exception.user.SignOutFailedException;
import com.donut.swab.global.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 사용자를 찾을 수 없을 때 발생하는 예외를 처리합니다.
	 *
	 * @return 404 NOT_FOUND
	 */
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(e.getMessage()));
	}

	/**
	 * 중복된 username으로 가입 시도할 때 발생하는 예외를 처리합니다.
	 *
	 * @return 409 CONFLICT
	 */
	@ExceptionHandler(DuplicateUsernameException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateUsernameException(
			DuplicateUsernameException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(e.getMessage()));
	}

	/**
	 * 중복된 nickname으로 가입/수정 시도할 때 발생하는 예외를 처리합니다.
	 *
	 * @return 409 CONFLICT
	 */
	@ExceptionHandler(DuplicateNicknameException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateNicknameException(
			DuplicateNicknameException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(e.getMessage()));
	}

	/**
	 * 권한이 없는 작업 시도 시 발생하는 예외를 처리합니다.
	 *
	 * @return 403 FORBIDDEN
	 */
	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAccessException(
			UnauthorizedAccessException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiResponse.error(e.getMessage()));
	}

	/**
	 * 인증 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @return 401 UNAUTHORIZED
	 */
	@ExceptionHandler(AuthenticationFailedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthenticationFailedException(
			AuthenticationFailedException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(e.getMessage()));
	}

	/**
	 * 유효하지 않은 토큰 사용 시 발생하는 예외를 처리합니다.
	 *
	 * @return 401 UNAUTHORIZED
	 */
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(e.getMessage()));
	}

	/**
	 * 로그아웃 처리 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @return 400 BAD_REQUEST
	 */
	@ExceptionHandler(LogoutFailedException.class)
	public ResponseEntity<ApiResponse<Void>> handleLogoutFailedException(LogoutFailedException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(e.getMessage()));
	}

	/**
	 * 회원탈퇴 처리 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @return 400 BAD_REQUEST
	 */
	@ExceptionHandler(SignOutFailedException.class)
	public ResponseEntity<ApiResponse<Void>> handleSignOutFailedException(
			SignOutFailedException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(e.getMessage()));
	}

	/**
	 * 요청 데이터 검증 실패 시 발생하는 예외를 처리합니다.
	 *
	 * @return 400 BAD_REQUEST
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(
			MethodArgumentNotValidException e) {
		String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(errorMessage));
	}

	/**
	 * 기타 예상하지 못한 예외를 처리합니다.
	 *
	 * @return 500 INTERNAL_SERVER_ERROR
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("Unexpected error occurred: ", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("Internal server error"));
	}
}