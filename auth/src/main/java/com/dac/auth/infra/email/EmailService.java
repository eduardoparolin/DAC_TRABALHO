package com.dac.auth.infra.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordEmail(String to, String plainPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Sua nova senha de acesso");
        message.setText("""
                Olá,

                Sua nova senha temporária é: %s

                Recomendamos que você altere a senha assim que fizer login.
                
                Atenciosamente,
                Equipe DAC
                """.formatted(plainPassword));

        mailSender.send(message);
    }
}
