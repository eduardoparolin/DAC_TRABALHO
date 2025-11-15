package com.dac.auth.infra.repository;

import com.dac.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);
    Optional<User> findByClientId(Long id);
    Optional<User> findByManagerId(Long id);
}
