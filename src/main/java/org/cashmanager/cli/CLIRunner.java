package org.cashmanager.cli;

import org.cashmanager.CashManager;
import org.cashmanager.contract.CashTransaction;
import org.cashmanager.contract.Currency;

import java.util.Map;
import java.util.Scanner;

import static org.cashmanager.cli.CLIUtil.*;

/**
 * Contains functionality for processing commands from the CLI
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
        try {
            if (splitCommand.length == 2) {
                String rawDenominationCounts = splitCommand[1];
                denominationCounts = processRawDenominations(rawDenominationCounts, currency);
            } else {
                denominationCounts = getCashFromInput(scanner, currency);
            }

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
                    cashManager.addCoins(Map.of(denomination, count));
                }
                case 2 -> {
                    try {
                        Integer denomination = Integer.parseInt(command[1]);
                        cashManager.addCoins(Map.of(denomination, 1));
                    } catch (NumberFormatException e) {
                        //Not a denomination so expect raw denomination counts list
                        cashManager.addCoins(processRawDenominations(command[1], currency));
                    }
                }
                default -> cashManager.addCoins(getCashFromInput(scanner, currency));
            }
        } catch (Throwable e) {
            System.out.printf("Unable to process add. %s", e.getMessage());
        }
        CLIUtil.printStatus(cashManager);

    }

    public void processTransaction(final String[] splitCommand) {
        try {
            CashTransaction cashTransaction;
            if (splitCommand.length == 3) {
                Integer cost = Integer.parseInt(splitCommand[1]);
                String rawDenominationCounts = splitCommand[2];
                cashTransaction = new CashTransaction(cost, processRawDenominations(rawDenominationCounts, currency));
            } else {
                cashTransaction = getTransactionInfoFromUser(scanner, currency);
            }

            Map<Integer, Integer> change = cashManager.processTransaction(cashTransaction);
            System.out.println("Calculated Change: ");
            CLIUtil.printDenominationCount(cashManager.getCurrency(), change);
            CLIUtil.printStatus(cashManager);
        } catch (Throwable e) {
            System.out.println("Unable to process transaction." + e.getMessage());
        }
    }

    public void processRemove(final String[] splitCommand) {
        if (splitCommand.length == 2) {
            try {
                try {
                    Integer amountToDispense = Integer.parseInt(splitCommand[1]);
                    Map<Integer, Integer> change = cashManager.removeCoins(amountToDispense);
                    System.out.println("\n----- Calculated Change ----");
                    printDenominationCount(cashManager.getCurrency(), change);
                } catch (NumberFormatException e) {
                    //Not a denomination so expect raw denomination counts list
                    Map<Integer, Integer> coinsToDispense = processRawDenominations(splitCommand[1], currency);
                    cashManager.removeCoins(coinsToDispense);
                }
            } catch (Throwable e) {
                System.out.println("Error encountered when removing counts" + e.getMessage());
            }
            CLIUtil.printStatus(cashManager);
        } else {
            System.out.println("Improper command to remove.");
        }
    }
}
