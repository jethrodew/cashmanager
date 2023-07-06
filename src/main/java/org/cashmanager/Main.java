package org.cashmanager;

import org.cashmanager.core.CashManagerImpl;
import org.cashmanager.contract.Currency;
import org.cashmanager.cli.CLIRunner;
import org.cashmanager.cli.CLIUtil;

import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Currency currency;
        try {
            currency = Currency.getCurrency(args[0]);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return;
        }

        Map<Integer, Integer> denominationCount;
        try {
            denominationCount = CLIUtil.processRawDenominations(args[1], currency);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return;
        }

        CashManager cashManager;
        try {
            cashManager = new CashManagerImpl(currency, denominationCount);
        } catch (Exception e) {
            System.out.println("An error was encountered initializing the Float");
            System.exit(1);
            return;
        }
        CLIUtil.printStatus(cashManager);

        boolean allowManualRunning = Boolean.TRUE.toString().equalsIgnoreCase(args[2]);
        if (allowManualRunning) {
            CLIRunner cliRunner = new CLIRunner(scanner, cashManager, currency);
            if (denominationCount.isEmpty()) {
                cashManager.addCoins(cliRunner.getCashFromInput());
            }
            initiateManualRunner(cashManager, cliRunner);
        }
    }

    private static void initiateManualRunner(final CashManager cashManager, final CLIRunner cliRunner) {
        System.out.println("\nRunning...\nEnter \"help\" for list of commands");
        while (true) {
            System.out.println("\nWaiting for input...");
            String command = scanner.nextLine();
            String[] splitCommand = command.split(" ");
            switch (splitCommand[0].toLowerCase()) {
                case "help" -> cliRunner.printHelp();
                case "status" -> CLIUtil.printStatus(cashManager);
                case "reset" ->

                        cliRunner.processReset(splitCommand);


                case "add" -> cliRunner.processAdd(splitCommand);
                case "transaction" -> cliRunner.processTransaction(splitCommand);
                case "dispense" -> cliRunner.processDispense(splitCommand);
                case "exit" -> System.exit(0);
                default -> System.exit(1);
            }
        }
    }
}