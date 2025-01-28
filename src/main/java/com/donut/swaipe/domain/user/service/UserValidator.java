package com.donut.swaipe.domain.user.service;

import com.donut.swaipe.domain.user.entity.User;
import com.donut.swaipe.domain.user.enums.UserRole;
import com.donut.swaipe.domain.user.repository.UserRepository;
import com.donut.swaipe.global.exception.auth.UnauthorizedAccessException;
import com.donut.swaipe.global.exception.user.DuplicateNicknameException;
import com.donut.swaipe.global.exception.user.DuplicateUsernameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 사용자 관련 유효성 검사를 처리하는 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

	private final UserRepository userRepository;

	/**
	 * 사용자 이름의 중복 여부를 검사합니다.
	 *
	 * @param username 검사할 사용자 이름
	 * @throws DuplicateUsernameException 중복된 사용자 이름이 존재하는 경우
	 */
	public void validateUsername(String username) {
		if (userRepository.existsByUsername(username)) {
			log.warn("중복된 사용자 이름 발견: {}", username);
			throw new DuplicateUsernameException();
		}
	}

	/**
	 * 닉네임의 중복 여부를 검사합니다.
	 *
	 * @param nickname 검사할 닉네임
	 * @throws DuplicateNicknameException 중복된 닉네임이 존재하는 경우
	 */
	public void validateNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			log.warn("중복된 닉네임 발견: {}", nickname);
			throw new DuplicateNicknameException();
		}
	}

	/**
	 * 관리자 권한을 검증합니다.
	 *
	 * @param admin 검증할 관리자 사용자
	 * @throws UnauthorizedAccessException 관리자 권한이 없는 경우
	 */
	public void validateAdminRole(User admin) {
		if (admin.getUserRole() != UserRole.MANAGER) {
			log.warn("권한 없는 사용자의 권한 변경 시도: {}", admin.getUsername());
			throw new UnauthorizedAccessException();
		}
	}
}