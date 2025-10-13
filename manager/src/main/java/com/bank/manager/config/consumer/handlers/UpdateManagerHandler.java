package com.bank.manager.config.consumer.handlers;

import com.bank.manager.config.consumer.ManagerSagaEvent;
import com.bank.manager.config.consumer.handlers.interfaces.ManagerMessageHandler;
import com.bank.manager.dto.ManagerUpdateDTO;
import com.bank.manager.dto.ManagerDTO;
import com.bank.manager.service.ManagerService;
import com.bank.manager.config.producer.ManagerProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerHandler implements ManagerMessageHandler {

    private final ManagerService managerService;
    private final ManagerProducer managerProducer;
    private final ObjectMapper objectMapper;

    @Autowired
    public UpdateManagerHandler(ManagerService managerService, ManagerProducer managerProducer, ObjectMapper objectMapper) {
        this.managerService = managerService;
        this.managerProducer = managerProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleMessage(ManagerSagaEvent event) {
        try {
            var node = objectMapper.readTree(event.getPayload());
            String cpf = node.get("cpf").asText();
            ManagerUpdateDTO updateDTO = objectMapper.treeToValue(node.get("update"), ManagerUpdateDTO.class);

            ManagerDTO updatedManager = managerService.update(cpf, updateDTO);

            ManagerSagaEvent responseEvent = new ManagerSagaEvent(
                    "MANAGER_UPDATED",
                    objectMapper.writeValueAsString(updatedManager)
            );

            managerProducer.sendUpdatedEvent(responseEvent);

        } catch (Exception e) {
            System.err.println("Erro ao processar evento de update de manager: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
