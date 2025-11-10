package com.bank.manager.config.consumer;

import com.bank.manager.config.producer.ManagerSagaProducer;
import com.bank.manager.model.Manager;
import com.bank.manager.repository.ManagerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Component
public class ManagerSagaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ManagerSagaConsumer.class);

    private final ManagerRepository managerRepository;
    private final ManagerSagaProducer managerSagaProducer;

    public ManagerSagaConsumer(ManagerRepository managerRepository, ManagerSagaProducer managerSagaProducer) {
        this.managerRepository = managerRepository;
        this.managerSagaProducer = managerSagaProducer;
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
            } else {
                log.warn("Unknown action: {}", action);
                managerSagaProducer.sendFailureResult(sagaId, action, "Unknown action: " + action);
            }
        } catch (Exception e) {
            log.error("Error processing saga message", e);
            managerSagaProducer.sendFailureResult(sagaId, action, e.getMessage());
        }
    }

    private void handleAssignManager(String sagaId, Map<String, Object> message) {
        log.info("Assigning manager for saga {}", sagaId);

        // Find manager with least accounts
        Optional<Manager> managerOpt = managerRepository.findAll().stream()
                .min(Comparator.comparing(Manager::getAccountCount));

        if (managerOpt.isEmpty()) {
            throw new RuntimeException("No managers available to assign");
        }

        Manager manager = managerOpt.get();

        // Increment account count
        manager.setAccountCount(manager.getAccountCount() + 1);
        managerRepository.save(manager);

        log.info("Assigned manager {} (ID: {}) for saga {}. New account count: {}",
                manager.getName(), manager.getId(), sagaId, manager.getAccountCount());

        // Send success result back to orchestrator
        managerSagaProducer.sendSuccessResult(sagaId, "ASSIGN_MANAGER", manager.getId());
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
