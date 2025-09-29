package com.bank.manager.service;

import com.bank.manager.ManagerDTO;
import com.bank.manager.model.Manager;
import com.bank.manager.repository.ManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    private final ManagerRepository repository;

    // Injeção via construtor → melhor prática
    public ManagerService(ManagerRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public ManagerDTO create(ManagerDTO dto) {
        Manager manager = new Manager();
        manager.setCpf(dto.cpf());
        manager.setName(dto.name());
        manager.setEmail(dto.email());
        manager.setType(dto.type());

        Manager saved = repository.save(manager);
        return toDTO(saved);
    }

    // READ - find all
    public List<ManagerDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ManagerDTO getByCpf(String cpf) {
        return ManagerDTO.fromEntity(findByCpf(cpf));
    }

    public Manager findByCpf(String cpf) {
        Optional<Manager> manager = repository.findById(cpf);
        if(manager.isEmpty()) {
            throw new RuntimeException("Manager not found with cpf: " + cpf);
        }
        return manager.get();
    }

    // UPDATE
    public Optional<ManagerDTO> update(String cpf, ManagerDTO dto) {
        return repository.findById(cpf).map(manager -> {
            manager.getCpf();
            manager.getName();
            manager.getEmail();
            manager.getType();

            Manager updated = repository.save(manager);
            return toDTO(updated);
        });
    }

    // DELETE
    public void deleteByCpf(String cpf) {
        repository.deleteById(cpf);
    }

    // Helper de conversão Entity -> DTO
    private ManagerDTO toDTO(Manager manager) {
        return new ManagerDTO(
                manager.getCpf(),
                manager.getName(),
                manager.getEmail(),
                manager.getType()
        );
    }
}
