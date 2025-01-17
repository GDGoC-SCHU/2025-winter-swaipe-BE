package com.donut.swab.security.filter;


import static com.donut.swab.security.jwt.JwtProvider.AUTHORIZATION_HEADER;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.donut.swab.ErrorResponse;
import com.donut.swab.security.details.UserDetailsServiceImpl;
import com.donut.swab.security.jwt.JwtProvider;
import com.donut.swab.user.dto.AccessTokenDto;
import com.donut.swab.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final UserDetailsServiceImpl userDetailsService;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
			FilterChain filterChain)
			throws ServletException, IOException {

		if (req.getRequestURI().equals("/users/login")
				|| req.getRequestURI().equals("/users")
				|| req.getRequestURI().startsWith("/swagger-ui")
				|| req.getRequestURI().startsWith("/v3/api-docs")
				|| req.getRequestURI().startsWith("/swagger-resources")
				|| req.getRequestURI().equals("/v3/api-docs.yaml")  // 추가
				|| req.getRequestURI().startsWith("/webjars")) {
			filterChain.doFilter(req, res);
			return;
		}

		String accessToken = jwtProvider.getAccessTokenFromHeader(req);

		if (!StringUtils.hasText(accessToken)) {
			log.error("토큰이 요청 헤더에 없습니다.");
			jwtExceptionHandler(res, UNAUTHORIZED, "Access token is missing.");
			return;
		}

		String username = jwtProvider.getUsernameFromToken(accessToken);

		// 액세스 토큰 검증
		if (jwtProvider.validateAccessToken(accessToken)) {
			log.info("액세스 토큰 검증 성공");
			setAuthentication(username);
		} else {
			// 액세스 토큰이 만료되었을 때 Refresh 토큰 검증
			if (jwtProvider.hasRefreshToken(username)) {
				String refreshToken = jwtProvider.substringToken(
						redisTemplate.opsForValue().get(username));
				if (!refreshToken.isEmpty()) {
					log.info("Refresh 토큰 검증 성공, 새로운 액세스 토큰 생성");
					updateAccessToken(refreshToken, username, res);
				} else {
					log.error("Refresh 토큰이 유효하지 않음.");
					jwtExceptionHandler(res, UNAUTHORIZED, "Refresh token is invalid.");
					return;
				}
			} else {
				log.error("유효하지 않은 토큰입니다.");
				jwtExceptionHandler(res, UNAUTHORIZED, "Invalid token.");
				return;
			}
		}
		filterChain.doFilter(req, res);
	}

	private void setAuthentication(String username) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(createAuthentication(username));
		SecurityContextHolder.setContext(context);
	}

	private Authentication createAuthentication(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
	}

	private void jwtExceptionHandler(HttpServletResponse res, HttpStatus status, String message) {
		res.setStatus(status.value());
		res.setContentType("application/json");
		try {
			String json = new ObjectMapper().writeValueAsString(new ErrorResponse(message));
			res.getWriter().write(json);
		} catch (IOException e) {
			log.error("응답 작성 중 오류: {}", e.getMessage());
		}
	}

	private void updateAccessToken(String refreshToken, String username, HttpServletResponse res)
			throws IOException {
		UserRole role = jwtProvider.getRoleFromToken(refreshToken);
		String newAccessToken = jwtProvider.createAccessToken(username, role);

		res.setHeader(AUTHORIZATION_HEADER, newAccessToken);
		setAuthentication(username);

		String jsonResponse = new ObjectMapper().writeValueAsString(
				new AccessTokenDto(newAccessToken));
		res.getWriter().write(jsonResponse);
		log.info("새로운 액세스 토큰 생성 및 응답 전송 완료: {}", newAccessToken);
	}
}
