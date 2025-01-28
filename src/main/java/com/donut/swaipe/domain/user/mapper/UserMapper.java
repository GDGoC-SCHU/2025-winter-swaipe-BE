package com.donut.swaipe.domain.user.mapper;

import com.donut.swaipe.domain.user.dto.SignUpRequestDto;
import com.donut.swaipe.domain.user.dto.SignupResponseDto;
import com.donut.swaipe.domain.user.dto.UserInfoDto;
import com.donut.swaipe.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 사용자 관련 객체 간의 매핑을 처리하는 매퍼 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

	private final PasswordEncoder passwordEncoder;

	/**
	 * User 엔티티를 UserInfoDto로 변환합니다.
	 *
	 * @param user 변환할 User 엔티티
	 * @return 변환된 UserInfoDto
	 */
	public UserInfoDto toUserInfoDto(User user) {
		return new UserInfoDto(
				user.getId(),
				user.getUsername(),
				user.getNickname()
		);
	}

	/**
	 * User 엔티티를 SignupResponseDto로 변환합니다.
	 *
	 * @param user 변환할 User 엔티티
	 * @return 변환된 SignupResponseDto
	 */
	public SignupResponseDto toSignupResponseDto(User user) {
		return new SignupResponseDto(
				user.getId(),
				user.getUsername(),
				user.getNickname()
		);
	}

	/**
	 * SignUpRequestDto를 User 엔티티로 변환합니다.
	 *
	 * @param signupRequestDto 변환할 SignUpRequestDto
	 * @return 변환된 User 엔티티
	 */
	public User toEntity(SignUpRequestDto signupRequestDto) {
		return User.builder()
				.username(signupRequestDto.getUsername())
				.password(passwordEncoder.encode(signupRequestDto.getPassword()))
				.nickname(signupRequestDto.getNickname())
				.build();
	}

	/**
	 * 비밀번호를 암호화합니다.
	 *
	 * @param password 암호화할 비밀번호
	 * @return 암호화된 비밀번호
	 */
	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}
}