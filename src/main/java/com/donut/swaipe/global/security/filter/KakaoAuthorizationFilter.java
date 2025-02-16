package com.donut.swaipe.global.security.filter;

import com.donut.swaipe.domain.kakao.dto.AuthResponseDto;
import com.donut.swaipe.domain.kakao.redis.KakaoTokenRedisService;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;
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
import org.springframework.lang.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
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
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		try {
			if (req.getRequestURI().equals("/favicon.ico")) {
				res.setStatus(HttpStatus.NO_CONTENT.value());
				return;
			}

			if (isPermitAllRequest(req.getRequestURI())) {
				log.info("Permitting request to: {}", req.getRequestURI());
				filterChain.doFilter(req, res);
				return;
			}

			String token = resolveToken(req);
			if (token == null) {
				filterChain.doFilter(req, res);
				return;
			}

			// Access Token 검증 및 인증 정보 설정
			if (jwtProvider.validateToken(token)) {
				String kakaoId = jwtProvider.getKakaoIdFromToken(token);
				KakaoUserDetailsImpl userDetails = userDetailsService.loadUserByKakaoId(kakaoId);
				
				// SecurityContext에 인증 정보 설정
				Authentication authentication = new UsernamePasswordAuthenticationToken(
					userDetails.getKakaoUser(),  // Principal을 KakaoUser 객체로 설정
					null,
					userDetails.getAuthorities()
				);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				filterChain.doFilter(req, res);
			} else {
				sendResponse(res, HttpStatus.UNAUTHORIZED, MessageCode.INVALID_TOKEN, null);
			}
		} catch (Exception e) {
			log.error("JWT 인증 처리 중 오류 발생", e);
			sendResponse(res, HttpStatus.INTERNAL_SERVER_ERROR, MessageCode.AUTHORIZED_ERROR, null);
		}
	}

	private void processValidToken(String token, HttpServletRequest req, HttpServletResponse res,
			FilterChain filterChain) throws ServletException, IOException {
		String kakaoId = jwtProvider.getKakaoIdFromToken(token);
		setAuthentication(kakaoId);
		filterChain.doFilter(req, res);
	}

	private void processExpiredToken(String token, HttpServletResponse res) throws IOException {
		String kakaoId = jwtProvider.getKakaoIdFromToken(token);
		
		// Redis에서 리프레시 토큰 검증
		if (!tokenRedisService.validateRefreshToken(kakaoId)) {
			sendResponse(res, HttpStatus.UNAUTHORIZED, MessageCode.INVALID_TOKEN, null);
			return;
		}

		try {
			String refreshToken = tokenRedisService.getRefreshToken(kakaoId);
			
			// 토큰 갱신 로직
			AuthResponseDto responseDto = AuthResponseDto.builder()
					.accessToken(refreshToken)
					.tokenType("Bearer")
					.build();

			sendResponse(res, HttpStatus.OK, MessageCode.REGENERATE_TOKEN, responseDto);
		} catch (Exception e) {
			log.error("토큰 갱신 실패: {}", e.getMessage());
			sendResponse(res, HttpStatus.UNAUTHORIZED, MessageCode.INVALID_TOKEN, null);
		}
	}

	private void setAuthentication(String kakaoId) {
		UserDetails userDetails = userDetailsService.loadUserByKakaoId(kakaoId);
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
		);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
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