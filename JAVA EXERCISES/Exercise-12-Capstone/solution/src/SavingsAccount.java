/** SavingsAccount — Solution */
public class SavingsAccount extends Account {
    private final double interestRate;

    SavingsAccount(String accountNumber, String ownerName, double initialBalance, double interestRate) throws BankException {
        super(accountNumber, ownerName, initialBalance);
        this.interestRate = interestRate;
    }

    @Override public void withdraw(double amount) throws BankException {
        if (amount <= 0) throw new BankException.InvalidAmountException("Withdrawal amount must be positive");
        if (amount > balance) throw new BankException.InsufficientFundsException(amount - balance);
        balance -= amount;
        addTransaction(Transaction.Type.WITHDRAW, amount, "Withdrawal");
    }

    public void applyInterest() {
        double interest = balance * interestRate;
        balance += interest;
        addTransaction(Transaction.Type.INTEREST, interest, "Interest applied");
        System.out.printf("Interest applied: $%,.2f → new balance: $%,.2f%n", interest, balance);
    }

    @Override public String getAccountType() { return "Savings"; }
}
