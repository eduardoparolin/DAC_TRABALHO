package com.bank.client.infra.consumer.handler.handlers;

import com.bank.client.dto.ClientRequest;
import com.bank.client.infra.consumer.handler.interfaces.ClientMessageHandler;
import com.bank.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateClientManagerHandler implements ClientMessageHandler {

    private final ClientService clientService;

    @Override
    public void handle(ClientRequest event) {
        Long clientId = event.getClientId();
        Long managerId = event.getManagerId();
        clientService.setManagerId(clientId, managerId);
    }
}
