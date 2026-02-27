package com.academy;

/**
 * Day 4 Part 1 — Classes, Constructors, Fields, Methods
 *
 * Theme: Bank Account System
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  Day 4 Part 1 — Classes & Constructors    ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // Create accounts using different constructors
        BankAccount checking = new BankAccount("Alice Johnson", "CHK-001");
        BankAccount savings  = new BankAccount("Bob Smith",     "SAV-002", 500.00);
        BankAccount premium  = new BankAccount("Carol White",   "PRE-003", 2000.00, "PREMIUM");

        System.out.println("=== Account Creation ===");
        checking.printInfo();
        savings.printInfo();
        premium.printInfo();

        System.out.println("\n=== Transactions ===");
        checking.deposit(1000.00);
        checking.deposit(250.50);
        checking.withdraw(100.00);
        checking.withdraw(5000.00);   // should fail - insufficient funds

        System.out.println("\n=== Final Balances ===");
        System.out.printf("  %s: $%.2f%n", checking.getOwner(), checking.getBalance());
        System.out.printf("  %s: $%.2f%n", savings.getOwner(),  savings.getBalance());
        System.out.printf("  %s: $%.2f%n", premium.getOwner(),  premium.getBalance());

        System.out.println("\n=== Static Counter ===");
        System.out.println("  Total accounts created: " + BankAccount.getAccountCount());
    }
}

/**
 * BankAccount demonstrates:
 * - Multiple constructors (constructor overloading)
 * - Constructor chaining with 'this()'
 * - Instance fields (state)
 * - Instance methods (behavior)
 * - Static field (shared across all instances)
 * - 'this' keyword
 */
class BankAccount {

    // Instance fields — each account has its own copy
    private String owner;
    private String accountNumber;
    private double balance;
    private String accountType;

    // Static field — shared across ALL BankAccount instances
    private static int accountCount = 0;

    // ── Constructors ──────────────────────────────────────────

    // Default constructor — no initial balance
    public BankAccount(String owner, String accountNumber) {
        this(owner, accountNumber, 0.0, "STANDARD"); // constructor chaining
    }

    // Parameterized constructor — with initial balance
    public BankAccount(String owner, String accountNumber, double initialBalance) {
        this(owner, accountNumber, initialBalance, "STANDARD");
    }

    // Full constructor — all fields
    public BankAccount(String owner, String accountNumber, double initialBalance, String accountType) {
        // 'this.' distinguishes fields from parameters when names match
        this.owner         = owner;
        this.accountNumber = accountNumber;
        this.balance       = initialBalance;
        this.accountType   = accountType;
        accountCount++;   // increment the static counter
        System.out.println("  ✓ Account created: " + accountNumber + " for " + owner);
    }

    // ── Instance Methods ───────────────────────────────────────

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("  ✗ Deposit failed: amount must be positive");
            return;
        }
        balance += amount;
        System.out.printf("  + Deposited $%.2f → Balance: $%.2f%n", amount, balance);
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("  ✗ Withdrawal failed: amount must be positive");
            return false;
        }
        if (amount > balance) {
            System.out.printf("  ✗ Withdrawal of $%.2f failed: insufficient funds (balance: $%.2f)%n",
                    amount, balance);
            return false;
        }
        balance -= amount;
        System.out.printf("  - Withdrew  $%.2f → Balance: $%.2f%n", amount, balance);
        return true;
    }

    public void printInfo() {
        System.out.printf("  [%s] %s (%s) — Balance: $%.2f%n",
                accountNumber, owner, accountType, balance);
    }

    // ── Getters ────────────────────────────────────────────────

    public String getOwner()         { return owner; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance()       { return balance; }
    public String getAccountType()   { return accountType; }

    // Static method — belongs to the class, not an instance
    public static int getAccountCount() { return accountCount; }
}
