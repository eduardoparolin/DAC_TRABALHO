package com.dac.auth.controller;

import com.dac.auth.dto.auth.AuthRequestDTO;
import com.dac.auth.dto.auth.AuthResponseDTO;
import com.dac.auth.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        service.logout(authorizationHeader);
        return ResponseEntity.ok().build();
    }

}
