package com.bank.manager.config.consumer.factory;

import com.bank.manager.config.consumer.handlers.interfaces.ManagerMessageHandler;
import com.bank.manager.config.consumer.handlers.CreateManagerHandler;
import com.bank.manager.config.consumer.handlers.UpdateManagerHandler;
import com.bank.manager.config.consumer.handlers.DeleteManagerHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageHandlerFactory {

    private final Map<String, ManagerMessageHandler> handlerMap;

    public MessageHandlerFactory(
            CreateManagerHandler createHandler,
            UpdateManagerHandler updateHandler,
            DeleteManagerHandler deleteHandler
    ) {
        handlerMap = Map.of(
                "CREATE", createHandler,
                "UPDATE", updateHandler,
                "DELETE_MANAGER_MS", deleteHandler
        );
    }

    public ManagerMessageHandler getHandler(String eventType) {
        return handlerMap.get(eventType);
    }
}
