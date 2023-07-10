package org.cashmanager.cli;

import org.cashmanager.CashManager;
import org.cashmanager.contract.Currency;

import java.util.*;

import static org.cashmanager.util.Validators.lessThanZero;

public class CLIUtil {

    public static Map<Integer, Integer> getCashFromInput(final Scanner scanner, final Currency currency) {
        Map<Integer, Integer> denominationCounts = new HashMap<>();
        currency.getDenominations().forEach(denomination ->
                denominationCounts.put(denomination, getDenominationCountFromInput(scanner, currency, denomination)));
        return denominationCounts;
    }

    private static Integer getDenominationCountFromInput(final Scanner scanner, final Currency currency, final Integer denomination) {
        System.out.printf("Enter coin count for %s%.2f:%n", currency.getSymbol(), denomination / 100.0);

        Integer count = null;
        while (count == null) {
            try {
                Integer readCount = scanner.nextInt();
                if (lessThanZero(readCount)) {
                    throw new InputMismatchException();
                } else {
                    count = readCount;
                }
            } catch (InputMismatchException e) {
                scanner.next();
                System.out.println("Please enter a number greater than or equal to 0.\n");
            }
        }
        return count;
    }

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
