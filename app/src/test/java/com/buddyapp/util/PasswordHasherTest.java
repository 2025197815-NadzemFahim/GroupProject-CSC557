package com.buddyapp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordHasherTest {

    @Test
    public void sameSaltAndPasswordProduceSameHash() {
        String salt = PasswordHasher.generateSalt();
        assertEquals(PasswordHasher.hash("secret123", salt),
                PasswordHasher.hash("secret123", salt));
    }

    @Test
    public void differentSaltsProduceDifferentHashes() {
        String hashA = PasswordHasher.hash("secret123", PasswordHasher.generateSalt());
        String hashB = PasswordHasher.hash("secret123", PasswordHasher.generateSalt());
        assertNotEquals(hashA, hashB);
    }

    @Test
    public void verifySucceedsForCorrectPassword() {
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash("secret123", salt);
        assertTrue(PasswordHasher.verify("secret123", salt, hash));
    }

    @Test
    public void verifyFailsForWrongPassword() {
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash("secret123", salt);
        assertFalse(PasswordHasher.verify("wrongpass", salt, hash));
    }

    @Test
    public void generatedSaltsAreRandom() {
        assertNotEquals(PasswordHasher.generateSalt(), PasswordHasher.generateSalt());
    }
}
