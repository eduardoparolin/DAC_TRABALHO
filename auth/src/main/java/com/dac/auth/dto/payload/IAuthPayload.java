package com.dac.auth.dto.payload;

import com.dac.auth.enums.Action;
import com.dac.auth.enums.Role;

public interface IAuthPayload {
    Action getAction();
    String getMessageSource();
    String getIdUser();
    String getEmail();
    String getPassword();
    Role getRole();
}
