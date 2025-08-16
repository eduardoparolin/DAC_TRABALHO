package com.dac.auth.dto.auth;

import com.dac.auth.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType;
    private UserType tipo;
    private String usuario;
}
