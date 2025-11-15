package com.bank.manager.config.consumer.handlers;

import com.bank.manager.config.ManagerSagaEvent;
import com.bank.manager.config.consumer.handlers.interfaces.ManagerMessageHandler;
import com.bank.manager.service.ManagerService;
import com.bank.manager.config.producer.ManagerProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerHandler implements ManagerMessageHandler {

    private final ManagerService managerService;
    private final ManagerProducer managerProducer;
    private final ObjectMapper objectMapper;

    @Autowired
    public DeleteManagerHandler(ManagerService managerService, ManagerProducer managerProducer, ObjectMapper objectMapper) {
        this.managerService = managerService;
        this.managerProducer = managerProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleMessage(ManagerSagaEvent event) {
        try {
            var node = objectMapper.readTree(event.getPayload());
            String cpf = node.get("cpf").asText();

            managerService.deleteByCpf(cpf);

            ManagerSagaEvent responseEvent = new ManagerSagaEvent(
                    "MANAGER_DELETED",
                    "{\"cpf\":\"" + cpf + "\"}"
            );

            managerProducer.sendDeletedEvent(responseEvent);

        } catch (Exception e) {
            System.err.println("Erro ao processar evento de delete de manager: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
