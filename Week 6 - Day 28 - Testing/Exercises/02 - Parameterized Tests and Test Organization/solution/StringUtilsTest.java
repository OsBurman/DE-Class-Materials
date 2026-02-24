package com.testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtils Tests")
class StringUtilsTest {

    // One instance is fine here — StringUtils has no mutable state
    StringUtils utils = new StringUtils();

    @Nested
    @DisplayName("Palindrome Tests")
    class PalindromeTests {

        // @CsvSource provides two arguments per invocation: input string + expected boolean
        @ParameterizedTest(name = "''{0}'' palindrome check should return {1}")
        @CsvSource({
            "racecar, true",
            "hello,   false",
            "level,   true",
            "world,   false",
            "madam,   true"
        })
        void testIsPalindrome(String input, boolean expected) {
            assertEquals(expected, utils.isPalindrome(input));
        }
    }

    @Nested
    @DisplayName("Vowel Count Tests")
    class VowelCountTests {

        @ParameterizedTest(name = "''{0}'' should have {1} vowel(s)")
        @CsvSource({
            "hello, 2",
            "rhythm, 0",
            "aeiou, 5",
            "Spring, 1"
        })
        void testCountVowels(String input, int expected) {
            assertEquals(expected, utils.countVowels(input));
        }
    }

    @Nested
    @DisplayName("Capitalize Tests")
    class CapitalizeTests {

        // @ValueSource provides a single string per invocation
        @ParameterizedTest(name = "''{0}'' should start with an uppercase letter after capitalize")
        @ValueSource(strings = {"hello", "world", "java", "spring"})
        void testCapitalize(String input) {
            String result = utils.capitalize(input);
            // Verify the first character has been uppercased
            assertTrue(Character.isUpperCase(result.charAt(0)),
                "First char of '" + result + "' should be uppercase");
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("isPalindrome(null) should return false")
        void testPalindromeNull() {
            assertFalse(utils.isPalindrome(null));
        }

        @Test
        @DisplayName("countVowels(\"\") should return 0")
        void testCountVowelsEmpty() {
            assertEquals(0, utils.countVowels(""));
        }
    }
}
