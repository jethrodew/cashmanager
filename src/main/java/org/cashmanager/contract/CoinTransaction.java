package org.cashmanager.contract;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO storing details for a transaction
 */
public class CoinTransaction {

    /**
     * Total Purchase Cost
     */
    private final Integer cost;

    /**
     * Coins provided as a set of denominations and each of their counts
     * Map<200, 10> = contains 10 £2 coins
     */
    private final Map<Integer, Integer> coinsProvided;


    public CoinTransaction(Integer cost) {
        this.cost = cost;
        coinsProvided = new HashMap<>();
    }

    public CoinTransaction(Integer cost, Map<Integer, Integer> coinsProvided) {
        this.cost = cost;
        this.coinsProvided = coinsProvided;
    }

    public Integer getCost() {
        return cost;
    }

    public Map<Integer, Integer> getCoinsProvided() {
        return coinsProvided;
    }

    public void addCoin(final Integer denomination ){
        Integer existingCount = coinsProvided.getOrDefault(denomination, 0);

        coinsProvided.put(denomination, existingCount + 1);
    }
}
