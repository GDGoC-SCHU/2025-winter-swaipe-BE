package com.donut.swab.domain.user.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.donut.swab.global.security.jwt.JwtProvider.REFRESH_TOKEN_TIME;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String REFRESH_TOKEN_PREFIX = "RT:";

	@Transactional
	public void saveRefreshToken(String username, String refreshToken) {
		try {
			String key = REFRESH_TOKEN_PREFIX + username;
			log.info("Saving Refresh Token - Key: {}", key);
			
			redisTemplate.opsForValue()
					.set(key, refreshToken, REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);
			
			// 저장된 토큰 확인
			String savedToken = redisTemplate.opsForValue().get(key);
			if (savedToken != null) {
				log.info("Refresh Token 저장 완료: username={}", username);
			} else {
				log.error("Refresh Token 저장 실패: username={}", username);
				throw new RuntimeException("Refresh Token 저장에 실패했습니다.");
			}
		} catch (Exception e) {
			log.error("Refresh Token 저장 중 오류 발생: username={}, error={}", username, e.getMessage());
			throw new RuntimeException("Refresh Token 저장에 실패했습니다.");
		}
	}

	public String getRefreshToken(String username) {
		String key = REFRESH_TOKEN_PREFIX + username;
		try {
			String token = redisTemplate.opsForValue().get(key);
			if (token != null) {
				log.info("Refresh Token 조회 성공: username={}", username);
				return token;
			} else {
				log.warn("Refresh Token not found for username: {}", username);
				return null;
			}
		} catch (Exception e) {
			log.error("Refresh Token 조회 중 오류 발생: username={}, error={}", username, e.getMessage());
			return null;
		}
	}

	@Transactional
	public boolean deleteRefreshToken(String username) {
		String key = REFRESH_TOKEN_PREFIX + username;
		try {
			// 삭제 전에 토큰 존재 여부 확인
			String token = redisTemplate.opsForValue().get(key);
			if (token == null) {
				log.warn("Refresh Token이 이미 없음: username={}, key={}", username, key);
				return false;
			}
			
			// 토큰 삭제
			Boolean deleted = redisTemplate.delete(key);
			if (Boolean.TRUE.equals(deleted)) {
				log.info("Refresh Token 삭제 완료: username={}, key={}", username, key);
				
				// 삭제 확인
				String checkToken = redisTemplate.opsForValue().get(key);
				if (checkToken == null) {
					log.info("Refresh Token 삭제 확인 완료: username={}", username);
					return true;
				} else {
					log.error("Refresh Token 삭제 실패 (토큰이 여전히 존재함): username={}", username);
					return false;
				}
			} else {
				log.error("Refresh Token 삭제 실패: username={}", username);
				return false;
			}
		} catch (Exception e) {
			log.error("Refresh Token 삭제 중 오류 발생: username={}, key={}, error={}", 
					username, key, e.getMessage());
			throw new RuntimeException("Refresh Token 삭제 중 오류가 발생했습니다.", e);
		}
	}

	public boolean validateRefreshToken(String username, String refreshToken) {
		String key = REFRESH_TOKEN_PREFIX + username;
		try {
			String storedToken = redisTemplate.opsForValue().get(key);
			boolean isValid = refreshToken.equals(storedToken);
			if (isValid) {
				log.info("Refresh Token 검증 성공: username={}", username);
			} else {
				log.warn("Refresh Token 검증 실패: username={}", username);
			}
			return isValid;
		} catch (Exception e) {
			log.error("Refresh Token 검증 중 오류 발생: username={}, error={}", username, e.getMessage());
			return false;
		}
	}
}