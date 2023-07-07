package org.cashmanager.core;

import org.cashmanager.contract.Currency;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.cashmanager.util.Validators.*;

/**
 * Representation of the cash float storage
 * <p>
 * N.B This needs better transactional protection but given this would be better stored in a DB where ACID transactions exist,
 * I didn't see a point in going that far as the change is generated and checked internally within this project.
 */
public class CashFloat {

    private final Currency currency;
    private final Map<Integer, Integer> denominationCounts;

    /**
     * Initializes the float with the contents of the cash float and the currency in use
     *
     * @param denominationCounts - map of coin denomination counts
     *                           e.g. {
     *                           200: 13,
     *                           100: 13,
     *                           50: 0,
     *                           20: 50,
     *                           10: 50,
     *                           5: 30,
     *                           2: 0,
     *                           1: 0
     *                           }
     */
    public CashFloat(final Currency currency, final Map<Integer, Integer> denominationCounts) {
        if (currency == null) {
            throw new IllegalArgumentException("Provided a null value for currency");
        }
        this.currency = currency;

        if (denominationCounts == null) {
            throw new IllegalArgumentException("Provided a null value for denomination counts");
        }
        validateDenominationCounts(denominationCounts);
        validateDenominationCurrency(currency, denominationCounts);

        this.denominationCounts = new HashMap<>(denominationCounts);
    }

    /**
     * Currency currently in use in this float
     *
     * @return the currency for the cash in this float
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Outputs the Denomination Counts currently in the float.
     *
     * @return Map<Denomination, Count> e.g <200, 10> - 10 £2 coins
     */
    public Map<Integer, Integer> getDenominationCounts() {
        return Collections.unmodifiableMap(denominationCounts);
    }


    /**
     * Adds count number of coins for each denomination to the float
     *
     * @param denominationCounts - map of coin denomination counts
     *                           e.g. {
     *                           200: 13,
     *                           100: 13,
     *                           50: 0,
     *                           20: 50,
     *                           10: 50,
     *                           5: 30,
     *                           2: 0,
     *                           1: 0
     *                           }
     */
    public void addCoins(final Map<Integer, Integer> denominationCounts) {
        alterFloat(denominationCounts, (denomination, count) -> {
            Integer existingCount = this.denominationCounts.getOrDefault(denomination, 0);
            this.denominationCounts.put(denomination, existingCount + count);
        });
    }

    /**
     * Removes count number of coins for each denomination to the float
     *
     * @param denominationCounts - map of coin denomination counts
     *                           e.g. {
     *                           200: 13,
     *                           100: 13,
     *                           50: 0,
     *                           20: 50,
     *                           10: 50,
     *                           5: 30,
     *                           2: 0,
     *                           1: 0
     *                           }
     */
    public void removeCoins(final Map<Integer, Integer> denominationCounts) {
        alterFloat(denominationCounts, (denomination, count) -> {
            Integer existingCount = this.denominationCounts.getOrDefault(denomination, 0);
            Integer newCount = existingCount - count;
            if (lessThanZero(newCount)) {
                throw new IllegalArgumentException(String.format("Float does not have enough coins to remove for Denomination: %s, Count: %s, ", denomination, count));
            }
            this.denominationCounts.put(denomination, newCount);
        });
    }

    private void alterFloat(final Map<Integer, Integer> denominationCounts, BiConsumer<Integer, Integer> floatAlteration) {
        if (denominationCounts == null) {
            throw new IllegalArgumentException("Provided a null value for denominationCounts");
        }
        validateDenominationCounts(denominationCounts);
        validateDenominationCurrency(currency, denominationCounts);

        denominationCounts.forEach((Integer denomination, Integer count) -> {
            if (count == 0) {
                return; //nothing to do
            }
            floatAlteration.accept(denomination, count);
        });
    }
}
