package com.academy;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 28 — Part 1: JUnit 5 Unit Tests for Calculator
 * =====================================================
 * Run: mvn test
 *
 * Demonstrates:
 *   ✓ @BeforeAll / @AfterAll  — run once per class
 *   ✓ @BeforeEach / @AfterEach — run per test
 *   ✓ @Test + @DisplayName
 *   ✓ @Nested — group related tests
 *   ✓ @Disabled — skip a test
 *   ✓ @ParameterizedTest + @CsvSource + @ValueSource
 *   ✓ assertEquals, assertTrue, assertFalse, assertAll, assertThrows
 */
@DisplayName("Calculator — JUnit 5 Demo")
class CalculatorTest {

    private Calculator calc;

    // ── Lifecycle Hooks ────────────────────────────────────────────────

    @BeforeAll
    static void setupAll() {
        System.out.println("=== @BeforeAll: runs ONCE before any test ===");
    }

    @AfterAll
    static void teardownAll() {
        System.out.println("=== @AfterAll: runs ONCE after all tests ===");
    }

    @BeforeEach
    void setup() {
        calc = new Calculator();
        System.out.println("@BeforeEach: fresh Calculator created");
    }

    @AfterEach
    void teardown() {
        System.out.println("@AfterEach: test finished\n");
    }

    // ── Addition ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("Addition")
    class AdditionTests {

        @Test
        @DisplayName("adds two positive numbers")
        void addPositive() {
            assertEquals(5, calc.add(2, 3), "2 + 3 should equal 5");
        }

        @Test
        @DisplayName("adds negative and positive")
        void addNegativePositive() {
            assertEquals(-1, calc.add(-3, 2));
        }

        @Test
        @DisplayName("adding zeros returns zero")
        void addZeros() {
            assertEquals(0, calc.add(0, 0));
        }

        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({"1, 2, 3", "10, 20, 30", "-5, 5, 0", "100, -50, 50"})
        @DisplayName("parameterized addition")
        void parameterizedAdd(double a, double b, double expected) {
            assertEquals(expected, calc.add(a, b));
        }
    }

    // ── Multiplication with assertAll ─────────────────────────────────────
    @Nested
    @DisplayName("Multiplication")
    class MultiplicationTests {

        @Test
        @DisplayName("assertAll — checks all assertions even if one fails")
        void multiply() {
            assertAll("multiplication",
                () -> assertEquals(6,  calc.multiply(2, 3)),
                () -> assertEquals(0,  calc.multiply(5, 0)),
                () -> assertEquals(-4, calc.multiply(-2, 2))
            );
        }
    }

    // ── Division ──────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Division")
    class DivisionTests {

        @Test
        @DisplayName("divides normally")
        void divideNormal() {
            assertEquals(5.0, calc.divide(10, 2), 0.001);
        }

        @Test
        @DisplayName("throws ArithmeticException on divide by zero")
        void divideByZero() {
            ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> calc.divide(10, 0),
                "Should throw ArithmeticException"
            );
            assertTrue(ex.getMessage().contains("zero"));
        }

        @ParameterizedTest(name = "{0} / {1} = {2}")
        @CsvSource({"10, 2, 5.0", "9, 3, 3.0", "7, 2, 3.5"})
        void parameterizedDivide(double a, double b, double expected) {
            assertEquals(expected, calc.divide(a, b), 0.001);
        }
    }

    // ── Factorial ─────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Factorial")
    class FactorialTests {

        @Test @DisplayName("0! = 1") void fact0()  { assertEquals(1, calc.factorial(0)); }
        @Test @DisplayName("1! = 1") void fact1()  { assertEquals(1, calc.factorial(1)); }
        @Test @DisplayName("5! = 120") void fact5() { assertEquals(120, calc.factorial(5)); }
        @Test @DisplayName("10! = 3628800") void fact10() { assertEquals(3628800, calc.factorial(10)); }

        @Test
        @DisplayName("throws IllegalArgumentException for negative input")
        void factorialNegative() {
            assertThrows(IllegalArgumentException.class, () -> calc.factorial(-1));
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -5, -100})
        @DisplayName("throws for any negative number")
        void factorialNegativeParameterized(int n) {
            assertThrows(IllegalArgumentException.class, () -> calc.factorial(n));
        }
    }

    // ── isPrime ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("isPrime")
    class PrimeTests {

        @ParameterizedTest(name = "{0} is prime")
        @ValueSource(ints = {2, 3, 5, 7, 11, 13, 97})
        void prime(int n) { assertTrue(calc.isPrime(n)); }

        @ParameterizedTest(name = "{0} is NOT prime")
        @ValueSource(ints = {0, 1, 4, 6, 8, 9, 100})
        void notPrime(int n) { assertFalse(calc.isPrime(n)); }
    }

    // ── TDD: Disabled (write test first, implement later) ─────────────────────
    @Test
    @Disabled("TDD: test written first — squareRoot not yet implemented")
    @DisplayName("squareRoot (not yet implemented)")
    void squareRoot() {
        assertEquals(4.0, calc.squareRoot(16), 0.001);
    }
}
