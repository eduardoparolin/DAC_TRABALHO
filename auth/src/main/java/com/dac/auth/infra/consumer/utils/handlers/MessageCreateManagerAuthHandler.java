package com.dac.auth.infra.consumer.utils.handlers;

import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.exception.custom.ApiException;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.IAuthPayload;
import com.dac.auth.infra.repository.UserRepository;
import com.dac.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MessageCreateManagerAuthHandler<T extends IAuthPayload> implements MessageHandler<T> {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO handle(T data) {
        // Check if user already exists
        User existingUser = repository.findByCpf(data.getCpf()).orElse(null);
        if (Objects.nonNull(existingUser)) {
            throw new ApiException("Usuario com CPF já existente.", HttpStatus.BAD_REQUEST);
        }

        existingUser = repository.findByEmail(data.getEmail()).orElse(null);
        if (Objects.nonNull(existingUser)) {
            throw new ApiException("Email em uso.", HttpStatus.BAD_REQUEST);
        }

        // Create manager auth user with provided password (not generated)
        String encodedPassword = passwordEncoder.encode(data.getPassword());

        User createUser = User.builder()
                .cpf(data.getCpf())
                .email(data.getEmail())
                .role(data.getRole()) // "GERENTE"
                .password(encodedPassword)
                .name(data.getName())
                .build();

        createUser.setUserId(data.getId());

        return UserDTO.fromEntity(repository.save(createUser));
    }
}
