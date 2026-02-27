/**
 * Represents a bank account.
 * Complete all TODOs in the order they appear in instructions.md.
 */
public class BankAccount {

    // TODO Task 1: Declare private instance fields: accountNumber,
    // accountHolderName, balance
    // Declare private static field: totalAccounts
    // Declare public static final constant: INTEREST_RATE = 0.035

    // TODO Task 2: Default constructor
    // - Set accountHolderName to "Unknown"
    // - Set balance to 0.0
    // - Auto-generate accountNumber: "ACC-" + totalAccounts
    // - Increment totalAccounts
    public BankAccount() {

    }

    // TODO Task 3: Parameterized constructor
    // - Use constructor chaining: call this() first
    // - Then set accountHolderName and balance to the provided values
    public BankAccount(String accountHolderName, double initialBalance) {

    }

    // TODO Task 4: Getters and setters
    public String getBalance() {
        return "";
    } // fix return type to double

    public String getAccountNumber() {
        return "";
    }

    public String getAccountHolderName() {
        return "";
    }
    // setAccountHolderName: validate name is not null or empty before setting

    // TODO Task 5: deposit(double amount) — validate > 0, then add to balance
    public void deposit(double amount) {

    }

    // TODO Task 6: withdraw(double amount) — validate > 0 AND <= balance
    // Return true if successful, false if insufficient funds
    public boolean withdraw(double amount) {
        return false;
    }

    // TODO Task 7: applyInterest() — add balance * INTEREST_RATE to balance
    public void applyInterest() {

    }

    // TODO Task 8: getTotalAccounts() — static method returning totalAccounts
    public static int getTotalAccounts() {
        return 0;
    }

    // TODO Task 9: Override toString() — return a formatted summary
    // Use String.format() for clean output
    @Override
    public String toString() {
        return "";
    }
}
