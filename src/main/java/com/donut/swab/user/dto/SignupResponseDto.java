package com.donut.swab.user.dto;

import com.donut.swab.security.details.UserDetailsImpl;
import com.donut.swab.user.entity.User;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
public class SignupResponseDto {

    private String username;
    private String nickname;
    private Collection<? extends GrantedAuthority> authorities;

    public SignupResponseDto(User user, UserDetailsImpl userDetails) {
        this.username = userDetails.getUsername();
        this.nickname = user.getNickname();
        this.authorities = userDetails.getAuthorities();
    }
}
