package org.cashmanager.cli;

import org.cashmanager.CashManager;
import org.cashmanager.contract.Currency;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CLIUtil {

    public static Map<Integer, Integer> processRawDenominations(final String rawDenominations, final Currency currency) {
        Map<Integer, Integer> denominationCount = new HashMap<>();
        if (rawDenominations.equals("0")) {
            return denominationCount;
        }

        try {
            String[] denominationPairs = rawDenominations.split(",");
            Arrays.stream(denominationPairs).forEach(denominationPair -> {
                String[] splitPair = denominationPair.split(":");
                Integer denomination = Integer.valueOf(splitPair[0]);
                Integer count = Integer.valueOf(splitPair[1]);
                if (!currency.getDenominations().contains(denomination)) {
                    throw new IllegalArgumentException(String.format("An unrecognized denomination was provided. Currency:%s, Denomination:%s", currency.name(), denomination));
                }
                denominationCount.put(denomination, count);
            });
        } catch (Throwable e) {
            System.out.println("An issue was encountered parsing denomination counts: " + e.getMessage());
            throw e;
        }
        return denominationCount;
    }


    public static void printStatus(final CashManager cashManager) {
        Map<Integer, Integer> floatStatus = cashManager.getDenominationCounts();
        System.out.println("\n--- Current Balance ---");
        printDenominationCount(cashManager, floatStatus);
        System.out.println("\n----------------------");

    }

    public static void printDenominationCount(final CashManager cashManager, final Map<Integer, Integer> denominationCount) {
        denominationCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> System.out.printf("%s%.2f: %d%n",
                cashManager.getCurrency().getSymbol(), entry.getKey() / 100.0, entry.getValue()));
    }
}
