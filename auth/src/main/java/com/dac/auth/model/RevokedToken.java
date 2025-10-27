package com.dac.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("Token")
public class RevokedToken {

    @Id
    private String id;

    private String token;

    private Instant expiration;

    private Instant revokedAt;
}
