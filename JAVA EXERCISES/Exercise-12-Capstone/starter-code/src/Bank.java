import java.util.*;

/**
 * Exercise 12 — Capstone
 * Bank — Singleton that manages all accounts
 *
 * TODO 1: Make Bank a Singleton (private constructor, private static instance,
 * getInstance()).
 *
 * TODO 2: Add field: private Map<String, Account> accounts = new HashMap<>()
 * Add field: private int nextId = 1 (for generating account numbers)
 *
 * TODO 3: createAccount(String ownerName, String type, double initialDeposit)
 * Generate account number: type.equals("savings") ? "SAV-" : "CHK-" +
 * String.format("%03d", nextId++)
 * Create SavingsAccount (rate=0.03) or CheckingAccount (overdraft=500) based on
 * type.
 * Put in accounts map. Return the new account.
 * Throw BankException.InvalidAmountException for unknown type.
 *
 * TODO 4: getAccount(String accountNumber)
 * Look up in map. If not found, throw BankException.AccountNotFoundException.
 *
 * TODO 5: transfer(String fromAcc, String toAcc, double amount)
 * getAccount both. Call from.withdraw(amount) and to.deposit(amount).
 * Add a TRANSFER transaction to both accounts.
 *
 * TODO 6: getAllAccounts() returns Collection<Account>
 *
 * TODO 7: getAllTransactions() returns flat List<Transaction> from all accounts
 * combined.
 * Hint: use streams — accounts.values().stream().flatMap(a ->
 * a.getTransactions().stream())
 */
public class Bank {

    // TODO 1: singleton fields & getInstance()

    // TODO 2: accounts map, nextId

    // TODO 3
    public Account createAccount(String ownerName, String type, double initialDeposit) throws BankException {
        // TODO
        return null;
    }

    // TODO 4
    public Account getAccount(String accountNumber) throws BankException {
        // TODO
        throw new BankException.AccountNotFoundException(accountNumber);
    }

    // TODO 5
    public void transfer(String fromAcc, String toAcc, double amount) throws BankException {
        // TODO
    }

    // TODO 6
    public Collection<Account> getAllAccounts() {
        return new ArrayList<>();
        /* TODO */ }

    // TODO 7
    public java.util.List<Transaction> getAllTransactions() {
        return new ArrayList<>();
        /* TODO */ }
}
