package com.example.orchestrator.controller;

import com.example.orchestrator.dto.SagaRequest;
import com.example.orchestrator.service.SagaOrchestratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/saga")
public class SagaController {

  private final SagaOrchestratorService sagaOrchestratorService;

  public SagaController(SagaOrchestratorService sagaOrchestratorService) {
    this.sagaOrchestratorService = sagaOrchestratorService;
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
}
