package org.cashmanager.util;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static org.cashmanager.util.ProcessDenominationCounts.combineDenominationCounts;
import static org.cashmanager.util.ProcessDenominationCounts.filterEmptyAndAddToTree;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessDenominationCountsTest {

    @Test
    void filterEmptyAndAddToTree_should_remove_0_value_denomination_counts_and_return_TreeMap_when_called() {
        Map<Integer, Integer> denominationCounts = Map.of(1, 10, 2, 20, 100, 0, 50, 5, 20, 3);
        assertEquals(5, denominationCounts.size());

        TreeMap<Integer, Integer> result = filterEmptyAndAddToTree(denominationCounts);

        Iterator<Map.Entry<Integer, Integer>> resultIterator = result.entrySet().iterator();
        assertEquals(4, result.size());
        Map.Entry one = resultIterator.next();
        assertEquals(1, one.getKey());
        assertEquals(10, one.getValue());
        Map.Entry two = resultIterator.next();
        assertEquals(2, two.getKey());
        assertEquals(20, two.getValue());
        Map.Entry twenty = resultIterator.next();
        assertEquals(20, twenty.getKey());
        assertEquals(3, twenty.getValue());
        Map.Entry fifty = resultIterator.next();
        assertEquals(50, fifty.getKey());
        assertEquals(5, fifty.getValue());
    }


    @Test
    void combineDenominationCounts_should_combine_existing_values_and_add_new_values_from_each_map_when_called() {
        Map<Integer, Integer> map1 = Map.of(10, 2, 50, 7);
        Map<Integer, Integer> map2 = Map.of(10, 1, 20, 5);
        TreeMap<Integer, Integer> combinedMap = combineDenominationCounts(map1, map2);

        assertEquals(3, combinedMap.get(10));
        assertEquals(7, combinedMap.get(50));
        assertEquals(5, combinedMap.get(20));
    }
}
