package org.cashmanager.core;

import com.google.common.annotations.VisibleForTesting;

import java.util.Collections;
import java.util.Map;

import static org.cashmanager.util.Validators.lessThanZero;
import static org.cashmanager.util.Validators.zeroOrLess;

public class CashFloat {

    private final Map<Integer, Integer> denominationCounts;
    /**
     * @param denominationCounts - map of denomination counts
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
    public CashFloat(final Map<Integer, Integer> denominationCounts) {
        if (denominationCounts == null) {
            throw new NullPointerException("Provided a null value for denomination amounts");
        }
        denominationCounts.forEach(this::validateCoinValue);
        this.denominationCounts = denominationCounts;
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
     * Adds count number of coins to the denomination
     *
     * @param denomination - coin value e.g. 50 (50 cents / 50 pence)
     * @param count        - number of coins
     */
    public void addSingleDenominationCoins(final Integer denomination, final Integer count) {
        validateCoinValue(denomination, count);
        if (count == 0) {
            return; //nothing to do
        }
        Integer existingCount = this.denominationCounts.getOrDefault(denomination, 0);
        this.denominationCounts.put(denomination, existingCount + count);
    }

    /**
     * Removes denomination counts from float counts.
     * <p>
     * N.B This needs better transactional protection but given this would be better stored in a DB where ACID transactions exist.
     * I didn't see a point in going that far as the change is generated and checked internally within this project.
     *
     * @param denominationCounts
     */
    public void removeCoins(final Map<Integer, Integer> denominationCounts) {
        denominationCounts.forEach((Integer denomination, Integer count) -> {
            validateCoinValue(denomination, count);
            if (count == 0) {
                return; //nothing to do
            }

            Integer newCount = this.denominationCounts.getOrDefault(denomination, 0) - count;
            if (lessThanZero(newCount)) {
                throw new IllegalStateException(String.format("Attempted to remove more coins than exist from the float. denomination: %s, count: %s, ", denomination, count));
            }
            this.denominationCounts.put(denomination, newCount);
        });
    }

    @VisibleForTesting
    void validateCoinValue(final Integer denomination, final Integer count) {
        if (count == null || denomination == null) {
            throw new NullPointerException(String.format("Provided a denomination count that contained null. denomination: %s, count: %s", denomination, count));
        }
        if (zeroOrLess(denomination) || lessThanZero(count)) {
            throw new IllegalArgumentException(String.format("Provided a denomination count that was below the minimum expected value. denomination: %s, count: %s", denomination, count));
        }
    }
}
