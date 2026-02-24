class BankAccount {

    // Static constant — accessible as BankAccount.MINIMUM_BALANCE
    public static final double MINIMUM_BALANCE = 0.0;

    // Auto-incrementing account number seed
    private static int nextAccountNumber = 1000;

    // Derive count from how far nextAccountNumber has advanced
    public static int getAccountCount() {
        return nextAccountNumber - 1000;
    }

    // Instance fields
    private final int    accountNumber;  // final — assigned once in constructor
    private       String ownerName;
    private       double balance;

    // 1-arg convenience constructor — delegates to 2-arg
    public BankAccount(String ownerName) {
        this(ownerName, 0.0);
    }

    // 2-arg canonical constructor — does all real initialization
    public BankAccount(String ownerName, double initialBalance) {
        this.accountNumber = nextAccountNumber++;   // assign current, then increment
        this.ownerName     = ownerName;
        this.balance       = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }
        balance += amount;
        System.out.println("Deposited $" + amount + ". New balance: $" + balance);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return;
        }
        if (balance - amount < MINIMUM_BALANCE) {
            System.out.println("Insufficient funds.");
            return;
        }
        balance -= amount;
        System.out.println("Withdrew $" + amount + ". New balance: $" + balance);
    }

    public double getBalance()       { return balance; }
    public String getOwnerName()     { return ownerName; }
    public int    getAccountNumber() { return accountNumber; }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;     // 'this.' needed because param name matches field name
    }

    @Override
    public String toString() {
        return "Account #" + accountNumber + " | Owner: " + ownerName + " | Balance: $" + balance;
    }
}

public class BankAccountDemo {

    public static void main(String[] args) {

        System.out.println("Accounts open: " + BankAccount.getAccountCount());   // 0

        BankAccount acc1 = new BankAccount("Diana");            // #1000, balance 0
        BankAccount acc2 = new BankAccount("Ethan", 500.0);     // #1001, balance 500

        System.out.println("Accounts open: " + BankAccount.getAccountCount());   // 2
        System.out.println(acc1);
        System.out.println(acc2);

        acc1.deposit(200);
        acc1.deposit(150);
        acc1.withdraw(100);

        acc2.withdraw(600);     // Insufficient funds
        acc2.withdraw(200);

        acc1.deposit(-50);      // Deposit amount must be positive

        System.out.println(acc1);
        System.out.println(acc2);
        System.out.println("Minimum balance: $" + BankAccount.MINIMUM_BALANCE);
    }
}
