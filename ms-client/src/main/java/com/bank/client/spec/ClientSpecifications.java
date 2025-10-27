package com.bank.client.spec;

import com.bank.client.entities.Client;
import org.springframework.data.jpa.domain.Specification;

public final class ClientSpecifications {

    private ClientSpecifications() { }

    public static Specification<Client> hasCpf(String cpfDigits) {
        if (cpfDigits == null || cpfDigits.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("cpf"), cpfDigits);
    }

    public static Specification<Client> hasEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return (root, q, cb) -> cb.equal(cb.lower(root.get("email")), email.toLowerCase());
    }

    public static Specification<Client> nameContains(String nome) {
        if (nome == null || nome.isBlank()) return null;
        String like = "%" + nome.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("name")), like);
    }

    public static Specification<Client> hasManagerId(Long managerId) {
        if (managerId == null) return null;
        return (root, q, cb) -> cb.equal(root.get("managerId"), managerId);
    }

    public static Specification<Client> hasStatus(Client.ClientStatus status) {
        if (status == null) return null;
        return (root, q, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Client> and(Specification<Client> a, Specification<Client> b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.and(b);
    }
}
