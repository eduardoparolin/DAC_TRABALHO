package com.dac.auth.consumer.utils.handlers;

import com.dac.auth.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.IAuthPayload;

public class MessageCreateHandler<T extends IAuthPayload> implements MessageHandler<T> {
    @Override
    public void handle(T data) {
        System.out.println("Criar usuario com email: " + data.getEmail());
    }
}
