package com.dac.auth.consumer;

import com.dac.auth.consumer.utils.handlers.factory.MessageHandlerFactory;
import com.dac.auth.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.AuthPayload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class AuthConsumer {

    @RabbitListener(queues = "user-queue")
    public void listenAuthQueue(@Payload AuthPayload payload) {
        System.out.println("Consumindo fila: "+payload.toString());
        MessageHandler<AuthPayload> strategyHandler = MessageHandlerFactory.getStrategy(payload.getAction());
        strategyHandler.handle(payload);
    }
}
