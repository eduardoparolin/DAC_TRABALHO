package com.bank.client.infra.producer;

import com.bank.client.enums.ClientAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientProducer {

    private final RabbitTemplate rabbitTemplate;
    private final String MESSAGE_SOURCE = "client";
    private final String RESULT_QUEUE = "client-result-queue";

    public void sendSuccessResult(ClientAction action, Long clientId) {
        log.info("Sending success result to queue");
        Map<String, Object> result = new HashMap<>();
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action);
        result.put("status", "SUCCESS");
        result.put("error", null);

        if(clientId != null){
            result.put("clientId", clientId);
        }
        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }

    public void sendFailureResult(String error, ClientAction action) {
        log.info("Sending failure result to queue");
        Map<String, Object> result = new HashMap<>();
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action);
        result.put("status", "FAILURE");
        result.put("error", error);
        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }
}
