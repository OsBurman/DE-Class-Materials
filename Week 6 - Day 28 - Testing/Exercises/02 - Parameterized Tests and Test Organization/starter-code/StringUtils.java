package com.testing;

/**
 * Utility class under test — do NOT modify.
 */
public class StringUtils {

    /** Returns true if the string reads the same forwards and backwards (case-insensitive). */
    public boolean isPalindrome(String s) {
        if (s == null || s.isEmpty()) return false;
        String clean = s.toLowerCase();
        return clean.equals(new StringBuilder(clean).reverse().toString());
    }

    /** Returns the number of vowels (a, e, i, o, u) in the string (case-insensitive). */
    public int countVowels(String s) {
        if (s == null) return 0;
        int count = 0;
        for (char c : s.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) count++;
        }
        return count;
    }

    /** Returns the string with its first letter capitalised; leaves the rest unchanged. */
    public String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
