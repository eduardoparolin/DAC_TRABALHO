package com.dac.bank_account.config.consumer;

import com.dac.bank_account.config.consumer.events.*;
import com.dac.bank_account.config.producer.AccountProducer;
import com.dac.bank_account.command.service.AccountCommandService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.dac.bank_account.enums.AccountAction;


@EnableRabbit
@Service
public class AccountSagaHandler {

    private final AccountCommandService accountCommandService;
    private final RabbitTemplate rabbitTemplate;
    private final AccountProducer accountProducer;

    public AccountSagaHandler(AccountCommandService accountCommandService, RabbitTemplate rabbitTemplate, AccountProducer accountProducer) {
        this.accountCommandService = accountCommandService;
        this.rabbitTemplate = rabbitTemplate;
        this.accountProducer = accountProducer;
    }

    @RabbitListener(queues = "create.account.saga")
    public void handleCreateAccountSaga(CreateAccountSagaEvent event) {
        try{
            accountCommandService.createAccount(event.getClientId(), event.getSalary(), event.getManagerId());
            accountProducer.sendSuccessResult(AccountAction.CREATE_ACCOUNT);
        }catch (Exception e){
            accountProducer.sendFailureResult(AccountAction.CREATE_ACCOUNT, e.getMessage());
        }
    }

    @RabbitListener(queues = "update.status.saga")
    public void handleUpdateStatusSaga(UpdateStatusSagaEvent event) {
        try {
            accountCommandService.updateAccountStatus(event.getClientId(), event.getIsApproved());
            accountProducer.sendSuccessResult(AccountAction.UPDATE_ACCOUNT_STATUS);
        } catch (Exception e) {
            accountProducer.sendFailureResult(AccountAction.UPDATE_ACCOUNT_STATUS, e.getMessage());
        }
    }

    @RabbitListener(queues = "update.client.saga")
    public void handleUpdateLimitSaga(UpdateLimitSagaEvent event) {
        try {
            accountCommandService.setLimit(event.getClientId(), event.getSalary());
            accountProducer.sendSuccessResult(AccountAction.UPDATE_LIMIT);
        } catch (Exception e) {
            accountProducer.sendFailureResult(AccountAction.UPDATE_LIMIT, e.getMessage());
        }
    }

    @RabbitListener(queues = "delete.manager.saga")
    public void handleDeleteManagerSaga(ReassignManagerSagaEvent event) {
        try {
            accountCommandService.reassignManager(event.getOldManagerId(), event.getNewManagerId());
            accountProducer.sendSuccessResult(AccountAction.DELETE_MANAGER);
        } catch (Exception e) {
            accountProducer.sendFailureResult(AccountAction.DELETE_MANAGER, e.getMessage());
        }
    }

    @RabbitListener(queues = "new.manager.saga")
    public void handleNewManagerSaga(AssignNewManagerSagaEvent event) {
        try {
            accountCommandService.assignAccountToNewManager(event.getOldManagerId(), event.getNewManagerId());
            accountProducer.sendSuccessResult(AccountAction.NEW_MANAGER);
        } catch (Exception e) {
            accountProducer.sendFailureResult(AccountAction.NEW_MANAGER, e.getMessage());
        }
    }
}
