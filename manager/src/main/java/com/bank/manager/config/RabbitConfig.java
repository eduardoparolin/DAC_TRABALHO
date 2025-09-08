package com.bank.manager.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "bantads.exchange";
    public static final String QUEUE_CLIENT_APPROVED = "bantads.client.approved";
    public static final String QUEUE_CLIENT_REJECTED = "bantads.client.rejected";
    public static final String ROUTING_APPROVED = "client.approved";
    public static final String ROUTING_REJECTED = "client.rejected";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue approvedQueue() {
        return QueueBuilder.durable(QUEUE_CLIENT_APPROVED).build();
    }

    @Bean
    public Queue rejectedQueue() {
        return QueueBuilder.durable(QUEUE_CLIENT_REJECTED).build();
    }

    @Bean
    public Binding bindingApproved(Queue approvedQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(approvedQueue).to(directExchange).with(ROUTING_APPROVED);
    }

    @Bean
    public Binding bindingRejected(Queue rejectedQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(rejectedQueue).to(directExchange).with(ROUTING_REJECTED);
    }
}
