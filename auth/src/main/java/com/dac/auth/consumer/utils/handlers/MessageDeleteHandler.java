package com.dac.auth.consumer.utils.handlers;

import com.dac.auth.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.IAuthPayload;

public class MessageDeleteHandler<T extends IAuthPayload> implements MessageHandler<T> {
    @Override
    public void handle(T data) {
        System.out.println("Deletar usuario com email e id: " + data.getEmail() + " | " + data.getIdUser());
    }
}
