package org.cashmanager.cli;

import org.cashmanager.CashManager;
import org.cashmanager.contract.CashTransaction;
import org.cashmanager.contract.Currency;

import java.util.*;

import static org.cashmanager.util.Validators.lessThanZero;
import static org.cashmanager.util.Validators.zeroOrLess;

/**
 * Util for functions which help to capture or interpret input from the CLI or to print out information to the CLI
 */
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

    public static CashTransaction getTransactionInfoFromUser(final Scanner scanner, final Currency currency) {
        System.out.println("Enter Product Cost:");
        Integer cost = null;
        while (cost == null) {
            try {
                cost = scanner.nextInt();
                if (zeroOrLess(cost)) {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                scanner.next();
                System.out.println("Please enter a number greater than 0\n");
            }

        }
        CashTransaction cashTransaction = new CashTransaction(cost);

        int coinValue = 0;
        while (coinValue < cost) {
            System.out.println("Add coin denomination:");
            try {
                Integer coinDenomination = scanner.nextInt();
                if (zeroOrLess(cost)) {
                    throw new InputMismatchException();
                }
                if (currency.getDenominations().contains(coinDenomination)) {
                    coinValue += coinDenomination;
                    cashTransaction.addCoin(coinDenomination);
                } else {
                    System.out.println("This coin denomination was not recognized for the given Currency");
                }
            } catch (InputMismatchException e) {
                scanner.next();
                System.out.println("Please enter a number greater than 0");
            }
        }
        return cashTransaction;
    }

    public static Map<Integer, Integer> processRawDenominations(final String rawDenominations, final Currency currency) {
        Map<Integer, Integer> denominationCount = new HashMap<>();
        if (rawDenominations.isEmpty()) {
            return denominationCount;
        }

        try {
            String[] denominationPairs = rawDenominations.split(",");
            Arrays.stream(denominationPairs).forEach(denominationPair -> {
                String[] splitPair = denominationPair.split(":");
                if(splitPair.length != 2){
                    throw new IllegalArgumentException(String.format("Unexpected number of values for denomination count: %s", denominationPair));
                }
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
        System.out.println("\n\n--- Current Balance ---");
        printDenominationCount(cashManager.getCurrency(), floatStatus);
        System.out.println("\n----------------------");

    }

    public static void printDenominationCount(final Currency currency, final Map<Integer, Integer> denominationCount) {
        denominationCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> System.out.printf("%s%.2f: %d%n",
                currency.getSymbol(), entry.getKey() / 100.0, entry.getValue()));
    }
}
