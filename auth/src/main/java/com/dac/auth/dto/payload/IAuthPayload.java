package com.dac.auth.dto.payload;

import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.enums.Action;
import com.dac.auth.enums.Role;

public interface IAuthPayload {
    Action getAction();
    String getIdUser();
    String getEmail();
    String getPassword();
    Role getRole();
    String getRequestedById();
    SagaContext getSagaContext();
}
