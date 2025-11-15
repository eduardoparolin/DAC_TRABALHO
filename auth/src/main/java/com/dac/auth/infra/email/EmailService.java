package com.dac.auth.infra.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordEmail(String name, String to, String plainPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Sua nova senha de acesso");
        message.setText("""
                Olá, %s

                Sua senha é: %s
                
                Atenciosamente,
                Equipe DAC
                """.formatted(name, plainPassword));

        mailSender.send(message);
    }
}
