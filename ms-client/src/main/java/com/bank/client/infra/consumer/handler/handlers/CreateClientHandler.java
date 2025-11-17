package com.bank.client.infra.consumer.handler.handlers;

import com.bank.client.dto.ClientCreateDTO;
import com.bank.client.dto.ClientRequest;
import com.bank.client.infra.consumer.handler.interfaces.ClientMessageHandler;
import com.bank.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateClientHandler implements ClientMessageHandler {

    private final ClientService clientService;

    @Override
    public void handle(ClientRequest event) {
        ClientCreateDTO dto = new ClientCreateDTO(
                event.getName(),
                event.getEmail(),
                event.getCpf(),
                event.getPhone(),
                event.getSalary(),
                event.getStreet(),
                event.getComplement(),
                event.getZipCode(),
                event.getCity(),
                event.getState()
        );
        Long clientId = clientService.create(dto);
        event.setClientId(clientId);
    }
}
