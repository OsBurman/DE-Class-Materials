/** Exercise 12 — Capstone: Main driver (Solution — identical to starter) */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Bank Management System ===\n");
        try {
            Bank bank = Bank.getInstance();

            SavingsAccount alice  = (SavingsAccount)  bank.createAccount("Alice", "savings",  5000.0);
            CheckingAccount bob   = (CheckingAccount) bank.createAccount("Bob",   "checking", 1000.0);
            SavingsAccount carol  = (SavingsAccount)  bank.createAccount("Carol", "savings",  6000.0);

            System.out.println("Created accounts:");
            System.out.println("  " + alice);
            System.out.println("  " + bob);
            System.out.println("  " + carol);

            alice.applyInterest();
            System.out.printf("%nAlice balance after 3%% interest: $%,.2f%n", alice.getBalance());

            bob.withdraw(1200.0);
            System.out.printf("Bob balance after $1,200 withdrawal (overdraft): $%,.2f%n", bob.getBalance());

            bank.transfer(alice.getAccountNumber(), carol.getAccountNumber(), 500.0);
            System.out.printf("Transferred $500 from Alice to Carol.%n");
            System.out.printf("  Alice: $%,.2f  Carol: $%,.2f%n", alice.getBalance(), carol.getBalance());

            System.out.println("\n--- Analytics ---");
            var allTxns = bank.getAllTransactions();
            System.out.printf("Total deposited:       $%,.2f%n", Analytics.totalDeposited(allTxns));
            System.out.println("Transactions by type:  " + Analytics.transactionsByType(allTxns));
            System.out.println("Top 1 spender:         " + Analytics.topSpenders(allTxns, 1));

            System.out.println("\n--- Account Report ---");
            System.out.println(Analytics.getAccountReport(bank.getAllAccounts()));

            System.out.println("--- Error Handling ---");
            try { bank.getAccount("XXX-999"); }
            catch (BankException.AccountNotFoundException e) { System.out.println("Caught: " + e.getMessage()); }
            try { alice.withdraw(999_999.0); }
            catch (BankException.InsufficientFundsException e) { System.out.println("Caught: " + e.getMessage()); }

        } catch (BankException e) {
            System.err.println("Unexpected bank error: " + e.getMessage());
        }
    }
}
