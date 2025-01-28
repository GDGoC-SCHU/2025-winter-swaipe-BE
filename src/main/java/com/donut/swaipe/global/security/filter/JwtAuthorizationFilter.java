package com.donut.swaipe.global.security.filter;

import static com.donut.swaipe.global.common.MessageCode.AUTHORIZED_ERROR;
import static com.donut.swaipe.global.common.MessageCode.GENERATE_TOKEN;

import com.donut.swaipe.domain.auth.dto.TokenDto;
import com.donut.swaipe.domain.user.enums.UserRole;
import com.donut.swaipe.domain.user.service.RedisService;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.auth.InvalidTokenException;
import com.donut.swaipe.global.security.details.UserDetailsServiceImpl;
import com.donut.swaipe.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증을 처리하는 필터 클래스입니다. 모든 HTTP 요청에 대해 JWT 토큰을 검증하고 인증을 처리합니다.
 *
 * @author donut
 * @version 1.1
 * @since 2024-01-28
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final List<String> PERMIT_ALL_PATHS = List.of(
			"/users/login",
			"/users",
			"/users/refresh",
			"/swagger-ui",
			"/v3/api-docs",
			"/swagger-resources",
			"/v3/api-docs.yaml",
			"/webjars"
	);

	private final JwtProvider jwtProvider;
	private final UserDetailsServiceImpl userDetailsService;
	private final RedisService redisService;
	private final ObjectMapper objectMapper;

	/**
	 * JWT 인증 필터의 주요 처리 로직을 수행합니다.
	 *
	 * @param req         현재 HTTP 요청
	 * @param res         HTTP 응답
	 * @param filterChain 필터 체인
	 * @throws ServletException 서블릿 처리 중 오류 발생 시
	 * @throws IOException      입출력 처리 중 오류 발생 시
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
			FilterChain filterChain) throws ServletException, IOException {
		try {
			if (isPermitAllRequest(req.getRequestURI())) {
				log.info("Permitting request to: {}", req.getRequestURI());
				filterChain.doFilter(req, res);
				return;
			}

			processTokenAuthentication(req, res, filterChain);

		} catch (InvalidTokenException e) {
			log.error("토큰 검증 실패: {}", e.getMessage());
			sendResponse(res, HttpStatus.UNAUTHORIZED, e.getMessageCode(), null);
		} catch (Exception e) {
			log.error("JWT 인증 처리 중 오류 발생", e);
			sendResponse(res, HttpStatus.INTERNAL_SERVER_ERROR, AUTHORIZED_ERROR, null);
		}
	}

	/**
	 * 토큰 기반 인증 처리를 수행합니다.
	 *
	 * @param req         HTTP 요청
	 * @param res         HTTP 응답
	 * @param filterChain 필터 체인
	 * @throws IOException      입출력 처리 중 오류 발생 시
	 * @throws ServletException 서블릿 처리 중 오류 발생 시
	 */
	private void processTokenAuthentication(HttpServletRequest req, HttpServletResponse res,
			FilterChain filterChain) throws IOException, ServletException {
		String token = resolveToken(req);
		validateTokenExists(token);

		String username = jwtProvider.getUsernameFromToken(token);
		validateRefreshTokenExists(username);

		if (jwtProvider.validateToken(token)) {
			log.info("액세스 토큰 검증 성공");
			setAuthentication(username);
			filterChain.doFilter(req, res);
		} else {
			log.info("액세스 토큰 만료. 리프레시 토큰으로 자동 재발급");
			String refreshToken = redisService.getRefreshToken(username);
			handleTokenReissue(username, refreshToken, res);
		}
	}

	/**
	 * HTTP 요청 헤더에서 JWT 토큰을 추출합니다.
	 *
	 * @param request HTTP 요청
	 * @return 추출된 JWT 토큰, 토큰이 없거나 유효하지 않은 형식인 경우 null
	 */
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		return null;
	}

	/**
	 * 요청 URI가 인증이 필요없는 경로인지 확인합니다.
	 *
	 * @param requestURI 검사할 요청 URI
	 * @return 인증이 필요없는 경로인 경우 true
	 */
	private boolean isPermitAllRequest(String requestURI) {
		return PERMIT_ALL_PATHS.stream()
				.anyMatch(requestURI::startsWith);
	}

	/**
	 * 토큰의 존재 여부를 검증합니다.
	 *
	 * @param token 검증할 토큰
	 * @throws InvalidTokenException 토큰이 null이거나 빈 문자열인 경우
	 */
	private void validateTokenExists(String token) {
		if (!StringUtils.hasText(token)) {
			throw new InvalidTokenException();
		}
	}

	/**
	 * 사용자의 리프레시 토큰 존재 여부를 검증합니다.
	 *
	 * @param username 검증할 사용자 이름
	 * @throws InvalidTokenException 리프레시 토큰이 존재하지 않는 경우
	 */
	private void validateRefreshTokenExists(String username) {
		String refreshToken = redisService.getRefreshToken(username);
		if (refreshToken == null) {
			throw new InvalidTokenException();
		}
	}

	/**
	 * 토큰 재발급 처리를 수행합니다.
	 *
	 * @param username     사용자 이름
	 * @param refreshToken 리프레시 토큰
	 * @param res          HTTP 응답
	 * @throws IOException 입출력 처리 중 오류 발생 시
	 */
	private void handleTokenReissue(String username, String refreshToken, HttpServletResponse res)
			throws IOException {
		if (!isValidRefreshToken(refreshToken)) {
			throw new InvalidTokenException();
		}

		TokenDto newTokens = generateNewTokens(username, refreshToken);
		sendResponse(res, HttpStatus.OK, GENERATE_TOKEN, newTokens);
	}

	/**
	 * 리프레시 토큰의 유효성을 검사합니다.
	 *
	 * @param refreshToken 검사할 리프레시 토큰
	 * @return 토큰이 유효한 경우 true
	 */
	private boolean isValidRefreshToken(String refreshToken) {
		return refreshToken != null && jwtProvider.validateToken(refreshToken);
	}

	/**
	 * 새로운 액세스 토큰과 리프레시 토큰을 생성합니다.
	 *
	 * @param username     사용자 이름
	 * @param refreshToken 현재 리프레시 토큰
	 * @return 새로 생성된 토큰 DTO
	 */
	private TokenDto generateNewTokens(String username, String refreshToken) {
		UserRole role = jwtProvider.getRoleFromToken(refreshToken);
		String newAccessToken = jwtProvider.createAccessToken(username, role);
		String newRefreshToken = jwtProvider.createRefreshToken(username, role);

		redisService.saveRefreshToken(username, newRefreshToken);
		return new TokenDto(newAccessToken);
	}

	/**
	 * Spring Security 인증 컨텍스트에 사용자 인증 정보를 설정합니다.
	 *
	 * @param username 인증할 사용자 이름
	 */
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

	/**
	 * HTTP 응답을 생성하고 전송합니다.
	 *
	 * @param res     HTTP 응답 객체
	 * @param status  HTTP 상태 코드
	 * @param message 응답 메시지 코드
	 * @param data    응답 데이터 (없는 경우 null)
	 * @throws IOException 입출력 처리 중 오류 발생 시
	 */
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