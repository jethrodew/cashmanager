package org.cashmanager.core.calculator;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.cashmanager.util.ProcessDenominationCounts.combineDenominationCounts;
import static org.cashmanager.util.ProcessDenominationCounts.filterEmptyAndAddToTree;

public class ChangeCalculator {

    /**
     * Works through the available coin denominations starting with the highest value coins to reduce the valueTotal down to 0
     * Uses a TreeMap to contain the availableDenominationCounts in an ordered format.
     * This means that we can specify the order that we work through them in as some Map implementations are not sorted.
     * Throws an @IllegalStateException if it is unable to calculate a solution
     *
     * The cheapest solution is to go down through the coin denomination values to work out the minimum number of coins, so we do that first.
     * If that fails to get the whole amount, we pop one of the lowest denomination out of the calculated change and recalculate just the remainder working from the bottom up.
     * This repeats for each denomination found in the change until there are none left.
     *
     * This algorithm has a gap which exists in the middle values i.e 111 when the float is short of coins (50:3,20:3,5:1,2:1,1:1)
     * The initial pass will attempt 50:2 and then add all the denominations  below 20 but because there are not enough coins to make up the remaining 11 it will fail.
     * The second pass will attempt to solve the remaining 3 by remove the 1, then the 2, then the 5 but because the algorithm attempts to add values to make up ascending it will reach the same value
     *
     * After all of this if it has not found a solution, it will throw an IllegalStateException as there are not enough coins available to process the transaction
     *
     * @param floatDenominationCounts - currently available denomination counts in the cash float
     * @param valueTotal              - total target value of the change e.g. 75
     * @return Map of the coins which would work as change
     */
    public Map<Integer, Integer> calculateChange(final Map<Integer, Integer> floatDenominationCounts, final int valueTotal) {
        TreeMap<Integer, Integer> availableDenominationCounts = filterEmptyAndAddToTree(floatDenominationCounts);

        ChangeCalculatorResult changeCalculatorResult = calculateChange(availableDenominationCounts, availableDenominationCounts.descendingKeySet(), valueTotal);

        if (changeCalculatorResult.getRemainingAmount() != 0) {
            if (changeCalculatorResult.getCalculatedChange().isEmpty()) {
                throw new IllegalStateException(String.format("No coins available to make the exact change: %s", valueTotal));
            }

            changeCalculatorResult = recalculateChange(changeCalculatorResult, availableDenominationCounts);
            if (changeCalculatorResult == null) {
                throw new IllegalStateException(String.format("Not enough coins available to make the exact change: %s", valueTotal));
            }
        }

        return changeCalculatorResult.getCalculatedChange();
    }

    private ChangeCalculatorResult recalculateChange(final ChangeCalculatorResult existingChangeCalculatorResult, final TreeMap<Integer, Integer> availableDenominationCounts) {
        Iterator<Integer> denominationIterator = existingChangeCalculatorResult.getCalculatedChange().keySet().iterator();

        int newRemainingAmount = existingChangeCalculatorResult.getRemainingAmount();
        TreeMap<Integer, Integer> currentlyCalculatedChange = new TreeMap<>(existingChangeCalculatorResult.getCalculatedChange());

        TreeMap<Integer, Integer> currentlyAvailableDenominationCounts = new TreeMap<>(availableDenominationCounts);
        currentlyCalculatedChange.keySet().forEach(denomination -> {
            int newCount = currentlyAvailableDenominationCounts.get(denomination) - currentlyCalculatedChange.get(denomination);
            currentlyAvailableDenominationCounts.put(denomination, newCount);
        });

        ChangeCalculatorResult changeCalculatorResult;

        while (denominationIterator.hasNext()) {
            int lowestDenomination = denominationIterator.next();
            int lowestDenominationCount = existingChangeCalculatorResult.getCalculatedChange().get(lowestDenomination);

            // Remove one of the lowest denomination from the calculated change
            currentlyCalculatedChange.put(lowestDenomination, lowestDenominationCount - 1);

            // Add one of the lowest denomination to our cached float state to ensure we're still working with the correct total
            int newCurrentlyAvailableLowestDenominationCountInFloat = currentlyAvailableDenominationCounts.get(lowestDenomination) + 1;
            currentlyAvailableDenominationCounts.put(lowestDenomination, newCurrentlyAvailableLowestDenominationCountInFloat);

            // Add the lowestDenomination to the remaining amount to calculate change for
            newRemainingAmount += lowestDenomination;

            // Calculate change for the remaining amount
            changeCalculatorResult = calculateChange(currentlyAvailableDenominationCounts, currentlyAvailableDenominationCounts.keySet(), newRemainingAmount);

            if (changeCalculatorResult.getRemainingAmount() == 0) {
                //Found a solution!
                return new ChangeCalculatorResult(0, combineDenominationCounts(currentlyCalculatedChange, changeCalculatorResult.getCalculatedChange()));
            }
        }

        /*
         * We've popped one of each denomination off and still can't make the change working from lowest denomination first - give up
         */
        return null;
    }


    private ChangeCalculatorResult calculateChange(final TreeMap<Integer, Integer> availableDenominationCounts,
                                                   final Set<Integer> sortedDenominations,
                                                   final int valueTotal) {
        TreeMap<Integer, Integer> calculatedChange = new TreeMap<>();
        int remainingAmount = valueTotal;


        for (int denomination : sortedDenominations) {
            int count = availableDenominationCounts.get(denomination);

            if (remainingAmount >= denomination && count > 0) {
                int numOfCoinsToUse = Math.min(count, remainingAmount / denomination);


                calculatedChange.put(denomination, numOfCoinsToUse);
                remainingAmount -= numOfCoinsToUse * denomination;
                availableDenominationCounts.put(denomination, count - numOfCoinsToUse);
            }

            if (remainingAmount == 0) {
                break;
            }
        }

        return new ChangeCalculatorResult(remainingAmount, calculatedChange);
    }
}
