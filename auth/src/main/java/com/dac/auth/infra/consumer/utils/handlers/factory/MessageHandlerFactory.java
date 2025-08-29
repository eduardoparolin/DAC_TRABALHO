package com.dac.auth.infra.consumer.utils.handlers.factory;

import com.dac.auth.infra.consumer.utils.handlers.MessageCreateHandler;
import com.dac.auth.infra.consumer.utils.handlers.MessageDeleteHandler;
import com.dac.auth.infra.consumer.utils.handlers.MessageUpdateHandler;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.AuthPayload;
import com.dac.auth.enums.Action;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageHandlerFactory {

    private final MessageCreateHandler<AuthPayload> createHandler;
    private final MessageUpdateHandler<AuthPayload> updateHandler;
    private final MessageDeleteHandler<AuthPayload> deleteHandler;

    public MessageHandler<AuthPayload> getStrategy(Action action) {
        return switch (action) {
            case CREATE -> createHandler;
            case UPDATE -> updateHandler;
            case DELETE -> deleteHandler;
        };
    }
}

