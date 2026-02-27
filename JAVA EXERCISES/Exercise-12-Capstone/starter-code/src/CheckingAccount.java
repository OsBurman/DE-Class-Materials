/**
 * Exercise 12 — Capstone
 * CheckingAccount extends Account
 *
 * TODO 1: Add field: private double overdraftLimit (default 500.0)
 *
 * TODO 2: Constructor(accountNumber, ownerName, initialBalance, overdraftLimit)
 * Call super. Set overdraftLimit.
 *
 * TODO 3: withdraw(double amount) throws BankException
 * Validate amount > 0.
 * If amount > balance + overdraftLimit, throw InsufficientFundsException.
 * Subtract from balance (may go negative). Add WITHDRAW transaction.
 *
 * TODO 4: getAccountType() returns "Checking"
 *
 * TODO 5: getOverdraftLimit() getter
 */
public class CheckingAccount extends Account {

    // TODO 1

    // TODO 2
    CheckingAccount(String accountNumber, String ownerName, double initialBalance, double overdraftLimit)
            throws BankException {
        super(accountNumber, ownerName, initialBalance);
        // TODO: set overdraftLimit
    }

    // TODO 3
    @Override
    public void withdraw(double amount) throws BankException {
        // TODO
    }

    // TODO 4
    @Override
    public String getAccountType() {
        return "Checking";
    }

    // TODO 5
    public double getOverdraftLimit() {
        return 0;
        /* TODO */ }
}
