package org.cashmanager.core.calculator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChangeCalculatorTest {

    @Test
    void calculateChange_should_return_all_coins_when_called() {
        Map<Integer, Integer> floatDenominationCounts = Map.of(200, 1, 100, 1, 50, 1, 20, 1, 10, 1, 5, 1, 2, 1, 1, 1);
        int total = 388;

        Map<Integer, Integer> result = new ChangeCalculator().calculateChange(floatDenominationCounts, total);
        assertEquals(8, result.size());
        assertEquals(1, result.get(200));
        assertEquals(1, result.get(100));
        assertEquals(1, result.get(50));
        assertEquals(1, result.get(20));
        assertEquals(1, result.get(10));
        assertEquals(1, result.get(5));
        assertEquals(1, result.get(2));
        assertEquals(1, result.get(1));
    }

    @Test
    void calculateChange_should_throw_ise_when_not_enough_coins_float() {
        Map<Integer, Integer> floatDenominationCounts = Map.of(10, 3, 5, 4);
        int total = 2;

        assertThrows(IllegalStateException.class, () -> new ChangeCalculator().calculateChange(floatDenominationCounts, total));
    }

    @Test
    void calculateChange_should_return_all_coins_when_top_down_denomination_match_will_fail_but_recalculate_successful() {
        Map<Integer, Integer> floatDenominationCounts = Map.of(200, 1, 100, 1, 50, 1, 20, 1, 10, 1, 5, 1, 2, 3);
        int total = 16;

        Map<Integer, Integer> result = new ChangeCalculator().calculateChange(floatDenominationCounts, total);
        assertEquals(3, result.size());
        assertEquals(1, result.get(10));
        assertEquals(3, result.get(2));
        assertEquals(0, result.get(5));
    }


    @Test
    void calculateChange_should_return_all_coins_when_top_down_denomination_match_succeeds() {
        Map<Integer, Integer> floatDenominationCounts = Map.of(200, 1, 100, 1, 50, 1, 20, 1, 10, 1, 5, 1, 2, 1, 1, 11);
        int total = 71;

        Map<Integer, Integer> result = new ChangeCalculator().calculateChange(floatDenominationCounts, total);
        assertEquals(3, result.size());
        assertEquals(1, result.get(50));
        assertEquals(1, result.get(20));
        assertEquals(1, result.get(1));
    }

    @Test
    void calculateChange_should_return_all_coins_when_top_down_denomination_match_fails_and_search_for_higher_number_recalculate() {
        Map<Integer, Integer> floatDenominationCounts = Map.of(200, 1, 100, 1, 50, 1, 20, 1, 10, 1, 5, 1, 2, 3);
        int total = 71;

        Map<Integer, Integer> result = new ChangeCalculator().calculateChange(floatDenominationCounts, total);
        assertEquals(5, result.size());
        assertEquals(1, result.get(50));
        assertEquals(0, result.get(20));
        assertEquals(1, result.get(10));
        assertEquals(1, result.get(5));
        assertEquals(3, result.get(2));
    }

    @Disabled("The algorithm is unable to solve this because there are not enough coins at both the top and bottom end.")
    @Test
    void calculateChange_should_return_all_coins_when_top_down_denomination_match_fails_and_search_for_higher_number_recalculate_even_higher() {
        Map<Integer, Integer> floatDenominationCounts = Map.of(50, 3, 20, 3, 5, 1, 2, 1, 1, 1);
        int total = 111;

        Map<Integer, Integer> result = new ChangeCalculator().calculateChange(floatDenominationCounts, total);
        assertEquals(5, result.size());
        assertEquals(1, result.get(50));
        assertEquals(3, result.get(20));
        assertEquals(0, result.get(5));
        assertEquals(0, result.get(2));
        assertEquals(1, result.get(1));
    }
}
