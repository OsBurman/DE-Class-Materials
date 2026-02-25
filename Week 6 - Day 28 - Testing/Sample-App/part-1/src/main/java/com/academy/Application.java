package com.academy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Day 28 — Part 1: JUnit 5 & Test Driven Development (TDD)
 * ===========================================================
 * Run: mvn spring-boot:run
 * Run tests: mvn test
 *
 * This app provides classes to test, demonstrating TDD workflow:
 *   1. RED   — write a failing test
 *   2. GREEN — write minimal code to pass the test
 *   3. REFACTOR — clean up the code
 *
 * Classes under test (in this file):
 *   - Calculator     (arithmetic + factorial + isPrime)
 *   - GpaCalculator  (letterGrade + honorsEligible)
 *   - StringUtils    (palindrome + countVowels + reverseWords)
 *   - BankAccount    (deposit + withdraw + transfer)
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// ── Custom Exception ────────────────────────────────────────────────────────
class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// ── Calculator ───────────────────────────────────────────────────────────────
class Calculator {

    public double add(double a, double b) { return a + b; }

    public double subtract(double a, double b) { return a - b; }

    public double multiply(double a, double b) { return a * b; }

    public double divide(double a, double b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return a / b;
    }

    public long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Factorial undefined for negative numbers");
        if (n == 0 || n == 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }

    public boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // TDD Example: write the test first, then implement
    public double squareRoot(double n) {
        throw new UnsupportedOperationException("Not yet implemented — write test first!");
    }
}

// ── GpaCalculator ─────────────────────────────────────────────────────────────
class GpaCalculator {

    public String calculateLetterGrade(double gpa) {
        if (gpa >= 3.5) return "A";
        if (gpa >= 3.0) return "B";
        if (gpa >= 2.0) return "C";
        if (gpa >= 1.0) return "D";
        return "F";
    }

    public boolean isHonorsEligible(double gpa) {
        return gpa >= 3.5;
    }

    public double calculateGpaPoints(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) return 0.0;
        return scores.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0) / 25.0; // normalize 0-100 to 0-4.0
    }
}

// ── StringUtils ───────────────────────────────────────────────────────────────
class StringUtils {

    public boolean isPalindrome(String s) {
        if (s == null) return false;
        String clean = s.toLowerCase().replaceAll("[^a-z0-9]", "");
        String reversed = new StringBuilder(clean).reverse().toString();
        return clean.equals(reversed);
    }

    public int countVowels(String s) {
        if (s == null) return 0;
        return (int) s.toLowerCase().chars()
            .filter(c -> "aeiou".indexOf(c) >= 0)
            .count();
    }

    public String reverseWords(String sentence) {
        if (sentence == null || sentence.isBlank()) return sentence;
        String[] words = sentence.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]);
            if (i > 0) sb.append(" ");
        }
        return sb.toString();
    }
}

// ── BankAccount ───────────────────────────────────────────────────────────────
class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        if (initialBalance < 0) throw new IllegalArgumentException("Initial balance cannot be negative");
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");
        if (amount > balance) throw new InsufficientFundsException(
            "Insufficient funds: balance=" + balance + ", requested=" + amount);
        balance -= amount;
    }

    public double getBalance() { return balance; }

    public void transfer(BankAccount other, double amount) {
        this.withdraw(amount);
        other.deposit(amount);
    }
}

// ── REST Reference Controller ─────────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class TestingGuideController {

    @GetMapping("/testing-guide")
    public Map<String, Object> testingGuide() {
        Map<String, Object> guide = new LinkedHashMap<>();

        guide.put("topic", "JUnit 5 & Test Driven Development");
        guide.put("runTests", "mvn test");
        guide.put("runApp",   "mvn spring-boot:run");

        // TDD Cycle
        Map<String, String> tdd = new LinkedHashMap<>();
        tdd.put("step1_RED",      "Write a failing test for the feature you want to build");
        tdd.put("step2_GREEN",    "Write the MINIMUM code needed to make the test pass");
        tdd.put("step3_REFACTOR", "Clean up the code without breaking the tests");
        tdd.put("benefit",        "Forces you to think about requirements before implementation");
        guide.put("tddCycle", tdd);

        // JUnit 5 Architecture
        Map<String, String> arch = new LinkedHashMap<>();
        arch.put("Jupiter",  "New programming model — @Test, @ParameterizedTest, @ExtendWith");
        arch.put("Platform", "Foundation for launching test frameworks on the JVM");
        arch.put("Vintage",  "Backward compatibility with JUnit 3 and 4");
        guide.put("junit5Architecture", arch);

        // Lifecycle Annotations
        Map<String, String> lifecycle = new LinkedHashMap<>();
        lifecycle.put("@BeforeAll",  "Static method — runs ONCE before any test in the class");
        lifecycle.put("@AfterAll",   "Static method — runs ONCE after all tests complete");
        lifecycle.put("@BeforeEach", "Runs before EACH test method — good for setup");
        lifecycle.put("@AfterEach",  "Runs after EACH test method — good for cleanup");
        guide.put("lifecycleAnnotations", lifecycle);

        // Test Annotations
        Map<String, String> annotations = new LinkedHashMap<>();
        annotations.put("@Test",                "Marks a method as a test case");
        annotations.put("@DisplayName",         "Human-readable name shown in test report");
        annotations.put("@Disabled",            "Skips the test (use sparingly)");
        annotations.put("@Nested",              "Group related tests inside an inner class");
        annotations.put("@ParameterizedTest",   "Run same test with multiple inputs");
        annotations.put("@ValueSource",         "Supply single-value parameters");
        annotations.put("@CsvSource",           "Supply comma-separated multiple parameters");
        annotations.put("@MethodSource",        "Use a factory method to supply parameters");
        guide.put("testAnnotations", annotations);

        // Assertions
        Map<String, String> assertions = new LinkedHashMap<>();
        assertions.put("assertEquals(expected, actual)",   "Values are equal");
        assertions.put("assertNotEquals(a, b)",            "Values are NOT equal");
        assertions.put("assertTrue(condition)",            "Condition is true");
        assertions.put("assertFalse(condition)",           "Condition is false");
        assertions.put("assertNull(object)",               "Object is null");
        assertions.put("assertNotNull(object)",            "Object is not null");
        assertions.put("assertThrows(ExType.class, code)", "Code throws the expected exception");
        assertions.put("assertAll(executables...)",        "Run multiple assertions, report all failures");
        assertions.put("assertTimeout(duration, code)",    "Code completes within time limit");
        guide.put("assertions", assertions);

        // Arrange-Act-Assert
        Map<String, String> aaa = new LinkedHashMap<>();
        aaa.put("ARRANGE", "Set up test data and preconditions");
        aaa.put("ACT",     "Execute the method or operation being tested");
        aaa.put("ASSERT",  "Verify the expected outcome");
        aaa.put("tip",     "Keep each test focused on ONE behaviour");
        guide.put("arrangeActAssert", aaa);

        // Test Coverage
        Map<String, String> coverage = new LinkedHashMap<>();
        coverage.put("lineCoverage",   "% of code lines executed by tests");
        coverage.put("branchCoverage", "% of conditional branches (if/else) exercised");
        coverage.put("target",         ">= 85% line coverage for production code");
        coverage.put("tool",           "JaCoCo plugin with Maven: mvn test jacoco:report");
        guide.put("coverage", coverage);

        return guide;
    }
}
