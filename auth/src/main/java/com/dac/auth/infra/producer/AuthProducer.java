package com.dac.auth.infra.producer;

import com.dac.auth.dto.payload.AuthPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendResult(AuthPayload payload, String status, String error) {
        Map<String, Object> result = new HashMap<>();
        result.put("action", payload.getAction() + "_RESULT");
        result.put("status", status);
        result.put("error", error);
        result.put("data", payload.getData());

        rabbitTemplate.convertAndSend("user-result-queue", result);
    }
}
