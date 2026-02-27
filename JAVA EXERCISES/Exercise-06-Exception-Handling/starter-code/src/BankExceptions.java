/**
 * TODO 2: InvalidAmountException — unchecked (extends RuntimeException)
 * Message: "Amount must be positive. Received: $X.XX"
 */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(double amount) {
        super("Amount must be positive. Received: $" + String.format("%.2f", amount));
    }
}

/**
 * TODO 3: AccountNotFoundException — checked (extends Exception)
 * Message: "Account not found: <id>"
 */
class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String id) {
        super("Account not found: " + id);
    }
}

/**
 * TODO 4: DailyLimitExceededException — checked (extends Exception)
 * Message: "Daily withdrawal limit of $X.XX exceeded."
 */
class DailyLimitExceededException extends Exception {
    public DailyLimitExceededException(double limit) {
        super("Daily withdrawal limit of $" + String.format("%.2f", limit) + " exceeded.");
    }
}
