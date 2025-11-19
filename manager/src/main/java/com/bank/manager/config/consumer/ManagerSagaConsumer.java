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
            } else if ("COMPLETE_MANAGER_DELETION".equals(action)) {
                handleCompleteManagerDeletion(sagaId, message);
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
      String requestedByEmail = (String) message.get("requestedByEmail");

      Manager deletedManager = managerService.findByCpf(cpf);

      // Validation 1: Manager cannot delete themselves
      // Compare by email since auth service and manager service use different ID systems
      if (requestedByEmail != null && deletedManager.getEmail().equalsIgnoreCase(requestedByEmail)) {
        throw new Exception("Manager cannot delete themselves");
      }

      // Validation 2: Cannot delete the last manager in the system
      Long totalManagers = managerService.countManagers();
      if (totalManagers <= 1) {
        throw new Exception("Cannot delete the last manager in the system. At least one manager must remain.");
      }

      log.info("Deleting manager ID {} (CPF: {}). Total managers before deletion: {}",
               deletedManager.getId(), cpf, totalManagers);

      // Don't delete yet - just mark for deletion and send to orchestrator
      // The orchestrator will coordinate with bank-account service to:
      // 1. Find the manager with fewest accounts (excluding this one)
      // 2. Reassign all accounts from this manager to that manager
      // 3. Then complete the deletion

      Map<String, Object> responseResult = new HashMap<>();
      responseResult.put("cpf", cpf);
      responseResult.put("managerId", deletedManager.getId());
      responseResult.put("managerName", deletedManager.getName());

      managerSagaProducer.sendSuccessResult(sagaId, "DELETE_MANAGER_MS", responseResult);
    }

    private void handleCompleteManagerDeletion(String sagaId, Map<String, Object> message) {
      log.info("Completing manager deletion for saga {}", sagaId);

      try {
        String cpf = (String) message.get("cpf");

        // Now actually delete the manager from the database
        managerService.deleteByCpf(cpf);

        log.info("Successfully deleted manager with CPF: {}", cpf);

        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("cpf", cpf);
        responseResult.put("deleted", true);

        managerSagaProducer.sendSuccessResult(sagaId, "COMPLETE_MANAGER_DELETION", responseResult);
      } catch (Exception e) {
        log.error("Error completing manager deletion for saga {}", sagaId, e);
        managerSagaProducer.sendFailureResult(sagaId, "COMPLETE_MANAGER_DELETION", e.getMessage());
      }
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
