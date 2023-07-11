package org.cashmanager.contract;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyTest {

    @Test
    void getCurrency_should_match_currency_by_case_insensitive_name_when_exists(){
        assertEquals(Currency.GBP, Currency.getCurrency("gbp"));
        assertEquals(Currency.GBP, Currency.getCurrency("GbP"));
        assertEquals(Currency.GBP, Currency.getCurrency("GBP"));
        assertEquals(Currency.GBP, Currency.getCurrency("Gbp"));
        assertEquals(Currency.GBP, Currency.getCurrency("gBp"));
        assertEquals(Currency.GBP, Currency.getCurrency("gbP"));
    }

    @Test
    void getSymbol_should_return_expected_symbol_when_called(){
        assertEquals("£", Currency.GBP.getSymbol());
    }

    @Test
    void getDenominations_should_return_expected_denominations_in_order_when_called(){
        List<Integer> gbpDenominations = Currency.GBP.getDenominations();

        assertEquals(8, gbpDenominations.size());
        assertEquals(200, gbpDenominations.get(0));
        assertEquals(100, gbpDenominations.get(1));
        assertEquals(50, gbpDenominations.get(2));
        assertEquals(20, gbpDenominations.get(3));
        assertEquals(10, gbpDenominations.get(4));
        assertEquals(5, gbpDenominations.get(5));
        assertEquals(2, gbpDenominations.get(6));
        assertEquals(1, gbpDenominations.get(7));
    }

    @Test
    void getCurrency_should_throw_iae_when_currency_not_found_by_name(){
        assertThrows(IllegalArgumentException.class, ()->Currency.getCurrency("USD"));
        assertThrows(IllegalArgumentException.class, ()->Currency.getCurrency("EUR"));
    }
}
