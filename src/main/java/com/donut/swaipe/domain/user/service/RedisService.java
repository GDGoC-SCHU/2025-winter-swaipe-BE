package com.donut.swaipe.domain.user.service;

import static com.donut.swaipe.global.security.jwt.JwtProvider.REFRESH_TOKEN_TIME;

import jakarta.transaction.Transactional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * Redis를 활용한 리프레시 토큰 관리 서비스입니다. 토큰의 저장, 조회, 삭제, 검증 기능을 제공합니다.
 *
 * <p>이 서비스는 다음과 같은 주요 기능을 제공합니다:</p>
 * <ul>
 *   <li>리프레시 토큰 저장</li>
 *   <li>리프레시 토큰 조회</li>
 *   <li>리프레시 토큰 삭제</li>
 *   <li>리프레시 토큰 유효성 검증</li>
 * </ul>
 *
 * @author donut
 * @version 1.1
 * @since 2024-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String REFRESH_TOKEN_PREFIX = "RT:";

	/**
	 * 리프레시 토큰을 Redis에 저장합니다. 토큰은 설정된 만료 시간(REFRESH_TOKEN_TIME)과 함께 저장됩니다.
	 *
	 * @param username     사용자 아이디
	 * @param refreshToken 저장할 리프레시 토큰
	 * @throws RuntimeException 토큰 저장 실패 시 발생
	 */
	@Transactional
	public void saveRefreshToken(String username, String refreshToken) {
		String key = generateKey(username);
		ValueOperations<String, String> ops = redisTemplate.opsForValue();

		try {
			log.info("리프레시 토큰 저장 시도: username={}", username);
			ops.set(key, refreshToken, REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);

			verifyTokenSaved(key, username);
		} catch (Exception e) {
			log.error("리프레시 토큰 저장 실패: username={}, error={}", username, e.getMessage());
			throw new RuntimeException("리프레시 토큰 저장에 실패했습니다.", e);
		}
	}

	/**
	 * 사용자의 리프레시 토큰을 조회합니다.
	 *
	 * @param username 사용자 아이디
	 * @return 저장된 리프레시 토큰, 없는 경우 null
	 */
	public String getRefreshToken(String username) {
		String key = generateKey(username);
		ValueOperations<String, String> ops = redisTemplate.opsForValue();

		try {
			String token = ops.get(key);
			logTokenRetrieval(username, token != null);
			return token;
		} catch (Exception e) {
			log.error("리프레시 토큰 조회 실패: username={}, error={}", username, e.getMessage());
			return null;
		}
	}

	/**
	 * 사용자의 리프레시 토큰을 삭제합니다.
	 *
	 * @param username 사용자 아이디
	 * @return 삭제 성공 여부
	 * @throws RuntimeException 삭제 중 오류 발생 시
	 */
	@Transactional
	public boolean deleteRefreshToken(String username) {
		String key = generateKey(username);

		try {
			if (!isTokenExists(key)) {
				log.warn("삭제할 리프레시 토큰이 없음: username={}", username);
				return false;
			}

			return performTokenDeletion(key, username);
		} catch (Exception e) {
			log.error("리프레시 토큰 삭제 실패: username={}, error={}", username, e.getMessage());
			throw new RuntimeException("리프레시 토큰 삭제 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 제공된 리프레시 토큰의 유효성을 검증합니다.
	 *
	 * @param username     사용자 아이디
	 * @param refreshToken 검증할 리프레시 토큰
	 * @return 토큰의 유효성 여부
	 */
	public boolean validateRefreshToken(String username, String refreshToken) {
		String key = generateKey(username);
		ValueOperations<String, String> ops = redisTemplate.opsForValue();

		try {
			String storedToken = ops.get(key);
			boolean isValid = refreshToken.equals(storedToken);
			logTokenValidation(username, isValid);
			return isValid;
		} catch (Exception e) {
			log.error("리프레시 토큰 검증 실패: username={}, error={}", username, e.getMessage());
			return false;
		}
	}

	/**
	 * Redis 키를 생성합니다.
	 *
	 * @param username 사용자 아이디
	 * @return 생성된 Redis 키
	 */
	private String generateKey(String username) {
		return REFRESH_TOKEN_PREFIX + username;
	}

	/**
	 * 토큰이 정상적으로 저장되었는지 확인합니다.
	 *
	 * @param key      Redis 키
	 * @param username 사용자 아이디
	 * @throws RuntimeException 저장 확인 실패 시
	 */
	private void verifyTokenSaved(String key, String username) {
		String savedToken = redisTemplate.opsForValue().get(key);
		if (savedToken == null) {
			log.error("리프레시 토큰 저장 확인 실패: username={}", username);
			throw new RuntimeException("리프레시 토큰 저장을 확인할 수 없습니다.");
		}
		log.info("리프레시 토큰 저장 완료: username={}", username);
	}

	/**
	 * 토큰의 존재 여부를 확인합니다.
	 *
	 * @param key Redis 키
	 * @return 토큰 존재 여부
	 */
	private boolean isTokenExists(String key) {
		return redisTemplate.opsForValue().get(key) != null;
	}

	/**
	 * 토큰 삭제를 수행하고 결과를 확인합니다.
	 *
	 * @param key      Redis 키
	 * @param username 사용자 아이디
	 * @return 삭제 성공 여부
	 */
	private boolean performTokenDeletion(String key, String username) {
		Boolean deleted = redisTemplate.delete(key);
		if (Boolean.TRUE.equals(deleted)) {
			boolean confirmDeletion = !isTokenExists(key);
			if (confirmDeletion) {
				log.info("리프레시 토큰 삭제 완료: username={}", username);
				return true;
			}
			log.error("리프레시 토큰 삭제 실패 (토큰이 여전히 존재함): username={}", username);
		}
		return false;
	}

	/**
	 * 토큰 조회 결과를 로깅합니다.
	 *
	 * @param username 사용자 아이디
	 * @param found    토큰 조회 성공 여부
	 */
	private void logTokenRetrieval(String username, boolean found) {
		if (found) {
			log.info("리프레시 토큰 조회 성공: username={}", username);
		} else {
			log.warn("리프레시 토큰을 찾을 수 없음: username={}", username);
		}
	}

	/**
	 * 토큰 검증 결과를 로깅합니다.
	 *
	 * @param username 사용자 아이디
	 * @param isValid  검증 결과
	 */
	private void logTokenValidation(String username, boolean isValid) {
		if (isValid) {
			log.info("리프레시 토큰 검증 성공: username={}", username);
		} else {
			log.warn("리프레시 토큰 검증 실패: username={}", username);
		}
	}
}