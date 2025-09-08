package com.bank.client.service;

import com.bank.client.dto.ClientRequest;
import com.bank.client.dto.ClientResponse;
import com.bank.client.entities.Client;
import com.bank.client.exception.DuplicateResourceException;
import com.bank.client.exception.NotFoundException;
import com.bank.client.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository repo;

    public ClientService(ClientRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ClientResponse create(ClientRequest req) {
        if (repo.existsByCpf(req.getCpf())) {
            throw new DuplicateResourceException("CPF já cadastrado");
        }
        if (repo.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email já cadastrado");
        }
        Client e = new Client();
        e.setNome(req.getNome());
        e.setEmail(req.getEmail());
        e.setCpf(req.getCpf());
        e.setTelefone(req.getTelefone());
        e.setSalario(req.getSalario());
        e = repo.save(e);
        return toResponse(e);
    }

    public List<ClientResponse> list() {
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ClientResponse getById(Long id) {
        Client e = repo.findById(id).orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + id));
        return toResponse(e);
    }

    @Transactional
    public ClientResponse update(Long id, ClientRequest req) {
        Client e = repo.findById(id).orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + id));

        // checa duplicidade apenas se mudou
        if (!Objects.equals(e.getCpf(), req.getCpf()) && repo.existsByCpf(req.getCpf())) {
            throw new DuplicateResourceException("CPF já cadastrado");
        }
        if (!Objects.equals(e.getEmail(), req.getEmail()) && repo.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email já cadastrado");
        }

        e.setNome(req.getNome());
        e.setEmail(req.getEmail());
        e.setCpf(req.getCpf());
        e.setTelefone(req.getTelefone());
        e.setSalario(req.getSalario());

        e = repo.save(e);
        return toResponse(e);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Cliente não encontrado: " + id);
        }
        repo.deleteById(id);
    }

    private ClientResponse toResponse(Client e) {
        return new ClientResponse(
                e.getId(),
                e.getNome(),
                e.getEmail(),
                e.getCpf(),
                e.getTelefone(),
                e.getSalario()
        );
    }
}
