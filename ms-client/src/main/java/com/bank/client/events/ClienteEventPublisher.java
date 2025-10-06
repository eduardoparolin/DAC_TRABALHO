package com.bank.client.events;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(value = "msclient.events.enabled", havingValue = "true")
public class ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange exchange;
    private final String rkCreated;
    private final String rkUpdated;


    public ClienteEventPublisher(RabbitTemplate rabbitTemplate,
                                 TopicExchange exchange,
                                 org.springframework.core.env.Environment env) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.rkCreated = env.getProperty("msclient.events.rk.created", "cliente.created");
        this.rkUpdated = env.getProperty("msclient.events.rk.updated", "cliente.updated");
    }

    public void publishCreated(ClienteCreatedEvent evt) {
        rabbitTemplate.convertAndSend(exchange.getName(), rkCreated, evt);
    }

    public void publishUpdated(ClienteUpdatedEvent evt) {
        rabbitTemplate.convertAndSend(exchange.getName(), rkUpdated, evt);
    }
}
