package com.dac.bank_account.config.consumer.handlers;

import com.dac.bank_account.command.service.AccountCommandService;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.consumer.AccountSagaEvent;
import com.dac.bank_account.enums.AccountAction;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssignManagerHandler implements AccountMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(AssignManagerHandler.class);
    private final AccountCommandService accountCommandService;

    @Override
    public void handle(AccountSagaEvent event) {
        if (event.getAction() == AccountAction.ASSIGN_MANAGER_WITH_LEAST_ACCOUNTS) {
            log.info("Finding manager with least accounts for saga {}", event.getSagaId());

            Long managerId = accountCommandService.findManagerWithLeastAccountsRandomTieBreaker();

            if (managerId == null) {
                log.error("No managers found to assign for saga {}", event.getSagaId());
                throw new RuntimeException("No managers available to assign to client");
            }

            log.info("Assigned manager {} (selected from managers with least accounts) for saga {}",
                    managerId, event.getSagaId());

            event.setManagerId(managerId);
            return;
        }

        log.warn("Unexpected action {} in AssignManagerHandler", event.getAction());
    }
}
