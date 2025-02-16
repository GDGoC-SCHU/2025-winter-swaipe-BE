package com.donut.swaipe.domain.kakao.redis;

import com.donut.swaipe.global.common.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 카카오 토큰 관련 Redis 처리를 담당하는 서비스입니다.
 * 리프레시 토큰의 저장, 조회, 삭제, 검증 기능을 제공합니다.
 *
 * @author donut
 * @version 1.0
 * @since 2024-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoTokenRedisService implements RedisService {
    private final StringRedisTemplate redisTemplate;
    private static final String TOKEN_PREFIX = "KAKAO_TOKEN:";
    private final ObjectMapper objectMapper;  // JSON 변환용

    /**
     * 리프레시 토큰 정보를 담는 내부 클래스
     */
    @Getter
    @AllArgsConstructor
    private static class TokenInfo {
        private String refreshToken;
        private Long expiresAt;  // 만료 시간 (timestamp)
    }

    /**
     * 리프레시 토큰을 Redis에 저장합니다.
     *
     * @param kakaoId 카카오 ID
     * @param refreshToken 리프레시 토큰
     * @param expiresIn 만료 시간(초)
     */
    public void saveRefreshToken(String kakaoId, String refreshToken, long expiresIn) {
        try {
            // 현재 시간 + 만료 시간으로 만료 timestamp 계산
            long expiresAt = System.currentTimeMillis() + (expiresIn * 1000);
            TokenInfo tokenInfo = new TokenInfo(refreshToken, expiresAt);
            
            // TokenInfo를 JSON으로 변환하여 저장
            String tokenJson = objectMapper.writeValueAsString(tokenInfo);
            setDataExpire(kakaoId, tokenJson, expiresIn);
            
            log.debug("카카오 리프레시 토큰 저장 완료: kakaoId={}, expiresAt={}", 
                    kakaoId, new Date(expiresAt));
        } catch (Exception e) {
            log.error("카카오 리프레시 토큰 저장 실패: kakaoId={}, error={}", kakaoId, e.getMessage());
            throw new RuntimeException("카카오 리프레시 토큰 저장에 실패했습니다.", e);
        }
    }

    /**
     * 리프레시 토큰을 조회합니다.
     *
     * @param kakaoId 카카오 ID
     * @return 저장된 리프레시 토큰
     */
    public String getRefreshToken(String kakaoId) {
        try {
            String tokenJson = getData(kakaoId);
            if (tokenJson == null) {
                log.warn("카카오 리프레시 토큰 없음: kakaoId={}", kakaoId);
                return null;
            }

            TokenInfo tokenInfo = objectMapper.readValue(tokenJson, TokenInfo.class);
            
            // 만료 시간 확인
            if (System.currentTimeMillis() > tokenInfo.getExpiresAt()) {
                log.warn("카카오 리프레시 토큰 만료됨: kakaoId={}", kakaoId);
                deleteData(kakaoId);  // 만료된 토큰 삭제
                return null;
            }

            return tokenInfo.getRefreshToken();
        } catch (Exception e) {
            log.error("카카오 리프레시 토큰 조회 실패: kakaoId={}, error={}", kakaoId, e.getMessage());
            return null;
        }
    }

    /**
     * 리프레시 토큰의 유효성을 검증합니다.
     *
     * @param kakaoId 카카오 ID
     * @return 토큰 유효 여부
     */
    public boolean validateRefreshToken(String kakaoId) {
        try {
            String tokenJson = getData(kakaoId);
            if (tokenJson == null) {
                return false;
            }

            TokenInfo tokenInfo = objectMapper.readValue(tokenJson, TokenInfo.class);
            boolean isValid = System.currentTimeMillis() <= tokenInfo.getExpiresAt();
            
            if (!isValid) {
                deleteData(kakaoId);  // 만료된 토큰 삭제
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("카카오 리프레시 토큰 검증 실패: kakaoId={}, error={}", kakaoId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public void setDataExpire(String key, String value, long duration) {
        try {
            String prefixedKey = TOKEN_PREFIX + key;
            redisTemplate.opsForValue().set(prefixedKey, value, duration, TimeUnit.SECONDS);
            log.debug("카카오 토큰 저장 완료: key={}, duration={}s", key, duration);
        } catch (Exception e) {
            log.error("카카오 토큰 저장 실패: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("카카오 토큰 저장에 실패했습니다.", e);
        }
    }
    
    @Override
    public String getData(String key) {
        try {
            String prefixedKey = TOKEN_PREFIX + key;
            String value = redisTemplate.opsForValue().get(prefixedKey);
            log.debug("카카오 토큰 조회: key={}, exists={}", key, value != null);
            return value;
        } catch (Exception e) {
            log.error("카카오 토큰 조회 실패: key={}, error={}", key, e.getMessage());
            return null;
        }
    }
    
    @Override
    public void deleteData(String key) {
        try {
            String prefixedKey = TOKEN_PREFIX + key;
            redisTemplate.delete(prefixedKey);
            log.debug("카카오 토큰 삭제 완료: key={}", key);
        } catch (Exception e) {
            log.error("카카오 토큰 삭제 실패: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("카카오 토큰 삭제에 실패했습니다.", e);
        }
    }
    
    @Override
    public boolean hasKey(String key) {
        try {
            String prefixedKey = TOKEN_PREFIX + key;
            boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(prefixedKey));
            log.debug("카카오 토큰 키 존재 여부 확인: key={}, exists={}", key, exists);
            return exists;
        } catch (Exception e) {
            log.error("카카오 토큰 키 확인 실패: key={}, error={}", key, e.getMessage());
            return false;
        }
    }
} 