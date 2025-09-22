package com.bank.manager.service;

import com.bank.manager.dto.ManagerDTO;
import com.bank.manager.entity.Manager;
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
        manager.setNome(dto.nome());
        manager.setEmail(dto.email());
        manager.setTelefone(dto.telefone());
        manager.setTipo(dto.tipo());

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

    // READ - find by cpf
    public Optional<ManagerDTO> findByCpf(String cpf) {
        return repository.findById(cpf).map(this::toDTO);
    }

    // UPDATE
    public Optional<ManagerDTO> update(String cpf, ManagerDTO dto) {
        return repository.findById(cpf).map(manager -> {
            manager.setNome(dto.nome());
            manager.setEmail(dto.email());
            manager.setTelefone(dto.telefone());
            manager.setTipo(dto.tipo());

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
                manager.getNome(),
                manager.getEmail(),
                manager.getTelefone(),
                manager.getTipo()
        );
    }
}
