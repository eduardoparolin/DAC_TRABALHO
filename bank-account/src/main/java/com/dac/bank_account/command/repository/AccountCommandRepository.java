package com.dac.bank_account.command.repository;

import com.dac.bank_account.command.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountCommandRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
