package com.dac.auth.infra.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        PasswordEncoder pe = new BCryptPasswordEncoder();
        String password = "tads";
        String encrypted = pe.encode(password);
        System.out.println(encrypted);
    }
}
