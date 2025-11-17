package com.dac.auth.infra.consumer.utils.factory;

import com.dac.auth.exception.custom.ApiException;
import com.dac.auth.infra.consumer.utils.handlers.MessageCreateHandler;
import com.dac.auth.infra.consumer.utils.handlers.MessageDeleteHandler;
import com.dac.auth.infra.consumer.utils.handlers.MessageUpdateHandler;
import com.dac.auth.infra.consumer.utils.handlers.MessageUpdatePasswordHandler;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.AuthPayload;
import com.dac.auth.enums.Action;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageHandlerFactory {

    private final Map<Action, MessageHandler<AuthPayload>> strategies = new HashMap<>();

    public MessageHandlerFactory(
            MessageCreateHandler<AuthPayload> createHandler,
            MessageUpdateHandler<AuthPayload> updateHandler,
            MessageDeleteHandler<AuthPayload> deleteHandler,
            MessageUpdatePasswordHandler<AuthPayload> updatePasswordHandler
    ) {
        strategies.put(Action.CREATE, createHandler);
        strategies.put(Action.UPDATE, updateHandler);
        strategies.put(Action.DELETE, deleteHandler);
        strategies.put(Action.UPDATE_PASSWORD, updatePasswordHandler);
    }

    public MessageHandler<AuthPayload> getStrategy(Action action) {
        return strategies.getOrDefault(action,
                data -> { throw new ApiException("Ação não suportada: " + action, HttpStatus.BAD_REQUEST); });
    }
}


