package com.bank.manager.config.consumer.handlers.interfaces;

import com.bank.manager.config.consumer.ManagerSagaEvent;

public interface ManagerMessageHandler {
    void handleMessage(ManagerSagaEvent event);
}
// Isso garante que todos os handlers terão uma assinatura comum: recebem um evento e o processam.