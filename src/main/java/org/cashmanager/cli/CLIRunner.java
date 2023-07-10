package org.cashmanager.cli;

import org.cashmanager.CashManager;
import org.cashmanager.contract.CoinTransaction;
import org.cashmanager.contract.Currency;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import static org.cashmanager.cli.CLIUtil.getCashFromInput;
import static org.cashmanager.cli.CLIUtil.processRawDenominations;
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

    public void processReset(final String[] splitCommand) {
        Map<Integer, Integer> denominationCounts;

        if (splitCommand.length == 2) {
            String rawDenominationCounts = splitCommand[1];
            denominationCounts = processRawDenominations(rawDenominationCounts, currency);
        } else {
            denominationCounts = getCashFromInput(scanner, currency);
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
        cashManager.addCoins(getCashFromInput(scanner, currency));
    }

    public void processAdd(final Integer denomination) {
        cashManager.addCoins(Map.of(denomination, 1));
    }

    public void processAdd(final String rawDenominationCounts) {
        cashManager.addCoins(processRawDenominations(rawDenominationCounts, currency));
    }

    public void processAdd(final Integer denomination, final Integer count) {
        cashManager.addCoins(Map.of(denomination, count));
    }

    public void processTransaction(final String[] splitCommand) {
        CoinTransaction coinTransaction;
        if (splitCommand.length == 3) {
            Integer cost = Integer.parseInt(splitCommand[1]);
            String rawDenominationCounts = splitCommand[2];
            coinTransaction = new CoinTransaction(cost, processRawDenominations(rawDenominationCounts, currency));
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

    public void processRemove(final String[] splitCommand) {
        if (splitCommand.length == 2) {
            try {
                Integer amountToDispense = Integer.parseInt(splitCommand[1]);
                cashManager.removeCoins(amountToDispense);
            } catch (NumberFormatException e) {
                //Contains a String
                Map<Integer, Integer> coinsToDispense = processRawDenominations(splitCommand[1], currency);
                cashManager.removeCoins(coinsToDispense);
            }
            CLIUtil.printStatus(cashManager);
        } else {
            System.out.println("No coins specified.");
        }
    }
}
