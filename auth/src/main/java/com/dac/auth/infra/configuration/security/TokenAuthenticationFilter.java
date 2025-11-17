package com.dac.auth.infra.configuration.security;

import com.dac.auth.infra.repository.RevokedTokenRepository;
import com.dac.auth.service.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final String BEARER = "Bearer ";
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null) {
            String requestPath = request.getRequestURI();
            if (requestPath.equals("/auth/logout")) {
                UsernamePasswordAuthenticationToken usuarioEntity = tokenService.isValid(token);
                if (usuarioEntity != null) {
                    SecurityContextHolder.getContext().setAuthentication(usuarioEntity);
                }
                filterChain.doFilter(request, response);
                return;
            }

            if (revokedTokenRepository.existsByToken(token)) {
                Map<String, Object> body = new HashMap<>();
                body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                body.put("message", "Token inválidado.");
                body.put("timestamp", LocalDateTime.now().toString());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(body));
                response.getWriter().flush();
                return;
            }

            UsernamePasswordAuthenticationToken usuarioEntity = tokenService.isValid(token);
            SecurityContextHolder.getContext().setAuthentication(usuarioEntity);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            return null;
        }
        return authHeader.replace(BEARER, "").trim();
    }

}
