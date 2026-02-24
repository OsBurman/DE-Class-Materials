package com.testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtils Tests")
class StringUtilsTest {

    StringUtils utils = new StringUtils();

    @Nested
    @DisplayName("Palindrome Tests")
    class PalindromeTests {

        // TODO: Write a @ParameterizedTest using @CsvSource with at least 4 rows.
        //       Each row: "input,expectedBoolean" — e.g., "racecar,true" and "hello,false"
        //       Method signature: void testIsPalindrome(String input, boolean expected)
        //       @DisplayName: "'{0}' palindrome check should return {1}"
        //       Assert: assertEquals(expected, utils.isPalindrome(input))
    }

    @Nested
    @DisplayName("Vowel Count Tests")
    class VowelCountTests {

        // TODO: Write a @ParameterizedTest using @CsvSource with at least 3 rows.
        //       Each row: "input,expectedCount" — e.g., "hello,2" and "rhythm,0"
        //       Method signature: void testCountVowels(String input, int expected)
        //       Assert: assertEquals(expected, utils.countVowels(input))
    }

    @Nested
    @DisplayName("Capitalize Tests")
    class CapitalizeTests {

        // TODO: Write a @ParameterizedTest using @ValueSource(strings = {...})
        //       Provide at least 3 lowercase strings (e.g., "hello", "world", "java")
        //       Method signature: void testCapitalize(String input)
        //       Assert: the first character of the result is uppercase
        //       Hint: Character.isUpperCase(result.charAt(0))
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        // TODO: Write a @Test that asserts isPalindrome(null) returns false
        // TODO: Write a @Test that asserts countVowels("") returns 0
    }
}
