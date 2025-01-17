package com.donut.swab.user.service;

import com.donut.swab.security.details.UserDetailsImpl;
import com.donut.swab.security.details.UserDetailsServiceImpl;
import com.donut.swab.user.dto.SignupResponseDto;
import com.donut.swab.user.dto.SignupUserDto;
import com.donut.swab.user.entity.User;
import com.donut.swab.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponseDto signup(SignupUserDto userDto) {
        User user = userDto.signup(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(user);
        return new SignupResponseDto(user, (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getUsername()));
    }
}
