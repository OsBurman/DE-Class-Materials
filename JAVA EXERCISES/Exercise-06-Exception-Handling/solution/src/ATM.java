import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ATM {
    private Map<String, BankAccount> accounts = new HashMap<>();

    public void addAccount(BankAccount a) { accounts.put(a.getId(), a); }

    private BankAccount getAccount(String id) throws AccountNotFoundException {
        BankAccount acc = accounts.get(id);
        if (acc == null) throw new AccountNotFoundException(id);
        return acc;
    }

    public void processWithdrawal(String accountId, double amount) {
        try {
            BankAccount acc = getAccount(accountId);
            acc.withdraw(amount);
        } catch (AccountNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (DailyLimitExceededException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("❌ " + e.getMessage());
        } finally {
            System.out.println("   [Transaction processed]");
        }
    }

    public void processTransfer(String fromId, String toId, double amount) {
        try (TransactionLogger logger = new TransactionLogger("transaction_log.txt")) {
            BankAccount from = getAccount(fromId);
            BankAccount to   = getAccount(toId);
            from.transfer(to, amount);
            logger.log(String.format("TRANSFER $%.2f from %s to %s — SUCCESS", amount, fromId, toId));
        } catch (AccountNotFoundException | InsufficientFundsException | DailyLimitExceededException e) {
            System.out.println("❌ Transfer failed: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("❌ Logger error: " + e.getMessage());
        }
    }

    public void printAllAccounts() {
        System.out.println("=== All Accounts ===");
        accounts.values().forEach(System.out::println);
    }
}

class TransactionLogger implements AutoCloseable {
    private PrintWriter writer;
    private String filename;

    public TransactionLogger(String filename) throws IOException {
        this.filename = filename;
        this.writer   = new PrintWriter(new FileWriter(filename, true));
        System.out.println("[Logger] Opened: " + filename);
    }

    public void log(String message) {
        writer.println("[" + java.time.LocalDateTime.now() + "] " + message);
        writer.flush();
    }

    @Override public void close() {
        writer.close();
        System.out.println("[Logger] Closed: " + filename);
    }
}
