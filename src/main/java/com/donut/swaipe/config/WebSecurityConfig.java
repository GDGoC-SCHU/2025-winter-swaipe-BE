package com.donut.swaipe.config;

import com.donut.swaipe.domain.user.service.RedisService;
import com.donut.swaipe.global.security.details.UserDetailsServiceImpl;
import com.donut.swaipe.global.security.filter.JwtAuthenticationFilter;
import com.donut.swaipe.global.security.filter.JwtAuthorizationFilter;
import com.donut.swaipe.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * Spring Security 설정을 담당하는 설정 클래스입니다. JWT 인증, CORS, CSRF 및 기타 보안 설정을 관리합니다.
 *
 * @author donut
 * @version 1.1
 * @since 2024-01-28
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final List<String> SWAGGER_PATHS = List.of(
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/v3/api-docs.yaml",
			"/swagger-resources/**",
			"/swagger-ui.html",
			"/webjars/**"
	);

	private static final List<String> PERMIT_ALL_PATHS = List.of(
			"/users",
			"/users/login",
			"/users/refresh",
			"/swagger-ui",
			"/v3/api-docs",
			"/swagger-resources",
			"/v3/api-docs.yaml",
			"/webjars",
			"/test/**",
			"/templates/**",
			"/api/v1/notifications"
	);

	private final JwtProvider jwtProvider;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final RedisService redisService;
	private final ObjectMapper objectMapper;

	/**
	 * 비밀번호 암호화를 위한 인코더를 설정합니다.
	 *
	 * @return BCrypt 암호화 알고리즘을 사용하는 PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 인증 관리자를 설정합니다.
	 *
	 * @param configuration 인증 설정
	 * @return AuthenticationManager 인스턴스
	 * @throws Exception 인증 관리자 생성 실패 시
	 */
	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration configuration)
			throws Exception {
		return configuration.getAuthenticationManager();
	}

	/**
	 * JWT 인증 필터를 설정합니다.
	 *
	 * @return 설정된 JwtAuthenticationFilter
	 * @throws Exception 필터 설정 실패 시
	 */
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, redisService);
		filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		return filter;
	}

	/**
	 * JWT 인가 필터를 설정합니다.
	 *
	 * @return 설정된 JwtAuthorizationFilter
	 */
	@Bean
	public JwtAuthorizationFilter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(
				jwtProvider,
				userDetailsService,
				redisService,
				objectMapper
		);
	}

	/**
	 * Spring Security 필터 체인을 구성합니다.
	 *
	 * @param http HttpSecurity 설정 객체
	 * @return 구성된 SecurityFilterChain
	 * @throws Exception 보안 설정 실패 시
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		configureBasicSecurity(http);
		configureAuthorization(http);
		configureFilters(http);

		return http.build();
	}

	/**
	 * 기본 보안 설정을 구성합니다.
	 *
	 * @param http HttpSecurity 설정 객체
	 */
	private void configureBasicSecurity(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(sessionManagement ->
						sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				);
	}

	/**
	 * 인가 규칙을 구성합니다.
	 *
	 * @param http HttpSecurity 설정 객체
	 */
	private void configureAuthorization(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
				.permitAll()
				.requestMatchers(SWAGGER_PATHS.toArray(String[]::new)).permitAll()
				.requestMatchers(HttpMethod.GET, "/test/**", "/templates/**", "/favicon.ico").permitAll()
				.requestMatchers(HttpMethod.POST, PERMIT_ALL_PATHS.toArray(String[]::new))
				.permitAll()
				.anyRequest().authenticated()
		);
	}

	/**
	 * 필터 체인을 구성합니다.
	 *
	 * @param http HttpSecurity 설정 객체
	 */
	private void configureFilters(HttpSecurity http) throws Exception {
		http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class)
				.addFilterBefore(jwtAuthenticationFilter(),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(contentCachingFilter(), JwtAuthorizationFilter.class);
	}

	/**
	 * CORS 설정을 구성합니다.
	 *
	 * @return 구성된 CorsConfigurationSource
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of(AUTHORIZATION_HEADER));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/**
	 * 요청 내용을 캐싱하기 위한 필터를 구성합니다.
	 *
	 * @return 구성된 ContentCachingFilter
	 */
	@Bean
	public OncePerRequestFilter contentCachingFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request,
					HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
				ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(
						request);
				filterChain.doFilter(wrappingRequest, response);
			}
		};
	}
}