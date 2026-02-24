package com.testing;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private Calculator calculator;

    // Runs once before all tests in this class — must be static
    @BeforeAll
    static void initAll() {
        System.out.println("=== Calculator tests starting ===");
    }

    // Runs before each individual test — creates a fresh instance
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    // Runs after each individual test
    @AfterEach
    void tearDown() {
        System.out.println("Test completed");
    }

    // Runs once after all tests — must be static
    @AfterAll
    static void tearDownAll() {
        System.out.println("=== All tests done ===");
    }

    @Test
    @DisplayName("3 + 4 should equal 7")
    void testAdd() {
        // Arrange
        int a = 3, b = 4;
        // Act
        int result = calculator.add(a, b);
        // Assert
        assertEquals(7, result);
    }

    @Test
    @DisplayName("10 - 4 should equal 6")
    void testSubtract() {
        assertEquals(6, calculator.subtract(10, 4));
    }

    @Test
    @DisplayName("3 * 5 should equal 15")
    void testMultiply() {
        assertEquals(15, calculator.multiply(3, 5));
    }

    @Test
    @DisplayName("10 / 2 should equal 5.0")
    void testDivide() {
        assertEquals(5.0, calculator.divide(10, 2));
    }

    @Test
    @DisplayName("Dividing by zero should throw ArithmeticException")
    void testDivideByZero() {
        // assertThrows verifies that the lambda throws the expected exception type
        assertThrows(ArithmeticException.class, () -> calculator.divide(5, 0));
    }
}
