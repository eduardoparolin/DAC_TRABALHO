package com.bank.manager.config.consumer;

import com.bank.manager.config.ManagerSagaEvent;
import com.bank.manager.config.consumer.factory.MessageHandlerFactory;
import com.bank.manager.config.consumer.handlers.interfaces.ManagerMessageHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
;

@Component
public class ManagerConsumer {

    private final ObjectMapper objectMapper;
    private final MessageHandlerFactory handlerFactory;

    public ManagerConsumer(ObjectMapper objectMapper, MessageHandlerFactory handlerFactory) {
        this.objectMapper = objectMapper;
        this.handlerFactory = handlerFactory;
    }

    @RabbitListener(queues = "${manager.events.queue}")
    public void handleEvent(String message) {
        try {

            ManagerSagaEvent event = objectMapper.readValue(message, ManagerSagaEvent.class);

            ManagerMessageHandler handler = handlerFactory.getHandler(event.getEventType());

            if (handler != null) {
                handler.handleMessage(event);
            } else {
                System.err.println("Nenhum handler encontrado para o tipo de evento: " + event.getEventType());
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem recebida do RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
