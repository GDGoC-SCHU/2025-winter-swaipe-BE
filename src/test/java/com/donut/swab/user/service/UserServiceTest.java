package com.donut.swab.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.donut.swab.Auth.service.AuthService;
import com.donut.swab.security.details.UserDetailsImpl;
import com.donut.swab.security.jwt.JwtProvider;
import com.donut.swab.user.dto.AccessTokenDto;
import com.donut.swab.user.dto.LoginRequestDto;
import com.donut.swab.user.entity.User;
import com.donut.swab.user.enums.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisService redisService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDto loginRequestDto;
    private UserDetailsImpl userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("username");
        loginRequestDto.setPassword("password");
        userDetails = Mockito.mock(UserDetailsImpl.class);
        authentication = Mockito.mock(Authentication.class);
    }

    @Test
    void login_Success() {
        // Given
        User user = Mockito.mock(User.class);  // User 객체에 대한 Mock 설정 추가
        when(user.getUserRole()).thenReturn(UserRole.USER);  // UserRole 설정
        when(userDetails.getUser()).thenReturn(user);  // User 객체를 반환하도록 설정

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("username");
        when(jwtProvider.createAccessToken(anyString(), any(UserRole.class))).thenReturn(
                "access-token");
        when(jwtProvider.createRefreshToken(anyString(), any(UserRole.class))).thenReturn(
                "refresh-token");

        // When
        AccessTokenDto result = authService.login(loginRequestDto, response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("token");
        verify(redisService).saveRefreshToken("username", "refresh-token");
        verify(response).setHeader(JwtProvider.AUTHORIZATION_HEADER, "access-token");
    }
}
