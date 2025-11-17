package com.dac.auth.model;

import com.dac.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Document("user")
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails {

    @Id
    private String id;
    private Long clientId;
    private Long managerId;
    private String email;
    private String password;
    private Role role;
    private String cpf;
    private String name;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(role);
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public Long getUserId() {
        if (Role.CLIENTE.equals(this.role)) {
            return this.clientId;
        }
        return this.managerId;
    }

    public void setUserId(Long id) {
        if (Role.CLIENTE.equals(this.role)) {
            this.clientId = id;
            return;
        }
        this.managerId = id;
    }
}
