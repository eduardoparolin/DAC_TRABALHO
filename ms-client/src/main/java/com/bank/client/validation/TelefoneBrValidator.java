package com.bank.client.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneBrValidator implements ConstraintValidator<TelefoneBr, String> {

    private boolean allowFormatted;

    @Override
    public void initialize(TelefoneBr constraintAnnotation) {
        this.allowFormatted = constraintAnnotation.allowFormatted();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Deixa @Size/@NotBlank cuidarem do vazio se quiser
        if (value == null || value.isBlank()) return true;

        String digits = allowFormatted ? value.replaceAll("\\D", "") : value;
        // 10 (fixo) ou 11 (celular) dígitos
        if (digits.length() != 10 && digits.length() != 11) return false;

        // evita sequências repetidas (000..., 111..., etc.)
        return digits.chars().distinct().count() > 1;
    }
}
