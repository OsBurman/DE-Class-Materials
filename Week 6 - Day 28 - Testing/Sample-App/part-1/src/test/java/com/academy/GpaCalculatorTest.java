package com.academy;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 28 — Part 1: GpaCalculator Tests
 * ======================================
 * Demonstrates boundary-value testing and parameterized tests.
 */
@DisplayName("GpaCalculator Tests")
class GpaCalculatorTest {

    private GpaCalculator gpaCalc;

    @BeforeEach
    void setup() {
        gpaCalc = new GpaCalculator();
    }

    @ParameterizedTest(name = "GPA {0} → grade {1}")
    @CsvSource({
        "4.0, A",
        "3.8, A",
        "3.5, A",
        "3.4, B",
        "3.0, B",
        "2.5, C",
        "2.0, C",
        "1.5, D",
        "1.0, D",
        "0.9, F",
        "0.0, F"
    })
    @DisplayName("calculateLetterGrade returns correct grade")
    void letterGrade(double gpa, String expected) {
        assertEquals(expected, gpaCalc.calculateLetterGrade(gpa));
    }

    @ParameterizedTest(name = "GPA {0} is honors eligible")
    @ValueSource(doubles = {3.5, 3.6, 3.8, 4.0})
    @DisplayName("isHonorsEligible returns true for GPA >= 3.5")
    void honorsEligible(double gpa) {
        assertTrue(gpaCalc.isHonorsEligible(gpa));
    }

    @ParameterizedTest(name = "GPA {0} is NOT honors eligible")
    @ValueSource(doubles = {3.4, 3.0, 2.5, 1.0, 0.0})
    @DisplayName("isHonorsEligible returns false for GPA < 3.5")
    void notHonorsEligible(double gpa) {
        assertFalse(gpaCalc.isHonorsEligible(gpa));
    }
}

class GpaCalculatorTest {

    private GpaCalculator gpaCalc;

    @BeforeEach
    void setup() {
        gpaCalc = new GpaCalculator();
    }

    @ParameterizedTest(name = "GPA {0} → grade {1}")
    @CsvSource({
        "3.9, A",
        "3.7, A",
        "3.5, B",
        "3.3, B",
        "3.0, C",
        "2.7, C",
        "2.3, D",
        "2.0, D",
        "1.5, F",
        "0.0, F"
    })
    @DisplayName("calculateLetterGrade — various GPAs")
    void calculateLetterGrade(double gpa, String expected) {
        assertEquals(expected, gpaCalc.calculateLetterGrade(gpa));
    }

    @ParameterizedTest(name = "GPA {0} → honors eligible")
    @ValueSource(doubles = {3.5, 3.6, 3.8, 4.0})
    @DisplayName("isHonorsEligible — true for GPA >= 3.5")
    void isHonorsEligible(double gpa) {
        assertTrue(gpaCalc.isHonorsEligible(gpa));
    }

    @ParameterizedTest(name = "GPA {0} → NOT honors eligible")
    @ValueSource(doubles = {3.4, 3.0, 2.5, 1.0, 0.0})
    @DisplayName("isHonorsEligible — false for GPA < 3.5")
    void isNotHonorsEligible(double gpa) {
        assertFalse(gpaCalc.isHonorsEligible(gpa));
    }

    @Test
    @DisplayName("calculateGpaPoints — mixed grades")
    void calculateGpaPoints() {
        List<Integer> grades = List.of(95, 85, 75, 65, 55); // 4.0, 3.0, 2.0, 1.0, 0.0
        double result = gpaCalc.calculateGpaPoints(grades);
        assertEquals(2.0, result, 0.01);
    }

    @Test
    @DisplayName("calculateGpaPoints — all A grades")
    void allAGrades() {
        List<Integer> grades = List.of(91, 95, 99, 100);
        assertEquals(4.0, gpaCalc.calculateGpaPoints(grades), 0.01);
    }

    @Test
    @DisplayName("calculateGpaPoints — empty list returns 0.0")
    void emptyGrades() {
        assertEquals(0.0, gpaCalc.calculateGpaPoints(List.of()), 0.001);
    }
}
