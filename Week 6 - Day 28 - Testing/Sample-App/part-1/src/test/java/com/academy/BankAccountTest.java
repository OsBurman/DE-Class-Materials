package com.academy;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 28 — Part 1: BankAccount Tests — Arrange-Act-Assert Pattern
 * ================================================================
 * Every test follows the AAA pattern:
 *   ARRANGE — set up data and preconditions
 *   ACT     — call the method being tested
 *   ASSERT  — verify the outcome
 */
@DisplayName("BankAccount — Arrange-Act-Assert Pattern")
class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setup() {
        // ARRANGE (shared): fresh account with $100 before each test
        account = new BankAccount(100.0);
    }

    @Test
    @DisplayName("initial balance is correct")
    void initialBalance() {
        // ACT
        double balance = account.getBalance();
        // ASSERT
        assertEquals(100.0, balance, 0.001);
    }

    @Test
    @DisplayName("deposit increases balance")
    void deposit() {
        // ARRANGE
        double depositAmount = 50.0;

        // ACT
        account.deposit(depositAmount);

        // ASSERT
        assertEquals(150.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("withdraw decreases balance")
    void withdraw() {
        // ARRANGE
        double withdrawAmount = 30.0;

        // ACT
        account.withdraw(withdrawAmount);

        // ASSERT
        assertEquals(70.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("withdraw throws InsufficientFundsException when amount > balance")
    void withdrawInsufficientFunds() {
        // ARRANGE
        double excessiveAmount = 200.0;

        // ACT + ASSERT (assertThrows combines these)
        InsufficientFundsException ex = assertThrows(
            InsufficientFundsException.class,
            () -> account.withdraw(excessiveAmount)
        );
        assertTrue(ex.getMessage().contains("Insufficient"),
            "Exception message should contain 'Insufficient'");
    }

    @Test
    @DisplayName("assertAll — multiple assertions at once")
    void assertAllExample() {
        // ARRANGE + ACT
        account.deposit(25.0);
        account.withdraw(10.0);

        // ASSERT — assertAll runs ALL assertions and reports all failures together
        assertAll("account state after transactions",
            () -> assertEquals(115.0, account.getBalance(), 0.001),
            () -> assertNotNull(account),
            () -> assertTrue(account.getBalance() > 0)
        );
    }

    @Test
    @DisplayName("transfer between two accounts")
    void transfer() {
        // ARRANGE
        BankAccount destination = new BankAccount(50.0);

        // ACT
        account.transfer(destination, 40.0);

        // ASSERT
        assertAll("transfer",
            () -> assertEquals(60.0,  account.getBalance(), 0.001),
            () -> assertEquals(90.0, destination.getBalance(), 0.001)
        );
    }

    @ParameterizedTest(name = "deposit {0} results in balance {1}")
    @CsvSource({"10.0, 110.0", "50.0, 150.0", "0.01, 100.01", "1000.0, 1100.0"})
    @DisplayName("parameterized deposit amounts")
    void depositParameterized(double amount, double expectedBalance) {
        account.deposit(amount);
        assertEquals(expectedBalance, account.getBalance(), 0.001);
    }
}

class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setup() {
        // Arrange (shared) — fresh account with $100 for each test
        account = new BankAccount(100.0);
    }

    @Test
    @DisplayName("initial balance is correct")
    void initialBalance() {
        // Arrange — done in @BeforeEach
        // Act — (no action needed — testing initial state)
        // Assert
        assertEquals(100.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("deposit increases balance")
    void deposit() {
        // Arrange
        double initialBalance = account.getBalance();
        double depositAmount  = 50.0;

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(initialBalance + depositAmount, account.getBalance(), 0.001);
        assertEquals(150.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("withdraw decreases balance")
    void withdraw() {
        // Arrange
        double withdrawAmount = 30.0;

        // Act
        account.withdraw(withdrawAmount);

        // Assert
        assertEquals(70.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("withdraw throws InsufficientFundsException")
    void withdrawInsufficientFunds() {
        // Arrange
        double excessiveAmount = 200.0;

        // Act & Assert — assertThrows combines act and assert
        InsufficientFundsException ex = assertThrows(
            InsufficientFundsException.class,
            () -> account.withdraw(excessiveAmount),
            "Withdrawing more than balance should throw"
        );
        assertTrue(ex.getMessage().contains("Insufficient"),
            "Exception message should contain 'Insufficient'");

        // Verify balance unchanged after failed withdrawal
        assertEquals(100.0, account.getBalance(), 0.001,
            "Balance should not change after failed withdrawal");
    }

    @Test
    @DisplayName("assertAll — check multiple properties at once")
    void assertAllExample() {
        // Arrange & Act
        account.deposit(25.0);
        account.withdraw(10.0);

        // Assert all in one go — all failures reported together
        assertAll("account state after transactions",
            () -> assertEquals(115.0, account.getBalance(), 0.001, "balance check"),
            () -> assertNotNull(account, "account should not be null"),
            () -> assertTrue(account.getBalance() > 0, "balance should be positive")
        );
    }

    @Test
    @DisplayName("transfer between two accounts")
    void transfer() {
        // Arrange
        BankAccount savings = new BankAccount(500.0);
        double transferAmount = 50.0;

        // Act
        account.transfer(savings, transferAmount);

        // Assert
        assertEquals(50.0,  account.getBalance(), 0.001, "Source account reduced");
        assertEquals(550.0, savings.getBalance(), 0.001, "Target account increased");
    }

    @ParameterizedTest(name = "deposit {0} → balance = {1}")
    @CsvSource({"10.0, 110.0", "50.0, 150.0", "100.0, 200.0", "0.01, 100.01"})
    @DisplayName("parameterized deposits")
    void parameterizedDeposit(double amount, double expectedBalance) {
        account.deposit(amount);
        assertEquals(expectedBalance, account.getBalance(), 0.001);
    }
}
