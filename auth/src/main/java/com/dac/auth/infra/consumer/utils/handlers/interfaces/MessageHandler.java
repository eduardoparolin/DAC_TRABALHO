package com.dac.auth.infra.consumer.utils.handlers.interfaces;

import com.dac.auth.dto.payload.IAuthPayload;

public interface MessageHandler<T extends IAuthPayload> {
    Object handle(T data);
}
