import java.util.ArrayList;
import java.util.List;

/** Account — Solution */
public abstract class Account {
    protected final String accountNumber;
    protected final String ownerName;
    protected double balance;
    protected final List<Transaction> transactions = new ArrayList<>();

    Account(String accountNumber, String ownerName, double initialBalance) throws BankException {
        if (initialBalance < 0) throw new BankException.InvalidAmountException("Initial balance cannot be negative");
        this.accountNumber = accountNumber;
        this.ownerName     = ownerName;
        this.balance       = initialBalance;
        addTransaction(Transaction.Type.DEPOSIT, initialBalance, "Initial deposit");
    }

    public void deposit(double amount) throws BankException {
        if (amount <= 0) throw new BankException.InvalidAmountException("Deposit amount must be positive");
        balance += amount;
        addTransaction(Transaction.Type.DEPOSIT, amount, "Deposit");
    }

    public abstract void withdraw(double amount) throws BankException;
    public abstract String getAccountType();

    protected void addTransaction(Transaction.Type type, double amount, String desc) {
        transactions.add(new Transaction(accountNumber, type, amount, desc));
    }

    public String            getAccountNumber() { return accountNumber; }
    public String            getOwnerName()     { return ownerName; }
    public double            getBalance()       { return balance; }
    public List<Transaction> getTransactions()  { return transactions; }

    @Override public String toString() {
        return String.format("%-8s %-12s %-10s $%,10.2f", accountNumber, ownerName, getAccountType(), balance);
    }
}
