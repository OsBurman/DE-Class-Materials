public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(double amount) {
        super("Amount must be positive. Received: $" + String.format("%.2f", amount));
    }
}

class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String id) { super("Account not found: " + id); }
}

class DailyLimitExceededException extends Exception {
    public DailyLimitExceededException(double limit) {
        super("Daily withdrawal limit of $" + String.format("%.2f", limit) + " exceeded.");
    }
}
