package com.dac.bank_account.query.consumer;

import com.dac.bank_account.command.events.AccountCreatedEvent;
import com.dac.bank_account.command.events.MoneyTransactionEvent;
import com.dac.bank_account.query.dto.AccountQueryMapper;
import com.dac.bank_account.query.entity.AccountView;
import com.dac.bank_account.query.repository.AccountQueryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class AccountEventHandler {
    private final AccountQueryRepository accountQueryRepository;
    private final AccountQueryMapper mapper;


    public AccountEventHandler(AccountQueryRepository accountQueryRepository, AccountQueryMapper mapper) {
        this.accountQueryRepository = accountQueryRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "bank.account.created")
    public void handleAccountCreatedEvent(AccountCreatedEvent event) {
        System.out.println("Recebido evento do RabbitMQ: " + event);
        AccountView account = mapper.toEntity(event);
        accountQueryRepository.save(account);

    }

    @RabbitListener(queues = "bank.account.transactions")
    public void handleTransactionEvent(MoneyTransactionEvent event) {
        switch (event.getTransactionType()){
            case DEPOSITO -> System.out.println("Recebido evento de deposito RabbitMQ: " + event);
            case SAQUE -> System.out.println("Recebido evento de saque RabbitMQ: " + event);
            case TRANSFERENCIA ->  System.out.println("Recebido evento de transferencia RabbitMQ: " + event);
        }
    }

}
