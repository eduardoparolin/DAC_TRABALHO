package com.dac.auth.controller;

import com.dac.auth.dto.auth.AuthRequestDTO;
import com.dac.auth.dto.auth.AuthResponseDTO;
import com.dac.auth.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
        return ResponseEntity.ok(service.login(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        service.logout(authorizationHeader);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/status")
    public ResponseEntity<Map<String, Boolean>> tokenStatus(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        boolean revoked = service.isTokenRevoked(authorizationHeader);

        return ResponseEntity.ok(Map.of("revoked", revoked));
    }

}
