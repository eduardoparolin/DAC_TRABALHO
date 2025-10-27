package com.bank.manager.config.consumer.handlers;

import com.bank.manager.config.consumer.ManagerSagaEvent;
import com.bank.manager.config.consumer.handlers.interfaces.ManagerMessageHandler;
import com.bank.manager.dto.ManagerDTO;
import com.bank.manager.service.ManagerService;
import com.bank.manager.config.producer.ManagerProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerHandler implements ManagerMessageHandler {

    private final ManagerService managerService;
    private final ManagerProducer managerProducer;
    private final ObjectMapper objectMapper;

    @Autowired
    public CreateManagerHandler(ManagerService managerService, ManagerProducer managerProducer, ObjectMapper objectMapper) {
        this.managerService = managerService;
        this.managerProducer = managerProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleMessage(ManagerSagaEvent event) {
        try {

            ManagerDTO dto = objectMapper.readValue(event.getPayload(), ManagerDTO.class);


            ManagerDTO createdManager = managerService.create(dto);


            ManagerSagaEvent responseEvent = new ManagerSagaEvent(
                    "MANAGER_CREATED", // Tipo de evento de resposta
                    objectMapper.writeValueAsString(createdManager) // JSON do resultado
            );

            managerProducer.sendCreatedEvent(responseEvent);


        } catch (Exception e) {
            System.err.println("Erro ao processar evento de criação de manager: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
