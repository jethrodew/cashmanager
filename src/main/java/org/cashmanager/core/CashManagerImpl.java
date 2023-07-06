package org.cashmanager.core;

import com.google.common.annotations.VisibleForTesting;
import org.cashmanager.CashManager;
import org.cashmanager.contract.CoinTransaction;
import org.cashmanager.contract.Currency;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CashManagerImpl implements CashManager {
    private final Currency currency;
    private CashFloat cashFloat;

    public CashManagerImpl(final Currency currency, final Map<Integer, Integer> denominationCounts) {
        if (currency == null) {
            throw new NullPointerException("Provided a null value for currency");
        }
        if (denominationCounts == null) {
            throw new NullPointerException("Provided a null value for denomination amounts");
        }
        this.currency = currency;
        this.cashFloat = new CashFloat(denominationCounts);
    }

    @Override
    public Map<Integer, Integer> getDenominationCounts() {
        return cashFloat.getDenominationCounts();
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public void resetCoins(final Map<Integer, Integer> denominationAmounts) {
        this.cashFloat = new CashFloat(denominationAmounts);
    }

    @Override
    public void addCoins(final Map<Integer, Integer> denominationAmounts) {
        denominationAmounts.forEach(this::addCoins);
    }

    @Override
    public void addCoins(final Integer denomination, final Integer count) {
        cashFloat.addSingleDenominationCoins(denomination, count);
    }

    @Override
    public Map<Integer, Integer> processTransaction(final CoinTransaction coinTransaction) {
        final Integer totalCoinValue = coinTransaction.getCoinsProvided().entrySet()
                .stream()
                .mapToInt(entry -> {
                    final Integer denomination = entry.getKey();
                    final Integer count = entry.getValue();

                    cashFloat.addSingleDenominationCoins(denomination, count);
                    return denomination * count;
                })
                .sum();

        int changeTotal = totalCoinValue - coinTransaction.getCost();
        if (changeTotal < 0) {
            throw new IllegalArgumentException("Insufficient coins provided to cover cost");
        }
        return removeCoins(changeTotal);
    }

    @Override
    public Map<Integer, Integer> removeCoins(final Integer changeTotal) {
        if (changeTotal < 0) {
            throw new IllegalArgumentException("Total to remove cannot be below 0");
        }
        if (changeTotal == 0) {
            return Collections.EMPTY_MAP;
        }

        Map<Integer, Integer> calculatedChange = new HashMap<>();
        int remainingAmount = calculateChangeForTotal(calculatedChange, cashFloat.getDenominationCounts(), changeTotal);

        if (remainingAmount == 0) {
            cashFloat.removeCoins(calculatedChange);
            return calculatedChange;
        } else {
            throw new IllegalStateException(String.format("Not enough coins available to make the exact change: %s", changeTotal));
        }
    }

    @VisibleForTesting
    int calculateChangeForTotal(final Map<Integer, Integer> calculatedChange, final Map<Integer, Integer> denominationCounts, final int changeTotal) {
        TreeMap<Integer, Integer> availableCoins = new TreeMap<>(denominationCounts);

        int remainingAmount = changeTotal;
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
        return remainingAmount;
    }

    @Override
    public void removeCoins(final Map<Integer, Integer> denominationsToRemove) {
        if (denominationsToRemove.isEmpty()) {
            return;
        }

        Map<Integer, Integer> currentFloatContents = cashFloat.getDenominationCounts();

        denominationsToRemove.forEach((Integer denomination, Integer count) -> {
            cashFloat.validateCoinValue(denomination, count);
            if (!currentFloatContents.containsKey(denomination) || currentFloatContents.get(count) <= count) {
                throw new IllegalStateException(String.format("Not enough coins available. denomination:%s, count:%s", denomination, count));
            }
        });

        cashFloat.removeCoins(denominationsToRemove);
    }
}
