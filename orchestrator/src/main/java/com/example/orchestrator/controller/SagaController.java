package com.example.orchestrator.controller;

import com.example.orchestrator.dto.SagaRequest;
import com.example.orchestrator.model.Saga;
import com.example.orchestrator.repository.SagaRepository;
import com.example.orchestrator.service.SagaOrchestratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/saga")
public class SagaController {

  private final SagaOrchestratorService sagaOrchestratorService;
  private final SagaRepository sagaRepository;

  public SagaController(SagaOrchestratorService sagaOrchestratorService, SagaRepository sagaRepository) {
    this.sagaOrchestratorService = sagaOrchestratorService;
    this.sagaRepository = sagaRepository;
  }

  @PostMapping("/client/create")
  public ResponseEntity<Map<String, String>> createClient(@RequestBody SagaRequest request) {
    try {
      String sagaId = sagaOrchestratorService.startCreateClientSaga(request);
      Map<String, String> response = new HashMap<>();
      response.put("sagaId", sagaId);
      response.put("status", "PENDING");
      response.put("message", "Saga initiated successfully");
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    } catch (Exception e) {
      Map<String, String> response = new HashMap<>();
      response.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @PutMapping("/client")
  public ResponseEntity<Map<String, String>> updateClient(@RequestBody SagaRequest request) {
    try {
      String sagaId = sagaOrchestratorService.startUpdateClientSaga(request);
      Map<String, String> response = new HashMap<>();
      response.put("sagaId", sagaId);
      response.put("status", "PENDING");
      response.put("message", "Saga initiated successfully");
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    } catch (Exception e) {
      Map<String, String> response = new HashMap<>();
      response.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @PostMapping("/manager/create")
  public ResponseEntity<Map<String, String>> createManager(@RequestBody SagaRequest request) {
    try {
      String sagaId = sagaOrchestratorService.startCreateManagerSaga(request);
      Map<String, String> response = new HashMap<>();
      response.put("sagaId", sagaId);
      response.put("status", "PENDING");
      response.put("message", "Manager creation saga initiated successfully");
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    } catch (Exception e) {
      Map<String, String> response = new HashMap<>();
      response.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @DeleteMapping("/manager")
    public ResponseEntity<Map<String, String>> deleteManager(@RequestBody SagaRequest request) {
      try {
          String sagaId = sagaOrchestratorService.startDeleteManagerSaga(request);
          Map<String, String> response = new HashMap<>();
          response.put("sagaId", sagaId);
          response.put("status", "PENDING");
          response.put("message", "Saga initiated successfully");
          return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
      } catch (Exception e) {
          Map<String, String> response = new HashMap<>();
          response.put("error", e.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }
  }

  @PostMapping("/client/approve")
  public ResponseEntity<Map<String, String>> approveClient(@RequestBody SagaRequest request) {
    try {
      String sagaId = sagaOrchestratorService.startApproveClientSaga(request);
      Map<String, String> response = new HashMap<>();
      response.put("sagaId", sagaId);
      response.put("status", "PENDING");
      response.put("message", "Approval saga initiated successfully");
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    } catch (Exception e) {
      Map<String, String> response = new HashMap<>();
      response.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping("/{sagaId}")
  public ResponseEntity<Map<String, Object>> getSagaStatus(@PathVariable String sagaId) {
    try {
      Optional<Saga> sagaOpt = sagaRepository.findById(sagaId);
      if (sagaOpt.isEmpty()) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Saga not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      Saga saga = sagaOpt.get();
      Map<String, Object> context = sagaOrchestratorService.getSagaContext(sagaId);

      Map<String, Object> response = new HashMap<>();
      response.put("sagaId", saga.getSagaId());
      response.put("status", saga.getStatus());
      response.put("createdAt", saga.getCreatedAt());

      if (context != null) {
        response.put("data", context);
      }

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
