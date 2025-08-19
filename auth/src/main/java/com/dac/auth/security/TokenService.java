package com.dac.auth.security;

import com.dac.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String ROLES_CLAIM = "roles";

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + Long.parseLong(expiration));

        return TOKEN_PREFIX + " " +
                Jwts.builder()
                        .setIssuer("bantads")
                        .claim(Claims.ID, user.getId().toString())
                        .claim(ROLES_CLAIM, List.of("ROLE_" + user.getRole()))
                        .setIssuedAt(now)
                        .setExpiration(exp)
                        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                        .compact();
    }

    public UsernamePasswordAuthenticationToken isValid(String token) {
        if (token != null) {
            Claims body = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, "").trim())
                    .getBody();

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
}
