package org.cashmanager.cli;

import org.cashmanager.contract.Currency;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CLIUtilTest {

    @Test
    void processRawDenominations_should_detect_all_denominationCounts_when_called_with_correct_value() {
        String rawDenominations = "200:1,100:2,50:3,20:4,10:5,5:6,2:7,1:8";
        Map<Integer, Integer> resultDenominationCounts = CLIUtil.processRawDenominations(rawDenominations, Currency.GBP);

        assertEquals(8, resultDenominationCounts.size());
        assertEquals(1, resultDenominationCounts.get(200));
        assertEquals(2, resultDenominationCounts.get(100));
        assertEquals(3, resultDenominationCounts.get(50));
        assertEquals(4, resultDenominationCounts.get(20));
        assertEquals(5, resultDenominationCounts.get(10));
        assertEquals(6, resultDenominationCounts.get(5));
        assertEquals(7, resultDenominationCounts.get(2));
        assertEquals(8, resultDenominationCounts.get(1));
    }

    @Test
    void processRawDenominations_should_return_empty_denominationCounts_when_called_with_empty_value() {
        Map<Integer, Integer> resultDenominationCounts = CLIUtil.processRawDenominations("", Currency.GBP);
        assertTrue(resultDenominationCounts.isEmpty());
    }

    @Test
    void processRawDenominations_should_throw_iae_when_denomination_process_not_in_currency(){
        String rawDenominations = "200:1,100:2,50:3,30:4,10:5,5:6,2:7,1:8";
        assertThrows(IllegalArgumentException.class, ()->CLIUtil.processRawDenominations(rawDenominations, Currency.GBP));
    }

    @Test
    void processRawDenominations_should_throw_iae_when_denominationCounts_contain_invalid_characters_or_formatting(){
        assertThrows(IllegalArgumentException.class, ()->CLIUtil.processRawDenominations("200:1,100:two,50:3,20:4,10:5,5:6,2:7,1:8", Currency.GBP));
        assertThrows(IllegalArgumentException.class, ()->CLIUtil.processRawDenominations("200:1,100:250:3,20:4,10:5,5:6,2:7,1:8", Currency.GBP));
        assertThrows(IllegalArgumentException.class, ()->CLIUtil.processRawDenominations("200:1,100:2,50,20:4,10:5,5:6,2:7,1:8", Currency.GBP));
    }
}
