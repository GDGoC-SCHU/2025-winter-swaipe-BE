package com.donut.swaipe.domain.kakao.service;

import com.donut.swaipe.domain.kakao.config.KakaoClient;
import com.donut.swaipe.domain.kakao.dto.AuthResponseDto;
import com.donut.swaipe.domain.kakao.dto.KakaoTokenDto;
import com.donut.swaipe.domain.kakao.dto.KakaoUserInfo;
import com.donut.swaipe.domain.kakao.entity.KakaoUser;
import com.donut.swaipe.domain.kakao.redis.KakaoTokenRedisService;
import com.donut.swaipe.domain.kakao.repository.KakaoUserRepository;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
import com.donut.swaipe.global.exception.kakao.KakaoAuthException;
import com.donut.swaipe.global.exception.kakao.KakaoTokenExpiredException;
import com.donut.swaipe.global.exception.kakao.KakaoTokenNotFoundException;
import com.donut.swaipe.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoAuthService {

	private final KakaoClient kakaoClient;
	private final KakaoUserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final KakaoTokenRedisService tokenRedisService;

	/**
	 * 카카오 인증 코드를 통해 사용자 인증을 처리합니다.
	 *
	 * @param code 카카오 인증 코드
	 * @return 인증 결과 및 토큰 정보
	 * @throws KakaoAuthException 카카오 인증 처리 중 오류가 발생한 경우
	 */
	@Transactional
	public ApiResponse<AuthResponseDto> authenticateUser(String code) {
		try {
			// 1. 카카오 토큰 발급
			KakaoTokenDto kakaoTokenDto = kakaoClient.getToken(code);

			// 2. 카카오 사용자 정보 조회
			KakaoUserInfo userInfo = kakaoClient.getUserInfo(kakaoTokenDto.getAccessToken());

			// 3. 사용자 정보 저장 또는 업데이트
			KakaoUser kakaoUser = userRepository.findByKakaoId(userInfo.getId())
					.orElseGet(() -> KakaoUser.createUser(
							userInfo.getId(),
							userInfo.getKakaoAccount().getEmail(),
							userInfo.getKakaoAccount().getProfile().getNickname()
					));
			userRepository.save(kakaoUser);

			// 4. JWT 토큰 생성
			String accessToken = jwtProvider.createAccessToken(kakaoUser.getKakaoId());
			String refreshToken = jwtProvider.createRefreshToken(kakaoUser.getKakaoId());

			log.info(accessToken);

			// 5. 리프레시 토큰 Redis 저장
			tokenRedisService.saveRefreshToken(
					kakaoUser.getKakaoId(),
					refreshToken,
					jwtProvider.getRefreshTokenExpiration()
			);

			return ApiResponse.success(
					MessageCode.SIGNIN_SUCCESS,
					AuthResponseDto.builder()
							.accessToken(accessToken)
							.refreshToken(refreshToken)
							.build()
			);
		} catch (Exception e) {
			log.error("카카오 로그인 실패: {}", e.getMessage());
			throw new CustomException(MessageCode.SIGNIN_FAILED);
		}
	}

	/**
	 * 카카오 토큰을 갱신합니다.
	 *
	 * @param kakaoUser 카카오 사용자 정보
	 * @return 갱신된 토큰 정보
	 * @throws KakaoTokenNotFoundException 토큰을 찾을 수 없는 경우
	 * @throws KakaoTokenExpiredException  리프레시 토큰이 만료된 경우
	 */
	@Transactional
	public ApiResponse<AuthResponseDto> refreshUserToken(KakaoUser kakaoUser) {
		String kakaoId = kakaoUser.getKakaoId();
		String storedRefreshToken = tokenRedisService.getRefreshToken(kakaoId);

		if (storedRefreshToken == null) {
			throw new CustomException(MessageCode.KAKAO_TOKEN_REFRESH_FAILED);
		}

		// 새로운 액세스 토큰과 리프레시 토큰 생성
		String newAccessToken = jwtProvider.createAccessToken(kakaoId);
		String newRefreshToken = jwtProvider.createRefreshToken(kakaoId);

		// 새로운 리프레시 토큰 저장
		tokenRedisService.saveRefreshToken(
				kakaoId,
				newRefreshToken,
				jwtProvider.getRefreshTokenExpiration()
		);

		return ApiResponse.success(
				MessageCode.REGENERATE_TOKEN,
				AuthResponseDto.builder()
						.accessToken(newAccessToken)
						.refreshToken(newRefreshToken)
						.build()
		);
	}
}