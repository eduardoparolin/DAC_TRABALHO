package com.bank.client.service;

import com.bank.client.dto.ClientRequest;
import com.bank.client.dto.ClientResponse;
import com.bank.client.dto.PageResponse;
import com.bank.client.entities.Client;
import com.bank.client.exception.DuplicateResourceException;
import com.bank.client.exception.NotFoundException;
import com.bank.client.repository.ClientRepository;
import com.bank.client.spec.ClientSpecifications;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        String cpf = normalizeCpf(req.getCpf());
        if (repo.existsByCpf(cpf)) throw new DuplicateResourceException("CPF já cadastrado");
        if (repo.existsByEmail(req.getEmail())) throw new DuplicateResourceException("Email já cadastrado");

        Client e = new Client();
        e.setNome(req.getNome());
        e.setEmail(req.getEmail());
        e.setCpf(cpf); // normalizado
        e.setTelefone(req.getTelefone());
        e.setSalario(round2(req.getSalario()));
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
        String cpf = normalizeCpf(req.getCpf());

        if (!Objects.equals(e.getCpf(), cpf) && repo.existsByCpf(cpf)) {
            throw new DuplicateResourceException("CPF já cadastrado");
        }
        if (!Objects.equals(e.getEmail(), req.getEmail()) && repo.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email já cadastrado");
        }

        e.setNome(req.getNome());
        e.setEmail(req.getEmail());
        e.setCpf(cpf);
        e.setTelefone(req.getTelefone());
        e.setSalario(round2(req.getSalario()));
        e = repo.save(e);
        return toResponse(e);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Cliente não encontrado: " + id);
        repo.deleteById(id);
    }

    /** Busca paginada/filtrada por cpf, email e nome (contém, case-insensitive). */
    public PageResponse<ClientResponse> search(String cpf, String email, String nome, int page, int size, String sort) {
        String cpfDigits = normalizeCpf(cpf);
        Specification<Client> spec = null;
        spec = ClientSpecifications.and(spec, ClientSpecifications.hasCpf(cpfDigits));
        spec = ClientSpecifications.and(spec, ClientSpecifications.hasEmail(email));
        spec = ClientSpecifications.and(spec, ClientSpecifications.nameContains(nome));

        Sort sortObj = parseSort(sort, "nome,asc");
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sortObj);

        Page<Client> clients = (spec == null) ? repo.findAll(pageable) : repo.findAll(spec, pageable);

        List<ClientResponse> content = clients.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                clients.getNumber(),
                clients.getSize(),
                clients.getTotalElements(),
                clients.getTotalPages(),
                sortObj.toString()
        );
    }

    // ==== helpers ====
    private ClientResponse toResponse(Client e) {
        return new ClientResponse(e.getId(), e.getNome(), e.getEmail(), e.getCpf(), e.getTelefone(), e.getSalario());
    }

    private String normalizeCpf(String value) {
        if (value == null) return null;
        return value.replaceAll("\\D", ""); // mantém só dígitos
    }

    private BigDecimal round2(BigDecimal v) {
        if (v == null) return null;
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private Sort parseSort(String sort, String fallback) {
        String s = (sort == null || sort.isBlank()) ? fallback : sort;
        String[] parts = s.split(",", 2);
        String prop = parts[0].trim();
        String dir = parts.length > 1 ? parts[1].trim() : "asc";
        return "desc".equalsIgnoreCase(dir) ? Sort.by(prop).descending() : Sort.by(prop).ascending();
    }
}
