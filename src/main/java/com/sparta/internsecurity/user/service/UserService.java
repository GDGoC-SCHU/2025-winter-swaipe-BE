package com.sparta.internsecurity.user.service;

import com.sparta.internsecurity.security.details.UserDetailsImpl;
import com.sparta.internsecurity.security.details.UserDetailsServiceImpl;
import com.sparta.internsecurity.user.dto.SignupResponseDto;
import com.sparta.internsecurity.user.dto.SignupUserDto;
import com.sparta.internsecurity.user.entity.User;
import com.sparta.internsecurity.user.repository.UserRepositroy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositroy userRepositroy;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponseDto signup(SignupUserDto userDto) {
        User user = userDto.signup(passwordEncoder.encode(userDto.getPassword()));

        userRepositroy.save(user);
        return new SignupResponseDto(user, (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getUsername()));
    }
}
