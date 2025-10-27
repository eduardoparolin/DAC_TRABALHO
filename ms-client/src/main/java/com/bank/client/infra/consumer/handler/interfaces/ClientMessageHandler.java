package com.bank.client.infra.consumer.handler.interfaces;

import com.bank.client.dto.ClientRequest;

public interface ClientMessageHandler {
    void handle(ClientRequest event);
}
