package org.cashmanager.core.calculator;

import java.util.TreeMap;

/**
 * Simple POJO to store the Calculation result so that it is easy to handle multiple attempts at calculation
 */
public class ChangeCalculatorResult {
    private int remainingAmount;
    private TreeMap<Integer, Integer> calculatedChange;

    public ChangeCalculatorResult(int remainingAmount, TreeMap<Integer, Integer> calculatedChange) {
        this.remainingAmount = remainingAmount;
        this.calculatedChange = calculatedChange;
    }

    public int getRemainingAmount() {
        return remainingAmount;
    }

    public TreeMap<Integer, Integer> getCalculatedChange() {
        return calculatedChange;
    }
}
