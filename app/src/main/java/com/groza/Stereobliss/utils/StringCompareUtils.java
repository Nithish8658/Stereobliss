/* */

package com.groza.Stereobliss.utils;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

/**
 * Utils class which holds several static methods for string comparison tasks.
 */
public class StringCompareUtils {

    /**
     * Global threshold for string comparison.
     */
    private static final double COMPARE_THRESHOLD = 0.20;

    /**
     * Method to compare to strings using normalized levenshtein distance.
     * <p>
     * If the COMPARE_THRESHOLD is violated a simple contains check will be applied.
     *
     * @param expected The expected string.
     * @param actual   The actual string.
     * @return True if the comparison succeed otherwise false.
     */
    public static boolean compareStrings(final String expected, final String actual) {
        final NormalizedLevenshtein comparator = new NormalizedLevenshtein();

        double distance = comparator.distance(expected, actual);

        if (distance < COMPARE_THRESHOLD) {
            return true;
        } else {
            return actual.toLowerCase().contains(expected.toLowerCase()) || expected.toLowerCase().contains(actual.toLowerCase());
        }
    }
}
