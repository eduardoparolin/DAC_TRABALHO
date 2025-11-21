package com.dac.auth.controller;

import com.dac.auth.dto.auth.AuthRequestDTO;
import com.dac.auth.dto.auth.AuthResponseDTO;
import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.dto.user.UserUpdateDTO;
import com.dac.auth.service.interfaces.AuthService;
import com.dac.auth.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;
    private final UserService userService;

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

    @PutMapping("/user/{cpf}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String cpf, @RequestBody UserUpdateDTO dto) {
        UserDTO updatedUser = userService.update(dto, cpf);
        return ResponseEntity.ok(updatedUser);
    }

}
