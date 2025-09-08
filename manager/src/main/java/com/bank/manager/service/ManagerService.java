package com.bank.manager.service;

import com.bank.manager.config.RabbitConfig;
import com.bank.manager.dto.ManagerDTO;
import com.bank.manager.dto.events.ClientApprovalEvent;
import com.bank.manager.dto.events.ClientRejectionEvent;
import com.bank.manager.dto.external.ClientOverviewDTO;
import com.bank.manager.dto.external.ClientSummaryDTO;
import com.bank.manager.entity.Manager;
import com.bank.manager.repository.ManagerRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.springframework.http.HttpStatus.*;

@Service
public class ManagerService {

    private final ManagerRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    private final String accountServiceBase;
    private final String clientServiceBase;
    private final Random random = new Random();

    public ManagerService(ManagerRepository repository,
                          RabbitTemplate rabbitTemplate,
                          RestTemplate restTemplate,
                          @Value("${external.account.base-url}") String accountServiceBase,
                          @Value("${external.client.base-url}") String clientServiceBase) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
        this.accountServiceBase = accountServiceBase;
        this.clientServiceBase = clientServiceBase;
    }

    /* -------------------- CRUD Gerente (R17,R19,R20,R18 partial) -------------------- */

    public ManagerDTO createManager(ManagerDTO dto) {
        if (dto == null || dto.cpf() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid manager payload");
        }
        if (repository.existsById(dto.cpf())) {
            throw new ResponseStatusException(CONFLICT, "Manager already exists");
        }
        Manager m = new Manager(dto.cpf(), dto.nome(), dto.email(), dto.telefone());
        Manager saved = repository.save(m);
        return toDto(saved);
    }

    public List<ManagerDTO> listManagers() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<ManagerDTO> getByCpf(String cpf) {
        return repository.findById(cpf).map(this::toDto);
    }

    public ManagerDTO updateManager(String cpf, ManagerDTO dto) {
        Manager m = repository.findById(cpf)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Manager not found"));
        m.setNome(dto.nome());
        m.setEmail(dto.email());
        m.setTelefone(dto.telefone());
        Manager saved = repository.save(m);
        return toDto(saved);
    }

    public void deleteManager(String cpf) {
        long total = repository.count();
        if (total <= 1) {
            throw new ResponseStatusException(BAD_REQUEST, "Cannot delete last manager");
        }

        // Ask account service to reassign accounts of this manager (endpoint must be implemented in MS-Conta)
        String url = accountServiceBase + "/accounts/reassign-from-manager/" + cpf;
        try {
            restTemplate.postForLocation(url, null);
        } catch (RestClientException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to reassign accounts: " + e.getMessage());
        }

        repository.deleteById(cpf);
        // optionally publish an event about manager removed
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "manager.removed", cpf);
    }

    /* -------------------- Approve / Reject client (R9,R10,R11) -------------------- */

    public void approveClient(String managerCpf, String clientCpf) {
        // validate manager exists
        if (!repository.existsById(managerCpf)) {
            throw new ResponseStatusException(NOT_FOUND, "Manager not found");
        }

        // 1) get client summary from MS-Cliente
        String clientUrl = clientServiceBase + "/clients/" + clientCpf + "/summary";
        ClientSummaryDTO client;
        try {
            ResponseEntity<ClientSummaryDTO> resp = restTemplate.getForEntity(clientUrl, ClientSummaryDTO.class);
            client = resp.getBody();
        } catch (RestClientException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to fetch client: " + e.getMessage());
        }

        if (client == null) throw new ResponseStatusException(NOT_FOUND, "Client not found");

        // 2) compute limit
        BigDecimal salary = client.getSalary() == null ? BigDecimal.ZERO : client.getSalary();
        BigDecimal limit = BigDecimal.ZERO;
        if (salary.compareTo(BigDecimal.valueOf(2000)) >= 0) {
            limit = salary.divide(BigDecimal.valueOf(2));
        }

        // 3) generate password
        String generatedPassword = randomPassword();

        // 4) publish approval event to RabbitMQ for SAGA orchestration
        ClientApprovalEvent event = new ClientApprovalEvent(clientCpf, managerCpf, generatedPassword, limit, client.getEmail());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_APPROVED, event);
    }

    public void rejectClient(String managerCpf, String clientCpf, String reason) {
        if (!repository.existsById(managerCpf)) {
            throw new ResponseStatusException(NOT_FOUND, "Manager not found");
        }

        String clientUrl = clientServiceBase + "/clients/" + clientCpf + "/summary";
        ClientSummaryDTO client = null;
        try {
            ResponseEntity<ClientSummaryDTO> resp = restTemplate.getForEntity(clientUrl, ClientSummaryDTO.class);
            client = resp.getBody();
        } catch (RestClientException ignored) {}

        String email = client != null ? client.getEmail() : null;
        ClientRejectionEvent event = new ClientRejectionEvent(clientCpf, managerCpf, reason, email);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_REJECTED, event);
    }

    /* -------------------- Queries that rely on account service (R12,R13,R14) -------------------- */

    public List<ClientOverviewDTO> getClientsOfManager(String managerCpf) {
        String url = accountServiceBase + "/accounts/manager/" + managerCpf + "/clients";
        try {
            ClientOverviewDTO[] arr = restTemplate.getForObject(url, ClientOverviewDTO[].class);
            return arr == null ? List.of() : List.of(arr);
        } catch (RestClientException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to get clients: " + e.getMessage());
        }
    }

    public ClientOverviewDTO getClientByCpf(String cpf) {
        String url = accountServiceBase + "/accounts/client/" + cpf + "/overview";
        try {
            return restTemplate.getForObject(url, ClientOverviewDTO.class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to get client overview: " + e.getMessage());
        }
    }

    public List<ClientOverviewDTO> getTop3ClientsByManager(String managerCpf) {
        String url = accountServiceBase + "/accounts/manager/" + managerCpf + "/top3";
        try {
            ClientOverviewDTO[] arr = restTemplate.getForObject(url, ClientOverviewDTO[].class);
            return arr == null ? List.of() : List.of(arr);
        } catch (RestClientException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to get top3: " + e.getMessage());
        }
    }

    /* -------------------- Helpers -------------------- */

    private ManagerDTO toDto(Manager m) {
        return new ManagerDTO(m.getCpf(), m.getNome(), m.getEmail(), m.getTelefone());
    }

    private String randomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }
}
