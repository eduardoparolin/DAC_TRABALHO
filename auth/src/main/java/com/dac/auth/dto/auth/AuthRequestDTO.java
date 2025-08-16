package com.dac.auth.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthRequestDTO {
    private String email;
    private String password;
}
