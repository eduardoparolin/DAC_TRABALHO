package com.example.orchestrator.consumer;

import com.example.orchestrator.dto.SagaResult;
import com.example.orchestrator.service.SagaOrchestratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SagaResultConsumer {

  private static final Logger log = LoggerFactory.getLogger(SagaResultConsumer.class);
  private final SagaOrchestratorService sagaOrchestratorService;

  public SagaResultConsumer(SagaOrchestratorService sagaOrchestratorService) {
    this.sagaOrchestratorService = sagaOrchestratorService;
  }

  @RabbitListener(queues = "client-result-queue")
  public void handleClientResult(SagaResult result) {
    log.info("Received result from client-result-queue: {}", result);
    sagaOrchestratorService.processResult(result);
  }

  @RabbitListener(queues = "account-result-queue")
  public void handleAccountResult(SagaResult result) {
    log.info("Received result from account-result-queue: {}", result);
    sagaOrchestratorService.processResult(result);
  }

  @RabbitListener(queues = "manager-result-queue")
  public void handleManagerResult(SagaResult result) {
    log.info("Received result from manager-result-queue: {}", result);
    sagaOrchestratorService.processResult(result);
  }

  @RabbitListener(queues = "transaction-result-queue")
  public void handleTransactionResult(SagaResult result) {
    log.info("Received result from transaction-result-queue: {}", result);
    sagaOrchestratorService.processResult(result);
  }

  @RabbitListener(queues = "user-result-queue")
  public void handleAuthResult(SagaResult result) {
    log.info("Received result from user-result-queue: {}", result);
    sagaOrchestratorService.processResult(result);
  }
}
