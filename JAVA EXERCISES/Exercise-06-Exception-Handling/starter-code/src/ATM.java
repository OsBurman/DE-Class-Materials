import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Exercise 06 — Exception Handling  (STARTER)
 * Simulates an ATM that processes transactions with full error handling.
 */
public class ATM {

    private Map<String, BankAccount> accounts = new HashMap<>();

    public void addAccount(BankAccount account) {
        accounts.put(account.getId(), account);
    }

    // Helper to look up an account — throws AccountNotFoundException if missing
    private BankAccount getAccount(String id) throws AccountNotFoundException {
        BankAccount acc = accounts.get(id);
        if (acc == null) throw new AccountNotFoundException(id);
        return acc;
    }

    // TODO 8: Implement processWithdrawal(String accountId, double amount)
    //   Use try/catch/finally:
    //     - Look up account (may throw AccountNotFoundException)
    //     - Call account.withdraw(amount)
    //     - Catch each specific exception and print a user-friendly message
    //     - finally: print "Transaction processed." regardless of outcome
    public void processWithdrawal(String accountId, double amount) {
        // your code here
    }

    // TODO 9: Implement processTransfer(String fromId, String toId, double amount)
    //   Use try-with-resources with TransactionLogger to log the transaction.
    //   The TransactionLogger writes to "transaction_log.txt".
    //   Catch all checked exceptions, log the error, print user-friendly message.
    public void processTransfer(String fromId, String toId, double amount) {
        // your code here (use: try (TransactionLogger logger = new TransactionLogger("transaction_log.txt")) { ... })
    }

    public void printAllAccounts() {
        System.out.println("=== All Accounts ===");
        accounts.values().forEach(System.out::println);
    }
}

/**
 * A simple AutoCloseable resource for logging — demonstrates try-with-resources.
 */
class TransactionLogger implements AutoCloseable {
    private PrintWriter writer;
    private String filename;

    public TransactionLogger(String filename) throws IOException {
        this.filename = filename;
        this.writer   = new PrintWriter(new FileWriter(filename, true));
        System.out.println("[Logger] Opened log: " + filename);
    }

    public void log(String message) {
        writer.println("[" + java.time.LocalDateTime.now() + "] " + message);
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
        System.out.println("[Logger] Closed log: " + filename);
    }
}
