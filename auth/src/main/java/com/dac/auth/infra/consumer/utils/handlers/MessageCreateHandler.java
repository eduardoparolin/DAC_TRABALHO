package com.dac.auth.infra.consumer.utils.handlers;

import com.dac.auth.dto.user.UserCreateDTO;
import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.exception.custom.ApiException;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.IAuthPayload;
import com.dac.auth.model.User;
import com.dac.auth.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageCreateHandler<T extends IAuthPayload> implements MessageHandler<T> {

    private final UserService service;

    @Override
    public UserDTO handle(T data) {
        UserCreateDTO dto = new UserCreateDTO(
            data.getCpf(),
            data.getEmail(),
            data.getRole(),
            data.getName(),
            data.getId()
        );

        return service.save(dto);
    }
}
