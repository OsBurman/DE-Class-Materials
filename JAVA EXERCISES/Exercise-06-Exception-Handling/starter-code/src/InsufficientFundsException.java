/**
 * Exercise 06 — Exception Handling (STARTER)
 *
 * TODO 1: Create InsufficientFundsException
 * - extends Exception (checked)
 * - Constructor: InsufficientFundsException(double shortfall)
 * - Store shortfall as a field; include it in getMessage()
 * e.g. "Insufficient funds. Short by $50.00"
 */
public class InsufficientFundsException extends Exception {
    // your code here
    public InsufficientFundsException(double shortfall) {
        super("Insufficient funds. Short by $" + String.format("%.2f", shortfall));
    }
}
