package com.dac.bank_account.config.consumer.handlers;

import com.dac.bank_account.command.repository.AccountCommandRepository;
import com.dac.bank_account.command.service.AccountCommandService;
import com.dac.bank_account.config.consumer.AccountSagaEvent;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler for reassigning all accounts when a manager is deleted.
 * According to R18 specification:
 * - When removing a manager, their accounts should be assigned to the manager with the fewest accounts
 * - This handler finds that manager and performs the reassignment atomically
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReassignAccountsForDeletedManagerHandler implements AccountMessageHandler {

    private final AccountCommandRepository accountCommandRepository;

    @Override
    @Transactional("commandTransactionManager")
    public void handle(AccountSagaEvent event) {
        Long deletedManagerId = event.getOldManagerId();

        if (deletedManagerId == null) {
            throw new IllegalArgumentException("oldManagerId is required for REASSIGN_ACCOUNTS_FOR_DELETED_MANAGER action");
        }

        log.info("Finding manager with fewest accounts to reassign accounts from deleted manager ID: {}", deletedManagerId);

        // Find the manager with the least accounts (excluding the deleted manager)
        // This query will return managers with fewest accounts from the account table
        List<Long> managersWithLeastAccounts = accountCommandRepository.findManagersWithLeastAccounts();

        if (managersWithLeastAccounts.isEmpty()) {
            throw new IllegalStateException("No managers found in the system");
        }

        // Filter out the deleted manager
        Long targetManagerId = managersWithLeastAccounts.stream()
                .filter(id -> !id.equals(deletedManagerId))
                .findFirst()
                .orElse(null);

        // If no other manager exists, check if there are managers without any accounts yet
        // (they won't show up in the account table)
        if (targetManagerId == null) {
            throw new IllegalStateException(
                "Cannot delete manager - no other manager available to reassign accounts. " +
                "This should have been prevented by validation."
            );
        }

        log.info("Reassigning all accounts from manager {} to manager {} (fewest accounts)",
                 deletedManagerId, targetManagerId);

        // Perform the reassignment atomically
        int updatedCount = accountCommandRepository.updateManagerForAccounts(deletedManagerId, targetManagerId);

        log.info("Successfully reassigned {} accounts from manager {} to manager {}",
                 updatedCount, deletedManagerId, targetManagerId);

        // Store the target manager ID in the event for the saga to use
        event.setNewManagerId(targetManagerId);
        event.setManagerId(targetManagerId);
    }
}
