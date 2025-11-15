package com.dac.auth.dto.user;

import com.dac.auth.enums.Role;
import com.dac.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String email;
    private String cpf;
    private Role role;

    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getEmail(),
                user.getCpf(),
                user.getRole()
        );
    }

    @Override
    public String toString() {
        return String.format(
                "{\"id\": %s, \"email\": \"%s\", \"cpf\": \"%s\", \"role\": \"%s\"}",
                id != null ? id : null,
                email,
                cpf,
                role != null ? role.name() : null
        );
    }

}
