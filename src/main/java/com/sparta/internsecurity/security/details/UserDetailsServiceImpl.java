package com.sparta.internsecurity.security.details;

import com.sparta.internsecurity.user.entity.User;
import com.sparta.internsecurity.user.repository.UserRepositroy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepositroy userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("Username is not founded")
        );
        return new UserDetailsImpl(user);
    }
}