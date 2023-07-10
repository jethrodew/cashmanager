package org.cashmanager.core;

import org.cashmanager.CashManager;
import org.cashmanager.contract.CoinTransaction;
import org.cashmanager.contract.Currency;
import org.cashmanager.core.calculator.ChangeCalculator;

import java.util.Collections;
import java.util.Map;

import static org.cashmanager.util.Validators.lessThanZero;
import static org.cashmanager.util.Validators.validateDenominationCurrency;

public class CashManagerImpl implements CashManager {
    private CashFloat cashFloat;

    public CashManagerImpl(final Currency currency, final Map<Integer, Integer> denominationCounts) {
        this.cashFloat = new CashFloat(currency, denominationCounts);
    }

    @Override
    public Map<Integer, Integer> getDenominationCounts() {
        return cashFloat.getDenominationCounts();
    }

    @Override
    public Currency getCurrency() {
        return cashFloat.getCurrency();
    }

    @Override
    public void resetCoins(final Map<Integer, Integer> denominationCounts) {
        Currency existingCurrency = cashFloat.getCurrency();
        validateDenominationCurrency(existingCurrency, denominationCounts);
        this.cashFloat = new CashFloat(existingCurrency, denominationCounts);
    }

    @Override
    public void addCoins(final Map<Integer, Integer> denominationCounts) {
        cashFloat.addCoins(denominationCounts);
    }

    @Override
    public Map<Integer, Integer> processTransaction(final CoinTransaction coinTransaction) {
        final Integer totalCoinValue = coinTransaction.getCoinsProvided().entrySet().stream()
                .mapToInt(entry -> entry.getKey() * entry.getValue())
                .sum();

        int changeTotal = totalCoinValue - coinTransaction.getCost();
        if (lessThanZero(changeTotal)) {
            throw new IllegalArgumentException("Insufficient coins provided to cover cost");
        }
        cashFloat.addCoins(coinTransaction.getCoinsProvided());
        return removeCoins(changeTotal);
    }

    @Override
    public Map<Integer, Integer> removeCoins(final Integer valueTotal) {
        if (lessThanZero(valueTotal)) {
            throw new IllegalArgumentException("Total to remove cannot be below 0");
        }
        if (valueTotal == 0) {
            return Collections.EMPTY_MAP;
        }

        Map<Integer, Integer> calculatedChange = new ChangeCalculator().calculateChange(cashFloat.getDenominationCounts(), valueTotal);
        cashFloat.removeCoins(calculatedChange);
        return calculatedChange;
    }

    @Override
    public void removeCoins(final Map<Integer, Integer> denominationsToRemove) {
        if (denominationsToRemove == null) {
            throw new IllegalArgumentException("denominationsToRemove cannot be null");
        }
        if (denominationsToRemove.isEmpty()) {
            return;
        }

        cashFloat.removeCoins(denominationsToRemove);
    }
}
