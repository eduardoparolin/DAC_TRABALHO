package com.bank.manager.config.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ManagerSagaProducer {

    private static final Logger log = LoggerFactory.getLogger(ManagerSagaProducer.class);
    private final RabbitTemplate rabbitTemplate;
    private final String MESSAGE_SOURCE = "manager";
    private final String RESULT_QUEUE = "manager-result-queue";

    public ManagerSagaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSuccessResult(String sagaId, String action, Long managerId) {
        log.info("Sending success result for action {} in saga {}", action, sagaId);

        Map<String, Object> result = new HashMap<>();
        result.put("sagaId", sagaId);
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action + "_RESULT");
        result.put("status", "SUCCESS");
        result.put("managerId", managerId);
        result.put("error", null);

        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }

    public void sendFailureResult(String sagaId, String action, String error) {
        log.error("Sending failure result for action {} in saga {}: {}", action, sagaId, error);

        Map<String, Object> result = new HashMap<>();
        result.put("sagaId", sagaId);
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action + "_RESULT");
        result.put("status", "FAILURE");
        result.put("error", error);

        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }
}
