class BankAccount {

    // TODO: Declare PUBLIC STATIC FINAL double MINIMUM_BALANCE = 0.0

    // TODO: Declare PRIVATE STATIC int nextAccountNumber = 1000

    // TODO: Write PUBLIC STATIC method  int getAccountCount()
    //       Hint: you'll need a separate static counter for the number of accounts created;
    //       OR derive it as nextAccountNumber - 1000 (since we start at 1000 and increment by 1)

    // TODO: Declare three PRIVATE instance fields:
    //       private final int accountNumber
    //       private String ownerName
    //       private double balance

    // TODO: Write constructor  BankAccount(String ownerName)
    //       Chain to the two-arg constructor: this(ownerName, 0.0)

    // TODO: Write constructor  BankAccount(String ownerName, double initialBalance)
    //       Assign: this.accountNumber = nextAccountNumber++  (assign then increment)
    //       Assign ownerName and initialBalance using 'this.'

    // TODO: Write  void deposit(double amount)
    //       If amount <= 0: print "Deposit amount must be positive." and return
    //       Otherwise: balance += amount
    //       Print "Deposited $[amount]. New balance: $[balance]"

    // TODO: Write  void withdraw(double amount)
    //       If amount <= 0: print "Withdrawal amount must be positive." and return
    //       If balance - amount < MINIMUM_BALANCE: print "Insufficient funds." and return
    //       Otherwise: balance -= amount
    //       Print "Withdrew $[amount]. New balance: $[balance]"

    // TODO: Write getters: getBalance(), getOwnerName(), getAccountNumber()
    // TODO: Write setter:  void setOwnerName(String ownerName) — use 'this.ownerName = ownerName'

    // TODO: Override toString()
    //       Return "Account #[accountNumber] | Owner: [ownerName] | Balance: $[balance]"
}

public class BankAccountDemo {

    public static void main(String[] args) {

        // TODO: Print "Accounts open: " + BankAccount.getAccountCount()  (expect 0)

        // TODO: Create acc1 = new BankAccount("Diana")           (1-arg constructor)
        // TODO: Create acc2 = new BankAccount("Ethan", 500.0)    (2-arg constructor)

        // TODO: Print "Accounts open: " + BankAccount.getAccountCount()  (expect 2)
        // TODO: Print acc1, then acc2

        // TODO: On acc1: deposit(200), deposit(150), withdraw(100)
        // TODO: On acc2: withdraw(600)  [expect: insufficient funds], then withdraw(200)
        // TODO: On acc1: deposit(-50)   [expect: error message]

        // TODO: Print acc1 and acc2 in final state
        // TODO: Print "Minimum balance: $" + BankAccount.MINIMUM_BALANCE
    }
}
