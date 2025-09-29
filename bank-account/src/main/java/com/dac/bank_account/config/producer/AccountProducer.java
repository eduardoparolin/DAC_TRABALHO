package com.dac.bank_account.config.producer;

import com.dac.bank_account.enums.AccountAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountProducer {

    private final RabbitTemplate rabbitTemplate;
    private final String MESSAGE_SOURCE = "account";
    private final String RESULT_QUEUE = "account-result-queue";

    public void sendSuccessResult(AccountAction action){
        log.info("Processamento de {} concluído com sucesso.", action);
        Map<String,Object> result = new HashMap<>();
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action.name() + "_RESULT");
        result.put("status", "SUCCESS");
        result.put("error", null);

        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }

    public void sendFailureResult(AccountAction action, String error){
        log.error("Falha em {}: {}", action, error);
        Map<String,Object> result = new HashMap<>();
        result.put("source", MESSAGE_SOURCE);
        result.put("action", action.name() + "_RESULT");
        result.put("status", "FAILURE");
        result.put("error", error);

        rabbitTemplate.convertAndSend(RESULT_QUEUE, result);
    }
}
