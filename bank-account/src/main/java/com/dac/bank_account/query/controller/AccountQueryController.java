package com.dac.bank_account.query.controller;

import com.dac.bank_account.query.dto.*;
import com.dac.bank_account.query.service.AccountQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/query/contas")
public class AccountQueryController {

    private final AccountQueryService accountQueryService;

    public AccountQueryController(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    @PostMapping("/{numero}/saldo")
    public ResponseEntity<BalanceResponseDTO> balance(@PathVariable String numero){
        BalanceResponseDTO dto = accountQueryService.getBalance(numero);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{numero}/extrato")
    public ResponseEntity<StatementResponseDTO> statement(@PathVariable String numero){
        StatementResponseDTO dto = accountQueryService.getStatement(numero);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{numero}")
    public ResponseEntity<AccountResponseDTO> getAccountDetails(@PathVariable String numero){
        AccountResponseDTO dto = accountQueryService.getAccountDetails(numero);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{idGerente}/gerente")
    public ResponseEntity<ManagerAccountsResponseDTO> getManagerAccounts(@PathVariable String idGerente){
        ManagerAccountsResponseDTO dto = accountQueryService.getManagerAccounts(idGerente);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{idGerente}/gerente/top3")
    public ResponseEntity<List<AccountResponseDTO>> getTop3Accounts(@PathVariable String idGerente){
        List<AccountResponseDTO> dto = accountQueryService.getTop3Accounts(idGerente);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("cliente/{idCliente}")
    public ResponseEntity<AccountResponseDTO> getClientAccount(@PathVariable String idCliente){
        AccountResponseDTO dto = accountQueryService.getClientAccount(idCliente);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/buscar")
    public ResponseEntity<?> getAccounts(
            @RequestBody AccountsRequestDTO request,
            @RequestParam(name = "incluirTransacoes", required = false, defaultValue = "false") boolean incluirTransacoes){

        if(incluirTransacoes){
            List<StatementResponseDTO> accountsWithStatement =
                    accountQueryService.getAccountsWithTransactions(request);
            return ResponseEntity.ok(accountsWithStatement);
        }else {
            List<AccountResponseDTO> accounts =  accountQueryService.getAccounts(request);
            return ResponseEntity.ok(accounts);
        }
    }

    @GetMapping("{idGerente}/gerente/contasAtivas")
    public ResponseEntity<ManagerAccountsResponseDTO> getActiveAccountsByManager(@PathVariable String idGerente){
        ManagerAccountsResponseDTO dto = accountQueryService.getActiveAccountsByManager(idGerente);
        return ResponseEntity.ok(dto);
    }
}
