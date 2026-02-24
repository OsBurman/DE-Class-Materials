public class BankAccountDemo {

    // ============================================================
    // Custom unchecked exception — extends RuntimeException
    // Unchecked because overdraft is a business-logic violation,
    // not a recoverable system condition
    // ============================================================
    static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(double amount, double balance) {
            super(String.format("Cannot withdraw $%.2f: balance is $%.2f", amount, balance));
        }
    }

    // ============================================================
    // BankAccount — domain model with input validation
    // ============================================================
    static class BankAccount {
        private final String owner;
        private double balance;

        public BankAccount(String owner, double balance) {
            this.owner = owner;
            this.balance = balance;
        }

        public String getOwner() { return owner; }
        public double getBalance() { return balance; }

        public void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive");
            }
            balance += amount;
        }

        public void withdraw(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive");
            }
            if (amount > balance) {
                // Pass both amount and current balance so the message is informative
                throw new InsufficientFundsException(amount, balance);
            }
            balance -= amount;
        }
    }

    public static void main(String[] args) {

        BankAccount account = new BankAccount("Alice", 500.00);

        // ---- Transaction 1: Valid deposit ----
        System.out.println("=== Transaction 1: Valid deposit ===");
        try {
            account.deposit(200.00);
            System.out.printf("Deposited $200.00. New balance: $%.2f%n", account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            // finally always runs — perfect for audit logging, DB connection closing, etc.
            System.out.println("[Audit] Transaction attempt completed for Alice");
        }
        System.out.println();

        // ---- Transaction 2: Invalid deposit amount ----
        System.out.println("=== Transaction 2: Invalid deposit amount ===");
        try {
            account.deposit(-50.00);
            System.out.println("Deposited $-50.00.");  // never reached
        } catch (IllegalArgumentException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            System.out.println("[Audit] Transaction attempt completed for Alice");
        }
        System.out.println();

        // ---- Transaction 3: Valid withdrawal ----
        System.out.println("=== Transaction 3: Valid withdrawal ===");
        try {
            account.withdraw(100.00);
            System.out.printf("Withdrew $100.00. New balance: $%.2f%n", account.getBalance());
        } catch (InsufficientFundsException e) {
            // InsufficientFundsException must come BEFORE IllegalArgumentException
            // because it is a more specific type (subclass of RuntimeException)
            System.out.println("Transaction failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            System.out.println("[Audit] Transaction attempt completed for Alice");
        }
        System.out.println();

        // ---- Transaction 4: Insufficient funds ----
        System.out.println("=== Transaction 4: Insufficient funds ===");
        try {
            account.withdraw(1000.00);
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            System.out.println("[Audit] Transaction attempt completed for Alice");
        }
    }
}
