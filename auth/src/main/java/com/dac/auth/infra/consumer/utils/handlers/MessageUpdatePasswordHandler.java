package com.dac.auth.infra.consumer.utils.handlers;

import com.dac.auth.dto.payload.IAuthPayload;
import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageUpdatePasswordHandler<T extends IAuthPayload> implements MessageHandler<T> {

    private final UserService service;

    @Override
    public UserDTO handle(T data) {
        log.info("Handling UPDATE_PASSWORD for CPF: {}", data.getCpf());
        // The newPassword parameter is not used - updatePassword generates a new random password
        return service.updatePassword(data.getCpf(), null);
    }
}
