package com.donut.swab.domain.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {
    private final Long id;
    private final String username;
    private final String password;
    private final String nickname;
    private final UserRole userRole;

    @Builder
    public User(Long id, String username, String password, String nickname, UserRole userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole != null ? userRole : UserRole.USER;
    }
} 