package org.cashmanager.util;

import org.cashmanager.contract.Currency;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.cashmanager.util.Validators.validateDenominationCurrency;
import static org.junit.jupiter.api.Assertions.*;

public class ValidatorsTest {
    @Test
    void zeroOrLess_should_return_true_when_integer_zero() {
        assertTrue(Validators.zeroOrLess(0));
    }

    @Test
    void zeroOrLess_should_return_true_when_integer_less_than_zero() {
        assertTrue(Validators.zeroOrLess(-1));
    }

    @Test
    void zeroOrLess_should_return_false_when_integer_greater_than_zero() {
        assertFalse(Validators.zeroOrLess(1));
    }

    @Test
    void lessThanZero_should_return_true_when_integer_less_than_zero() {
        assertTrue(Validators.lessThanZero(-1));
    }

    @Test
    void lessThanZero_should_return_false_when_integer_equals_zero() {
        assertFalse(Validators.lessThanZero(0));
    }

    @Test
    void lessThanZero_should_return_false_when_integer_greater_than_zero() {
        assertFalse(Validators.lessThanZero(1));
    }


    @Test
    void validateDenominationCurrency_should_not_throw_iae_when_all_denominations_match_currency_denominations(){
        validateDenominationCurrency(Currency.GBP, Map.of(10,5,20,3));
    }

    @Test
    void validateDenominationCurrency_should_throw_iae_when_one_denomination_not_match_currency_denominations(){
        assertThrows(IllegalArgumentException.class, ()->validateDenominationCurrency(Currency.GBP, Map.of(10,5,7,3)));
    }

    @Test
    void validateDenominationCurrency_should_not_throw_iae_when_denomination_matches_currency_denominations(){
        validateDenominationCurrency(Currency.GBP, 10);
    }

    @Test
    void validateDenominationCurrency_should_throw_iae_when_denomination_matches_currency_denominations(){
        assertThrows(IllegalArgumentException.class, ()->validateDenominationCurrency(Currency.GBP, 7));
    }
}
