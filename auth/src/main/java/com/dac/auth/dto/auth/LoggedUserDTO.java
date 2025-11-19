package com.dac.auth.dto.auth;

import com.dac.auth.enums.Role;
import com.dac.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoggedUserDTO {
    private Long id;
    private String name;
    private String cpf;
    private String email;
    private Role role;

    public static LoggedUserDTO fromUser(User user) {
        return new LoggedUserDTO(
                user.getUserId(),
                user.getName(),
                user.getCpf(),
                user.getEmail(),
                user.getRole()
        );
    }
}
