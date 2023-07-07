package org.cashmanager.cli;

import org.cashmanager.CashManager;
import org.cashmanager.contract.CoinTransaction;
import org.cashmanager.contract.Currency;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import static org.cashmanager.util.Validators.lessThanZero;
import static org.cashmanager.util.Validators.zeroOrLess;

/**
 * Contains functionality specifically for managing user interaction via the CLI
 */
public class CLIRunner {
    private final Scanner scanner;
    private final CashManager cashManager;
    private final Currency currency;

    public CLIRunner(Scanner scanner, CashManager cashManager, Currency currency) {
        this.scanner = scanner;
        this.cashManager = cashManager;
        this.currency = currency;
    }

    public Map<Integer, Integer> getCashFromInput() {
        Map<Integer, Integer> denominationCounts = new HashMap<>();
        currency.getDenominations().forEach(denomination ->
                denominationCounts.put(denomination, getDenominationCountFromInput(denomination)));
        return denominationCounts;
    }

    private Integer getDenominationCountFromInput(final Integer denomination) {
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

    public void processReset(final String[] splitCommand) {
        Map<Integer, Integer> denominationCounts;

        if (splitCommand.length == 2) {
            String rawDenominationCounts = splitCommand[1];
            denominationCounts = CLIUtil.processRawDenominations(rawDenominationCounts, currency);
        } else {
            denominationCounts = getCashFromInput();
        }

        try {
            cashManager.resetCoins(denominationCounts);
            System.out.println("Reset Complete!");
        } catch (Throwable e) {
            System.out.println("Unable to reset float." + e.getMessage());
        }
        CLIUtil.printStatus(cashManager);
    }

    public void processAdd(final String[] command) {
        try {
            switch (command.length) {
                case 3 -> {
                    Integer denomination = Integer.parseInt(command[1]);
                    Integer count = Integer.parseInt(command[2]);
                    processAdd(denomination, count);
                }
                case 2 -> {
                    try {
                        Integer denomination = Integer.parseInt(command[1]);
                        processAdd(denomination);
                    } catch (NumberFormatException e) {
                        //Contains a String
                        processAdd(command[1]);
                    }
                }
                default -> processAdd();
            }
        } catch (Throwable e) {
            System.out.printf("Unable to process add. %s", e.getMessage());
        }

        CLIUtil.printStatus(cashManager);
    }

    public void processAdd() {
        Map<Integer, Integer> denominationCounts = getCashFromInput();
        cashManager.addCoins(denominationCounts);
    }

    public void processAdd(final Integer denomination) {
        cashManager.addCoins(Map.of(denomination, 1));
    }

    public void processAdd(final String rawDenominationCounts) {
        Map<Integer, Integer> denominationCounts = CLIUtil.processRawDenominations(rawDenominationCounts, currency);
        cashManager.addCoins(denominationCounts);
    }

    public void processAdd(final Integer denomination, final Integer count) {
        cashManager.addCoins(Map.of(denomination, count));
    }

    public void processTransaction(final String[] splitCommand) {
        CoinTransaction coinTransaction;
        if (splitCommand.length == 3) {
            Integer cost = Integer.parseInt(splitCommand[1]);
            String rawDenominationCounts = splitCommand[2];
            coinTransaction = new CoinTransaction(cost, CLIUtil.processRawDenominations(rawDenominationCounts, currency));
        } else {
            coinTransaction = getTransactionInfoFromUser();
        }

        try {
            Map<Integer, Integer> change = cashManager.processTransaction(coinTransaction);
            System.out.println("Calculated Change: ");
            CLIUtil.printDenominationCount(cashManager, change);
            CLIUtil.printStatus(cashManager);
        } catch (Throwable e) {
            System.out.println("Unable to process add." + e.getMessage());
        }
    }

    private CoinTransaction getTransactionInfoFromUser() {
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
        CoinTransaction coinTransaction = new CoinTransaction(cost);

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
                    coinTransaction.addCoin(coinDenomination);
                } else {
                    System.out.println("This coin denomination was not recognized for the given Currency");
                }
            } catch (InputMismatchException e) {
                scanner.next();
                System.out.println("Please enter a number greater than 0");
            }
        }
        return coinTransaction;
    }

    public void processRemove(String[] splitCommand) {
        if (splitCommand.length == 2) {
            try {
                Integer amountToDispense = Integer.parseInt(splitCommand[1]);
                cashManager.removeCoins(amountToDispense);
            } catch (NumberFormatException e) {
                //Contains a String
                Map<Integer, Integer> coinsToDispense = CLIUtil.processRawDenominations(splitCommand[1], currency);
                cashManager.removeCoins(coinsToDispense);
            }
            CLIUtil.printStatus(cashManager);
        } else {
            System.out.println("No coins specified.");
        }
    }
}
