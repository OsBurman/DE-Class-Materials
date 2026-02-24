public class BankAccountDemo {

    // ============================================================
    // TODO 1: Define InsufficientFundsException (extends RuntimeException)
    //   Constructor: InsufficientFundsException(double amount, double balance)
    //   Message: "Cannot withdraw $X.XX: balance is $Y.YY"
    //   Use String.format("$%.2f", amount) for formatting
    // ============================================================


    // ============================================================
    // TODO 2: Define BankAccount class
    //   Fields: String owner, double balance
    //   Constructor: BankAccount(String owner, double balance)
    //   deposit(double amount): throws IllegalArgumentException if amount <= 0
    //   withdraw(double amount): throws IllegalArgumentException if amount <= 0,
    //                            throws InsufficientFundsException if amount > balance
    // ============================================================


    public static void main(String[] args) {

        // TODO 3: Create BankAccount("Alice", 500.00)
        // BankAccount account = new BankAccount("Alice", 500.00);

        // ---- Transaction 1: Valid deposit ----
        System.out.println("=== Transaction 1: Valid deposit ===");
        // TODO: try { account.deposit(200.00); print success message }
        //       catch (IllegalArgumentException e) { print "Transaction failed: " + e.getMessage() }
        //       finally { print "[Audit] Transaction attempt completed for Alice" }
        System.out.println();

        // ---- Transaction 2: Invalid deposit amount ----
        System.out.println("=== Transaction 2: Invalid deposit amount ===");
        // TODO: try { account.deposit(-50.00) }
        //       catch (IllegalArgumentException e) { print "Transaction failed: " + e.getMessage() }
        //       finally { print "[Audit] Transaction attempt completed for Alice" }
        System.out.println();

        // ---- Transaction 3: Valid withdrawal ----
        System.out.println("=== Transaction 3: Valid withdrawal ===");
        // TODO: try { account.withdraw(100.00); print success message }
        //       catch (InsufficientFundsException e) { ... }
        //       catch (IllegalArgumentException e) { ... }
        //       finally { print "[Audit] Transaction attempt completed for Alice" }
        System.out.println();

        // ---- Transaction 4: Insufficient funds ----
        System.out.println("=== Transaction 4: Insufficient funds ===");
        // TODO: try { account.withdraw(1000.00) }
        //       catch (InsufficientFundsException e) { print "Transaction failed: " + e.getMessage() }
        //       catch (IllegalArgumentException e) { print "Transaction failed: " + e.getMessage() }
        //       finally { print "[Audit] Transaction attempt completed for Alice" }
    }
}
