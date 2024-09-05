package com.sparta.internsecurity.Auth.service;

import com.sparta.internsecurity.security.details.UserDetailsImpl;
import com.sparta.internsecurity.security.jwt.JwtProvider;
import com.sparta.internsecurity.user.dto.AccessTokenDto;
import com.sparta.internsecurity.user.dto.LoginRequestDto;
import com.sparta.internsecurity.user.enums.UserRole;
import com.sparta.internsecurity.user.service.RedisService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    public AccessTokenDto login(LoginRequestDto loginRequestDto, HttpServletResponse res) {
        try {
            // 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername();
            UserRole role = userDetails.getUser().getUserRole();

            // JWT 생성
            String accessToken = jwtProvider.createAccessToken(username, role);
            String refreshToken = jwtProvider.createRefreshToken(username, role);

            // Redis에 RefreshToken 저장
            redisService.saveRefreshToken(username, refreshToken);

            // 응답 헤더에 JWT 추가
            res.setHeader(JwtProvider.AUTHORIZATION_HEADER, accessToken);

            return new AccessTokenDto(accessToken);
        } catch (AuthenticationException e) {
            // 인증 실패 시 처리 로직
            handleAuthenticationFailure(res, e);
            return null; // 인증 실패 시에는 null을 반환하거나 예외를 던질 수 있음
        }
    }

    private void handleAuthenticationFailure(HttpServletResponse res, AuthenticationException e) {
        try {
            if (e instanceof BadCredentialsException) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter()
                        .print("{\"error\":\"Unauthorized\", \"message\":\"Invalid username or password\"}");
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().print("{\"error\":\"Unauthorized\", \"message\":\"" + e.getMessage()
                        + "\"}");
            }
        } catch (IOException ex) {
            log.error("Failed to write response for authentication failure: {}", ex.getMessage(),
                    ex);  // 예외 로그 추가
        }
    }
}
