package com.donut.swaipe.global.security.filter;

import com.donut.swaipe.domain.kakao.redis.KakaoTokenRedisService;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.kakao.KakaoAuthException;
import com.donut.swaipe.global.exception.kakao.KakaoTokenExpiredException;
import com.donut.swaipe.global.security.details.KakaoUserDetailsImpl;
import com.donut.swaipe.global.security.details.KakaoUserDetailsService;
import com.donut.swaipe.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class KakaoAuthorizationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private static final List<String> PERMIT_ALL_PATHS = List.of(
			"/api/auth/kakao/**",
			"oauth/**",
			"/swagger-ui",
			"/v3/api-docs",
			"/swagger-resources",
			"/v3/api-docs.yaml",
			"/webjars",
			"/favicon.ico",
			"/test/**",
			"/templates/**"
	);

	private final JwtProvider jwtProvider;
	private final KakaoUserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;
	private final KakaoTokenRedisService tokenRedisService;

	public KakaoAuthorizationFilter(
			JwtProvider jwtProvider,
			KakaoUserDetailsService userDetailsService,
			ObjectMapper objectMapper,
			KakaoTokenRedisService tokenRedisService) {
		this.jwtProvider = jwtProvider;
		this.userDetailsService = userDetailsService;
		this.objectMapper = objectMapper;
		this.tokenRedisService = tokenRedisService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		if (isPermitAllRequest(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = resolveToken(request);

		if (token != null) {
			try {
				if (jwtProvider.isTokenExpired(token)) {
					log.info("Access Token 만료, Refresh Token으로 재발급 시도");
					String kakaoId = jwtProvider.getKakaoIdFromToken(token);
					String refreshToken = jwtProvider.getRefreshToken(kakaoId);

					if (refreshToken != null && jwtProvider.validateRefreshToken(refreshToken)) {
						// Refresh Token이 유효한 경우 새로운 Access Token 발급
						String newAccessToken = jwtProvider.reissueAccessToken(kakaoId);
						response.setHeader("Authorization", "Bearer " + newAccessToken);
						token = newAccessToken;
						log.info("새로운 Access Token 발급 성공");
					} else {
						log.error("Refresh Token이 유효하지 않거나 없음");
						throw new KakaoTokenExpiredException();
					}
				}

				// 토큰 검증 및 인증 처리
				if (jwtProvider.validateToken(token)) {
					String kakaoId = jwtProvider.getKakaoIdFromToken(token);
					KakaoUserDetailsImpl userDetails = userDetailsService.loadUserByKakaoId(
							kakaoId);

					Authentication authentication = new UsernamePasswordAuthenticationToken(
							userDetails.getKakaoUser(),
							null,
							userDetails.getAuthorities()
					);

					SecurityContextHolder.getContext().setAuthentication(authentication);
					filterChain.doFilter(request, response);
					return;
				}
			} catch (KakaoTokenExpiredException e) {
				log.error("토큰 재발급 실패: {}", e.getMessage());
				sendResponse(response, HttpStatus.UNAUTHORIZED, MessageCode.KAKAO_TOKEN_EXPIRED, null);
				return;
			} catch (Exception e) {
				log.error("인증 처리 중 오류 발생: {}", e.getMessage());
				SecurityContextHolder.clearContext();
				sendResponse(response, HttpStatus.UNAUTHORIZED, MessageCode.AUTHORIZED_ERROR, null);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private boolean isPermitAllRequest(String requestURI) {
		return PERMIT_ALL_PATHS.stream()
				.anyMatch(requestURI::startsWith);
	}

	private void sendResponse(HttpServletResponse res, HttpStatus status,
			MessageCode message, Object data) throws IOException {
		res.setStatus(status.value());
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		ApiResponse<?> response = data != null ?
				ApiResponse.success(message, data) :
				ApiResponse.error(message);

		res.getWriter().write(objectMapper.writeValueAsString(response));
	}
}