package org.cashmanager;

import org.cashmanager.contract.Currency;
import org.cashmanager.contract.CoinTransaction;

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
     * Adds the count for the denomination to the existing float
     *
     * @param denomination - e.g. 200 - £2
     * @param count        - e.g. 10 - there are 10 of the provided denomination
     */
    void addCoins(Integer denomination, Integer count);

    /**
     * Processes a transaction, updating the float with the provided coins and deducting the change necessary.
     *
     * @param coinTransaction - @{CoinTransaction} instance which has a cost and the provided set of coins
     * @return coins to dispense as change as Map<Denomination, Count> e.g. <200, 10> = 10 £2 coins
     */
    Map<Integer, Integer> processTransaction(CoinTransaction coinTransaction);

    /**
     *
     * @param changeTotal
     * @return
     */
    Map<Integer, Integer> removeCoins(Integer changeTotal);


    void removeCoins(Map<Integer,Integer> denominationsToRemove);
}