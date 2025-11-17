package com.bank.client.infra.consumer.factory;

import com.bank.client.enums.ClientAction;
import com.bank.client.infra.consumer.handler.handlers.ApproveClientHandler;
import com.bank.client.infra.consumer.handler.handlers.CreateClientHandler;
import com.bank.client.infra.consumer.handler.handlers.DeleteClientHandler;
import com.bank.client.infra.consumer.handler.handlers.RejectClientHandler;
import com.bank.client.infra.consumer.handler.handlers.UpdateClientHandler;
import com.bank.client.infra.consumer.handler.handlers.UpdateClientManagerHandler;
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
            UpdateClientManagerHandler updateClientManagerHandler,
            ApproveClientHandler approveClientHandler,
            RejectClientHandler rejectClientHandler,
            DeleteClientHandler deleteClientHandler
    ) {
        strategies.put(ClientAction.CREATE_CLIENT, createClientHandler);
        strategies.put(ClientAction.UPDATE_CLIENT, updateClientHandler);
        strategies.put(ClientAction.UPDATE_CLIENT_MANAGER, updateClientManagerHandler);
        strategies.put(ClientAction.APPROVE_CLIENT, approveClientHandler);
        strategies.put(ClientAction.REJECT_CLIENT, rejectClientHandler);
        strategies.put(ClientAction.DELETE_CLIENT, deleteClientHandler);
    }

    public ClientMessageHandler getStrategy(ClientAction action) {
        return strategies.getOrDefault(action,
                data -> {throw new IllegalArgumentException("No handler found for action: " + action);});
    }
}
