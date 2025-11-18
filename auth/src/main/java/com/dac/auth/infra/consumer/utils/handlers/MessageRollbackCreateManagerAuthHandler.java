package com.dac.auth.infra.consumer.utils.handlers;

import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.IAuthPayload;
import com.dac.auth.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRollbackCreateManagerAuthHandler<T extends IAuthPayload> implements MessageHandler<T> {

    private final UserRepository repository;

    @Override
    public UserDTO handle(T data) {
        log.info("Rolling back manager auth creation for user ID: {}", data.getId());

        try {
            // Delete the auth user that was created (direct repository delete for compensation)
            repository.deleteById(String.valueOf(data.getId()));
            log.info("Successfully rolled back manager auth for user ID: {}", data.getId());
        } catch (Exception e) {
            log.error("Error rolling back manager auth for user ID: {}", data.getId(), e);
            // Don't throw - compensation should be best effort
        }

        return null; // Compensation doesn't need to return data
    }
}
