package org.cashmanager.core;

import org.cashmanager.contract.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

public class CashFloatTest {
    private CashFloat cashFloat;

    private Currency currency;

    private Map<Integer, Integer> denominationCounts;
    private final Integer denomination = 100;
    private final Integer count = 15;
    private final Integer denomination2 = 50;
    private final Integer count2 = 0;
    private final Integer newDenomination = 20;
    private final Integer newCount = 30;
    private final Integer newDenomination2 = 200;
    private final Integer newCount2 = 3;

    @BeforeEach
    void setup() {
        denominationCounts = new HashMap<>();
        denominationCounts.put(denomination, count);
        denominationCounts.put(denomination2, count2);

        currency = Currency.GBP;

        cashFloat = spy(new CashFloat(currency, denominationCounts));
    }

    @Test
    void constructor_should_throw_iae_when_currency_null() {
        assertThrows(IllegalArgumentException.class, () -> new CashFloat(null, Collections.EMPTY_MAP));
    }

    @Test
    void constructor_should_throw_iae_when_denomination_counts_null() {
        assertThrows(IllegalArgumentException.class, () -> new CashFloat(currency, null));
    }

    @Test
    void constructor_should_throw_npe_when_denomination_counts_contains_null_denomination() {
        denominationCounts.put(null, count);
        assertThrows(NullPointerException.class, () -> new CashFloat(currency, denominationCounts));
    }

    @Test
    void constructor_should_throw_npe_when_denomination_counts_contains_null_count() {
        denominationCounts.put(denomination, null);
        assertThrows(NullPointerException.class, () -> new CashFloat(currency, denominationCounts));
    }

    @Test
    void constructor_should_throw_iae_when_denomination_counts_contains_zero_denomination() {
        denominationCounts.put(0, count);
        assertThrows(IllegalArgumentException.class, () -> new CashFloat(currency, denominationCounts));
    }

    @Test
    void constructor_should_throw_iae_when_denomination_counts_contains_less_than_zero_denomination() {
        denominationCounts.put(-1, count);
        assertThrows(IllegalArgumentException.class, () -> new CashFloat(currency, denominationCounts));
    }

    @Test
    void constructor_should_throw_iae_when_denomination_counts_contains_less_than_zero_count() {
        denominationCounts.put(denomination, -1);
        assertThrows(IllegalArgumentException.class, () -> new CashFloat(currency, denominationCounts));
    }

    @Test
    void constructor_should_store_currency_and_denomination_counts_when_called() {
        CashFloat cashFloat = new CashFloat(currency, denominationCounts);
        Map<Integer, Integer> cashFloatContents = cashFloat.getDenominationCounts();

        assertEquals(currency, cashFloat.getCurrency());
        assertEquals(2, cashFloatContents.size());
        assertEquals(count, cashFloatContents.get(denomination));
        assertEquals(count2, cashFloatContents.get(denomination2));
    }

    @Test
    void constructor_should_store_new_modifiable_map_of_denomination_counts_when_called() {
        CashFloat cashFloat = new CashFloat(currency, denominationCounts);

        Map<Integer, Integer> floatContents = cashFloat.getDenominationCounts();
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
        assertEquals(2, floatContents.size());

        denominationCounts.put(newDenomination, newCount);
        cashFloat.addCoins(Map.of(newDenomination2, newCount2));

        Map<Integer, Integer> floatNewContents = cashFloat.getDenominationCounts();
        assertEquals(3, floatNewContents.size());

        assertEquals(count, floatNewContents.get(denomination));
        assertEquals(count2, floatNewContents.get(denomination2));
        assertFalse(floatNewContents.containsKey(newDenomination));
        assertEquals(newCount2, floatNewContents.get(newDenomination2));
    }

    @Test
    void addCoins_should_throw_iae_when_denominationCounts_null(){
        assertThrows(IllegalArgumentException.class, ()->cashFloat.addCoins(null));
    }

    @Test
    void addCoins_should_throw_npe_when_denomination_null() {
        assertThrows(NullPointerException.class, () -> cashFloat.addCoins(Map.of(null, count)));
    }

    @Test
    void addCoins_should_throw_npe_when_count_null() {
        assertThrows(NullPointerException.class, () -> cashFloat.addCoins(Map.of(denomination, null)));
    }

    @Test
    void addCoins_should_throw_iae_when_count_less_than_zero() {
        assertThrows(IllegalArgumentException.class, () -> cashFloat.addCoins(Map.of(denomination, -1)));
    }

    @Test
    void addCoins_should_throw_iae_when_denomination_less_than_zero() {
        assertThrows(IllegalArgumentException.class, () -> cashFloat.addCoins(Map.of(-1, count)));
    }

    @Test
    void addCoins_should_throw_iae_when_denomination_zero() {
        assertThrows(IllegalArgumentException.class, () -> cashFloat.addCoins(Map.of(0, count)));
    }

    @Test
    void addCoins_should_throw_iae_when_denomination_not_in_currency(){
        assertThrows(IllegalArgumentException.class, ()->cashFloat.addCoins(Map.of(13, 10)));
    }

    @Test
    void addCoins_should_do_nothing_when_count_zero() {
        assertFalse(cashFloat.getDenominationCounts().containsKey(newDenomination));
        cashFloat.addCoins(Map.of(newDenomination, 0));
        assertFalse(cashFloat.getDenominationCounts().containsKey(newDenomination));
    }

    @Test
    void addCoins_should_add_denomination_and_count_when_new_denomination_and_count_above_zero() {
        Map<Integer, Integer> counts = cashFloat.getDenominationCounts();
        assertFalse(counts.containsKey(newDenomination));

        cashFloat.addCoins(Map.of(newDenomination, newCount));

        Map<Integer, Integer> newCounts = cashFloat.getDenominationCounts();
        assertTrue(newCounts.containsKey(newDenomination));
        assertEquals(newCount, newCounts.get(newDenomination));
    }

    @Test
    void addCoins_should_add_count_when_existing_denomination_and_count_above_zero() {
        Map<Integer, Integer> counts = cashFloat.getDenominationCounts();
        assertTrue(counts.containsKey(denomination));

        cashFloat.addCoins(Map.of(denomination, newCount));


        Map<Integer, Integer> newCounts = cashFloat.getDenominationCounts();
        assertTrue(newCounts.containsKey(denomination));
        assertEquals(count + newCount, newCounts.get(denomination));
    }

    @Test
    void removeCoins_should_throw_iae_when_denominationCounts_null(){
        assertThrows(IllegalArgumentException.class, ()->cashFloat.addCoins(null));
    }

    @Test
    void removeCoins_should_remove_coin_count_for_each_denomination_when_called_with_zero_or_above_zero() {
        Map<Integer, Integer> existingCounts = cashFloat.getDenominationCounts();

        assertEquals(2, existingCounts.size());
        assertEquals(count, existingCounts.get(denomination));
        assertEquals(count2, existingCounts.get(denomination2));

        Map<Integer, Integer> coinsToRemove = Map.of(denomination, 7, denomination2, 0);
        cashFloat.removeCoins(coinsToRemove);
        assertEquals(2, existingCounts.size());
        assertEquals(count - 7, existingCounts.get(denomination));
        assertEquals(count2, existingCounts.get(denomination2));
    }

    @Test
    void removeCoins_should_throw_npe_when_coin_denomination_or_count_null() {
        Map<Integer, Integer> coinsToRemove = new HashMap<>();
        coinsToRemove.put(denomination, 3);
        coinsToRemove.put(null, 0);
        assertThrows(NullPointerException.class, () -> cashFloat.removeCoins(coinsToRemove));

        Map<Integer, Integer> coinsToRemove2 = new HashMap<>();
        coinsToRemove2.put(denomination, null);
        coinsToRemove2.put(denomination2, 0);
        assertThrows(NullPointerException.class, () -> cashFloat.removeCoins(coinsToRemove2));

        Map<Integer, Integer> coinsToRemove3 = new HashMap<>();
        coinsToRemove3.put(null, 3);
        coinsToRemove3.put(denomination2, 0);
        assertThrows(NullPointerException.class, () -> cashFloat.removeCoins(coinsToRemove3));
    }

    @Test
    void removeCoins_should_throw_iae_when_denomination_not_in_currency(){
        assertThrows(IllegalArgumentException.class, ()->cashFloat.removeCoins(Map.of(13, 10)));
    }

    @Test
    void removeCoins_should_throw_iae_when_no_existing_denomination_and_new_count_less_than_zero() {
        Map<Integer, Integer> coinsToRemove = Map.of(newDenomination, 3, denomination2, 0);

        assertThrows(IllegalArgumentException.class, () -> cashFloat.removeCoins(coinsToRemove));

        Map<Integer, Integer> floatContents = cashFloat.getDenominationCounts();
        assertEquals(2, floatContents.size());
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
    }
}
