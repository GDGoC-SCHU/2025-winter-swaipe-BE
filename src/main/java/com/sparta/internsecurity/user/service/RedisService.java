package com.sparta.internsecurity.user.service;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;
	private final Long refreshTokenExpiration = 14 * 24 * 60 * 60 * 1000L; // 14일

	@Transactional
	public void saveRefreshToken(String username, String refreshToken) {
		redisTemplate.opsForValue().set(username, refreshToken, refreshTokenExpiration, TimeUnit.MILLISECONDS);
	}


	@Transactional
	public String getValue(String key, String prefix) {
		return redisTemplate.opsForValue().get(key + prefix);
	}
}
