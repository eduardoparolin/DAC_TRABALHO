package com.dac.auth.infra.configuration.security;

import com.dac.auth.exception.custom.ApiException;
import com.dac.auth.infra.repository.RevokedTokenRepository;
import com.dac.auth.model.RevokedToken;
import com.dac.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String ROLES_CLAIM = "roles";
    private final RevokedTokenRepository revokedTokenRepository;

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        // Include timestamp in JTI to guarantee uniqueness even for concurrent requests
        String jti = UUID.randomUUID().toString() + "-" + now.toEpochMilli();
        Instant exp = now.plusMillis(Long.parseLong(expiration));

        log.info("Generating token for user {} with JTI: {} at {}", user.getEmail(), jti, now);

        String token = Jwts.builder()
                .issuer("bantads")
//                .id(jti)
                .claim(Claims.ID, jti)
                .claim(ROLES_CLAIM, List.of("ROLE_" + user.getRole()))
                .claim("email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(getSigningKey())
                .compact();

        log.info("Generated token (first 50 chars): {}", token.substring(0, Math.min(50, token.length())));

        return token;
    }

    public UsernamePasswordAuthenticationToken isValid(String token) {
        if (token != null) {

            Claims body = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token.replace(TOKEN_PREFIX, "").trim())
                    .getPayload();

            String user = body.get(Claims.ID, String.class);
            if (user != null) {
                List<String> cargos = body.get(ROLES_CLAIM, List.class);
                List<SimpleGrantedAuthority> authorities = cargos.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }
        }
        return null;
    }

    public void invalidateToken(String token) {
        log.info("Invalidating token: {}", token.substring(0, Math.min(50, token.length())));
        Date expirationDate = extractExpiration(token);

        RevokedToken revoked = new RevokedToken(
                null,
                token,
                expirationDate.toInstant(),
                Instant.now()
        );

        RevokedToken saved = revokedTokenRepository.save(revoked);
        log.info("Token revoked and saved to database with ID: {}", saved.getId());
    }

    public boolean isTokenRevoked(String token) {
        boolean revoked = revokedTokenRepository.existsByToken(token);
        log.debug("Checking if token is revoked: {} - Result: {}",
                 token.substring(0, Math.min(20, token.length())), revoked);
        return revoked;
    }

    public Date extractExpiration(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token.replace(TOKEN_PREFIX, "").trim())
                .getPayload();

        return claims.getExpiration();
    }

}
