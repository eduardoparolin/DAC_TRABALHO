package com.dac.auth.dto.user;

import com.dac.auth.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateDTO  {

    @NotBlank
    private String id;

    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}
