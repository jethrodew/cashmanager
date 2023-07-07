package org.cashmanager.core;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ChangeCalculator {

    /**
     * Works through the available coin denominations starting with the highest value coins to reduce the valueTotal down to 0
     * Uses a TreeMap to contain the availableDenominationCounts in an ordered format.
     * This means that we can specify the order that we work through them in as some Map implementations are not sorted.
     * Throws an @IllegalStateException if it is unable to calculate a solution
     *
     * @param availableDenominationCounts - currently available denomination counts in the cash float
     * @param valueTotal                  - total target value of the change e.g. 75
     * @return Map of the coins which would work as change
     */
    public static Map<Integer, Integer> calculateChange(final Map<Integer, Integer> availableDenominationCounts, final int valueTotal) {
        Map<Integer, Integer> calculatedChange = new HashMap<>();
        TreeMap<Integer, Integer> availableCoins = new TreeMap<>(availableDenominationCounts);

        int remainingAmount = valueTotal;
        for (int denomination : availableCoins.descendingKeySet()) {
            int count = availableCoins.get(denomination);

            if (remainingAmount >= denomination && count > 0) {
                int numOfCoinsToUse = Math.min(count, remainingAmount / denomination);

                calculatedChange.put(denomination, numOfCoinsToUse);
                remainingAmount -= numOfCoinsToUse * denomination;
                availableCoins.put(denomination, count - numOfCoinsToUse);
            }

            if (remainingAmount == 0) {
                break;
            }
        }

        if (remainingAmount != 0) {
            throw new IllegalStateException(String.format("Not enough coins available to make the exact change: %s", valueTotal));
        }

        return calculatedChange;
    }
}
