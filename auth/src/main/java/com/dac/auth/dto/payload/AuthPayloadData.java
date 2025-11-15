package com.dac.auth.dto.payload;

import com.dac.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthPayloadData {
    private Long id;
    private String cpf;
    private String email;
    private String password;
    private String name;
    private Role role;
}
