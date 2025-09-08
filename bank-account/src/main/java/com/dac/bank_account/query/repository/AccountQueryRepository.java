package com.dac.bank_account.query.repository;

import com.dac.bank_account.query.entity.AccountView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountQueryRepository extends JpaRepository<AccountView, Long> {
    Optional<AccountView> findByAccountNumber(String accountNumber);
    List<AccountView> findByManagerId(Long managerId);
}
