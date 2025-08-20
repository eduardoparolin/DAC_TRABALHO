package com.dac.auth.consumer;

import com.dac.auth.dto.payload.AuthPayload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class AuthConsumer {

    @RabbitListener(queues = "user-queue")
    public void listenAuthQueue(@Payload AuthPayload payload) {
        System.out.println("Consumindo fila: "+payload.toString());
    }
}
