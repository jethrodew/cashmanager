package org.cashmanager.contract;

import java.util.Arrays;
import java.util.List;

/**
 * Enum of the accepted currencies which can be defined
 */
public enum Currency {
    GBP("gbp", "£", List.of(200, 100, 50, 20, 10, 5, 2, 1));

    private final String name;
    private final String symbol;
    private final List<Integer> denominations;


    Currency(String name, String symbol, List<Integer> denominations){
        this.name = name;
        this.symbol = symbol;
        this.denominations = denominations;
    }

    public String getSymbol() {
        return symbol;
    }

    public List<Integer> getDenominations() {
        return denominations;
    }

    /**
     * Retrieves the Currency reference for the requested currency by name
     *
     * @param requestedCurrencyName - NOT case sensitive i.e gbp, GBP Gbp will match the same result
     * @return finds and returns the currency enum entry which corresponds to the provided name
     */
    public static Currency getCurrency(final String requestedCurrencyName){
       return Arrays.stream(Currency.values())
              .filter(currency -> currency.name.equalsIgnoreCase(requestedCurrencyName))
              .findFirst()
               .orElseThrow(()-> new IllegalArgumentException("101: Unexpected currency encountered"));
    }
}
