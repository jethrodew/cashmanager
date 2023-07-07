package org.cashmanager.core;

import org.cashmanager.CashManager;
import org.cashmanager.contract.CoinTransaction;
import org.cashmanager.contract.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

public class CashManagerTest {

    private CashManager cashManager;
    private Currency currency;
    private final Integer denomination = 10;
    private final Integer count = 17;
    private final Integer denomination2 = 20;
    private final Integer count2 = 0;
    private final Integer newDenomination = 5;
    private final Integer newCount = 28;

    @BeforeEach
    public void setup() {
        currency = Currency.GBP;

        Map<Integer, Integer> denominationCounts;
        denominationCounts = new HashMap<>();
        denominationCounts.put(denomination, count);
        denominationCounts.put(denomination2, count2);

        cashManager = spy(new CashManagerImpl(currency, denominationCounts));
    }

    @Test
    void constructor_should_set_currency_and_float_when_called() {
        assertEquals(currency, cashManager.getCurrency());
        Map<Integer, Integer> floatContents = cashManager.getDenominationCounts();
        assertEquals(2, floatContents.size());
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
    }

    @Test
    void getCurrency_should_get_currency_when_called() {
        assertEquals(currency, cashManager.getCurrency());
        Map<Integer, Integer> floatContents = cashManager.getDenominationCounts();
        assertEquals(2, floatContents.size());
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
    }

    @Test
    void getDenominationCounts_should_get_float_denomination_counts_when_called() {
        Map<Integer, Integer> floatContents = cashManager.getDenominationCounts();
        assertEquals(2, floatContents.size());
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
    }

    @Test
    void addCoins_should_add_coins_for_new_denomination_when_called() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        cashManager.addCoins(Map.of(newDenomination, newCount));

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(3, newFloatContents.size());
        assertEquals(count, newFloatContents.get(denomination));
        assertEquals(count2, newFloatContents.get(denomination2));
        assertEquals(newCount, newFloatContents.get(newDenomination));
    }

    @Test
    void addCoins_should_add_coins_for_existing_denomination_when_called() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        cashManager.addCoins(Map.of(denomination, newCount));

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, newFloatContents.size());
        assertEquals(count + newCount, newFloatContents.get(denomination));
        assertEquals(count2, newFloatContents.get(denomination2));
    }

    @Test
    void addCoins_should_add_to_new_and_existing_coins_for_each_denomination_when_called() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        Map<Integer, Integer> coinsToAdd = Map.of(denomination, 7, newDenomination, newCount);
        cashManager.addCoins(coinsToAdd);

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(3, newFloatContents.size());
        assertEquals(count + 7, newFloatContents.get(denomination));
        assertEquals(count2, newFloatContents.get(denomination2));
        assertEquals(newCount, newFloatContents.get(newDenomination));
    }

    @Test
    void processTransaction_should_calculate_total_coin_value_and_add_coins_to_float_and_return_change() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        CoinTransaction coinTransaction = new CoinTransaction(30, Map.of(20, 2));

        Map<Integer, Integer> change = cashManager.processTransaction(coinTransaction);

        assertEquals(1, change.size());
        assertEquals(1, change.get(10), "Failed to return correct change");

        assertEquals(count2 + 2, cashManager.getDenominationCounts().get(denomination2), "Failed to add coins to float");
        assertEquals(count - 1, cashManager.getDenominationCounts().get(denomination), "Failed to subtract coins to float");
    }

    @Test
    void processTransaction_should_throw_iae_when_not_enough_coins_to_cover_cost() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        CoinTransaction coinTransaction = new CoinTransaction(30, Map.of(20, 1));

        assertThrows(IllegalArgumentException.class, () -> cashManager.processTransaction(coinTransaction));

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, newFloatContents.size());
        assertEquals(count2, cashManager.getDenominationCounts().get(denomination2), "Should not have added to float");
        assertEquals(count, newFloatContents.get(denomination));
    }

    @Test
    void removeCoins_valueTotal_should_throw_iae_if_change_less_than_zero() {
        cashManager.addCoins(Map.of(5, 1));
        assertThrows(IllegalArgumentException.class, () -> cashManager.removeCoins(-1));

        assertEquals(count2, cashManager.getDenominationCounts().get(denomination2), "Added coins to float");
        assertEquals(count, cashManager.getDenominationCounts().get(denomination), "Should not extract coins");
    }

    @Test
    void removeCoins_valueTotal_should_return_empty_map_when_total_is_zero() {
        cashManager.addCoins(Map.of(5, 1));

        Map<Integer, Integer> output = cashManager.removeCoins(0);

        assertTrue(output.isEmpty());
    }

    @Test
    void removeCoins_valueTotal_should_throw_ise_if_not_enough_change_in_float_to_give() {
        cashManager.addCoins(Map.of(5, 1));
        assertThrows(IllegalStateException.class, () -> cashManager.removeCoins(22));

        assertEquals(count2, cashManager.getDenominationCounts().get(denomination2), "Added coins to float");
        assertEquals(count, cashManager.getDenominationCounts().get(denomination), "Should not extract coins");
    }

    @Test
    void removeCoins_valueTotal_should_deduct_correct_coins_when_total_provided() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        Map<Integer, Integer> change = cashManager.removeCoins(30);

        assertEquals(1, change.size());
        assertEquals(3, change.get(10), "Failed to return correct change");

        assertEquals(count - 3, cashManager.getDenominationCounts().get(denomination), "Failed to subtract coins from float");
    }


    @Test
    void removeCoins_map_should_throw_iae_when_denominationsToRemove_is_null() {
        assertThrows(IllegalArgumentException.class, () -> cashManager.removeCoins((Map) null));
    }

    @Test
    void removeCoins_map_should_do_nothing_when_no_denominations_specified() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        cashManager.removeCoins(Collections.EMPTY_MAP);

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, newFloatContents.size());
        assertEquals(count, newFloatContents.get(denomination));
        assertEquals(count2, newFloatContents.get(denomination2));
    }

    @Test
    void removeCoins_map_should_not_remove_coins_if_coins_to_remove_contain_invalid_values() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        Map<Integer, Integer> coinsToRemove = new HashMap<>();
        coinsToRemove.put(denomination, 3);
        coinsToRemove.put(null, 0);
        assertThrows(NullPointerException.class, () -> cashManager.removeCoins(coinsToRemove));

        Map<Integer, Integer> coinsToRemove2 = new HashMap<>();
        coinsToRemove2.put(denomination, null);
        coinsToRemove2.put(denomination2, 0);
        assertThrows(NullPointerException.class, () -> cashManager.removeCoins(coinsToRemove2));

        Map<Integer, Integer> coinsToRemove3 = new HashMap<>();
        coinsToRemove3.put(null, 3);
        coinsToRemove3.put(denomination2, 0);
        assertThrows(NullPointerException.class, () -> cashManager.removeCoins(coinsToRemove3));

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, newFloatContents.size());
        assertEquals(count, newFloatContents.get(denomination));
        assertEquals(count2, newFloatContents.get(denomination2));
    }

    @Test
    void removeCoins_map_should_not_remove_coins_if_coin_denomination_zero_or_less_or_count_less_than_zero() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        Map<Integer, Integer> coinsToRemoveZeroDenomination = new HashMap<>();
        coinsToRemoveZeroDenomination.put(0, 3);
        coinsToRemoveZeroDenomination.put(denomination2, 0);
        assertThrows(IllegalArgumentException.class, () -> cashManager.removeCoins(coinsToRemoveZeroDenomination));

        Map<Integer, Integer> coinsToRemoveLessThanZeroDenomination = new HashMap<>();
        coinsToRemoveLessThanZeroDenomination.put(-1, 3);
        coinsToRemoveLessThanZeroDenomination.put(denomination2, 0);
        assertThrows(IllegalArgumentException.class, () -> cashManager.removeCoins(coinsToRemoveLessThanZeroDenomination));

        Map<Integer, Integer> coinsToRemoveCountLessThanZero = new HashMap<>();
        coinsToRemoveCountLessThanZero.put(denomination, 3);
        coinsToRemoveCountLessThanZero.put(denomination2, -1);
        assertThrows(IllegalArgumentException.class, () -> cashManager.removeCoins(coinsToRemoveCountLessThanZero));

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, newFloatContents.size());
        assertEquals(count, newFloatContents.get(denomination));
        assertEquals(count2, newFloatContents.get(denomination2));
    }

    @Test
    void removeCoins_map_should_deduct_correct_coins_when_correctly_requested() {
        cashManager.resetCoins(Map.of(10, 5, 5, 8));

        cashManager.removeCoins(Map.of(10, 3, 5, 2));

        assertEquals(2, cashManager.getDenominationCounts().get(10), "Failed to subtract 10s");
        assertEquals(6, cashManager.getDenominationCounts().get(5), "Failed to subtract 5s");
    }

}
