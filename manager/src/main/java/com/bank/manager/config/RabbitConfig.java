package com.bank.manager.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Saga orchestration queues
    private final String MANAGER_SAGA_QUEUE = "manager-saga-queue";
    private final String MANAGER_RESULT_QUEUE = "manager-result-queue";
    private final String MANAGER_EVENTS_QUEUE = "manager-events-queue";

    @Bean
    public Queue managerSagaQueue() {
        return QueueBuilder.durable(MANAGER_SAGA_QUEUE).build();
    }

    @Bean
    public Queue managerResultQueue() {
        return QueueBuilder.durable(MANAGER_RESULT_QUEUE).build();
    }

    @Bean
    public Queue managerEventsQueue() {
        return QueueBuilder.durable(MANAGER_EVENTS_QUEUE).build();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
