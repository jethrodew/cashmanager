package org.cashmanager.core.calculator;

import java.util.*;
import java.util.stream.IntStream;

import static org.cashmanager.util.ProcessDenominationCounts.filterEmptyAndAddToTree;

public class ChangeCalculator {

    /**
     * Works through the available coin denominations starting with the highest value coins to reduce the valueTotal down to 0
     * Uses a TreeMap to contain the availableDenominationCounts in an ordered format.
     * This means that we can specify the order that we work through them in as some Map implementations are not sorted.
     * Throws an @IllegalStateException if it is unable to calculate a solution
     *
     * The cheapest solution is to go down through the coin denomination values to work out the minimum number of coins expecting that there will be enough of them all to find suitable change
     * If we still can't solve it. We will need to investigate every possible
     *
     * Attempt 1 - O(n) where n = denominations and most common solve will happen in less than 10 operations
     * Attempt 2 - O(2n*n) where n = denominations as it must compute every possible combination of coins against other coins before it retrieves the answer because we want the shortest path
     *
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

        ChangeCalculatorResult changeCalculatorResult = calculateChangeDescendingLinearly(new TreeMap<>(availableDenominationCounts), valueTotal);

        if (changeCalculatorResult.getRemainingAmount() != 0) {
            if (changeCalculatorResult.getCalculatedChange().isEmpty()) {
                throw new IllegalStateException(String.format("Not enough coins available to make the exact change: %s", valueTotal));
            }
            List<Map<Integer, Integer>> correctChanges = new ArrayList<>();

            List<Integer> denominationsToBranch = availableDenominationCounts.keySet().stream()
                    .filter(denomination -> denomination < valueTotal)
                    .sorted(Comparator.reverseOrder())
                    .toList();

            calculateChangeBranch(correctChanges, availableDenominationCounts, denominationsToBranch, valueTotal, new HashMap<>());

            return correctChanges.stream().min(Comparator.comparingInt(Map::size))
                    .orElseThrow(() -> new IllegalStateException(String.format("Not enough coins available to make the exact change: %s", valueTotal)));
        }

        return changeCalculatorResult.getCalculatedChange();
    }

    private ChangeCalculatorResult calculateChangeDescendingLinearly(final TreeMap<Integer, Integer> availableDenominationCounts, final int valueTotal) {
        TreeMap<Integer, Integer> calculatedChange = new TreeMap<>();
        int remainingAmount = valueTotal;

        for (int denomination : availableDenominationCounts.descendingKeySet()) {
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

    private void calculateChangeBranch(final List<Map<Integer, Integer>> correctChanges, final Map<Integer, Integer> availableDenominationCounts,
                                       final List<Integer> orderedDenominationsToBranch, final Integer currentAmount, final Map<Integer, Integer> currentChange) {

        int currentDenomination = orderedDenominationsToBranch.get(0);
        int maxNumOfCoins = Math.min(availableDenominationCounts.get(currentDenomination), currentAmount / currentDenomination);

        IntStream.range(0, maxNumOfCoins+1).forEach(currentDenominationCount -> {
            Map<Integer, Integer> newCurrentChange = new HashMap<>(currentChange);
            newCurrentChange.put(currentDenomination, currentDenominationCount);
            int newCurrentAmount = currentAmount - currentDenomination * currentDenominationCount;

            if (newCurrentAmount == 0) {
                correctChanges.add(newCurrentChange);
                return;
            }

            if (currentDenomination != orderedDenominationsToBranch.get(orderedDenominationsToBranch.size()-1)) {
                List<Integer> nextDenominations = orderedDenominationsToBranch.subList(1, orderedDenominationsToBranch.size());
                calculateChangeBranch(correctChanges, availableDenominationCounts, nextDenominations, newCurrentAmount, newCurrentChange);
            }
        });
    }
}
