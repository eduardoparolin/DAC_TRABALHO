package com.bank.manager.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${manager.events.exchange}")
    private String exchangeName;

    @Value("${manager.events.queue}")
    private String queueName;

    @Value("${manager.events.rk.created}")
    private String createdRoutingKey;

    @Value("${manager.events.rk.updated}")
    private String updatedRoutingKey;

    @Value("${manager.events.rk.deleted}")
    private String deletedRoutingKey;

    @Bean
    public Exchange sagaManagerExchange() {
        return ExchangeBuilder.directExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Queue managerSagaQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding bindingCreated(Queue managerSagaQueue, Exchange sagaManagerExchange) {
        return BindingBuilder.bind(managerSagaQueue).to(sagaManagerExchange).with(createdRoutingKey).noargs();
    }

    @Bean
    public Binding bindingUpdated(Queue managerSagaQueue, Exchange sagaManagerExchange) {
        return BindingBuilder.bind(managerSagaQueue).to(sagaManagerExchange).with(updatedRoutingKey).noargs();
    }

    @Bean
    public Binding bindingDeleted(Queue managerSagaQueue, Exchange sagaManagerExchange) {
        return BindingBuilder.bind(managerSagaQueue).to(sagaManagerExchange).with(deletedRoutingKey).noargs();
    }
}
