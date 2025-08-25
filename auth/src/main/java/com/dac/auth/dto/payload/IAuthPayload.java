package com.dac.auth.dto.payload;

import com.dac.auth.enums.Action;

public interface IAuthPayload {
    Action getAction();
    String getMessageSource();
    Long getIdUser();
    String getEmail();
    String getPassword();
}
