package com.bank.client.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

    private boolean allowFormatted;

    @Override
    public void initialize(Cpf constraintAnnotation) {
        this.allowFormatted = constraintAnnotation.allowFormatted();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Deixa @NotBlank/@NotNull cuidarem do vazio/nulo
        if (value == null || value.isBlank()) return true;

        String digits = allowFormatted ? value.replaceAll("\\D", "") : value;
        if (digits.length() != 11) return false;

        // Rejeita sequências iguais (000..., 111..., etc.)
        if (digits.chars().distinct().count() == 1) return false;

        try {
            int dv1 = calcDv(digits, 9, 10);  // usa os 9 primeiros, pesos 10..2
            int dv2 = calcDv(digits, 10, 11); // usa os 10 primeiros, pesos 11..2
            return dv1 == Character.getNumericValue(digits.charAt(9))
                    && dv2 == Character.getNumericValue(digits.charAt(10));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private int calcDv(String digits, int len, int startWeight) {
        int sum = 0;
        for (int i = 0; i < len; i++) {
            int num = Character.getNumericValue(digits.charAt(i));
            sum += num * (startWeight - i);
        }
        int mod = sum % 11;
        int dv = 11 - mod;
        return (dv >= 10) ? 0 : dv;
    }
}
