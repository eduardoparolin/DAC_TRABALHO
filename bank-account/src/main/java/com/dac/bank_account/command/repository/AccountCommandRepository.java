package com.dac.bank_account.command.repository;

import com.dac.bank_account.command.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountCommandRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByClientId(Long ClientId);

    @Modifying
    @Query("UPDATE Account a SET a.managerId = :newManagerId WHERE a.managerId = :oldManagerId")
    int updateManagerForAccounts(Long oldManagerId, Long newManagerId);

    Optional<Account> findFirstByManagerIdAndBalanceGreaterThanOrderByBalanceAsc(Long managerId, BigDecimal zero);

}
