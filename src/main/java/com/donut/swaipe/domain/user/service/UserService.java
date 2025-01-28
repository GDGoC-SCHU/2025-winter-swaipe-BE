package com.donut.swaipe.domain.user.service;

import com.donut.swaipe.domain.user.dto.SignUpRequestDto;
import com.donut.swaipe.domain.user.dto.SignupResponseDto;
import com.donut.swaipe.domain.user.dto.UpdateUserRequestDto;
import com.donut.swaipe.domain.user.dto.UserInfoDto;
import com.donut.swaipe.domain.user.entity.User;
import com.donut.swaipe.domain.user.enums.UserRole;
import com.donut.swaipe.domain.user.mapper.UserMapper;
import com.donut.swaipe.domain.user.repository.UserRepository;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.user.SignUpFailedException;
import com.donut.swaipe.global.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author donut
 * @version 1.1
 * @since 2024-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserValidator userValidator;

	/**
	 * 새로운 사용자를 등록합니다.
	 *
	 * @param signupRequestDto 회원가입 요청 정보
	 * @return 회원가입 결과 및 사용자 정보
	 * @throws SignUpFailedException 회원가입 처리 중 오류가 발생한 경우
	 */
	@Transactional
	public ApiResponse<SignupResponseDto> signup(SignUpRequestDto signupRequestDto) {
		userValidator.validateUsername(signupRequestDto.getUsername());

		try {
			User newUser = userMapper.toEntity(signupRequestDto);
			User savedUser = userRepository.save(newUser);

			log.info("회원가입 완료: username={}", savedUser.getUsername());

			return ApiResponse.success(
					MessageCode.SIGNUP_SUCCESS,
					userMapper.toSignupResponseDto(savedUser)
			);
		} catch (Exception e) {
			log.error("회원가입 실패: {}", e.getMessage());
			throw new SignUpFailedException();
		}
	}

	/**
	 * 사용자 정보를 업데이트합니다.
	 *
	 * @param username  업데이트할 사용자의 이름
	 * @param updateDto 업데이트할 정보
	 * @return 업데이트된 사용자 정보
	 * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
	 */
	@Transactional
	public ApiResponse<UserInfoDto> updateUser(String username, UpdateUserRequestDto updateDto) {
		User user = findUserByUsername(username);

		updateUserFields(user, updateDto);

		return ApiResponse.success(
				MessageCode.USER_UPDATE_SUCCESS,
				userMapper.toUserInfoDto(user)
		);
	}

	/**
	 * 사용자의 역할을 업데이트합니다.
	 *
	 * @param adminUsername  관리자 사용자명
	 * @param targetUsername 권한을 변경할 대상 사용자명
	 * @param newRole        새로운 권한
	 * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
	 */
	@Transactional
	public void updateUserRole(String adminUsername, String targetUsername, UserRole newRole) {
		User admin = findUserByUsername(adminUsername);
		userValidator.validateAdminRole(admin);

		User targetUser = findUserByUsername(targetUsername);
		targetUser.updateRole(newRole);

		log.info("사용자 권한 업데이트 완료: username={}, newRole={}", targetUsername, newRole);
	}

	/**
	 * 사용자 이름으로 사용자를 찾습니다.
	 */
	private User findUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(UserNotFoundException::new);
	}

	/**
	 * 사용자의 필드를 업데이트합니다.
	 */
	private void updateUserFields(User user, UpdateUserRequestDto updateDto) {
		if (updateDto.getNickname() != null) {
			userValidator.validateNickname(updateDto.getNickname());
			user.updateNickname(updateDto.getNickname());
		}

		if (updateDto.getPassword() != null) {
			String encodedPassword = userMapper.encodePassword(updateDto.getPassword());
			user.updatePassword(encodedPassword);
		}
	}
}