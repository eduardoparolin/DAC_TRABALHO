package com.dac.auth.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthPayloadData {
    private String idUser;
    private String email;
    private String password;
}
