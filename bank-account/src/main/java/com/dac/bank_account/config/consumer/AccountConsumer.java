package com.dac.bank_account.config.consumer;

import com.dac.bank_account.config.consumer.factory.MessageHandlerFactory;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.producer.AccountProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountConsumer {

    private final MessageHandlerFactory messageHandlerFactory;
    private final AccountProducer accountProducer;

    @RabbitListener(queues = "account-saga-queue")
    public void listenAccountQueue(AccountSagaEvent event) {
        try {
            AccountMessageHandler handler = messageHandlerFactory.getStrategy(event.getAction());
            handler.handle(event);
            accountProducer.sendSuccessResult(event.getSagaId(), event.getAction(), event.getManagerId(), event.getAccountId(), event.getAccountNumber(), event.getOldManagerId());
        } catch (Exception e) {
            accountProducer.sendFailureResult(event.getSagaId(), event.getAction(), e.getMessage());
        }
    }
}
