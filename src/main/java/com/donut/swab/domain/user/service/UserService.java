package com.donut.swab.domain.user.service;

import com.donut.swab.domain.user.dto.UpdateUserRequestDto;
import com.donut.swab.domain.user.dto.UserInfoDto;
import com.donut.swab.domain.user.mapper.UserMapper;
import com.donut.swab.global.common.ApiResponse;
import com.donut.swab.domain.user.dto.SignUpRequestDto;
import com.donut.swab.domain.user.dto.SignupResponseDto;
import com.donut.swab.domain.user.entity.User;
import com.donut.swab.domain.user.enums.UserRole;
import com.donut.swab.domain.user.repository.UserRepository;
import com.donut.swab.global.exception.auth.UnauthorizedException;
import com.donut.swab.global.exception.user.SignUpFailedException;
import com.donut.swab.global.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public ApiResponse<SignupResponseDto> signup(SignUpRequestDto signupRequestDto) {
        try {
            // 아이디 중복 검사
            if (userRepository.existsByUsername(signupRequestDto.getUsername())) {
                throw new IllegalArgumentException("이미 존재하는 username 입니다.");
            }

            // 비밀번호 인코딩 및 회원 생성
            User user = User.builder()
                    .username(signupRequestDto.getUsername())
                    .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                    .nickname(signupRequestDto.getNickname())
                    .build();

            userRepository.save(user);
            log.info("회원가입 완료: username={}", user.getUsername());

            return ApiResponse.success(
                    "회원가입 성공",
                    new SignupResponseDto(user.getId(), user.getUsername(), user.getNickname())
            );
        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage());
            throw new SignUpFailedException("회원가입에 실패했습니다: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<UserInfoDto> getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return ApiResponse.success(
                "사용자 정보 조회 성공",
                userMapper.toUserInfoDto(user)
        );
    }

    @Transactional
    public ApiResponse<UserInfoDto> updateUser(String username, UpdateUserRequestDto updateDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (updateDto.getNickname() != null) {
            user.updateNickname(updateDto.getNickname());
        }
        if (updateDto.getPassword() != null) {
            user.updatePassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        return ApiResponse.success(
                "사용자 정보 수정 성공",
                userMapper.toUserInfoDto(user)
        );
    }

    // 관리자용 역할 변경 메소드
    @Transactional
    public void updateUserRole(String adminUsername, String targetUsername, UserRole newRole) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new UserNotFoundException("관리자를 찾을 수 없습니다."));

        if (admin.getUserRole() != UserRole.MANAGER) {
            throw new UnauthorizedException("관리자 권한이 필요합니다.");
        }

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        targetUser.updateRole(newRole);
    }

    @Transactional(readOnly = true)
    public ApiResponse<Boolean> checkUsername(String username) {
        boolean exists = userRepository.existsByUsername(username);
        return ApiResponse.success(
                exists ? "이미 존재하는 username 입니다." : "사용 가능한 username 입니다.",
                exists
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Boolean> checkNickname(String nickname) {
        boolean exists = userRepository.existsByNickname(nickname);
        return ApiResponse.success(
                exists ? "이미 존재하는 nickname 입니다." : "사용 가능한 nickname 입니다.",
                exists
        );
    }
}