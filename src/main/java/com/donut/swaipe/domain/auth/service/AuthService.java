package com.donut.swaipe.domain.auth.service;

import com.donut.swaipe.domain.auth.dto.TokenDto;
import com.donut.swaipe.domain.auth.dto.TokenRequestDto;
import com.donut.swaipe.domain.user.dto.SignOutRequestDto;
import com.donut.swaipe.domain.user.enums.UserRole;
import com.donut.swaipe.domain.user.repository.UserRepository;
import com.donut.swaipe.domain.user.service.RedisService;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.auth.InvalidTokenException;
import com.donut.swaipe.global.exception.user.LogoutFailedException;
import com.donut.swaipe.global.exception.user.SignOutFailedException;
import com.donut.swaipe.global.security.details.UserDetailsImpl;
import com.donut.swaipe.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스 클래스 토큰 재발급, 로그아웃, 회원탈퇴 등의 기능을 제공합니다.
 *
 * @author donut
 * @version 1.0
 * @since 2024-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RedisService redisService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 토큰을 재발급합니다.
	 *
	 * @param tokenRequestDto 재발급 요청 DTO
	 * @return 새로운 액세스 토큰이 포함된 응답
	 * @throws InvalidTokenException 토큰이 유효하지 않은 경우
	 */
	public ApiResponse<TokenDto> refresh(TokenRequestDto tokenRequestDto) {
		String token = tokenRequestDto.getToken();

		try {
			// 액세스 토큰에서 사용자 정보 추출
			String username = jwtProvider.getUsernameFromToken(token);

			// Redis에서 해당 사용자의 리프레시 토큰 조회
			String refreshToken = redisService.getRefreshToken(username);
			if (refreshToken == null) {
				throw new InvalidTokenException();
			}

			// 리프레시 토큰 검증
			if (!jwtProvider.validateToken(refreshToken)) {
				throw new InvalidTokenException();
			}

			UserRole role = jwtProvider.getRoleFromToken(refreshToken);

			// 새로운 토큰 발급
			String newAccessToken = jwtProvider.createAccessToken(username, role);
			String newRefreshToken = jwtProvider.createRefreshToken(username, role);

			// 새로운 리프레시 토큰을 Redis에 저장
			redisService.saveRefreshToken(username, newRefreshToken);

			log.info("토큰 재발급 성공: username={}", username);
			return ApiResponse.success(MessageCode.REGENERATE_TOKEN, new TokenDto(newAccessToken));
		} catch (Exception e) {
			log.error("토큰 재발급 실패: {}", e.getMessage());
			throw new InvalidTokenException();
		}
	}

	public ApiResponse<Void> logout(UserDetailsImpl userDetails) {
		String username = userDetails.getUsername();
		log.info("로그아웃 처리 시작: username={}", username);

		try {
			// 중복 호출 제거
			boolean isDeleted = redisService.deleteRefreshToken(username);
			if (!isDeleted) {
				log.warn("Refresh token not found for user: {}", username);
			}
			SecurityContextHolder.clearContext();
			return ApiResponse.success(MessageCode.LOGOUT_SUCCESS, null);
		} catch (Exception e) {
			log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage());
			throw new LogoutFailedException();
		}
	}

	@Transactional
	public ApiResponse<Void> signOut(UserDetailsImpl userDetails, SignOutRequestDto requestDto) {
		String username = userDetails.getUsername();
		log.info("회원탈퇴 처리 시작: username={}", username);

		try {
			// 리프레시 토큰 확인
			String refreshToken = redisService.getRefreshToken(username);
			if (refreshToken == null) {
				log.info("이미 로그아웃된 사용자입니다.");
				throw new SignOutFailedException();
			}

			// 비밀번호 검증
			if (!passwordEncoder.matches(requestDto.getPassword(), userDetails.getPassword())) {
				log.info("비밀번호가 일치하지 않습니다.");
				throw new SignOutFailedException();
			}

			// Redis에서 리프레시 토큰 삭제
			boolean tokenDeleted = redisService.deleteRefreshToken(username);
			if (!tokenDeleted) {
				log.warn("Refresh token not found for user: {}", username);
			}

			// DB에서 사용자 삭제
			int deletedCount = userRepository.deleteByUsername(username);
			if (deletedCount == 0) {
				log.info("사용자 정보를 찾을 수 없습니다.");
				throw new SignOutFailedException();
			}

			SecurityContextHolder.clearContext();
			return ApiResponse.success(MessageCode.SIGNOUT_SUCCESS, null);
		} catch (SignOutFailedException e) {
			throw e;
		} catch (Exception e) {
			log.error("회원탈퇴 처리 중 오류 발생: {}", e.getMessage());
			log.info("회원탈퇴 처리 중 오류가 발생했습니다.");
			throw new SignOutFailedException();
		}
	}
}