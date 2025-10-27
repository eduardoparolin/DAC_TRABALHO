package com.bank.manager.exception.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiException extends RuntimeException {
    private HttpStatus status;
    private List<String> errors = new ArrayList<>();

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
