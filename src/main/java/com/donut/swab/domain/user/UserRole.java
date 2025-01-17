package com.donut.swab.domain.user;

public enum UserRole {
    USER("ROLE_USER"),
    MANAGER("ROLE_MANAGER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
} 