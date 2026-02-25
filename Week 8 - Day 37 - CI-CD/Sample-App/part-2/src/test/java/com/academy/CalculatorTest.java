package com.academy;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CalculatorTest — Advanced JUnit 5 test patterns for CI pipelines.
 *
 * This class demonstrates test patterns that are particularly valuable in CI:
 *
 *   @Nested           — Groups related tests into inner classes.
 *                       Makes test reports more readable in the CI artifacts.
 *
 *   @ParameterizedTest — Runs the same test logic with multiple inputs.
 *                        One test method covers many cases → better coverage,
 *                        less code.
 *
 *   @CsvSource         — Provides comma-separated (input, expected) pairs.
 *                        Compact way to test a function against a table of values.
 *
 *   @ValueSource        — Provides a list of single values to a test.
 *
 * WHY THIS MATTERS IN CI:
 *   Parameterized tests dramatically increase coverage without code duplication.
 *   More coverage means more confidence that a green CI build is truly healthy.
 *   JaCoCo measures and reports this coverage; the report is uploaded as a
 *   CI artifact after each run.
 */
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // NESTED CLASS: Addition Tests
    //
    // @Nested groups related tests. In CI reports (Surefire HTML), nested
    // classes appear as sub-sections, making it easy to identify which
    // operation failed.
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Addition Tests")
    class AdditionTests {

        /**
         * @ParameterizedTest + @CsvSource:
         *   Each string in @CsvSource is one test run: "a, b, expected".
         *   JUnit 5 parses the CSV and passes values as method parameters.
         *   This single test method generates 6 distinct test cases in the CI report.
         */
        @ParameterizedTest(name = "add({0}, {1}) = {2}")
        @CsvSource({
                "2,  3,  5",    // basic positive numbers
                "0,  0,  0",    // zero identity
                "-3, 2, -1",    // negative + positive
                "-3, -2, -5",   // both negative
                "100, 200, 300",// large numbers
                "0,  5,  5"     // zero + positive
        })
        void testAdd(int a, int b, int expected) {
            assertEquals(expected, calculator.add(a, b),
                    () -> String.format("Expected add(%d, %d) = %d", a, b, expected));
        }

        @Test
        @DisplayName("Addition is commutative: a + b == b + a")
        void testAddCommutative() {
            assertEquals(calculator.add(3, 7), calculator.add(7, 3));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // NESTED CLASS: Division Tests
    //
    // Groups all division-related tests — happy path, edge cases, and
    // error cases — so they appear together in CI reports.
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Division Tests")
    class DivisionTests {

        @Test
        @DisplayName("Basic division returns correct result")
        void testDivideHappyPath() {
            assertEquals(2.0, calculator.divide(6, 3), 0.0001);
        }

        @Test
        @DisplayName("Division with decimal result")
        void testDivideDecimal() {
            assertEquals(1.5, calculator.divide(3, 2), 0.0001);
        }

        @Test
        @DisplayName("Divide by zero throws IllegalArgumentException")
        void testDivideByZero() {
            // assertThrows verifies the exception type AND that it was actually thrown.
            // If divide(5, 0) does NOT throw, this test fails in CI.
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.divide(5, 0)
            );
            // Also verify the exception message — CI catches if someone changes the message
            assertEquals("Cannot divide by zero", exception.getMessage());
        }

        @Test
        @DisplayName("Division with negative dividend")
        void testDivideNegativeDividend() {
            assertEquals(-2.0, calculator.divide(-6, 3), 0.0001);
        }

        @Test
        @DisplayName("Division with negative divisor")
        void testDivideNegativeDivisor() {
            assertEquals(-2.0, calculator.divide(6, -3), 0.0001);
        }

        @Test
        @DisplayName("Division of two negatives gives positive")
        void testDivideBothNegative() {
            assertEquals(2.0, calculator.divide(-6, -3), 0.0001);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PARAMETERIZED PRIME TESTS
    //
    // @ValueSource provides a flat list of inputs.
    // Two methods: one for primes, one for non-primes.
    // Together they cover all known-prime and known-composite cases.
    // ═════════════════════════════════════════════════════════════════════════

    @ParameterizedTest(name = "{0} is prime")
    @ValueSource(ints = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 97 })
    @DisplayName("Should return true for known prime numbers")
    void testIsPrime_knownPrimes(int n) {
        assertTrue(calculator.isPrime(n),
                () -> n + " should be recognized as prime");
    }

    @ParameterizedTest(name = "{0} is NOT prime")
    @ValueSource(ints = { 0, 1, 4, 6, 8, 9, 10, 15, 25, 100 })
    @DisplayName("Should return false for non-prime numbers")
    void testIsPrime_notPrime(int n) {
        assertFalse(calculator.isPrime(n),
                () -> n + " should NOT be recognized as prime");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // BASIC ARITHMETIC — remaining operations
    // ═════════════════════════════════════════════════════════════════════════

    @ParameterizedTest(name = "subtract({0}, {1}) = {2}")
    @CsvSource({
            "5,  3, 2",
            "3,  5, -2",
            "0,  0, 0",
            "-1, -1, 0"
    })
    void testSubtract(int a, int b, int expected) {
        assertEquals(expected, calculator.subtract(a, b));
    }

    @ParameterizedTest(name = "multiply({0}, {1}) = {2}")
    @CsvSource({
            "2,  3,   6",
            "0,  5,   0",
            "-2, 3,  -6",
            "-2, -3,  6"
    })
    void testMultiply(int a, int b, int expected) {
        assertEquals(expected, calculator.multiply(a, b));
    }
}
