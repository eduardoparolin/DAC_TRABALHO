package com.dac.bank_account.query.repository;

import com.dac.bank_account.query.entity.TransactionView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TransactionQueryRepository extends JpaRepository<TransactionView, Long> {
    //Retornar as transações por número da conta de origem ou destino
    //Retorna as transacoes de transferência onde a conta de origem ou destino corresponde ao número fornecido
    List<TransactionView> findBySourceAccountNumberOrTargetAccountNumber(String sourceAccountNumber, String targetAccountNumber);
















}
