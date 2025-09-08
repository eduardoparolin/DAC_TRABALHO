package com.dac.auth.service;

import com.dac.auth.dto.auth.AuthRequestDTO;
import com.dac.auth.dto.auth.AuthResponseDTO;
import com.dac.auth.model.User;
import com.dac.auth.infra.configuration.security.TokenService;
import com.dac.auth.service.interfaces.AuthService;
import com.dac.auth.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public AuthResponseDTO login(AuthRequestDTO dto) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);
        String token = tokenService.generateToken((User) auth.getPrincipal());
        User user = userService.findByEmail(dto.getEmail());

        return new AuthResponseDTO(
                token,
                "Bearer",
                user.getRole()
        );
    }

    @Override
    public void logout() {
        return;
    }
}
