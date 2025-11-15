package com.dac.auth.dto.payload;

import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.enums.Action;
import com.dac.auth.enums.Role;

public interface IAuthPayload {
    Action getAction();
    Long getId();
    String getCpf();
    String getEmail();
    String getPassword();
    Role getRole();
    Long getRequestedById();
    SagaContext getSagaContext();
    String getName();
}
