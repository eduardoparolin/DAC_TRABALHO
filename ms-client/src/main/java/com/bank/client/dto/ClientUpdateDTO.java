package com.bank.client.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientUpdateDTO {

    @NotNull
    private Long clientId;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String phone;

    @NotNull(message = "Salário é obrigatório")
    @DecimalMin(value = "0.00", message = "Salário deve ser maior ou igual a zero")
    private BigDecimal salary;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    private String street;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complement;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos numéricos")
    private String zipCode;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String city;

    @NotBlank(message = "Estado é obrigatório")
    @Pattern(regexp = "[A-Z]{2}", message = "Estado deve ser uma sigla de 2 letras maiúsculas")
    private String state;
}
