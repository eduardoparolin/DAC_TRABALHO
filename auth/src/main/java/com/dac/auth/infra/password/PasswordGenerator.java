package com.dac.auth.infra.password;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordGenerator {

    private final PasswordEncoder passwordEncoder;

    public PasswordData generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder raw = new StringBuilder();
        int length = 8;

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            raw.append(chars.charAt(index));
        }

        String encoded = passwordEncoder.encode(raw.toString());
        return new PasswordData(raw.toString(), encoded);
    }
}
