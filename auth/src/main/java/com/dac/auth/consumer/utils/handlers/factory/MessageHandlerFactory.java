package com.dac.auth.consumer.utils.handlers.factory;

import com.dac.auth.consumer.utils.handlers.MessageCreateHandler;
import com.dac.auth.consumer.utils.handlers.MessageDeleteHandler;
import com.dac.auth.consumer.utils.handlers.MessageUpdateHandler;
import com.dac.auth.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.AuthPayload;
import com.dac.auth.enums.Action;

public class MessageHandlerFactory {

    public static MessageHandler<AuthPayload> getStrategy(Action action) {
        return switch (action) {
            case CREATE -> new MessageCreateHandler<AuthPayload>();
            case UPDATE -> new MessageUpdateHandler<AuthPayload>();
            case DELETE -> new MessageDeleteHandler<AuthPayload>();
        };
    }
}
