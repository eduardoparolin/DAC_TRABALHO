package com.bank.manager.service;

import com.bank.manager.dto.ManagerDTO;
import com.bank.manager.dto.ManagerUpdateDTO;
import com.bank.manager.dto.ManagersDTO;
import com.bank.manager.dto.ManagersResponseDTO;
import com.bank.manager.exception.custom.ApiException;
import com.bank.manager.model.Manager;
import com.bank.manager.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository repository;

    public ManagerDTO create(ManagerDTO dto) {
        Manager manager = new Manager();
        manager.setCpf(dto.cpf());
        manager.setName(dto.name());
        manager.setEmail(dto.email());
        manager.setType(dto.type());

        Manager saved = repository.save(manager);
        return ManagerDTO.fromEntity(saved);
    }

    public List<ManagerDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(ManagerDTO::fromEntity)
                .toList();
    }

    public ManagerDTO getByCpf(String cpf) {
        return ManagerDTO.fromEntity(findByCpf(cpf));
    }

    public Manager findByCpf(String cpf) {
        Optional<Manager> manager = repository.findByCpf(cpf);
        if(manager.isEmpty()) {
            throw new ApiException("Manager not found with cpf: " + cpf, HttpStatus.NOT_FOUND);
        }
        return manager.get();
    }

    public ManagerDTO update(String cpf, ManagerUpdateDTO dto) {
        Manager existingManager = findByCpf(cpf);
        Manager updatedManager = updateValidValues(existingManager, dto);
        return ManagerDTO.fromEntity(repository.save(updatedManager));
    }

    private Manager updateValidValues(Manager manager, ManagerUpdateDTO dto) {
        if(Objects.nonNull(dto.name())) {
            manager.setName(dto.name());
        }

        if(Objects.nonNull(dto.email())) {
            manager.setEmail(dto.email());
        }

        if(Objects.nonNull(dto.type())) {
            manager.setType(dto.type());
        }

        return manager;
    }

    public Manager findById(Long id) {
        Optional<Manager> opManager = repository.findById(id);
        if(opManager.isEmpty()) {
            throw new ApiException("Manager not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return opManager.get();
    }

    public void deleteByCpf(String cpf) {
        Manager manager = findByCpf(cpf);
        repository.delete(manager);
    }

    public List<ManagersResponseDTO> getManagers(ManagersDTO request){
        List<Long> managerIds = request.managerIds();
        List<Manager> managers = repository.findByIdIn(managerIds);

        return managers.stream()
                .map(manager -> new ManagersResponseDTO(
                        manager.getId(),
                        manager.getCpf(),
                        manager.getName(),
                        manager.getEmail(),
                        manager.getType()
                ))
                .toList();
    }

    public Manager getWithLessAccounts() {
      return repository.findFirstByOrderByAccountCountAsc();
    }

    public Manager save(Manager manager) {
      return repository.save(manager);
    }
}
