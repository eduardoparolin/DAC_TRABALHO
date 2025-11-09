package com.example.orchestrator.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SagaProducer {

  private static final Logger log = LoggerFactory.getLogger(SagaProducer.class);
  private final RabbitTemplate rabbitTemplate;

  private static final String CLIENT_QUEUE = "client-queue";
  private static final String ACCOUNT_QUEUE = "account-saga-queue";
  private static final String MANAGER_QUEUE = "manager-saga-queue";
  private static final String TRANSACTION_QUEUE = "transaction-queue";
  private static final String USER_QUEUE = "user-queue";

  public SagaProducer(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendToClientService(Object message) {
    log.info("Sending message to client-queue: {}", message);
    rabbitTemplate.convertAndSend(CLIENT_QUEUE, message);
  }

  public void sendToAccountService(Object message) {
    log.info("Sending message to account-queue: {}", message);
    rabbitTemplate.convertAndSend(ACCOUNT_QUEUE, message);
  }

  public void sendToManagerService(Object message) {
    log.info("Sending message to manager-queue: {}", message);
    rabbitTemplate.convertAndSend(MANAGER_QUEUE, message);
  }

  public void sendToTransactionService(Object message) {
    log.info("Sending message to transaction-queue: {}", message);
    rabbitTemplate.convertAndSend(TRANSACTION_QUEUE, message);
  }

  public void sendToAuthService(Object message) {
    log.info("Sending message to user-queue: {}", message);
    rabbitTemplate.convertAndSend(USER_QUEUE, message);
  }
}
