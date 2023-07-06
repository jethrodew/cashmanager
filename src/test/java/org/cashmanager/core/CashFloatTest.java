package org.cashmanager.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

public class CashFloatTest {
    private CashFloat cashFloat;

    private Map<Integer, Integer> denominationCounts;
    private final Integer denomination = 100;
    private final Integer count = 15;
    private final Integer denomination2 = 50;
    private final Integer count2 = 0;
    private final Integer newDenomination = 20;
    private final Integer newCount = 30;

    @BeforeEach
    public void setup() {
        denominationCounts = new HashMap<>();
        denominationCounts.put(denomination, count);
        denominationCounts.put(denomination2, count2);
        cashFloat = spy(new CashFloat(denominationCounts));
    }

    @Test
    public void constructor_should_throw_npe_when_denomination_counts_null() {
        assertThrows(NullPointerException.class, () -> new CashFloat(null));
    }

    @Test
    public void constructor_should_throw_npe_when_denomination_counts_contains_null_denomination() {
        denominationCounts.put(null, count);
        assertThrows(NullPointerException.class, () -> new CashFloat(denominationCounts));
    }

    @Test
    public void constructor_should_throw_npe_when_denomination_counts_contains_null_count() {
        denominationCounts.put(denomination, null);
        assertThrows(NullPointerException.class, () -> new CashFloat(denominationCounts));
    }

    @Test
    public void constructor_should_throw_iae_when_denomination_counts_contains_zero_denomination() {
        denominationCounts.put(0, count);
        assertThrows(IllegalArgumentException.class, () -> new CashFloat(denominationCounts));
    }

    @Test
    public void constructor_should_throw_iae_when_denomination_counts_contains_less_than_zero_denomination() {
        denominationCounts.put(-1, count);
        assertThrows(IllegalArgumentException.class, () -> new CashFloat(denominationCounts));
    }

    @Test
    public void constructor_should_throw_iae_when_denomination_counts_contains_less_than_zero_count() {
        denominationCounts.put(denomination, -1);
        assertThrows(IllegalArgumentException.class, () -> new CashFloat(denominationCounts));
    }

    @Test
    public void constructor_should_store_complete_denomination_counts_when_called() {
        CashFloat cashFloat = new CashFloat(denominationCounts);
        Map<Integer, Integer> cashFloatContents = cashFloat.getDenominationCounts();

        assertEquals(count, cashFloatContents.get(denomination));
        assertEquals(count2, cashFloatContents.get(denomination2));
        assertEquals(2, cashFloatContents.size());
    }

    @Test
    public void getDenominationCounts_should_return_complete_contents_of_cash_float_when_called() {

        assertTrue(cashFloat.getDenominationCounts().containsKey(denomination));
        assertTrue(cashFloat.getDenominationCounts().containsKey(denomination2));
        assertEquals(count, cashFloat.getDenominationCounts().get(denomination));
        assertEquals(count2, cashFloat.getDenominationCounts().get(denomination2));
    }

    @Test
    public void addSingleDenominationCoins_should_throw_npe_when_denomination_null() {
        assertThrows(NullPointerException.class, () -> cashFloat.addSingleDenominationCoins(null, count));
    }

    @Test
    public void addSingleDenominationCoins_should_throw_npe_when_count_null() {
        assertThrows(NullPointerException.class, () -> cashFloat.addSingleDenominationCoins(denomination, null));
    }

    @Test
    public void addSingleDenominationCoins_should_throw_iae_when_count_less_than_zero() {
        assertThrows(IllegalArgumentException.class, () -> cashFloat.addSingleDenominationCoins(denomination, -1));
    }

    @Test
    public void addSingleDenominationCoins_should_throw_iae_when_denomination_less_than_zero() {
        assertThrows(IllegalArgumentException.class, () -> cashFloat.addSingleDenominationCoins(-1, count));
    }

    @Test
    public void addSingleDenominationCoins_should_throw_iae_when_denomination_zero() {
        assertThrows(IllegalArgumentException.class, () -> cashFloat.addSingleDenominationCoins(0, count));
    }

    @Test
    public void addSingleDenominationCoins_should_do_nothing_when_count_zero() {
        assertFalse(cashFloat.getDenominationCounts().containsKey(newDenomination));
        cashFloat.addSingleDenominationCoins(newDenomination, 0);
        assertFalse(cashFloat.getDenominationCounts().containsKey(newDenomination));
    }

    @Test
    public void addSingleDenominationCoins_should_add_denomination_and_count_when_new_denomination_and_count_above_zero() {
        Map<Integer, Integer> counts = cashFloat.getDenominationCounts();
        assertFalse(counts.containsKey(newDenomination));

        cashFloat.addSingleDenominationCoins(newDenomination, newCount);

        Map<Integer, Integer> newCounts = cashFloat.getDenominationCounts();
        assertTrue(newCounts.containsKey(newDenomination));
        assertEquals(newCount, newCounts.get(newDenomination));
    }

    @Test
    public void addSingleDenominationCoins_should_add_count_when_existing_denomination_and_count_above_zero() {
        Map<Integer, Integer> counts = cashFloat.getDenominationCounts();
        assertTrue(counts.containsKey(denomination));

        cashFloat.addSingleDenominationCoins(denomination, newCount);


        Map<Integer, Integer> newCounts = cashFloat.getDenominationCounts();
        assertTrue(newCounts.containsKey(denomination));
        assertEquals(count + newCount, newCounts.get(denomination));
    }

    @Test
    public void removeCoins_should_remove_coin_count_for_each_denomination_when_called_with_zero_or_above_zero() {
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
    public void removeCoins_should_throw_ise_when_coin_value_invalid() {
        doThrow(new IllegalStateException()).when(cashFloat).validateCoinValue(denomination, 3);
        Map<Integer, Integer> coinsToRemove = Map.of(denomination, 3, denomination2, 0);

        assertThrows(IllegalStateException.class, ()-> cashFloat.removeCoins(coinsToRemove));
    }
    @Test
    public void removeCoins_should_throw_ise_when_no_existing_denomination_and_new_count_less_than_zero() {
        Map<Integer, Integer> coinsToRemove = Map.of(newDenomination, 3, denomination2, 0);

        assertThrows(IllegalStateException.class, ()-> cashFloat.removeCoins(coinsToRemove));

        Map<Integer, Integer> floatContents = cashFloat.getDenominationCounts();
        assertEquals(2, floatContents.size());
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
    }
}
