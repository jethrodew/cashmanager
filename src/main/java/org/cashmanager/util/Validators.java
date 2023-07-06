package org.cashmanager.util;

/**
 * Class to contain static validators which can be generically reused.
 * Should provided text representations of validation rather than requiring complex or easily mistyped operations
 */
public class Validators {
    public static boolean zeroOrLess(final Integer integer) {
        return integer <= 0;
    }
    public static boolean lessThanZero(final Integer integer) {
        return integer < 0;
    }
}
