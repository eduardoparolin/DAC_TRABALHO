package com.dac.bank_account.command.repository;

import com.dac.bank_account.command.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionCommandRepository extends JpaRepository<Transaction, Long> {
}
