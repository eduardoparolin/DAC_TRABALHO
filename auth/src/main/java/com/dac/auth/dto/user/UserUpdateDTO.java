package com.dac.auth.dto.user;

import com.dac.auth.enums.Role;
import com.dac.auth.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateDTO {

    private String email;

    private String name;

    private String password;

    public UserUpdateDTO(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static UserUpdateDTO fromEntity(User user) {
        return new UserUpdateDTO(user.getEmail(), user.getName());
    }
}
