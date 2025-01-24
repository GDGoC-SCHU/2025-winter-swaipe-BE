package com.donut.swaipe.domain.user.enums;

import lombok.Getter;

@Getter
public enum UserRole{
    USER("ROLE_USER"),
    MANAGER("ROLE_MANAGER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }
}
