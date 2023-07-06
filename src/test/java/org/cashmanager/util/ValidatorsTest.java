package org.cashmanager.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorsTest {
    @Test
    public void zeroOrLess_should_return_true_when_integer_zero() {
        assertTrue(Validators.zeroOrLess(0));
    }

    @Test
    public void zeroOrLess_should_return_true_when_integer_less_than_zero() {
        assertTrue(Validators.zeroOrLess(-1));
    }

    @Test
    public void zeroOrLess_should_return_false_when_integer_greater_than_zero() {
        assertFalse(Validators.zeroOrLess(1));
    }

    @Test
    public void lessThanZero_should_return_true_when_integer_less_than_zero() {
        assertTrue(Validators.lessThanZero(-1));
    }

    @Test
    public void lessThanZero_should_return_false_when_integer_equals_zero() {
        assertFalse(Validators.lessThanZero(0));
    }

    @Test
    public void lessThanZero_should_return_false_when_integer_greater_than_zero() {
        assertFalse(Validators.lessThanZero(1));
    }
}
