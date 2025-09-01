package com.bank.client.mapper;

import com.bank.client.dto.ClientRequest;
import com.bank.client.dto.ClientResponse;
import com.bank.client.entities.Client;

public final class ClientMapper {

    private ClientMapper() { /* util class */ }

    public static Client toEntity(ClientRequest req) {
        if (req == null) return null;
        Client e = new Client();
        e.setNome(req.getNome());
        e.setEmail(req.getEmail());
        e.setCpf(req.getCpf());
        e.setTelefone(req.getTelefone());
        e.setSalario(req.getSalario());
        return e;
    }

    public static void copyToEntity(ClientRequest req, Client target) {
        if (req == null || target == null) return;
        target.setNome(req.getNome());
        target.setEmail(req.getEmail());
        target.setCpf(req.getCpf());
        target.setTelefone(req.getTelefone());
        target.setSalario(req.getSalario());
    }

    public static ClientResponse toResponse(Client e) {
        if (e == null) return null;
        return new ClientResponse(
                e.getId(),
                e.getNome(),
                e.getEmail(),
                e.getCpf(),
                e.getTelefone(),
                e.getSalario()
        );
    }
}
