package com.dac.auth.service.interfaces;

import com.dac.auth.dto.auth.AuthRequestDTO;
import com.dac.auth.dto.auth.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO dto);
    void logout();
}
