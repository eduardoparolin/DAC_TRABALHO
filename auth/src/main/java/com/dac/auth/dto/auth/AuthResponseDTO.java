package com.dac.auth.dto.auth;

import com.dac.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType;
    private Role tipo;
}
