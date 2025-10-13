package com.bank.client.controller;

import com.bank.client.dto.ClientReportResponse;
import com.bank.client.dto.ClientRequest;
import com.bank.client.dto.ClientResponse;
import com.bank.client.dto.RejectClientRequest;
import com.bank.client.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest req,
            UriComponentsBuilder uriBuilder) {
        ClientResponse res = service.create(req);
        return ResponseEntity.created(
                uriBuilder.path("/cliente/{id}").buildAndExpand(res.getId()).toUri()).body(res);
    }

    @GetMapping
    public List<ClientResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ClientResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientRequest req) {
        return service.update(id, req);
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

    @PostMapping("/{id}/reject")
    public ClientResponse rejectClient(
            @PathVariable Long id,
            @Valid @RequestBody RejectClientRequest request) {
        return service.rejectClient(id, request);
    }

    @PostMapping("/{id}/approve")
    public ClientResponse approveClient(@PathVariable Long id) {
        return service.approveClient(id);
    }

    @GetMapping("/status/{status}")
    public List<ClientReportResponse> getClientsByStatus(
            @PathVariable String status,
            @RequestParam(required = false) Long managerId) {
        return service.getClientsByStatus(status, managerId);
    }
}
