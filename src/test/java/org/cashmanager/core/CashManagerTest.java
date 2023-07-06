package org.cashmanager.core;

import org.cashmanager.CashManager;
import org.cashmanager.contract.CoinTransaction;
import org.cashmanager.contract.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

public class CashManagerTest {

    private CashManager cashManager;
    private Currency currency;
    private Map<Integer, Integer> denominationCounts;
    private final Integer denomination = 10;
    private final Integer count = 17;
    private final Integer denomination2 = 20;
    private final Integer count2 = 0;
    private final Integer newDenomination = 5;
    private final Integer newCount = 28;

    @BeforeEach
    public void setup() {
        currency = Currency.GBP;
        denominationCounts = new HashMap<>();
        denominationCounts.put(denomination, count);
        denominationCounts.put(denomination2, count2);

        cashManager = spy(new CashManagerImpl(currency, denominationCounts));
    }

    @Test
    public void constructor_should_throw_npe_when_currency_null() {
        assertThrows(NullPointerException.class, () -> new CashManagerImpl(null, denominationCounts));
    }

    @Test
    public void constructor_should_throw_npe_when_denominationCounts_null() {
        assertThrows(NullPointerException.class, () -> new CashManagerImpl(currency, null));
    }

    @Test
    public void constructor_should_set_currency_and_float_when_called() {
        assertEquals(currency, cashManager.getCurrency());
        Map<Integer, Integer> floatContents = cashManager.getDenominationCounts();
        assertEquals(2, floatContents.size());
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
    }

    @Test
    public void getCurrency_should_get_currency_when_called() {
        assertEquals(currency, cashManager.getCurrency());
        Map<Integer, Integer> floatContents = cashManager.getDenominationCounts();
        assertEquals(2, floatContents.size());
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
    }

    @Test
    public void getDenominationCounts_should_get_float_denomination_counts_when_called() {
        Map<Integer, Integer> floatContents = cashManager.getDenominationCounts();
        assertEquals(2, floatContents.size());
        assertEquals(count, floatContents.get(denomination));
        assertEquals(count2, floatContents.get(denomination2));
    }

    @Test
    public void addCoins_should_add_coins_for_new_denomination_when_called() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        cashManager.addCoins(newDenomination, newCount);

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(3, newFloatContents.size());
        assertEquals(count, newFloatContents.get(denomination));
        assertEquals(count2, newFloatContents.get(denomination2));
        assertEquals(newCount, newFloatContents.get(newDenomination));
    }

    @Test
    public void addCoins_should_add_coins_for_existing_denomination_when_called() {
        Map<Integer, Integer> existingFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, existingFloatContents.size());
        assertEquals(count, existingFloatContents.get(denomination));
        assertEquals(count2, existingFloatContents.get(denomination2));

        cashManager.addCoins(denomination, newCount);

        Map<Integer, Integer> newFloatContents = cashManager.getDenominationCounts();
        assertEquals(2, newFloatContents.size());
        assertEquals(count + newCount, newFloatContents.get(denomination));
        assertEquals(count2, newFloatContents.get(denomination2));
    }

    @Test
    public void addCoins_should_add_to_new_and_existing_coins_for_each_denomination_when_called() {
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
    public void processTransaction_should_calculate_total_coin_value_and_add_coins_to_float_and_return_change() {
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
    public void removeCoins_should_throw_iae_if_change_less_than_zero() {
        cashManager.addCoins(5, 1);
        assertThrows(IllegalArgumentException.class, () -> cashManager.removeCoins(-1));

        assertEquals(count2, cashManager.getDenominationCounts().get(denomination2), "Added coins to float");
        assertEquals(count, cashManager.getDenominationCounts().get(denomination), "Should not extract coins");
    }

    @Test
    public void removeCoins_should_return_empty_map_when_total_is_zero() {
        cashManager.addCoins(5, 1);

        Map<Integer, Integer> output = cashManager.removeCoins(0);

        assertTrue(output.isEmpty());
    }

    //TODO further remove coins testing (TotalChange)

    @Test
    public void removeCoins_should_throw_ise_if_not_enough_change() {
        cashManager.addCoins(5, 1);
        assertThrows(IllegalStateException.class, () -> cashManager.removeCoins(22));

        assertEquals(count2, cashManager.getDenominationCounts().get(denomination2), "Added coins to float");
        assertEquals(count, cashManager.getDenominationCounts().get(denomination), "Should not extract coins");
    }

    //TODO test change calculator

    //TODO test removeCoins(Map<Denomination,Count>)

}
