package com.dac.auth.infra.consumer.utils.handlers;

import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.IAuthPayload;
import com.dac.auth.model.User;
import com.dac.auth.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageUpdateHandler<T extends IAuthPayload> implements MessageHandler<T> {

    private final UserService service;

    @Override
    public void handle(T data) {
        User user = new User(
                data.getIdUser(),
                data.getEmail(),
                data.getPassword(),
                data.getRole()
        );

        service.update(user, data.getIdUser());
    }
}
