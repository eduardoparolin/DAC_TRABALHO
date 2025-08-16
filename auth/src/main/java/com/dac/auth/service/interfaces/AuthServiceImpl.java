package com.dac.auth.service.interfaces;

import com.dac.auth.dto.auth.AuthRequestDTO;
import com.dac.auth.dto.auth.AuthResponseDTO;
import com.dac.auth.enums.UserType;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService{

    @Override
    public AuthResponseDTO login(AuthRequestDTO dto) {
        return new AuthResponseDTO();
    }

    @Override
    public void logout() {
        return;
    }
}
