package com.testing;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    // TODO: Declare a Calculator field named 'calculator' (do not initialise it here)

    @BeforeAll
    static void initAll() {
        // TODO: Print "=== Calculator tests starting ==="
    }

    @BeforeEach
    void setUp() {
        // TODO: Assign a new Calculator() to the 'calculator' field
    }

    @AfterEach
    void tearDown() {
        // TODO: Print "Test completed"
    }

    @AfterAll
    static void tearDownAll() {
        // TODO: Print "=== All tests done ==="
    }

    // TODO: Write a @Test method named testAdd
    //       @DisplayName: "3 + 4 should equal 7"
    //       Arrange: inputs 3 and 4
    //       Act: call calculator.add(3, 4)
    //       Assert: result equals 7

    // TODO: Write a @Test method named testSubtract
    //       @DisplayName: "10 - 4 should equal 6"
    //       Assert: calculator.subtract(10, 4) equals 6

    // TODO: Write a @Test method named testMultiply
    //       @DisplayName: "3 * 5 should equal 15"
    //       Assert: calculator.multiply(3, 5) equals 15

    // TODO: Write a @Test method named testDivide
    //       @DisplayName: "10 / 2 should equal 5.0"
    //       Assert: calculator.divide(10, 2) equals 5.0

    // TODO: Write a @Test method named testDivideByZero
    //       @DisplayName: "Dividing by zero should throw ArithmeticException"
    //       Use assertThrows to verify that calculator.divide(5, 0) throws ArithmeticException
}
