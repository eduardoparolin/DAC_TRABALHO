package com.example.orchestrator.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

  private final String CLIENT_QUEUE = "client-queue";
  private final String ACCOUNT_QUEUE = "account-saga-queue";
  private final String MANAGER_QUEUE = "manager-saga-queue";
  private final String TRANSACTION_QUEUE = "transaction-queue";
  private final String USER_QUEUE = "user-queue";

  private final String CLIENT_RESULT_QUEUE = "client-result-queue";
  private final String ACCOUNT_RESULT_QUEUE = "account-result-queue";
  private final String MANAGER_RESULT_QUEUE = "manager-result-queue";
  private final String TRANSACTION_RESULT_QUEUE = "transaction-result-queue";
  private final String USER_RESULT_QUEUE = "user-result-queue";

  @Bean
  public Queue clientQueue() {
    return new Queue(CLIENT_QUEUE, true);
  }

  @Bean
  public Queue accountQueue() {
    return new Queue(ACCOUNT_QUEUE, true);
  }

  @Bean
  public Queue managerQueue() {
    return new Queue(MANAGER_QUEUE, true);
  }

  @Bean
  public Queue transactionQueue() {
    return new Queue(TRANSACTION_QUEUE, true);
  }

  @Bean
  public Queue clientResultQueue() {
    return new Queue(CLIENT_RESULT_QUEUE, true);
  }

  @Bean
  public Queue accountResultQueue() {
    return new Queue(ACCOUNT_RESULT_QUEUE, true);
  }

  @Bean
  public Queue managerResultQueue() {
    return new Queue(MANAGER_RESULT_QUEUE, true);
  }

  @Bean
  public Queue transactionResultQueue() {
    return new Queue(TRANSACTION_RESULT_QUEUE, true);
  }

  @Bean
  public Queue userQueue() {
    return new Queue(USER_QUEUE, true);
  }

  @Bean
  public Queue userResultQueue() {
    return new Queue(USER_RESULT_QUEUE, true);
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
