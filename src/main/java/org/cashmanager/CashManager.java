package org.cashmanager;

import org.cashmanager.contract.CoinTransaction;
import org.cashmanager.contract.Currency;

import java.util.Map;

/**
 * Interface for interacting with the cash manager
 */
public interface CashManager {

    /**
     * Returns the float contents as denomination counts
     *
     * @return Map<Denomination, Count> e.g. <200, 10> = 10 £2 coins
     */
    Map<Integer, Integer> getDenominationCounts();

    /**
     * Returns the currently set currency
     *
     * @return @{Currency}
     */
    Currency getCurrency();

    /**
     * Overrides current recorded contents of the float with new denomination counts
     *
     * @param denominationAmounts - Map<Denomination, Count> e.g. <200, 10> = 10 £2 coins
     */
    void resetCoins(Map<Integer, Integer> denominationAmounts);

    /**
     * Adds the denomination amounts to the existing float
     *
     * @param denominationAmounts e.g. <200, 10> = 10 £2 coins
     */
    void addCoins(Map<Integer, Integer> denominationAmounts);

    /**
     * Processes a transaction, updating the float with the provided coins and deducting the change necessary.
     *
     * @param coinTransaction - @{CoinTransaction} instance which has a cost and the provided set of coins
     * @return coins to dispense as change as Map<Denomination, Count> e.g. <200, 10> = 10 £2 coins
     */
    Map<Integer, Integer> processTransaction(CoinTransaction coinTransaction);

    /**
     * Calculates a total coin value which would exactly match the value total provided and then removes it from the float
     * Returns a map of denomination counts which are the change it has calculated to remove
     *
     * @param valueTotal - the total value that you wish to have removed
     * @return - Map of denomination counts {200:1,20:2,5:1,2:1}
     */
    Map<Integer, Integer> removeCoins(Integer valueTotal);


    /**
     * Removes count number of coins for each denomination from the float
     *
     * @param denominationCountsToRemove - map of coin denomination counts
     *                                   e.g. {
     *                                   200: 13,
     *                                   100: 13,
     *                                   50: 0,
     *                                   20: 50,
     *                                   10: 50,
     *                                   5: 30,
     *                                   2: 0,
     *                                   1: 0
     *                                   }
     */
    void removeCoins(Map<Integer, Integer> denominationCountsToRemove);
}