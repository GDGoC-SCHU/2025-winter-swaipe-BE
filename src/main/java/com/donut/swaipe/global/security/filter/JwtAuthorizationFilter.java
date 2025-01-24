package com.donut.swaipe.global.security.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.donut.swaipe.domain.Auth.dto.TokenDto;
import com.donut.swaipe.domain.user.enums.UserRole;
import com.donut.swaipe.domain.user.service.RedisService;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.exception.auth.InvalidTokenException;
import com.donut.swaipe.global.security.details.UserDetailsServiceImpl;
import com.donut.swaipe.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtProvider jwtProvider;
	private final UserDetailsServiceImpl userDetailsService;
	private final RedisService redisService;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
			FilterChain filterChain) throws ServletException, IOException {

		if (isPermitAllRequest(req.getRequestURI())) {
			log.info("Current request : {}", req.getRequestURI());
			filterChain.doFilter(req, res);
			return;
		}

		try {
			String token = resolveToken(req);

			if (!StringUtils.hasText(token)) {
				throw new InvalidTokenException("Access token is missing.");
			}

			String username = jwtProvider.getUsernameFromToken(token);
			
			// Redis에서 리프레시 토큰 존재 여부 확인
			String refreshToken = redisService.getRefreshToken(username);
			if (refreshToken == null) {
				throw new InvalidTokenException("로그아웃된 사용자입니다. 다시 로그인해주세요.");
			}

			if (jwtProvider.validateToken(token)) {
				log.info("액세스 토큰 검증 성공");
				setAuthentication(username);
				filterChain.doFilter(req, res);
			} else {
				log.info("액세스 토큰 만료. 리프레시 토큰으로 자동 재발급");
				handleTokenReissue(username, refreshToken, res);
			}

		} catch (InvalidTokenException e) {
			log.error("토큰 검증 실패: {}", e.getMessage());
			sendErrorResponse(res, HttpStatus.UNAUTHORIZED, e.getMessage());
		} catch (Exception e) {
			log.error("JWT 인증 처리 중 오류 발생", e);
			sendErrorResponse(res, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed");
		}
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		return null;
	}

	private boolean isPermitAllRequest(String requestURI) {
		return requestURI.equals("/users/login")
				|| requestURI.equals("/users")
				|| requestURI.equals("/users/refresh")
				|| requestURI.startsWith("/swagger-ui")
				|| requestURI.startsWith("/v3/api-docs")
				|| requestURI.startsWith("/swagger-resources")
				|| requestURI.equals("/v3/api-docs.yaml")
				|| requestURI.startsWith("/webjars");
	}

	private void handleTokenReissue(String username, String refreshToken, HttpServletResponse res) 
			throws IOException {
		if (!jwtProvider.validateToken(refreshToken)) {
			throw new InvalidTokenException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
		}

		UserRole role = jwtProvider.getRoleFromToken(refreshToken);
		String newAccessToken = jwtProvider.createAccessToken(username, role);
		String newRefreshToken = jwtProvider.createRefreshToken(username, role);
		
		redisService.saveRefreshToken(username, newRefreshToken);
		
		TokenDto tokenDto = new TokenDto(newAccessToken);
		sendTokenResponse(res, tokenDto);
	}

	private void setAuthentication(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
		);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private void sendTokenResponse(HttpServletResponse res, TokenDto tokenDto) throws IOException {
		res.setStatus(HttpStatus.OK.value());
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		ObjectMapper objectMapper = new ObjectMapper();
		ApiResponse<TokenDto> response = ApiResponse.success("토큰 재발급 성공", tokenDto);
		res.getWriter().write(objectMapper.writeValueAsString(response));
	}

	private void sendErrorResponse(HttpServletResponse res, HttpStatus status, String message)
			throws IOException {
		res.setStatus(status.value());
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		ObjectMapper objectMapper = new ObjectMapper();
		ApiResponse<Void> errorResponse = ApiResponse.error(message);
		res.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
