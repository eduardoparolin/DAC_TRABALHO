package com.bank.manager.config.consumer;

import com.bank.manager.config.ManagerSagaEvent;
import com.bank.manager.config.producer.ManagerSagaProducer;
import com.bank.manager.model.Manager;
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
            if ("ASSIGN_MANAGER".equals(action)) {
                handleAssignManager(sagaId, message);
            } else if ("UNASSIGN_MANAGER".equals(action)) {
                handleUnassignManager(sagaId, message);
            } else if ("INCREMENT_ACCOUNT_COUNT".equals(action)) {
                handleIncrementAccountCount(sagaId, message);
            } else if ("DECREMENT_ACCOUNT_COUNT".equals(action)) {
                handleDecrementAccountCount(sagaId, message);
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

    private void handleDeleteManager(String sagaId, Map<String, Object> message) throws Exception {
      log.info("Delete manager for saga {}", sagaId);

      String cpf = (String) message.get("cpf");
      Long requestedById = Long.parseLong(String.valueOf(message.get("requestedById")));

      Manager managerLessAccounts = managerService.getWithLessAccounts();
      log.info("id manager less acounts: {}", managerLessAccounts.getId());
      Manager deletedManager = managerService.findByCpf(cpf);

      if (deletedManager.getId().equals(requestedById)) {
        throw new Exception("Manager não pode deletar a si mesmo");
      }

      managerLessAccounts.setAccountCount(managerLessAccounts.getAccountCount() + deletedManager.getAccountCount());

      managerService.save(managerLessAccounts);
      managerService.deleteByCpf(cpf);

      Map<String, Object> responseResult = new HashMap<>();
      responseResult.put("cpf", cpf);
      responseResult.put("managerIdLessAccounts", managerLessAccounts.getId());
      responseResult.put("managerId", deletedManager.getId());

      managerSagaProducer.sendSuccessResult(sagaId, "DELETE_MANAGER_MS", responseResult);
    }


  private void handleAssignManager(String sagaId, Map<String, Object> message) {
        log.info("Assigning manager for saga {}", sagaId);

        // Find manager with least accounts (approved clients only)
        Optional<Manager> managerOpt = managerRepository.findAll().stream()
                .min(Comparator.comparing(Manager::getAccountCount));

        if (managerOpt.isEmpty()) {
            throw new RuntimeException("No managers available to assign");
        }

        Manager manager = managerOpt.get();

        log.info("Assigned manager {} (ID: {}) for saga {}. Current account count: {}",
                manager.getName(), manager.getId(), sagaId, manager.getAccountCount());

        // Send success result back to orchestrator
        managerSagaProducer.sendSuccessResult(sagaId, "ASSIGN_MANAGER", manager.getId());
    }

    private void handleIncrementAccountCount(String sagaId, Map<String, Object> message) {
        log.info("Incrementing account count for saga {}", sagaId);

        Object managerIdObj = message.get("managerId");
        if (managerIdObj == null) {
            log.error("No managerId provided for increment");
            managerSagaProducer.sendFailureResult(sagaId, "INCREMENT_ACCOUNT_COUNT", "No managerId provided");
            return;
        }

        Long managerId;
        if (managerIdObj instanceof Integer) {
            managerId = ((Integer) managerIdObj).longValue();
        } else if (managerIdObj instanceof Long) {
            managerId = (Long) managerIdObj;
        } else {
            log.error("Invalid managerId type: {}", managerIdObj.getClass());
            managerSagaProducer.sendFailureResult(sagaId, "INCREMENT_ACCOUNT_COUNT", "Invalid managerId type");
            return;
        }

        Optional<Manager> managerOpt = managerRepository.findById(managerId);
        if (managerOpt.isEmpty()) {
            log.error("Manager {} not found for increment", managerId);
            managerSagaProducer.sendFailureResult(sagaId, "INCREMENT_ACCOUNT_COUNT", "Manager not found");
            return;
        }

        Manager manager = managerOpt.get();
        manager.setAccountCount(manager.getAccountCount() + 1);
        managerRepository.save(manager);

        log.info("Incremented account count for manager {} (ID: {}). New count: {}",
                manager.getName(), manager.getId(), manager.getAccountCount());

        managerSagaProducer.sendSuccessResult(sagaId, "INCREMENT_ACCOUNT_COUNT", manager.getId());
    }

    private void handleDecrementAccountCount(String sagaId, Map<String, Object> message) {
        log.info("Decrementing account count for saga {}", sagaId);

        Object managerIdObj = message.get("managerId");
        if (managerIdObj == null) {
            log.error("No managerId provided for decrement");
            managerSagaProducer.sendFailureResult(sagaId, "DECREMENT_ACCOUNT_COUNT", "No managerId provided");
            return;
        }

        Long managerId;
        if (managerIdObj instanceof Integer) {
            managerId = ((Integer) managerIdObj).longValue();
        } else if (managerIdObj instanceof Long) {
            managerId = (Long) managerIdObj;
        } else {
            log.error("Invalid managerId type: {}", managerIdObj.getClass());
            managerSagaProducer.sendFailureResult(sagaId, "DECREMENT_ACCOUNT_COUNT", "Invalid managerId type");
            return;
        }

        Optional<Manager> managerOpt = managerRepository.findById(managerId);
        if (managerOpt.isEmpty()) {
            log.error("Manager {} not found for decrement", managerId);
            managerSagaProducer.sendFailureResult(sagaId, "DECREMENT_ACCOUNT_COUNT", "Manager not found");
            return;
        }

        Manager manager = managerOpt.get();
        if (manager.getAccountCount() > 0) {
            manager.setAccountCount(manager.getAccountCount() - 1);
            managerRepository.save(manager);
            log.info("Decremented account count for manager {} (ID: {}). New count: {}",
                    manager.getName(), manager.getId(), manager.getAccountCount());
        } else {
            log.warn("Manager {} already has 0 accounts, cannot decrement", managerId);
        }

        managerSagaProducer.sendSuccessResult(sagaId, "DECREMENT_ACCOUNT_COUNT", manager.getId());
    }

    private void handleUnassignManager(String sagaId, Map<String, Object> message) {
        log.info("Unassigning manager for saga {} (compensation)", sagaId);

        Object managerIdObj = message.get("managerId");
        if (managerIdObj == null) {
            log.warn("No managerId provided for unassignment, skipping compensation");
            return;
        }

        Long managerId;
        if (managerIdObj instanceof Integer) {
            managerId = ((Integer) managerIdObj).longValue();
        } else if (managerIdObj instanceof Long) {
            managerId = (Long) managerIdObj;
        } else {
            log.error("Invalid managerId type: {}", managerIdObj.getClass());
            return;
        }

        Optional<Manager> managerOpt = managerRepository.findById(managerId);
        if (managerOpt.isEmpty()) {
            log.warn("Manager {} not found for unassignment", managerId);
            return;
        }

        Manager manager = managerOpt.get();

        if (manager.getAccountCount() > 0) {
            manager.setAccountCount(manager.getAccountCount() - 1);
            managerRepository.save(manager);
            log.info("Decremented account count for manager {} (ID: {}). New count: {}",
                    manager.getName(), manager.getId(), manager.getAccountCount());
        } else {
            log.warn("Manager {} already has 0 accounts, cannot decrement", managerId);
        }
    }
}
