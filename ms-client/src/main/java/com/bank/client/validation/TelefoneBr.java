package com.bank.client.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TelefoneBrValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefoneBr {
    String message() default "Telefone inválido (use 10 ou 11 dígitos)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowFormatted() default true; // aceita (xx)xxxxx-xxxx, etc.
}
