package com.dac.auth.consumer.utils.handlers.interfaces;

import com.dac.auth.dto.payload.IAuthPayload;

public interface MessageHandler<T extends IAuthPayload> {
    void handle(T data);
}
