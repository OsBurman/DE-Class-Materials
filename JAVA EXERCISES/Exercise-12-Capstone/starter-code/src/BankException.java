/**
 * Exercise 12 — Capstone: Bank Management System
 * Custom Exceptions — complete (provided for you)
 */
public class BankException extends Exception {
    public BankException(String msg) { super(msg); }

    public static class InsufficientFundsException extends BankException {
        private final double shortfall;
        public InsufficientFundsException(double shortfall) {
            super(String.format("Insufficient funds. Shortfall: $%.2f", shortfall));
            this.shortfall = shortfall;
        }
        public double getShortfall() { return shortfall; }
    }

    public static class AccountNotFoundException extends BankException {
        public AccountNotFoundException(String accountNumber) {
            super("Account not found: " + accountNumber);
        }
    }

    public static class InvalidAmountException extends BankException {
        public InvalidAmountException(String msg) { super(msg); }
    }
}
