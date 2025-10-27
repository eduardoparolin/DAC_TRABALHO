package com.bank.client.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Validators {

    private static final Pattern ONLY_DIGITS = Pattern.compile("\\d+");
    private static final Pattern EMAIL_BASIC = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validators() { /* utility class */ }

    public static String onlyDigits(String value) {
        if (value == null) return null;
        return value.replaceAll("\\D", "");
    }

    public static boolean isBasicCpf(String value) {
        String v = onlyDigits(value);
        return v != null && v.length() == 11 && ONLY_DIGITS.matcher(v).matches();
    }

    public static boolean isBasicEmail(String email) {
        return email != null && EMAIL_BASIC.matcher(email).matches();
    }

    public static BigDecimal round2(BigDecimal v) {
        if (v == null) return null;
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

    public static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " não pode ser vazio");
        }
        return value;
    }

      public static boolean equalsIgnoreSpace(String a, String b) {
        String a1 = a == null ? null : a.trim();
        String b1 = b == null ? null : b.trim();
        return Objects.equals(a1, b1);
    }
}
