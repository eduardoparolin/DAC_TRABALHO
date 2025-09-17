package com.dac.bank_account.command.repository;

import com.dac.bank_account.command.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountCommandRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);

    @Modifying
    @Query("UPDATE Account a SET a.managerId = :newManagerId WHERE a.managerId = :oldManagerId")
    int updateManagerForAccounts(Long oldManagerId, Long newManagerId);

    long countByManagerId(Long managerId);

    @Query(value = """
    SELECT *
    FROM Account a
    WHERE a.balance > 0
      AND a.managerId IN (
          SELECT s.managerId
          FROM Account s
          GROUP BY s.managerId
          HAVING COUNT(s) = (
              SELECT MAX(cnt)
              FROM (
                  SELECT COUNT(x) as cnt
                  FROM Account x
                  GROUP BY x.managerId
              )
          )
      )
    ORDER BY a.balance ASC
    LIMIT 1
    """, nativeQuery = true)
    Optional<Account> findAccountWithLowestPositiveBalanceFromTopManagers();
}
