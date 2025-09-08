package com.bank.client.controller;

import com.bank.client.dto.ClientRequest;
import com.bank.client.dto.ClientResponse;
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
        // manter coerência com o prefixo singular:
        return ResponseEntity.created(
                uriBuilder.path("/cliente/{id}").buildAndExpand(res.getId()).toUri()
        ).body(res);
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
}
