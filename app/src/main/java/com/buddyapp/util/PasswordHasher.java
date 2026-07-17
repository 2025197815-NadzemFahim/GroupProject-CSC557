package com.buddyapp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final int ITERATIONS = 10_000;
    private static final int SALT_BYTES = 16;

    private PasswordHasher() {
    }

    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return toHex(salt);
    }

    public static String hash(String password, String saltHex) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(fromHex(saltHex));
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            for (int i = 1; i < ITERATIONS; i++) {
                md.reset();
                digest = md.digest(digest);
            }
            return toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be available on every Java platform.
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    public static boolean verify(String password, String saltHex, String expectedHashHex) {
        return constantTimeEquals(hash(password, saltHex), expectedHashHex);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return out;
    }
}
