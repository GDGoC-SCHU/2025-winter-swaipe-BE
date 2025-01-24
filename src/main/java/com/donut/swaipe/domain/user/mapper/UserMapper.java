package com.donut.swaipe.domain.user.mapper;

import com.donut.swaipe.domain.user.dto.SignUpRequestDto;
import com.donut.swaipe.domain.user.dto.UserInfoDto;
import com.donut.swaipe.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

	private final PasswordEncoder passwordEncoder;

	public UserInfoDto toUserInfoDto(User user) {
		return new UserInfoDto(
				user.getId(),
				user.getUsername(),
				user.getNickname()
		);
	}

	public User toEntity(SignUpRequestDto signupRequestDto) {
		return User.builder()
				.username(signupRequestDto.getUsername())
				.password(passwordEncoder.encode(signupRequestDto.getPassword()))
				.nickname(signupRequestDto.getNickname())
				.build();
	}
}