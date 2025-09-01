package com.dac.auth.dto.payload;

import com.dac.auth.enums.Action;
import com.dac.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthPayload implements IAuthPayload{
    private Action action;
    private String messageSource;
    private AuthPayloadData data;

    @Override
    public String getIdUser() {
        return getData().getIdUser();
    }

    @Override
    public String getEmail() {
        return getData().getEmail();
    }

    @Override
    public String getPassword() {
        return getData().getPassword();
    }

    @Override
    public Role getRole() {
        return getData().getRole();
    }
}
