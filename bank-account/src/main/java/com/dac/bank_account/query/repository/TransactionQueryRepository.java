package com.dac.bank_account.query.repository;

import com.dac.bank_account.query.entity.TransactionView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TransactionQueryRepository extends JpaRepository<TransactionView, Long> {
    List<TransactionView> findBySourceAccountNumber(String sourceAccountNumber);
}
