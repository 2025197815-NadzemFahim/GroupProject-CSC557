package com.buddyapp.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatorsTest {

    @Test
    public void isNotBlankRejectsNullAndWhitespace() {
        assertFalse(Validators.isNotBlank(null));
        assertFalse(Validators.isNotBlank("   "));
        assertTrue(Validators.isNotBlank("Ali"));
    }

    @Test
    public void hasMinLengthTrimsBeforeChecking() {
        assertFalse(Validators.hasMinLength("ab", 3));
        assertFalse(Validators.hasMinLength("  ab  ", 3));
        assertTrue(Validators.hasMinLength("abc", 3));
    }

    @Test
    public void emailIsOptionalButMustBeValidWhenPresent() {
        assertTrue(Validators.isValidEmail(""));
        assertTrue(Validators.isValidEmail(null));
        assertTrue(Validators.isValidEmail("ali@mail.com"));
        assertFalse(Validators.isValidEmail("not-an-email"));
        assertFalse(Validators.isValidEmail("missing@domain"));
    }
}
