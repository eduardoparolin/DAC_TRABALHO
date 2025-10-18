package com.bank.client.infra.consumer.factory;

import com.bank.client.enums.ClientAction;
import com.bank.client.infra.consumer.handler.handlers.ApproveClientHandler;
import com.bank.client.infra.consumer.handler.handlers.CreateClientHandler;
import com.bank.client.infra.consumer.handler.handlers.RejectClientHandler;
import com.bank.client.infra.consumer.handler.handlers.UpdateClientHandler;
import com.bank.client.infra.consumer.handler.interfaces.ClientMessageHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClientMessageHandleFactory {

    private final Map<ClientAction, ClientMessageHandler> strategies = new HashMap<>();

    public ClientMessageHandleFactory(
            CreateClientHandler createClientHandler,
            UpdateClientHandler updateClientHandler,
            ApproveClientHandler approveClientHandler,
            RejectClientHandler rejectClientHandler
    ) {
        strategies.put(ClientAction.CREATE_CLIENT, createClientHandler);
        strategies.put(ClientAction.UPDATE_CLIENT, updateClientHandler);
        strategies.put(ClientAction.APPROVE_CLIENT, approveClientHandler);
        strategies.put(ClientAction.REJECT_CLIENT, rejectClientHandler);
    }

    public ClientMessageHandler getStrategy(ClientAction action) {
        return strategies.getOrDefault(action,
                data -> {throw new IllegalArgumentException("No handler found for action: " + action);});
    }
}
