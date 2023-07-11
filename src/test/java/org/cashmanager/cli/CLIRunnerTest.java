package org.cashmanager.cli;

import org.cashmanager.CashManager;
import org.cashmanager.contract.CashTransaction;
import org.cashmanager.contract.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CLIRunnerTest {
    /**
     * Additional Testing of user inputs could be achieved either with a separate script run in the CI or with a set of integration type tests run with multi-threaded testing where one runs the application and the other
     * I believe there is greater value in creating ci based integration tests which can form an acceptance test contract
     */
    private CLIRunner cliRunner;

    private CashManager cashManager;


    private

    @BeforeEach
    void setup() {
        Scanner scanner = new Scanner(System.in);
        cashManager = mock(CashManager.class);
        Currency currency = Currency.GBP;

        cliRunner = new CLIRunner(scanner, cashManager, currency);


    }

    @Test
    void processReset_should_reset_when_called_with_denominationCounts() {
        String[] splitCommand = Arrays.asList("reset", "200:1,100:3,50:10").toArray(new String[2]);
        ArgumentCaptor<Map<Integer, Integer>> denominationCountCaptor = ArgumentCaptor.forClass(Map.class);

        cliRunner.processReset(splitCommand);

        verify(cashManager).resetCoins(denominationCountCaptor.capture());

        Map<Integer, Integer> denominationCountCaptured = denominationCountCaptor.getValue();
        assertEquals(3, denominationCountCaptured.size());
        assertEquals(1, denominationCountCaptured.get(200));
        assertEquals(3, denominationCountCaptured.get(100));
        assertEquals(10, denominationCountCaptured.get(50));
    }

    @Test
    void processReset_should_catch_when_reset_coins_throws_exception() {
        String[] splitCommand = Arrays.asList("reset", "200:1,100:3,50:10").toArray(new String[2]);

        doThrow(new IllegalArgumentException()).when(cashManager).resetCoins(anyMap());
        cliRunner.processReset(splitCommand);

        doThrow(new NullPointerException()).when(cashManager).resetCoins(anyMap());
        cliRunner.processReset(splitCommand);

        verify(cashManager,times(2)).resetCoins(anyMap());
    }

    @Test
    void processAdd_should_add_when_called_with_denomination_and_count() {
        String[] splitCommand = Arrays.asList("add", "20", "3").toArray(new String[3]);
        ArgumentCaptor<Map<Integer, Integer>> denominationCountCaptor = ArgumentCaptor.forClass(Map.class);

        cliRunner.processAdd(splitCommand);

        verify(cashManager).addCoins(denominationCountCaptor.capture());

        Map<Integer, Integer> denominationCountCaptured = denominationCountCaptor.getValue();
        assertEquals(1, denominationCountCaptured.size());
        assertEquals(3, denominationCountCaptured.get(20));
    }

    @Test
    void processAdd_should_add_when_called_with_denomination() {
        String[] splitCommand = Arrays.asList("add", "10").toArray(new String[2]);
        ArgumentCaptor<Map<Integer, Integer>> denominationCountCaptor = ArgumentCaptor.forClass(Map.class);

        cliRunner.processAdd(splitCommand);

        verify(cashManager).addCoins(denominationCountCaptor.capture());

        Map<Integer, Integer> denominationCountCaptured = denominationCountCaptor.getValue();
        assertEquals(1, denominationCountCaptured.size());
        assertEquals(1, denominationCountCaptured.get(10));
    }

    @Test
    void processAdd_should_add_when_called_with_denominationCounts() {
        String[] splitCommand = Arrays.asList("add", "200:1,100:3,50:10").toArray(new String[2]);
        ArgumentCaptor<Map<Integer, Integer>> denominationCountCaptor = ArgumentCaptor.forClass(Map.class);

        cliRunner.processAdd(splitCommand);

        verify(cashManager).addCoins(denominationCountCaptor.capture());

        Map<Integer, Integer> denominationCountCaptured = denominationCountCaptor.getValue();
        assertEquals(3, denominationCountCaptured.size());
        assertEquals(1, denominationCountCaptured.get(200));
        assertEquals(3, denominationCountCaptured.get(100));
        assertEquals(10, denominationCountCaptured.get(50));
    }

    @Test
    void processAdd_should_catch_when_add_coins_throws_exception() {
        String[] splitCommand = Arrays.asList("add", "200:1,100:3,50:10").toArray(new String[2]);

        doThrow(new IllegalArgumentException()).when(cashManager).addCoins(anyMap());
        cliRunner.processAdd(splitCommand);

        doThrow(new NullPointerException()).when(cashManager).addCoins(anyMap());
        cliRunner.processAdd(splitCommand);

        verify(cashManager,times(2)).addCoins(anyMap());
    }

    @Test
    void processTransaction_should_capture_cost_and_raw_denomination_counts_and_submit_transaction_when_called() {
        String[] splitCommand = Arrays.asList("transaction", "45", "20:1,10:3,5:1").toArray(new String[3]);
        ArgumentCaptor<CashTransaction> transactionCaptor = ArgumentCaptor.forClass(CashTransaction.class);

        cliRunner.processTransaction(splitCommand);

        verify(cashManager).processTransaction(transactionCaptor.capture());

        CashTransaction cashTransaction = transactionCaptor.getValue();
        assertEquals(45, cashTransaction.getCost());

        Map<Integer, Integer> denominationCountCaptured = cashTransaction.getCoinsProvided();
        assertEquals(3, denominationCountCaptured.size());
        assertEquals(1, denominationCountCaptured.get(20));
        assertEquals(3, denominationCountCaptured.get(10));
        assertEquals(1, denominationCountCaptured.get(5));
    }

    @Test
    void processTransaction_should_catch_when_transaction_throws_exception() {
        String[] splitCommand = Arrays.asList("transaction", "33","10:3,5:1").toArray(new String[2]);

        doThrow(new IllegalArgumentException()).when(cashManager).processTransaction(any(CashTransaction.class));
        cliRunner.processTransaction(splitCommand);

        doThrow(new NullPointerException()).when(cashManager).processTransaction(any(CashTransaction.class));
        cliRunner.processTransaction(splitCommand);

        verify(cashManager,times(2)).processTransaction(any(CashTransaction.class));
    }

    @Test
    void processRemove_should_remove_value_when_called_with_only_integer_value() {
        String[] splitCommand = Arrays.asList("remove", "75").toArray(new String[2]);
        ArgumentCaptor<Integer> valueCaptor = ArgumentCaptor.forClass(Integer.class);

        cliRunner.processRemove(splitCommand);

        verify(cashManager).removeCoins(valueCaptor.capture());

        assertEquals(75, valueCaptor.getValue());
    }

    @Test
    void processRemove_should_remove_count_of_denomination_when_denomination_and_count_provided() {
        String[] splitCommand = Arrays.asList("remove", "200:1,50:3,10:7").toArray(new String[2]);
        ArgumentCaptor<Map<Integer, Integer>> denominationCountCaptor = ArgumentCaptor.forClass(Map.class);

        cliRunner.processRemove(splitCommand);

        verify(cashManager).removeCoins(denominationCountCaptor.capture());

        Map<Integer, Integer> denominationCountCaptured = denominationCountCaptor.getValue();
        assertEquals(3, denominationCountCaptured.size());
        assertEquals(1, denominationCountCaptured.get(200));
        assertEquals(3, denominationCountCaptured.get(50));
        assertEquals(7, denominationCountCaptured.get(10));
    }

    @Test
    void processRemove_should_do_nothing_when_remove_coins_encounters_exception() {
        String[] splitCommand = Arrays.asList("remove", "50").toArray(new String[1]);
        doThrow(new NullPointerException()).when(cashManager).removeCoins(anyInt());

        cliRunner.processRemove(splitCommand);

        verify(cashManager).removeCoins(anyInt());
    }

    @Test
    void processRemove_should_do_nothing_when_command_empty() {
        String[] splitCommand = Arrays.asList("remove").toArray(new String[1]);

        cliRunner.processRemove(splitCommand);

        verify(cashManager,never()).removeCoins(anyInt());
        verify(cashManager,never()).removeCoins(anyMap());
    }


    @Test
    void processRemove_should_do_nothing_when_command_too_long() {
        String[] splitCommand = Arrays.asList("remove", "20:1", "10:2").toArray(new String[3]);

        cliRunner.processRemove(splitCommand);

        verify(cashManager,never()).removeCoins(anyInt());
        verify(cashManager,never()).removeCoins(anyMap());
    }

}
