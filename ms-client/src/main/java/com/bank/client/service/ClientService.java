package com.bank.client.service;

import com.bank.client.dto.*;
import com.bank.client.entities.Client;
import com.bank.client.exception.DuplicateResourceException;
import com.bank.client.exception.NotFoundException;
import com.bank.client.repository.ClientRepository;
import com.bank.client.spec.ClientSpecifications;
import com.bank.client.dto.AccountSagaEvent;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.client.events.ClienteEventPublisher;
import com.bank.client.infra.producer.AccountProducer;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientService {

    private final ClientRepository repo;

    public ClientService(ClientRepository repo) {
        this.repo = repo;
    }

    @Autowired(required = false)
    private ClienteEventPublisher publisher;

    @Autowired(required = false)
    private AccountProducer accountProducer;

    @Transactional
    public Long create(ClientCreateDTO req) {
        if (repo.existsByCpf(req.getCpf()))
            throw new DuplicateResourceException("CPF já cadastrado");
        if (repo.existsByEmail(req.getEmail()))
            throw new DuplicateResourceException("Email já cadastrado");

        Client client = new Client();
        client.setName(req.getName());
        client.setEmail(req.getEmail());
        client.setCpf(req.getCpf());
        client.setPhone(req.getPhone());
        client.setSalary(req.getSalary());
        client.setStreet(req.getStreet());
        client.setComplement(req.getComplement());
        client.setZipCode(req.getZipCode());
        client.setCity(req.getCity());
        client.setState(req.getState());
        client.setStatus(Client.ClientStatus.AGUARDANDO_APROVACAO);
        client.setCreationDate(OffsetDateTime.now());

        client = repo.save(client);
        return client.getId();
    }

    @Transactional
    public void setManagerId(Long clientId, Long managerId) {
        Client client = repo.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + clientId));
        client.setManagerId(managerId);
        repo.save(client);
    }

    public List<ClientResponse> list() {
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ClientResponse getById(Long id) {
        Client client = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + id));
        return toResponse(client);
    }

    public ClientResponse getByCpf(String cpf) {
        String cpfDigits = normalizeCpf(cpf);
        Client client = repo.findByCpf(cpfDigits)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado com CPF: " + cpf));
        return toResponse(client);
    }

    @Transactional
    public void update(ClientUpdateDTO req) {
        Client client = repo.findById(req.getClientId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + req.getClientId()));

        if (req.getEmail() != null &&
                !Objects.equals(client.getEmail(), req.getEmail()) &&
                repo.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email já cadastrado");
        }

        if (req.getName() != null)
            client.setName(req.getName());
        if (req.getEmail() != null)
            client.setEmail(req.getEmail());
        if (req.getPhone() != null)
            client.setPhone(req.getPhone());
        if (req.getSalary() != null)
            client.setSalary(req.getSalary());
        if (req.getStreet() != null)
            client.setStreet(req.getStreet());
        if (req.getComplement() != null)
            client.setComplement(req.getComplement());
        if (req.getZipCode() != null)
            client.setZipCode(req.getZipCode());
        if (req.getCity() != null)
            client.setCity(req.getCity());
        if (req.getState() != null)
            client.setState(req.getState());

        repo.save(client);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new NotFoundException("Cliente não encontrado: " + id);
        repo.deleteById(id);
    }

    // R16
    public List<ClientReportResponse> getClientsReport() {
        Sort sort = Sort.by("name").ascending();
        List<Client> clients = repo.findAll(sort);

        return clients.stream()
                .map(this::toReportResponse)
                .collect(Collectors.toList());
    }

    // R13
    public List<ClientReportResponse> getClientsByManager(Long managerId, String cpf, String name) {
        String cpfDigits = normalizeCpf(cpf);

        Specification<Client> spec = ClientSpecifications.hasManagerId(managerId);
        spec = ClientSpecifications.and(spec, ClientSpecifications.hasCpf(cpfDigits));
        spec = ClientSpecifications.and(spec, ClientSpecifications.nameContains(name));

        Sort sort = Sort.by("name").ascending();
        List<Client> clients = repo.findAll(spec, sort);

        return clients.stream()
                .map(this::toReportResponse)
                .collect(Collectors.toList());
    }

    // R11
    @Transactional
    public void rejectClient(ClientRejectDTO req) {
        Long clientId = req.getClientId();
        String reason = req.getRejectionReason();
        Client client = repo.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + clientId));

        if (client.getStatus().equals(Client.ClientStatus.APROVADO)) {
            throw new IllegalArgumentException("Cliente ja aprovado");
        }

        client.setStatus(Client.ClientStatus.REJEITADO);
        client.setRejectionReason(reason);
        client.setApprovalDate(OffsetDateTime.now());

        repo.save(client);
        // TODO: Chamar Mailer para Notificar Cliente
    }

    // Link account to client without approving
    @Transactional
    public void linkAccount(ClientLinkAccountDTO req) {
        Long clientId = req.getClientId();
        Long accountNumber = req.getAccountNumber();
        log.info("Linking account {} to client {}", accountNumber, clientId);
        Client client = repo.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + clientId));

        log.info("Found client: id={}, name={}, current accountId={}", client.getId(), client.getName(), client.getAccountId());
        client.setAccountId(accountNumber);
        repo.save(client);
        log.info("Saved client with new accountId: {}", client.getAccountId());
    }

    // R10
    @Transactional
    public void approveClient(ClientApproveDTO req) {
        Long clientId = req.getClientId();
        Long managerId = req.getManagerId();
        Long accountNumber = req.getAccountNumber();
        Client client = repo.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + clientId));

        if (client.getStatus().equals(Client.ClientStatus.REJEITADO)) {
            throw new IllegalArgumentException("Cliente ja rejeitdo");
        }

        client.setStatus(Client.ClientStatus.APROVADO);
        client.setManagerId(managerId);
        // Only set accountId if it's not already set and accountNumber is provided
        if (client.getAccountId() == null && accountNumber != null) {
            client.setAccountId(accountNumber);
        }
        // Use the existing accountId if accountNumber is not provided or already set
        Long finalAccountNumber = client.getAccountId() != null ? client.getAccountId() : accountNumber;
        client.setApprovalDate(OffsetDateTime.now());

        repo.save(client);

        if (accountProducer != null) {
            AccountSagaEvent event = new AccountSagaEvent();
            event.setSagaId(UUID.randomUUID().toString());
            event.setAction("UPDATE_ACCOUNT_STATUS");
            event.setClientId(clientId);
            event.setIsApproved(true);
            event.setAccountId(finalAccountNumber);
            accountProducer.sendUpdateAccountStatusEvent(event);
        }
    }

    // R9
    public List<ClientReportResponse> getClientsByStatus(String status, Long managerId) {
        Client.ClientStatus clientStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                clientStatus = Client.ClientStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status inválido: " + status);
            }
        }

        Specification<Client> spec = ClientSpecifications.hasStatus(clientStatus);
        spec = ClientSpecifications.and(spec, ClientSpecifications.hasManagerId(managerId));

        Sort sort = Sort.by("creationDate").ascending();
        List<Client> clients = repo.findAll(spec, sort);

        return clients.stream()
                .map(this::toReportResponse)
                .collect(Collectors.toList());
    }

    public List<ClientResponse> getClients(ClientsRequestDTO req) {
        List<Long> clientIds = req.clientIds();
        List<Client> clients = repo.findByIdIn(clientIds);

        return clients.stream()
                .map(this::toResponse)
                .toList();
    }

    private ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setName(client.getName());
        response.setEmail(client.getEmail());
        response.setCpf(client.getCpf());
        response.setPhone(client.getPhone());
        response.setSalary(client.getSalary());
        response.setAccountId(client.getAccountId());
        response.setStatus(client.getStatus() != null ? client.getStatus().name() : null);
        response.setRejectionReason(client.getRejectionReason());
        response.setManagerId(client.getManagerId());
        response.setCreationDate(client.getCreationDate());
        response.setApprovalDate(client.getApprovalDate());
        response.setStreet(client.getStreet());
        response.setComplement(client.getComplement());
        response.setZipCode(client.getZipCode());
        response.setCity(client.getCity());
        response.setState(client.getState());
        return response;
    }

    private ClientReportResponse toReportResponse(Client e) {
        return new ClientReportResponse(
                e.getCpf(),
                e.getName(),
                e.getEmail(),
                e.getSalary(),
                e.getAccountId(),
                e.getManagerId());
    }

    private String normalizeCpf(String value) {
        if (value == null)
            return null;
        return value.replaceAll("\\D", "");
    }

    public boolean clientExists(String cpf) {
        String cpfDigits = normalizeCpf(cpf);
        return repo.existsByCpf(cpfDigits);
    }
}
