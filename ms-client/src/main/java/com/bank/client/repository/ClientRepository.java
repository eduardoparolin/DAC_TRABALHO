package com.bank.client.repository;

import com.bank.client.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    List<Client> findByIdIn(List<Long> clientIds);
}
