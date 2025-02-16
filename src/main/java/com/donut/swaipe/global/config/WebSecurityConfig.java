package com.donut.swaipe.global.config;

import com.donut.swaipe.global.security.details.KakaoUserDetailsService;
import com.donut.swaipe.global.security.filter.KakaoAuthorizationFilter;
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import com.donut.swaipe.domain.kakao.redis.KakaoTokenRedisService;

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
			"/api/auth/kakao/**",
			"/favicon.ico"
	);

	private final JwtProvider jwtProvider;
	private final KakaoUserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;
	private final KakaoTokenRedisService tokenRedisService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
						.permitAll()
						.requestMatchers(
								"/api/auth/kakao/**",
								"/favicon.ico",
								"/error",
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/swagger-resources/**",
								"/webjars/**"
						).permitAll()
						.anyRequest().authenticated()
				)
				.addFilterAfter(kakaoAuthorizationFilter(),
						UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

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

	@Bean
	public KakaoAuthorizationFilter kakaoAuthorizationFilter() {
		return new KakaoAuthorizationFilter(
				jwtProvider,
				userDetailsService,
				objectMapper,
				tokenRedisService
		);
	}
}