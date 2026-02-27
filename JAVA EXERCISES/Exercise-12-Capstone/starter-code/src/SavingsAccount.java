/**
 * Exercise 12 — Capstone
 * SavingsAccount extends Account
 *
 * TODO 1: Add field: private double interestRate (e.g. 0.03 = 3%)
 *
 * TODO 2: Constructor(accountNumber, ownerName, initialBalance, interestRate)
 *         Call super(...). Set interestRate.
 *
 * TODO 3: withdraw(double amount) throws BankException
 *         Validate amount > 0.
 *         If amount > balance, throw InsufficientFundsException(amount - balance).
 *         Subtract from balance. Call addTransaction(WITHDRAW, amount, "Withdrawal").
 *
 * TODO 4: applyInterest()
 *         Calculate interest = balance * interestRate.
 *         Add to balance. Call addTransaction(INTEREST, interest, "Interest applied").
 *         Print "Interest applied: $X.XX → new balance: $Y.YY"
 *
 * TODO 5: getAccountType() returns "Savings"
 */
public class SavingsAccount extends Account {

    // TODO 1: interestRate field

    // TODO 2
    SavingsAccount(String accountNumber, String ownerName, double initialBalance, double interestRate) throws BankException {
        super(accountNumber, ownerName, initialBalance);
        // TODO: set interestRate
    }

    // TODO 3
    @Override public void withdraw(double amount) throws BankException {
        // TODO
    }

    // TODO 4
    public void applyInterest() {
        // TODO
    }

    // TODO 5
    @Override public String getAccountType() { return "Savings"; }
}
