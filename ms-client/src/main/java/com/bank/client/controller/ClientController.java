package com.bank.client.controller;

import com.bank.client.dto.*;
import com.bank.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clientes")
public class ClientController {

    private final ClientService service;

    @GetMapping
    public List<ClientResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ClientResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/cpf/{cpf}")
    public ClientResponse getByCpf(@PathVariable String cpf) {
        return service.getByCpf(cpf);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report")
    public List<ClientReportResponse> getClientsReport() {
        return service.getClientsReport();
    }

    @GetMapping("/manager/{managerId}")
    public List<ClientReportResponse> getClientsByManager(
            @PathVariable Long managerId,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String name) {
        return service.getClientsByManager(managerId, cpf, name);
    }

    @GetMapping("/status/{status}")
    public List<ClientReportResponse> getClientsByStatus(
            @PathVariable String status,
            @RequestParam(required = false) Long managerId) {
        return service.getClientsByStatus(status, managerId);
    }

    @PostMapping("/clientes")
    public ResponseEntity<List<ClientResponse>> getCliente(@RequestBody ClientsRequestDTO request) {
        return ResponseEntity.ok(service.getClients(request));
    }

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<Map<String, Long>> approveClient(@PathVariable String cpf) {
        ClientResponse client = service.getByCpf(cpf);
        ClientApproveDTO dto = new ClientApproveDTO();
        dto.setClientId(client.getId());
        dto.setManagerId(client.getManagerId());
        dto.setAccountNumber(client.getAccountId());
        service.approveClient(dto);
        return ResponseEntity.ok(Map.of("clientId", client.getId()));
    }

    @PostMapping("/{cpf}/reprovar")
    public ResponseEntity<Void> rejectClient(
            @PathVariable String cpf,
            @RequestBody Map<String, String> body) {
        ClientResponse client = service.getByCpf(cpf);
        ClientRejectDTO dto = new ClientRejectDTO();
        dto.setClientId(client.getId());
        dto.setRejectionReason(body.get("rejectionReason"));
        service.rejectClient(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/existe/{cpf}")
    public ResponseEntity<Map<String, Boolean>> checkClientExists(@PathVariable String cpf) {
        boolean exists = service.clientExists(cpf);
        return ResponseEntity.ok(Map.of("clienteExiste", exists));
    }
}
