package com.bank.client.controller;

import com.bank.client.dto.ClientReportResponse;
import com.bank.client.dto.ClientResponse;
import com.bank.client.dto.ClientsRequestDTO;
import com.bank.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
