public class Main {
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.addAccount(new BankAccount("ACC001", "Alice", 1000.00, 500.00));
        atm.addAccount(new BankAccount("ACC002", "Bob",    250.00, 500.00));

        atm.printAllAccounts();

        System.out.println("\n--- Valid withdrawal ---");
        atm.processWithdrawal("ACC001", 200.00);

        System.out.println("\n--- Insufficient funds ---");
        atm.processWithdrawal("ACC002", 300.00);

        System.out.println("\n--- Invalid amount ---");
        atm.processWithdrawal("ACC001", -50.00);

        System.out.println("\n--- Account not found ---");
        atm.processWithdrawal("ACC999", 100.00);

        System.out.println("\n--- Daily limit exceeded ---");
        atm.processWithdrawal("ACC001", 400.00); // already withdrew 200, limit is 500

        System.out.println("\n--- Transfer: Alice → Bob ---");
        atm.processTransfer("ACC001", "ACC002", 100.00);

        System.out.println("\n--- Final balances ---");
        atm.printAllAccounts();
    }
}
