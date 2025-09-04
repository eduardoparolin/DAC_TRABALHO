package com.dac.bank_account.query.consumer;

import com.dac.bank_account.command.events.AccountCreatedEvent;
import com.dac.bank_account.command.events.AccountLimitChangedEvent;
import com.dac.bank_account.command.events.MoneyTransactionEvent;
import com.dac.bank_account.command.events.RemovedManagerEvent;
import com.dac.bank_account.query.dto.AccountQueryMapper;
import com.dac.bank_account.query.entity.AccountView;
import com.dac.bank_account.query.entity.TransactionView;
import com.dac.bank_account.query.repository.AccountQueryRepository;
import com.dac.bank_account.query.repository.TransactionQueryRepository;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@EnableRabbit
@Service
@RabbitListener(queues = "bank.account")
public class AccountEventHandler {
    private final AccountQueryRepository accountQueryRepository;
    private final AccountQueryMapper mapper;
    private final TransactionQueryRepository transactionQueryRepository;


    public AccountEventHandler(AccountQueryRepository accountQueryRepository, AccountQueryMapper mapper, TransactionQueryRepository transactionQueryRepository) {
        this.accountQueryRepository = accountQueryRepository;
        this.mapper = mapper;
        this.transactionQueryRepository = transactionQueryRepository;
    }

    @RabbitHandler
    public void handleAccountCreatedEvent(AccountCreatedEvent event) {
        System.out.println("Recebido evento do RabbitMQ: " + event);
        AccountView account = mapper.toEntity(event);
        accountQueryRepository.save(account);

    }

    @RabbitHandler
    public void handleTransactionEvent(MoneyTransactionEvent event) {
        switch (event.getTransactionType()){
            case DEPOSITO -> handleDeposit(event);
            case SAQUE -> handleWithdraw(event);
            case TRANSFERENCIA ->  handleTransfer(event);
        }

        saveTransaction(event);
    }

    @RabbitHandler
    public void handleAccountUpdatedEvent(RemovedManagerEvent event) {
        List<AccountView> accounts = accountQueryRepository.findByManagerId(event.getOldManagerId());
        accounts.forEach(acc -> acc.setManagerId(event.getNewManagerId()));
        accountQueryRepository.saveAll(accounts);
    }

    @RabbitHandler
    public void handleAccountLimitChangedEvent(AccountLimitChangedEvent event) {
        System.out.println("Recebido evento do RabbitMQ: " + event);
        AccountView account = accountQueryRepository.findByAccountNumber(event.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found with number: " + event.getAccountNumber()));

        account.setLimitAmount(event.getNewLimit());
        accountQueryRepository.save(account);
    }

    private void handleDeposit(MoneyTransactionEvent event) {
        AccountView account = accountQueryRepository.findByAccountNumber(event.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found with number: " + event.getAccountNumber()));

        account.setBalance(account.getBalance().add(event.getAmount()));
        accountQueryRepository.save(account);
    }

    private void handleWithdraw(MoneyTransactionEvent event) {
        AccountView account = accountQueryRepository.findByAccountNumber(event.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found with number: " + event.getAccountNumber()));

        account.setBalance(account.getBalance().subtract(event.getAmount()));
        accountQueryRepository.save(account);
    }

    private void handleTransfer(MoneyTransactionEvent event) {
        AccountView source = accountQueryRepository.findByAccountNumber(event.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Source account not found with number: " + event.getAccountNumber()));
        AccountView target = accountQueryRepository.findByAccountNumber(event.getTargetAccountNumber())
                .orElseThrow(() -> new RuntimeException("Target account not found with number: " + event.getTargetAccountNumber()));

        source.setBalance(source.getBalance().subtract(event.getAmount()));
        target.setBalance(target.getBalance().add(event.getAmount()));

        accountQueryRepository.save(source);
        accountQueryRepository.save(target);
    }

    private void saveTransaction(MoneyTransactionEvent event) {
        TransactionView tx = mapper.toEntity(event);
        transactionQueryRepository.save(tx);
    }

}
