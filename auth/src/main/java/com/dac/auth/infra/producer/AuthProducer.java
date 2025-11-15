package com.dac.auth.infra.producer;

import com.dac.auth.dto.payload.AuthPayload;
import com.dac.auth.enums.Action;
import com.dac.auth.exception.custom.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthProducer {

    private final RabbitTemplate rabbitTemplate;
    private final String MESSAGE_SOURCE = "auth";
    private final String RESULT_QUEUE = "user-result-queue";
    private final ObjectMapper objectMapper;

    public void sendFailResult(Action action, String sagaId, String error) {
        log.error("Uma exceção foi gerada: {}", error);
        Map<String, Object> result = new HashMap<>();
        result.put("sagaId", sagaId);
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action + "_RESULT");
        result.put("status", "FAIL");
        result.put("error", error);

        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }

    public void sendSuccessResult(Action action, String sagaId) {
        log.info("Processamento concluído com sucesso");

        Map<String, Object> response = new HashMap<>();
        response.put("sagaId", sagaId);
        response.put("source", MESSAGE_SOURCE);
        response.put("action", action + "_RESULT");
        response.put("status", "SUCCESS");
        response.put("error", null);

        rabbitTemplate.convertAndSend(RESULT_QUEUE, response);
    }

}
