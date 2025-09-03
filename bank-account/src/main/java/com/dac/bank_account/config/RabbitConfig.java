package com.dac.bank_account.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "bank.account.exchange";

    public static final String QUEUE_ACCOUNT_CREATED = "bank.account.created";
    public static final String QUEUE_TRANSACTION = "bank.account.transactions";
    public static final String QUEUE_ACCOUNT_UPDATED = "bank.account.updated";


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue accountCreatedQueue() {
        return new Queue(QUEUE_ACCOUNT_CREATED, true);
    }

    @Bean
    public Queue transactionQueue() {
        return new Queue(QUEUE_TRANSACTION, true);
    }

    @Bean Queue accountUpdatedQueue() {return new Queue(QUEUE_ACCOUNT_UPDATED, true);}


    // 🔹 Bindings
    @Bean
    public Binding createdBinding(Queue accountCreatedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(accountCreatedQueue).to(exchange).with(QUEUE_ACCOUNT_CREATED);
    }

    @Bean
    public Binding transactionBinding(Queue transactionQueue, TopicExchange exchange) {
        return BindingBuilder.bind(transactionQueue).to(exchange).with(QUEUE_TRANSACTION);
    }

    @Bean Binding updatedBinding(Queue accountUpdatedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(accountUpdatedQueue).to(exchange).with(QUEUE_ACCOUNT_UPDATED);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

}
