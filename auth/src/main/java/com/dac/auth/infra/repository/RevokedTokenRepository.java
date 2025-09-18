package com.dac.auth.infra.repository;

import com.dac.auth.model.RevokedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RevokedTokenRepository extends MongoRepository<RevokedToken, String> {
    boolean existsByToken(String token);
}
