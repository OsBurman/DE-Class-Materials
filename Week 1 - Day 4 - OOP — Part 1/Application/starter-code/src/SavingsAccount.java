/**
 * A savings account with a minimum balance requirement.
 * Extends BankAccount.
 *
 * TODO: Extend BankAccount and override withdraw() so the
 *       balance cannot drop below minimumBalance.
 */
public class SavingsAccount extends BankAccount {

    // TODO: Add private double minimumBalance field


    // TODO: Constructor — call super(accountHolderName, initialBalance)
    //       and set minimumBalance
    public SavingsAccount(String accountHolderName, double initialBalance, double minimumBalance) {

    }


    // TODO: Override withdraw(double amount)
    // Before withdrawing, check: (balance - amount) >= minimumBalance
    // If not, print a message and return false.
    @Override
    public boolean withdraw(double amount) {
        return false;
    }
}
