package com.bank.manager.config.consumer;

import com.bank.manager.config.ManagerSagaEvent;
import com.bank.manager.config.producer.ManagerSagaProducer;
import com.bank.manager.model.Manager;
import com.bank.manager.model.ManagerType;
import com.bank.manager.repository.ManagerRepository;
import com.bank.manager.service.ManagerService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ManagerSagaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ManagerSagaConsumer.class);

    private final ManagerService managerService;
    private final ManagerRepository managerRepository;
    private final ManagerSagaProducer managerSagaProducer;
    private final ObjectMapper objectMapper;

    public ManagerSagaConsumer(@Lazy ManagerService managerService, ManagerRepository managerRepository, ManagerSagaProducer managerSagaProducer, ObjectMapper objectMapper) {
      this.managerService = managerService;
      this.managerRepository = managerRepository;
      this.managerSagaProducer = managerSagaProducer;
      this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "manager-saga-queue")
    public void handleSagaMessage(Map<String, Object> message) {
        log.info("Received saga message: {}", message);

        String sagaId = (String) message.get("sagaId");
        String action = (String) message.get("action");

        try {
            if ("CREATE_MANAGER".equals(action)) {
                handleCreateManager(sagaId, message);
            } else if ("ASSIGN_MANAGER".equals(action)) {
                handleAssignManager(sagaId, message);
            } else if ("DELETE_MANAGER_MS".equals(action)) {
                handleDeleteManager(sagaId, message);
            } else {
                log.warn("Unknown action: {}", action);
                managerSagaProducer.sendFailureResult(sagaId, action, "Unknown action: " + action);
            }
        } catch (Exception e) {
            log.error("Error processing saga message", e);
            managerSagaProducer.sendFailureResult(sagaId, action, e.getMessage());
        }
    }

    private void handleCreateManager(String sagaId, Map<String, Object> message) {
        log.info("Creating manager for saga {}", sagaId);

        try {
            String cpf = (String) message.get("cpf");
            String name = (String) message.get("name");
            String email = (String) message.get("email");
            String password = (String) message.get("password");
            String type = (String) message.get("type");

            // Check if manager already exists
            Optional<Manager> existing = managerRepository.findByCpf(cpf);
            if (existing.isPresent()) {
                log.error("Manager with CPF {} already exists", cpf);
                managerSagaProducer.sendFailureResult(sagaId, "CREATE_MANAGER", "Manager with this CPF already exists");
                return;
            }

            // Create new manager
            Manager manager = new Manager();
            manager.setCpf(cpf);
            manager.setName(name);
            manager.setEmail(email);
            manager.setPassword(password); // Will be hashed by Auth service
            manager.setType(ManagerType.valueOf(type));

            Manager savedManager = managerRepository.save(manager);

            log.info("Created manager {} (ID: {}) for saga {}", savedManager.getName(), savedManager.getId(), sagaId);

            // Send success result with managerId
            Map<String, Object> responseResult = new HashMap<>();
            responseResult.put("managerId", savedManager.getId());
            responseResult.put("cpf", savedManager.getCpf());
            responseResult.put("name", savedManager.getName());
            responseResult.put("email", savedManager.getEmail());

            managerSagaProducer.sendSuccessResult(sagaId, "CREATE_MANAGER", responseResult);
        } catch (Exception e) {
            log.error("Error creating manager for saga {}", sagaId, e);
            managerSagaProducer.sendFailureResult(sagaId, "CREATE_MANAGER", e.getMessage());
        }
    }

    private void handleDeleteManager(String sagaId, Map<String, Object> message) throws Exception {
      log.info("Delete manager for saga {}", sagaId);

      String cpf = (String) message.get("cpf");
      Long requestedById = Long.parseLong(String.valueOf(message.get("requestedById")));

      Manager deletedManager = managerService.findByCpf(cpf);

      if (deletedManager.getId().equals(requestedById)) {
        throw new Exception("Manager não pode deletar a si mesmo");
      }

      // Get manager with least accounts (should query bank-account service)
      // TODO: Query bank-account service for actual manager with least accounts
      List<Long> allManagerIds = managerService.getAllManagerIds();
      Long managerIdLessAccounts = allManagerIds.stream()
              .filter(id -> !id.equals(deletedManager.getId()))
              .findFirst()
              .orElseThrow(() -> new Exception("No manager available to reassign accounts"));

      log.info("Manager with least accounts: {}", managerIdLessAccounts);

      // Delete the manager
      managerService.deleteByCpf(cpf);

      Map<String, Object> responseResult = new HashMap<>();
      responseResult.put("cpf", cpf);
      responseResult.put("managerIdLessAccounts", managerIdLessAccounts);
      responseResult.put("managerId", deletedManager.getId());

      managerSagaProducer.sendSuccessResult(sagaId, "DELETE_MANAGER_MS", responseResult);
    }


  private void handleAssignManager(String sagaId, Map<String, Object> message) {
        log.info("Assigning manager for saga {}", sagaId);

        // NOTE: Manager assignment now relies on bank-account service to calculate
        // which manager has the least accounts. The orchestrator queries bank-account
        // service which returns the appropriate managerId.
        // This handler is kept for backward compatibility but should be refactored
        // to query bank-account service via REST or messaging.

        List<Long> allManagerIds = managerService.getAllManagerIds();
        if (allManagerIds.isEmpty()) {
            throw new RuntimeException("No managers available to assign");
        }

        // For now, return first manager ID
        // TODO: Query bank-account service for actual manager with least accounts
        Long managerId = allManagerIds.get(0);

        Optional<Manager> managerOpt = managerRepository.findById(managerId);
        if (managerOpt.isEmpty()) {
            throw new RuntimeException("Manager not found: " + managerId);
        }

        Manager manager = managerOpt.get();
        log.info("Assigned manager {} (ID: {}) for saga {}", manager.getName(), manager.getId(), sagaId);

        // Send success result back to orchestrator
        managerSagaProducer.sendSuccessResult(sagaId, "ASSIGN_MANAGER", manager.getId());
    }
}
