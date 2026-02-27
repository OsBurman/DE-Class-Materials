import java.util.*;
import java.util.stream.*;

/** Bank — Singleton Solution */
public class Bank {
    private static volatile Bank instance;
    private final Map<String, Account> accounts = new HashMap<>();
    private int nextId = 1;

    private Bank() {}

    public static Bank getInstance() {
        if (instance == null) {
            synchronized (Bank.class) {
                if (instance == null) instance = new Bank();
            }
        }
        return instance;
    }

    public Account createAccount(String ownerName, String type, double initialDeposit) throws BankException {
        String prefix = type.equalsIgnoreCase("savings") ? "SAV" : "CHK";
        String accNum = prefix + "-" + String.format("%03d", nextId++);
        Account acc = switch (type.toLowerCase()) {
            case "savings"  -> new SavingsAccount(accNum, ownerName, initialDeposit, 0.03);
            case "checking" -> new CheckingAccount(accNum, ownerName, initialDeposit, 500.0);
            default         -> throw new BankException.InvalidAmountException("Unknown account type: " + type);
        };
        accounts.put(accNum, acc);
        return acc;
    }

    public Account getAccount(String accountNumber) throws BankException {
        Account acc = accounts.get(accountNumber);
        if (acc == null) throw new BankException.AccountNotFoundException(accountNumber);
        return acc;
    }

    public void transfer(String fromAcc, String toAcc, double amount) throws BankException {
        Account from = getAccount(fromAcc);
        Account to   = getAccount(toAcc);
        from.withdraw(amount);
        to.deposit(amount);
        from.addTransaction(Transaction.Type.TRANSFER, amount, "Transfer to " + toAcc);
        to.addTransaction(Transaction.Type.TRANSFER, amount, "Transfer from " + fromAcc);
    }

    public Collection<Account> getAllAccounts() { return accounts.values(); }

    public List<Transaction> getAllTransactions() {
        return accounts.values().stream()
            .flatMap(a -> a.getTransactions().stream())
            .collect(Collectors.toList());
    }
}
