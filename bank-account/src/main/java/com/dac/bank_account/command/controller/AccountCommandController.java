package com.dac.bank_account.command.controller;

import com.dac.bank_account.command.dto.request.AccountRequestDTO;
import com.dac.bank_account.command.dto.request.LimitRequestDTO;
import com.dac.bank_account.command.dto.request.MovementRequestDTO;
import com.dac.bank_account.command.dto.request.TransferRequestDTO;
import com.dac.bank_account.command.dto.response.AccountResponseDTO;
import com.dac.bank_account.command.dto.response.MovementResponseDTO;
import com.dac.bank_account.command.dto.response.TransferResponseDTO;
import com.dac.bank_account.command.service.AccountCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/command/contas")
public class AccountCommandController {
    private final AccountCommandService accountCommandService;

    public AccountCommandController(AccountCommandService accountCommandService) {
        this.accountCommandService = accountCommandService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody @Valid AccountRequestDTO request) {
        AccountResponseDTO dto = accountCommandService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/{numero}/deposito")
    public ResponseEntity<MovementResponseDTO> deposit(@PathVariable String numero, @RequestBody @Valid MovementRequestDTO request) {
        MovementResponseDTO dto = accountCommandService.deposit(numero, request.valor());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{numero}/sacar")
    public ResponseEntity<MovementResponseDTO> withdraw(@PathVariable String numero , @RequestBody @Valid MovementRequestDTO request) {
        MovementResponseDTO dto = accountCommandService.withdraw(numero, request.valor());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{numero}/transferir")
    public ResponseEntity<TransferResponseDTO> transfer(@PathVariable String numero, @RequestBody @Valid TransferRequestDTO request) {
        TransferResponseDTO dto = accountCommandService.transfer(numero, request.valor(), request.destino());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{numero}/limite")
    public ResponseEntity<AccountResponseDTO> setLimit(@PathVariable String numero, @RequestBody @Valid LimitRequestDTO request) {
        AccountResponseDTO dto = accountCommandService.setLimit(numero, request.salario());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/reatribuir/{gerenteOrigem}/{gerenteDestino}")
    public ResponseEntity<Void> reassignManager(@PathVariable Long gerenteOrigem, @PathVariable Long gerenteDestino) {
        accountCommandService.reassignManager(gerenteOrigem, gerenteDestino);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/redistribuir/{novoGerente}")
    public ResponseEntity<AccountResponseDTO> assignToNewManager(@PathVariable Long novoGerente) {
        return accountCommandService.assignAccountToNewManager(novoGerente)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
