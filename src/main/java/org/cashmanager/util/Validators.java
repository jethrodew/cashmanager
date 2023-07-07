package org.cashmanager.util;

import org.cashmanager.contract.Currency;

import java.util.Map;

/**
 * Class to contain static validators which can be generically reused.
 * Should provided text representations of validation rather than requiring complex or easily mistyped operations
 */
public class Validators {
    public static boolean zeroOrLess(final Integer integer) {
        return integer <= 0;
    }

    public static boolean lessThanZero(final Integer integer) {
        return integer < 0;
    }

    public static void validateDenominationCurrency(final Currency currency, final Map<Integer, Integer> denominationCounts) {
        denominationCounts.keySet().forEach((denomination) -> validateDenominationCurrency(currency, denomination));
    }

    public static void validateDenominationCurrency(final Currency currency, final Integer denomination) {
        if (!currency.getDenominations().contains(denomination)) {
            throw new IllegalArgumentException(String.format("Denomination included which does not match provided currency: %s", denomination));
        }
    }

    public static void validateDenominationCounts(final Map<Integer, Integer> denominationCounts) {
        denominationCounts.forEach(Validators::validateDenominationCount);
    }

    public static void validateDenominationCount(final Integer denomination, final Integer count) {
        if (count == null || denomination == null) {
            throw new NullPointerException(String.format("Provided a denomination count that contained null. denomination: %s, count: %s", denomination, count));
        }
        if (zeroOrLess(denomination) || lessThanZero(count)) {
            throw new IllegalArgumentException(String.format("Provided a denomination count that was below the minimum expected value. denomination: %s, count: %s", denomination, count));
        }
    }
}
