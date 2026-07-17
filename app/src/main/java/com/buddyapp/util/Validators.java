package com.buddyapp.util;

import java.util.regex.Pattern;

public final class Validators {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validators() {
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean hasMinLength(String value, int min) {
        return value != null && value.trim().length() >= min;
    }

    public static boolean isValidEmail(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return EMAIL_PATTERN.matcher(value.trim()).matches();
    }
}
