package com.bank.manager.config.producer;

import com.bank.manager.config.consumer.ManagerSagaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ManagerProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${manager.events.exchange}")
    private String exchange;

    @Value("${manager.events.rk.created}")
    private String createdRoutingKey;

    @Value("${manager.events.rk.updated}")
    private String updatedRoutingKey;

    @Value("${manager.events.rk.deleted}")
    private String deletedRoutingKey;

    public ManagerProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendCreatedEvent(ManagerSagaEvent event) {
        send(event, createdRoutingKey);
    }

    public void sendUpdatedEvent(ManagerSagaEvent event) {
        send(event, updatedRoutingKey);
    }

    public void sendDeletedEvent(ManagerSagaEvent event) {
        send(event, deletedRoutingKey);
    }

    private void send(ManagerSagaEvent event, String routingKey) {
        try {
            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem para RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
