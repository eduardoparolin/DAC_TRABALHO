package com.dac.auth.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    CLIENT,
    MANAGER,
    ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
