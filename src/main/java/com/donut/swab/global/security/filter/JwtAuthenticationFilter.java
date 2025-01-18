package com.donut.swab.global.security.filter;

import com.donut.swab.domain.Auth.dto.TokenDto;
import com.donut.swab.domain.user.service.RedisService;
import com.donut.swab.global.common.ApiResponse;
import com.donut.swab.global.security.details.UserDetailsImpl;
import com.donut.swab.global.security.jwt.JwtProvider;
import com.donut.swab.domain.user.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final JwtProvider jwtProvider;
	private final RedisService redisService;

	public JwtAuthenticationFilter(JwtProvider jwtProvider,
			RedisService redisService) {
		this.jwtProvider = jwtProvider;
		this.redisService = redisService;
		setFilterProcessesUrl("/users/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {
			return handleStandardLogin(req);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Authentication handleStandardLogin(HttpServletRequest req) throws IOException {
		LoginRequestDto requestDto = new ObjectMapper().readValue(req.getInputStream(),
				LoginRequestDto.class);
		return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
						requestDto.getUsername(),
						requestDto.getPassword(),
						null
				)
		);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain, Authentication authResult) throws IOException {
		UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
		String username = userDetails.getUsername();

		// Access Token과 Refresh Token 생성
		String accessToken = jwtProvider.createAccessToken(username,
				userDetails.getUserRole());
		String refreshToken = jwtProvider.createRefreshToken(username,
				userDetails.getUserRole());

		// Refresh Token을 Redis에 저장
		redisService.saveRefreshToken(username, refreshToken);
		log.info("Refresh Token saved for user: {}", username);

		// 응답 바디에 성공 메시지와 토큰 포함
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		TokenDto tokenDto = new TokenDto(accessToken);
		ApiResponse<TokenDto> apiResponse = ApiResponse.success("로그인 성공", tokenDto);

		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res,
			AuthenticationException failed) throws IOException {
		res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		res.getWriter()
				.print("{\"error\":\"Unauthorized\", \"message\":\"" + failed.getMessage() + "\"}");
	}
}