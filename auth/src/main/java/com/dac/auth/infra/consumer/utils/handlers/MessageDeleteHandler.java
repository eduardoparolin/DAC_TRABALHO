package com.dac.auth.infra.consumer.utils.handlers;

import com.dac.auth.exception.custom.ApiException;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.IAuthPayload;
import com.dac.auth.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageDeleteHandler<T extends IAuthPayload> implements MessageHandler<T> {

    private final UserService service;

    @Override
    public void handle(T data) {
        service.delete(data.getCpf(), data.getRequestedById());
    }
}
