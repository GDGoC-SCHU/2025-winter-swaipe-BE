package com.donut.swaipe.domain.mms.redis;

import com.donut.swaipe.global.common.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

/**
 * SMS 인증 관련 Redis 처리를 담당하는 서비스입니다.
 * 인증번호와 사용자 인증 상태를 관리합니다.
 *
 * @author donut
 * @version 1.0
 * @since 2024-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsRedisService implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public void setDataExpire(String key, String value, long duration) {
        try {
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(duration));
            log.debug("SMS 데이터 저장 완료: key={}, duration={}s", key, duration);
        } catch (Exception e) {
            log.error("SMS 데이터 저장 실패: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("SMS 데이터 저장에 실패했습니다.", e);
        }
    }
    
    @Override
    public String getData(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            log.debug("SMS 데이터 조회: key={}, exists={}", key, value != null);
            return value;
        } catch (Exception e) {
            log.error("SMS 데이터 조회 실패: key={}, error={}", key, e.getMessage());
            return null;
        }
    }
    
    @Override
    public void deleteData(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("SMS 데이터 삭제 완료: key={}", key);
        } catch (Exception e) {
            log.error("SMS 데이터 삭제 실패: key={}, error={}", key, e.getMessage());
            throw new RuntimeException("SMS 데이터 삭제에 실패했습니다.", e);
        }
    }
    
    @Override
    public boolean hasKey(String key) {
        try {
            boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
            log.debug("SMS 키 존재 여부 확인: key={}, exists={}", key, exists);
            return exists;
        } catch (Exception e) {
            log.error("SMS 키 확인 실패: key={}, error={}", key, e.getMessage());
            return false;
        }
    }
} 