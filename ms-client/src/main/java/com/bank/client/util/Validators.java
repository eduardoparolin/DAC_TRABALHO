package com.bank.client.util;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Funções utilitárias simples de validação/sanitização para o domínio de Client.
 * Mantém as regras centralizadas; pode evoluir para validar CPF "de verdade".
 */
public final class Validators {

    private static final Pattern ONLY_DIGITS = Pattern.compile("\\d+");
    private static final Pattern EMAIL_BASIC = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validators() { /* util class */ }

    /** Remove tudo que não é dígito. */
    public static String onlyDigits(String value) {
        if (value == null) return null;
        return value.replaceAll("\\D", "");
    }

    /** CPF válido de forma básica: exatamente 11 dígitos (sem checagem de dígitos verificadores). */
    public static boolean isBasicCpf(String value) {
        String v = onlyDigits(value);
        return v != null && v.length() == 11 && ONLY_DIGITS.matcher(v).matches();
    }

    /** Email válido de forma básica (não exaustiva, mas suficiente pra early validation). */
    public static boolean isBasicEmail(String email) {
        return email != null && EMAIL_BASIC.matcher(email).matches();
    }

    /** Lança IllegalArgumentException se a condição não for atendida. */
    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

    /** Exige não nulo/não vazio (apenas espaços não é válido). */
    public static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " não pode ser vazio");
        }
        return value;
    }

    /** Compara valores desconsiderando nulos e espaços. */
    public static boolean equalsIgnoreSpace(String a, String b) {
        String a1 = a == null ? null : a.trim();
        String b1 = b == null ? null : b.trim();
        return Objects.equals(a1, b1);
    }
}
