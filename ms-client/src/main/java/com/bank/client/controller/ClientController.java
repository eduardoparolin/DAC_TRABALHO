package com.bank.client.controller;

import com.bank.client.dto.*;
import com.bank.client.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.*;
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
        return ResponseEntity.created(uriBuilder.path("/clientes/{id}").buildAndExpand(res.getId()).toUri())
                .body(res);
    }

    @GetMapping
    public List<ClientResponse> list() {
        return service.list();
    }
}
