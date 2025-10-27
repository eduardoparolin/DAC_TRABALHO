package com.dac.bank_account.command.repository;

import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AccountCommandRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByClientId(Long ClientId);

    @Modifying
    @Query("UPDATE Account a SET a.managerId = :newManagerId WHERE a.managerId = :oldManagerId")
    int updateManagerForAccounts(Long oldManagerId, Long newManagerId);

    @Query(
            value = """
        SELECT managerId
        FROM account
        GROUP BY managerId
        ORDER BY COUNT(*) DESC, SUM(balance) ASC
        LIMIT 1
    """,
            nativeQuery = true
    )
    Long findManagerWithMostAccountsAndLowestBalance();


    @Query("SELECT a.managerId, COUNT(a) FROM Account a GROUP BY a.managerId")
    List<Object[]> findManagerAccountCountsRaw();

    @Query(
            value = """
            SELECT managerId
            FROM account
            GROUP BY managerId
            ORDER BY COUNT(*) ASC
            LIMIT 1
            """,
            nativeQuery = true
    )
    Long findManagerWithLeastAccounts();

    Optional<Account> findFirstByManagerIdAndStatus(Long managerId, AccountStatus status);
}


