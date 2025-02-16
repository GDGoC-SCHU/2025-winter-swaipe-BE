package com.donut.swaipe.global.security.jwt;

import com.donut.swaipe.domain.kakao.entity.KakaoUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 *
 * @author donut
 * @version 1.0
 * @since 2024-01-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

	private static final String AUTHORIZATION_KEY = "auth";
	private static final Long ACCESS_TOKEN_TIME = 30 * 60 * 1000L; // 30분
	public static final Long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; // 2주

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${jwt-secret-key}")
	private String secretKey;
	private Key key;

	/**
	 * 시크릿 키를 Base64로 디코딩하여 HMAC-SHA 키를 생성합니다.
	 */
	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
	}

	/**
	 * 액세스 토큰을 생성합니다.
	 *
	 * @param kakaoUser 카카오 사용자 정보
	 * @return 생성된 액세스 토큰
	 */
	public String createAccessToken(String kakaoUserId) {
		Date date = new Date();

		return Jwts.builder()
				.setSubject(kakaoUserId)
				.claim(AUTHORIZATION_KEY, "ROLE_USER")
				.setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Refresh Token을 생성합니다.
	 *
	 * @param kakaoUser 카카오 사용자 정보
	 */
	public String createRefreshToken(String kakaoUserId) {
		Date date = new Date();

		return Jwts.builder()
				.setSubject(kakaoUserId)
				.claim(AUTHORIZATION_KEY, "ROLE_USER")
				.setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * 토큰에서 카카오 ID를 추출합니다.
	 *
	 * @param token JWT 토큰
	 */
	public String getKakaoIdFromToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();
			return claims.getSubject();
		} catch (ExpiredJwtException e) {
			// 토큰이 만료된 경우에도 사용자 정보 반환
			return e.getClaims().getSubject();
		}
	}

	/**
	 * JWT 토큰의 유효성을 검증합니다.
	 *
	 * @param token 검증할 JWT 토큰
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("유효하지 않는 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.error("잘못된 JWT 토큰입니다.");
		}
		return false;
	}

	/**
	 * 토큰의 남은 만료 시간을 반환합니다.
	 */
	public Long getAccessTokenExpiration() {
		return ACCESS_TOKEN_TIME;
	}

	public Long getRefreshTokenExpiration() {
		return REFRESH_TOKEN_TIME;
	}
}