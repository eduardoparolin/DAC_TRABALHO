package com.bank.manager.controller;

import com.bank.manager.dto.ManagerDTO;
import com.bank.manager.dto.external.ClientOverviewDTO;
import com.bank.manager.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/managers")
public class ManagerController {

    private final ManagerService service;

    public ManagerController(ManagerService service) {
        this.service = service;
    }

    // R17 - Insert manager
    @PostMapping
    public ResponseEntity<ManagerDTO> create(@RequestBody ManagerDTO dto) {
        return ResponseEntity.status(201).body(service.createManager(dto));
    }

    // R19 - List managers
    @GetMapping
    public ResponseEntity<List<ManagerDTO>> list() {
        return ResponseEntity.ok(service.listManagers());
    }

    // R20 - Update manager
    @PutMapping("/{cpf}")
    public ResponseEntity<ManagerDTO> update(@PathVariable String cpf, @RequestBody ManagerDTO dto) {
        return ResponseEntity.ok(service.updateManager(cpf, dto));
    }

    // R18 - Delete manager
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(@PathVariable String cpf) {
        service.deleteManager(cpf);
        return ResponseEntity.noContent().build();
    }

    // R10 - Approve client (SAGA start)
    @PostMapping("/{managerCpf}/approve/{clientCpf}")
    public ResponseEntity<Void> approveClient(@PathVariable String managerCpf, @PathVariable String clientCpf) {
        service.approveClient(managerCpf, clientCpf);
        return ResponseEntity.accepted().build();
    }

    // R11 - Reject client
    @PostMapping("/{managerCpf}/reject/{clientCpf}")
    public ResponseEntity<Void> rejectClient(@PathVariable String managerCpf,
                                             @PathVariable String clientCpf,
                                             @RequestParam String reason) {
        service.rejectClient(managerCpf, clientCpf, reason);
        return ResponseEntity.accepted().build();
    }

    // R12 - List clients of manager
    @GetMapping("/{managerCpf}/clients")
    public ResponseEntity<List<ClientOverviewDTO>> clients(@PathVariable String managerCpf) {
        return ResponseEntity.ok(service.getClientsOfManager(managerCpf));
    }

    // R13 - Consult client by CPF
    @GetMapping("/clients/{cpf}")
    public ResponseEntity<ClientOverviewDTO> clientByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(service.getClientByCpf(cpf));
    }

    // R14 - Top3 clients
    @GetMapping("/{managerCpf}/clients/top3")
    public ResponseEntity<List<ClientOverviewDTO>> top3(@PathVariable String managerCpf) {
        return ResponseEntity.ok(service.getTop3ClientsByManager(managerCpf));
    }
}
