package com.donut.swaipe.global.security.details;

import com.donut.swaipe.domain.user.entity.User;
import com.donut.swaipe.domain.user.repository.UserRepository;
import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.map(UserDetailsImpl::new)
				.orElseThrow(() -> new UserNotFoundException());
	}
}