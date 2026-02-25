package com.academy;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AppTest — Unit tests for the Calculator class.
 *
 * These tests run automatically in the CI pipeline via: mvn test
 *
 * JUnit 5 annotations used here:
 *   @Test       — marks a method as a test case
 *   @BeforeEach — runs before every @Test method (fresh setup each time)
 *
 * WHY WE TEST IN CI:
 *   Every push to the repository triggers the CI workflow.
 *   If any test fails, the build fails and the team is notified immediately.
 *   This "fail fast" approach prevents broken code from ever reaching production.
 *
 * TEST COVERAGE (reported by JaCoCo):
 *   JaCoCo measures what percentage of your code is exercised by tests.
 *   Aim for 80%+ coverage in production applications.
 *   The coverage report is uploaded as a CI artifact after each run.
 */
public class AppTest {

    private Calculator calculator;

    // @BeforeEach: creates a fresh Calculator before each test.
    // This ensures tests are ISOLATED — one test's state cannot affect another.
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    // ── Basic Arithmetic Tests ────────────────────────────────────────────────
    // assertEquals(expected, actual) — test fails if values are not equal.
    // The first argument is always the EXPECTED value.

    @Test
    void testAdd() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    void testSubtract() {
        assertEquals(1, calculator.subtract(3, 2));
    }

    @Test
    void testMultiply() {
        assertEquals(6, calculator.multiply(2, 3));
    }

    @Test
    void testDivide() {
        assertEquals(2.0, calculator.divide(6, 3));
    }

    // ── Edge Case Tests ───────────────────────────────────────────────────────
    // Edge cases are the boundaries and error conditions of your logic.
    // CI catches regressions — if someone accidentally removes the divide-by-zero
    // check, this test fails and the broken code never reaches production.

    @Test
    void testDivideByZero() {
        // assertThrows verifies that the correct exception type is thrown.
        // The lambda () -> calculator.divide(5, 0) is the code under test.
        assertThrows(IllegalArgumentException.class, () -> calculator.divide(5, 0));
    }

    @Test
    void testAddNegative() {
        // Tests that the calculator handles negative numbers correctly.
        assertEquals(-1, calculator.add(-3, 2));
    }
}
