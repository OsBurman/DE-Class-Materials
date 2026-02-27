/**
 * Exercise 06 — Exception Handling (STARTER)
 * BankAccount with validated deposits and withdrawals.
 */
public class BankAccount {

    private String id;
    private String owner;
    private double balance;
    private double dailyWithdrawLimit;
    private double withdrawnToday;

    public BankAccount(String id, String owner, double initialBalance, double dailyLimit) {
        this.id = id;
        this.owner = owner;
        this.balance = initialBalance;
        this.dailyWithdrawLimit = dailyLimit;
        this.withdrawnToday = 0.0;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public double getWithdrawnToday() {
        return withdrawnToday;
    }

    // TODO 5: Implement deposit(double amount)
    // - Throw InvalidAmountException (unchecked) if amount <= 0
    // - Otherwise add amount to balance and print confirmation
    public void deposit(double amount) {
        // your code here
    }

    // TODO 6: Implement withdraw(double amount)
    // Throws: InvalidAmountException (unchecked), DailyLimitExceededException
    // (checked),
    // InsufficientFundsException (checked)
    // Checks (in order):
    // 1. amount <= 0 → InvalidAmountException
    // 2. withdrawnToday + amount > dailyLimit →
    // DailyLimitExceededException(dailyLimit)
    // 3. amount > balance → InsufficientFundsException(amount - balance)
    // If all checks pass: deduct amount, add to withdrawnToday, print confirmation.
    public void withdraw(double amount)
            throws DailyLimitExceededException, InsufficientFundsException {
        // your code here
    }

    // TODO 7: Implement transfer(BankAccount target, double amount)
    // Call this.withdraw(amount) — if it succeeds, call target.deposit(amount)
    // Declare throws for all checked exceptions from withdraw.
    public void transfer(BankAccount target, double amount)
            throws DailyLimitExceededException, InsufficientFundsException {
        // your code here
    }

    public void resetDailyLimit() {
        withdrawnToday = 0.0;
    }

    @Override
    public String toString() {
        return String.format("Account[%s] %s  balance=$%.2f", id, owner, balance);
    }
}
