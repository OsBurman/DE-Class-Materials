public class BankAccount {
    private String id, owner;
    private double balance, dailyWithdrawLimit, withdrawnToday;

    public BankAccount(String id, String owner, double balance, double dailyLimit) {
        this.id = id; this.owner = owner; this.balance = balance;
        this.dailyWithdrawLimit = dailyLimit; this.withdrawnToday = 0;
    }

    public String getId()      { return id;      }
    public String getOwner()   { return owner;   }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount <= 0) throw new InvalidAmountException(amount);
        balance += amount;
        System.out.printf("✓ Deposited $%.2f to %s. New balance: $%.2f%n", amount, owner, balance);
    }

    public void withdraw(double amount)
            throws DailyLimitExceededException, InsufficientFundsException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (withdrawnToday + amount > dailyWithdrawLimit) throw new DailyLimitExceededException(dailyWithdrawLimit);
        if (amount > balance) throw new InsufficientFundsException(amount - balance);
        balance       -= amount;
        withdrawnToday += amount;
        System.out.printf("✓ Withdrew $%.2f from %s. New balance: $%.2f%n", amount, owner, balance);
    }

    public void transfer(BankAccount target, double amount)
            throws DailyLimitExceededException, InsufficientFundsException {
        this.withdraw(amount);
        target.deposit(amount);
        System.out.printf("✓ Transferred $%.2f from %s to %s%n", amount, owner, target.owner);
    }

    public void resetDailyLimit() { withdrawnToday = 0; }

    @Override public String toString() {
        return String.format("Account[%s] %-8s  balance=$%.2f", id, owner, balance);
    }
}
