/**
 * Exercise 12 — Capstone
 * Transaction — immutable record of one bank transaction (provided for you)
 */
public class Transaction {
    public enum Type {
        DEPOSIT, WITHDRAW, TRANSFER, INTEREST
    }

    private final String accountNumber;
    private final Type type;
    private final double amount;
    private final String description;
    private final long timestamp;

    public Transaction(String accountNumber, Type type, double amount, String description) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s  $%,.2f  (%s)", accountNumber, type, amount, description);
    }
}
