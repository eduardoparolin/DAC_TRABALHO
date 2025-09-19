package com.dac.bank_account.command.saga;

import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.events.saga.*;
import com.dac.bank_account.command.saga.events.failed.*;
import com.dac.bank_account.command.saga.events.success.*;
import com.dac.bank_account.command.service.AccountCommandService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@EnableRabbit
@Service
public class AccountSagaHandler {

    private final AccountCommandService accountCommandService;
    private final RabbitTemplate rabbitTemplate;

    public AccountSagaHandler(AccountCommandService accountCommandService, RabbitTemplate rabbitTemplate) {
        this.accountCommandService = accountCommandService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "create.account.saga")
    public void handleCreateAccountSaga(CreateAccountSagaEvent event) {
        try {
            Account account = accountCommandService.createAccount(event.getClientId(),event.getSalary(), event.getManagerId());
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "create.account.saga.success",
                    new AccountCreatedSagaEvent(account.getId(), account.getClientId(), account.getManagerId()));
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "create.account.saga.failure",
                    new AccountCreationFailedSagaEvent(event.getClientId(), e.getMessage()));
        }
    }

    @RabbitListener(queues = "update.status.saga")
    public void handleUpdateStatusSaga(UpdateStatusSagaEvent event) {
        try {
            Account account = accountCommandService.updateAccountStatus(event.getClientId(), event.getIsApproved());
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "update.status.saga.success",
                    new AccountStatusUpdatedSagaEvent(account.getClientId(), account.getStatus().name()));
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "update.status.saga.failure",
                    new AccountStatusUpdateFailedSagaEvent(event.getClientId(), e.getMessage()));
        }
    }

    @RabbitListener(queues = "update.client.saga")
    public void handleUpdateLimitSaga(UpdateLimitSagaEvent event) {
        try {
            Account account = accountCommandService.setLimit(event.getClientId(), event.getSalary());
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "update.account.saga.success",
                    new AccountLimitUpdatedSagaEvent(account.getClientId(), account.getLimitAmount()));
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "update.account.saga.failure",
                    new AccountLimitUpdateFailedSagaEvent(event.getClientId(), e.getMessage()));
        }
    }

    @RabbitListener(queues = "delete.manager.saga")
    public void handleDeleteManagerSaga(ReassignManagerSagaEvent event) {
        try {
            accountCommandService.reassignManager(event.getOldManagerId(), event.getNewManagerId());
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "delete.manager.saga.success",
                    new ManagerReassignedSagaEvent(event.getOldManagerId()));
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "delete.manager.saga.failure",
                    new ManagerReassignmentFailedSagaEvent(event.getOldManagerId(), e.getMessage()));
        }
    }

    @RabbitListener(queues = "new.manager.saga")
    public void handleNewManagerSaga(AssignNewManagerSagaEvent event) {
        try {
            Account account = accountCommandService.assignAccountToNewManager(event.getOldManagerId(), event.getNewManagerId());
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "new.manager.saga.success",
                    new AccountAssignedToNewManagerSagaEvent(account.getAccountNumber(), account.getManagerId()));

        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "new.manager.saga.failure",
                    new AccountAssignmentFailedSagaEvent(event.getNewManagerId(), e.getMessage()));
        }
    }
}
