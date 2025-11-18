package com.bank.client.infra.consumer.handler.handlers;

import com.bank.client.dto.ClientLinkAccountDTO;
import com.bank.client.dto.ClientRequest;
import com.bank.client.infra.consumer.handler.interfaces.ClientMessageHandler;
import com.bank.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkAccountHandler implements ClientMessageHandler {

    private final ClientService clientService;

    @Override
    public void handle(ClientRequest event) {
        ClientLinkAccountDTO dto = new ClientLinkAccountDTO(
                event.getClientId(),
                event.getAccountNumber()
        );
        clientService.linkAccount(dto);
    }
}
