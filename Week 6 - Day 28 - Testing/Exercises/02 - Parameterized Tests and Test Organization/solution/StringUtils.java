package com.testing;

public class StringUtils {

    public boolean isPalindrome(String s) {
        if (s == null || s.isEmpty()) return false;
        String clean = s.toLowerCase();
        return clean.equals(new StringBuilder(clean).reverse().toString());
    }

    public int countVowels(String s) {
        if (s == null) return 0;
        int count = 0;
        for (char c : s.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) count++;
        }
        return count;
    }

    public String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
