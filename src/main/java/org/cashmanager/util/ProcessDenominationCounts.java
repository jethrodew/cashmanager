package org.cashmanager.util;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessDenominationCounts {

    /**
     * Removes denominations where the count is 0 and returns as a TreeMap
     *
     * @param denominationCounts - map of denomination counts to process
     * @return
     */
    public static TreeMap<Integer, Integer> filterEmptyAndAddToTree(final Map<Integer, Integer> denominationCounts) {
        return new TreeMap<>(denominationCounts.entrySet()
                .stream().filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    /**
     * Combines two denomination count maps.
     * Adds counts together if the same denomination exists in both
     *
     * @param map1 - first map
     * @param map2 - second map
     * @return
     */
    public static TreeMap<Integer, Integer> combineDenominationCounts(Map<Integer, Integer> map1, Map<Integer, Integer> map2) {
        return new TreeMap<>(Stream.of(map1, map2)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum)));
    }
}
