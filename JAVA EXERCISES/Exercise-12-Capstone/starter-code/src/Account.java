import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 12 — Capstone
 * Account — abstract base class
 *
 * TODO 1: Add protected fields: accountNumber (String), ownerName (String),
 * balance (double),
 * transactions (List<Transaction>) — initialize as new ArrayList<>()
 *
 * TODO 2: Constructor(String accountNumber, String ownerName, double
 * initialBalance)
 * Validate initialBalance >= 0, throw BankException.InvalidAmountException if
 * not.
 * Set fields. Call addTransaction with type DEPOSIT, amount=initialBalance.
 *
 * TODO 3: deposit(double amount) throws BankException
 * Validate amount > 0 (throw InvalidAmountException). Add to balance.
 * Call addTransaction(DEPOSIT, amount, "Deposit").
 *
 * TODO 4: abstract void withdraw(double amount) throws BankException;
 *
 * TODO 5: abstract String getAccountType();
 *
 * TODO 6: protected void addTransaction(Transaction.Type type, double amount,
 * String desc)
 * Add new Transaction to transactions list.
 *
 * TODO 7: Getters: getAccountNumber(), getOwnerName(), getBalance(),
 * getTransactions()
 *
 * TODO 8: toString() — "ACC-XXX OwnerName Type $balance"
 */
public abstract class Account {

    // TODO 1: fields

    // TODO 2: constructor
    Account(String accountNumber, String ownerName, double initialBalance) throws BankException {
        // TODO
    }

    // TODO 3
    public void deposit(double amount) throws BankException {
        /* TODO */ }

    // TODO 4
    public abstract void withdraw(double amount) throws BankException;

    // TODO 5
    public abstract String getAccountType();

    // TODO 6
    protected void addTransaction(Transaction.Type type, double amount, String desc) {
        /* TODO */ }

    // TODO 7: getters
    public String getAccountNumber() {
        return "";
    }

    public String getOwnerName() {
        return "";
    }

    public double getBalance() {
        return 0;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>();
    }

    // TODO 8
    @Override
    public String toString() {
        return "Account";
    }
}
