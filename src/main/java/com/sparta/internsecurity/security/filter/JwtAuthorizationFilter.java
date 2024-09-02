package com.sparta.internsecurity.security.filter;


import static com.sparta.internsecurity.security.jwt.JwtProvider.AUTHORIZATION_HEADER;
import static org.springframework.http.HttpStatus.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.internsecurity.security.details.UserDetailsServiceImpl;
import com.sparta.internsecurity.security.jwt.JwtProvider;
import com.sparta.internsecurity.user.enums.UserRole;
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

        if (req.getRequestURI().equals("/users/login")) {
            filterChain.doFilter(req, res);
            return;
        }

        String accessToken = jwtProvider.getAccessTokenFromHeader(req);
        String username = jwtProvider.getUsernameFromToken(accessToken);

        if (StringUtils.hasText(accessToken)) {
            if (jwtProvider.validateAccessToken(accessToken)) {
                log.info("액세스 토큰 검증 성공");
                updateToken(accessToken, username, res);
            } else if (jwtProvider.hasRefreshToken(username)) {
                String refreshToken = jwtProvider.substringToken(
                        redisTemplate.opsForValue().get(username));
                updateToken(refreshToken, username, res);
                log.info("토큰 Refresh 성공");
            } else {
                jwtExceptionHandler(res, UNAUTHORIZED);
                return;
            }
        } else {
            log.error("토큰 없음");
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

    private void jwtExceptionHandler(HttpServletResponse res, HttpStatus status) {
        int statusCode = status.value();
        res.setStatus(statusCode);
        res.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString("invalid token error");
            res.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void updateToken(String token, String username, HttpServletResponse res)
            throws IOException {
        UserRole role = jwtProvider.getRoleFromToken(token);
        String newAccessToken = jwtProvider.createAccessToken(username, role);

        res.setHeader(AUTHORIZATION_HEADER, newAccessToken);
        setAuthentication(jwtProvider.getUsernameFromToken(newAccessToken));

        String jsonResponse = new ObjectMapper().writeValueAsString(
                "new token generated : " + newAccessToken);
    }
}
