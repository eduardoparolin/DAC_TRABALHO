package com.dac.auth.infra.producer;

import com.dac.auth.dto.payload.AuthPayload;
import com.dac.auth.enums.Action;
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

    public void sendFailResult(Action action, String error) {
        log.error("Uma exceção foi gerada: {}", error);
        Map<String, Object> result = new HashMap<>();
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action + "_RESULT");
        result.put("status", "FAIL");
        result.put("error", error);

        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }

    public void sendSuccessResult(Action action) {
        log.info("Processamento concluído com sucesso");
        Map<String, Object> result = new HashMap<>();
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action + "_RESULT");
        result.put("status", "SUCCESS");
        result.put("error", null);

        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }
}
