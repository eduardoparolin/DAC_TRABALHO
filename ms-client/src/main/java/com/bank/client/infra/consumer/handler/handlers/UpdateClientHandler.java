package com.bank.client.infra.consumer.handler.handlers;

import com.bank.client.dto.ClientRequest;
import com.bank.client.dto.ClientUpdateDTO;
import com.bank.client.infra.consumer.handler.interfaces.ClientMessageHandler;
import com.bank.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateClientHandler implements ClientMessageHandler {

    private final ClientService clientService;

    @Override
    public void handle(ClientRequest event) {
        ClientUpdateDTO dto = new ClientUpdateDTO(
                event.getClientId(),
                event.getName(),
                event.getEmail(),
                event.getPhone(),
                event.getSalary(),
                event.getStreet(),
                event.getNumber(),
                event.getComplement(),
                event.getZipCode(),
                event.getCity(),
                event.getState()
        );
        clientService.update(dto);
    }
}
