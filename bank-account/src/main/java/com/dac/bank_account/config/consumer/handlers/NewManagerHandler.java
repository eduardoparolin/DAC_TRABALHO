package com.dac.bank_account.config.consumer.handlers;

import com.dac.bank_account.command.dto.AccountReassignmentCandidateDTO;
import com.dac.bank_account.command.dto.request.NewManagerDTO;
import com.dac.bank_account.command.service.AccountCommandService;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.consumer.AccountSagaEvent;
import com.dac.bank_account.enums.AccountAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class NewManagerHandler implements AccountMessageHandler {

    private final AccountCommandService accountCommandService;

    @Override
    public void handle(AccountSagaEvent event){
        if (event.getAction() == AccountAction.FIND_ACCOUNT_FOR_NEW_MANAGER) {
            Optional<AccountReassignmentCandidateDTO> candidate = accountCommandService.findAccountForNewManager();
            candidate.ifPresent(result -> {
                event.setOldManagerId(result.getOldManagerId());
                event.setAccountId(result.getAccountId());
                event.setAccountNumber(result.getAccountNumber());
            });
            return;
        }

        NewManagerDTO dto = new NewManagerDTO(
                event.getNewManagerId(),
                event.getOldManagerId(),
                event.getAccountId()
        );
        Map<String, Long> result = accountCommandService.assignAccountToNewManager(dto);

        if (!result.isEmpty()) {
            event.setOldManagerId(result.get("oldManagerId"));
            event.setAccountId(result.get("accountId"));
        }
    }
}
