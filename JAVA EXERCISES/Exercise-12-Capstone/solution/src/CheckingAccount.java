/** CheckingAccount — Solution */
public class CheckingAccount extends Account {
    private final double overdraftLimit;

    CheckingAccount(String accountNumber, String ownerName, double initialBalance, double overdraftLimit) throws BankException {
        super(accountNumber, ownerName, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override public void withdraw(double amount) throws BankException {
        if (amount <= 0) throw new BankException.InvalidAmountException("Withdrawal amount must be positive");
        if (amount > balance + overdraftLimit)
            throw new BankException.InsufficientFundsException(amount - balance - overdraftLimit);
        balance -= amount;
        addTransaction(Transaction.Type.WITHDRAW, amount, "Withdrawal");
    }

    @Override public String getAccountType() { return "Checking"; }
    public double getOverdraftLimit() { return overdraftLimit; }
}
