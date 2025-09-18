package com.dac.bank_account.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitConfig {

    //CQRS
    public static final String EXCHANGE = "bank.account.exchange";
    public static final String QUEUE_BANK_ACCOUNT = "bank.account";
    public static final String EXCHANGE_DLQ = "bank.account.exchange.dlq";
    public static final String QUEUE_BANK_ACCOUNT_DLQ = "bank.account.dlq";
    public static final String ROUTING_KEY_DLQ = "deadLetter";

    //SAGA
    public static final String SAGA_EXCHANGE = "saga.exchange";

    // Filas de comando
    public static final String QUEUE_CREATE_ACCOUNT_SAGA = "create.account.saga";
    public static final String QUEUE_UPDATE_CLIENT_SAGA = "update.client.saga";
    public static final String QUEUE_DELETE_MANAGER_SAGA = "delete.manager.saga";
    public static final String QUEUE_NEW_MANAGER_SAGA = "new.manager.saga";

    // Filas de sucesso
    public static final String QUEUE_CREATE_ACCOUNT_SAGA_SUCCESS = "create.account.saga.success";
    public static final String QUEUE_UPDATE_CLIENT_SAGA_SUCCESS = "update.client.saga.success";
    public static final String QUEUE_DELETE_MANAGER_SAGA_SUCCESS = "delete.manager.saga.success";
    public static final String QUEUE_NEW_MANAGER_SAGA_SUCCESS = "new.manager.saga.success";

    // Filas de falha
    public static final String QUEUE_CREATE_ACCOUNT_SAGA_FAILURE = "create.account.saga.failure";
    public static final String QUEUE_UPDATE_CLIENT_SAGA_FAILURE = "update.client.saga.failure";
    public static final String QUEUE_DELETE_MANAGER_SAGA_FAILURE = "delete.manager.saga.failure";
    public static final String QUEUE_NEW_MANAGER_SAGA_FAILURE = "new.manager.saga.failure";

    // ---------------- CQRS Beans ----------------
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue accountQueue() {
        return QueueBuilder.durable(QUEUE_BANK_ACCOUNT)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLQ)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_DLQ)
                .build();
    }

    @Bean
    public Binding createdBinding(Queue accountQueue, TopicExchange exchange) {
        return BindingBuilder.bind(accountQueue).to(exchange).with(QUEUE_BANK_ACCOUNT);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(EXCHANGE_DLQ);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(QUEUE_BANK_ACCOUNT_DLQ, true);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(ROUTING_KEY_DLQ);
    }

    // ---------------- SAGA Beans ----------------
    @Bean
    public DirectExchange sagaExchange() {
        return new DirectExchange(SAGA_EXCHANGE);
    }

    // ---- Filas de comando SAGA ----
    @Bean public Queue createAccountSagaQueue() { return new Queue(QUEUE_CREATE_ACCOUNT_SAGA, true); }
    @Bean public Queue updateClientSagaQueue() { return new Queue(QUEUE_UPDATE_CLIENT_SAGA, true); }
    @Bean public Queue deleteManagerSagaQueue() { return new Queue(QUEUE_DELETE_MANAGER_SAGA, true); }
    @Bean public Queue newManagerSagaQueue() { return new Queue(QUEUE_NEW_MANAGER_SAGA, true); }

    // ---- Bindings de comando SAGA ----
    @Bean public Binding bindingCreateAccountSaga(Queue createAccountSagaQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(createAccountSagaQueue).to(sagaExchange).with(QUEUE_CREATE_ACCOUNT_SAGA);
    }
    @Bean public Binding bindingUpdateClientSaga(Queue updateClientSagaQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(updateClientSagaQueue).to(sagaExchange).with(QUEUE_UPDATE_CLIENT_SAGA);
    }
    @Bean public Binding bindingDeleteManagerSaga(Queue deleteManagerSagaQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(deleteManagerSagaQueue).to(sagaExchange).with(QUEUE_DELETE_MANAGER_SAGA);
    }
    @Bean public Binding bindingNewManagerSaga(Queue newManagerSagaQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(newManagerSagaQueue).to(sagaExchange).with(QUEUE_NEW_MANAGER_SAGA);
    }

    // ---- Filas de sucesso SAGA ----
    @Bean public Queue createAccountSagaSuccessQueue() { return new Queue(QUEUE_CREATE_ACCOUNT_SAGA_SUCCESS, true); }
    @Bean public Queue updateClientSagaSuccessQueue() { return new Queue(QUEUE_UPDATE_CLIENT_SAGA_SUCCESS, true); }
    @Bean public Queue deleteManagerSagaSuccessQueue() { return new Queue(QUEUE_DELETE_MANAGER_SAGA_SUCCESS, true); }
    @Bean public Queue newManagerSagaSuccessQueue() { return new Queue(QUEUE_NEW_MANAGER_SAGA_SUCCESS, true); }

    // ---- Bindings de sucesso SAGA ----
    @Bean public Binding bindingCreateAccountSagaSuccess(Queue createAccountSagaSuccessQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(createAccountSagaSuccessQueue).to(sagaExchange).with(QUEUE_CREATE_ACCOUNT_SAGA_SUCCESS);
    }
    @Bean public Binding bindingUpdateClientSagaSuccess(Queue updateClientSagaSuccessQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(updateClientSagaSuccessQueue).to(sagaExchange).with(QUEUE_UPDATE_CLIENT_SAGA_SUCCESS);
    }
    @Bean public Binding bindingDeleteManagerSagaSuccess(Queue deleteManagerSagaSuccessQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(deleteManagerSagaSuccessQueue).to(sagaExchange).with(QUEUE_DELETE_MANAGER_SAGA_SUCCESS);
    }
    @Bean public Binding bindingNewManagerSagaSuccess(Queue newManagerSagaSuccessQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(newManagerSagaSuccessQueue).to(sagaExchange).with(QUEUE_NEW_MANAGER_SAGA_SUCCESS);
    }

    // ---- Filas de falha SAGA ----
    @Bean public Queue createAccountSagaFailureQueue() { return new Queue(QUEUE_CREATE_ACCOUNT_SAGA_FAILURE, true); }
    @Bean public Queue updateClientSagaFailureQueue() { return new Queue(QUEUE_UPDATE_CLIENT_SAGA_FAILURE, true); }
    @Bean public Queue deleteManagerSagaFailureQueue() { return new Queue(QUEUE_DELETE_MANAGER_SAGA_FAILURE, true); }
    @Bean public Queue newManagerSagaFailureQueue() { return new Queue(QUEUE_NEW_MANAGER_SAGA_FAILURE, true); }

    // ---- Bindings de falha SAGA ----
    @Bean public Binding bindingCreateAccountSagaFailure(Queue createAccountSagaFailureQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(createAccountSagaFailureQueue).to(sagaExchange).with(QUEUE_CREATE_ACCOUNT_SAGA_FAILURE);
    }
    @Bean public Binding bindingUpdateClientSagaFailure(Queue updateClientSagaFailureQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(updateClientSagaFailureQueue).to(sagaExchange).with(QUEUE_UPDATE_CLIENT_SAGA_FAILURE);
    }
    @Bean public Binding bindingDeleteManagerSagaFailure(Queue deleteManagerSagaFailureQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(deleteManagerSagaFailureQueue).to(sagaExchange).with(QUEUE_DELETE_MANAGER_SAGA_FAILURE);
    }
    @Bean public Binding bindingNewManagerSagaFailure(Queue newManagerSagaFailureQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(newManagerSagaFailureQueue).to(sagaExchange).with(QUEUE_NEW_MANAGER_SAGA_FAILURE);
    }

    // ---------------- General Beans ----------------
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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());

        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(3)
                        .backOffOptions(2000, 2.0, 30000)
                        .build()
        );
        return factory;
    }
}
