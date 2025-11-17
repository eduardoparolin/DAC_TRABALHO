package com.bank.client.repository;

import com.bank.client.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    List<Client> findByIdIn(List<Long> clientIds);

    @Query("SELECT c FROM Client c WHERE c.cpf = :cpf AND c.status != 'REJEITADO'")
    Optional<Client> findByCpf(@Param("cpf") String cpf);
}
