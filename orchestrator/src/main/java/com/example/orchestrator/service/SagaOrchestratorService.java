package com.example.orchestrator.service;

import com.example.orchestrator.dto.ClientMessageRequest;
import com.example.orchestrator.dto.SagaRequest;
import com.example.orchestrator.dto.SagaResult;
import com.example.orchestrator.model.Saga;
import com.example.orchestrator.model.SagaStep;
import com.example.orchestrator.producer.SagaProducer;
import com.example.orchestrator.repository.SagaRepository;
import com.example.orchestrator.repository.SagaStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class SagaOrchestratorService {

  private static final Logger log = LoggerFactory.getLogger(SagaOrchestratorService.class);

  private final SagaRepository sagaRepository;
  private final SagaStepRepository sagaStepRepository;
  private final SagaProducer sagaProducer;

  public SagaOrchestratorService(SagaRepository sagaRepository,
                                  SagaStepRepository sagaStepRepository,
                                  SagaProducer sagaProducer) {
    this.sagaRepository = sagaRepository;
    this.sagaStepRepository = sagaStepRepository;
    this.sagaProducer = sagaProducer;
  }

  @Transactional
  public String startCreateClientSaga(SagaRequest request) {
    String sagaId = UUID.randomUUID().toString();
    log.info("Starting CREATE_CLIENT saga with id: {}", sagaId);

    // Create saga
    Saga saga = new Saga(sagaId, "PENDING", Instant.now());
    sagaRepository.save(saga);

    // Create saga step
    SagaStep step = new SagaStep("CREATE_CLIENT", "PENDING",
        convertMapToString(request.getData()), "DELETE_CLIENT");
    step.setSaga(saga);
    sagaStepRepository.save(step);

    // Send message to client service
    ClientMessageRequest clientRequest = new ClientMessageRequest();
    clientRequest.setAction(request.getAction());

    Map<String, Object> data = request.getData();
    if (data != null) {
      clientRequest.setCpf((String) data.get("cpf"));
      clientRequest.setName((String) data.get("name"));
      clientRequest.setEmail((String) data.get("email"));
      clientRequest.setPhone((String) data.get("phone"));
    }

    sagaProducer.sendToClientService(clientRequest);

    return sagaId;
  }

  @Transactional
  public void processResult(SagaResult result) {
    log.info("Processing result from {}: status={}, action={}",
        result.getSource(), result.getStatus(), result.getAction());

    if ("SUCCESS".equals(result.getStatus())) {
      handleSuccess(result);
    } else {
      handleFailure(result);
    }
  }

  private void handleSuccess(SagaResult result) {
    log.info("Handling success for source: {}", result.getSource());
    // Update saga step status to COMPLETED
    // Here you can add logic to proceed to the next step if needed
  }

  private void handleFailure(SagaResult result) {
    log.error("Handling failure for source: {}, error: {}",
        result.getSource(), result.getError());
    // Update saga step status to FAILED
    // Trigger compensation if needed
  }

  private String convertMapToString(Map<String, Object> data) {
    if (data == null) {
      return null;
    }
    try {
      return data.toString();
    } catch (Exception e) {
      return null;
    }
  }
}
