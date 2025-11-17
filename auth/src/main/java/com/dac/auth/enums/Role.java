package com.dac.auth.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    CLIENTE,
    GERENTE,
    ADMINISTRADOR;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
