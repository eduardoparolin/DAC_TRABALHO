package com.dac.auth.dto.payload;

import com.dac.auth.enums.Action;
import com.dac.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthPayload {
    private Action action;
    private String messageSource;
    private AuthPayloadData data;
}
