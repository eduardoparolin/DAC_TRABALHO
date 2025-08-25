package com.bank.client.service;

import com.bank.client.dto.*;
import com.bank.client.entities.Client;
import com.bank.client.exception.DuplicateResourceException;
import com.bank.client.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final ClientRepository repo;
    public ClientService(ClientRepository repo) { this.repo = repo; }

    public ClientResponse create(ClientRequest req) {
        if (repo.existsByCpf(req.getCpf()))   throw new DuplicateResourceException("CPF já cadastrado");
        if (repo.existsByEmail(req.getEmail())) throw new DuplicateResourceException("Email já cadastrado");

        Client e = new Client();
        e.setNome(req.getNome()); e.setEmail(req.getEmail()); e.setCpf(req.getCpf());
        e.setTelefone(req.getTelefone()); e.setSalario(req.getSalario());
        e = repo.save(e);
        return new ClientResponse(e.getId(), e.getNome(), e.getEmail(), e.getCpf(), e.getTelefone(), e.getSalario());
    }

    public List<ClientResponse> list() {
        return repo.findAll().stream()
                .map(e -> new ClientResponse(e.getId(), e.getNome(), e.getEmail(), e.getCpf(), e.getTelefone(), e.getSalario()))
                .collect(Collectors.toList());
    }
}
