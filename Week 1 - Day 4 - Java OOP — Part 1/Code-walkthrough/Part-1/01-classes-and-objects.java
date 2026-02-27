/**
 * 01-classes-and-objects.java
 * Day 4 — OOP Part 1
 *
 * Topics covered:
 *   - What a class is (blueprint / template)
 *   - What an object is (instance)
 *   - Class anatomy: fields, methods, constructors
 *   - Creating objects with 'new'
 *   - Accessing fields and methods via dot notation
 *   - Multiple objects from one class
 *   - Object references and null
 *   - Overriding toString() for readable output
 *   - Objects in memory (heap concept)
 */
public class ClassesAndObjects {

    // =========================================================
    // CLASS DEFINITION: BankAccount
    // =========================================================
    // A class is a BLUEPRINT. It describes what data an object holds
    // (fields) and what it can do (methods).
    // No actual bank account exists yet — just the template.

    static class BankAccount {

        // --- FIELDS (instance variables) ---
        // Each object gets its own copy of these variables
        String ownerName;
        String accountNumber;
        double balance;
        boolean isActive;

        // --- CONSTRUCTOR ---
        // Special method that runs when we do 'new BankAccount(...)'
        // Sets up the initial state of the object
        BankAccount(String ownerName, String accountNumber, double initialBalance) {
            this.ownerName = ownerName;
            this.accountNumber = accountNumber;
            this.balance = initialBalance;
            this.isActive = true;
        }

        // --- METHODS (instance methods) ---
        // These define what a BankAccount can DO

        void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
                System.out.printf("Deposited $%.2f → New balance: $%.2f%n", amount, balance);
            } else {
                System.out.println("Deposit amount must be positive.");
            }
        }

        boolean withdraw(double amount) {
            if (!isActive) {
                System.out.println("Account is closed. Cannot withdraw.");
                return false;
            }
            if (amount > balance) {
                System.out.printf("Insufficient funds. Balance: $%.2f, Requested: $%.2f%n",
                        balance, amount);
                return false;
            }
            balance -= amount;
            System.out.printf("Withdrew $%.2f → New balance: $%.2f%n", amount, balance);
            return true;
        }

        double getBalance() {
            return balance;
        }

        void closeAccount() {
            isActive = false;
            System.out.println("Account " + accountNumber + " has been closed.");
        }

        // toString() — called automatically when you print an object
        // Without this, printing gives something like: ClassesAndObjects$BankAccount@6d06d69c
        @Override
        public String toString() {
            return String.format("BankAccount{owner='%s', acct='%s', balance=$%.2f, active=%b}",
                    ownerName, accountNumber, balance, isActive);
        }
    }

    // =========================================================
    // CLASS DEFINITION: Student
    // =========================================================

    static class Student {
        String name;
        int studentId;
        double gpa;
        String major;

        Student(String name, int studentId, String major) {
            this.name = name;
            this.studentId = studentId;
            this.major = major;
            this.gpa = 0.0;  // new students start with 0 GPA
        }

        void updateGpa(double newGpa) {
            if (newGpa >= 0.0 && newGpa <= 4.0) {
                this.gpa = newGpa;
            } else {
                System.out.println("Invalid GPA. Must be between 0.0 and 4.0");
            }
        }

        String getAcademicStanding() {
            if (gpa >= 3.5) return "Honors";
            if (gpa >= 2.0) return "Good Standing";
            if (gpa > 0.0)  return "Academic Probation";
            return "No grades yet";
        }

        @Override
        public String toString() {
            return String.format("Student{id=%d, name='%s', major='%s', gpa=%.2f, standing='%s'}",
                    studentId, name, major, gpa, getAcademicStanding());
        }
    }

    // =========================================================
    // MAIN METHOD — Where objects come to life
    // =========================================================

    public static void main(String[] args) {

        // =========================================================
        // SECTION 1: Creating objects with 'new'
        // =========================================================

        // 'new' allocates memory on the HEAP and calls the constructor
        // The variable 'account1' holds a REFERENCE (address) to the object
        BankAccount account1 = new BankAccount("Alice Johnson", "ACC-1001", 1500.00);
        BankAccount account2 = new BankAccount("Bob Martinez", "ACC-1002", 500.00);

        // Each object is INDEPENDENT — they have separate copies of all fields
        System.out.println("=== Accounts Created ===");
        System.out.println(account1);  // calls toString()
        System.out.println(account2);

        // =========================================================
        // SECTION 2: Accessing fields and methods (dot notation)
        // =========================================================

        System.out.println("\n=== Accessing Fields ===");
        System.out.println("Owner: " + account1.ownerName);
        System.out.println("Balance: $" + account1.balance);

        System.out.println("\n=== Calling Methods ===");
        account1.deposit(250.00);
        account1.withdraw(100.00);
        account2.deposit(1000.00);
        account2.withdraw(2000.00);  // should fail — insufficient funds

        // =========================================================
        // SECTION 3: Multiple independent objects
        // =========================================================

        System.out.println("\n=== Multiple Objects ===");
        Student student1 = new Student("Diana Patel", 1001, "Computer Science");
        Student student2 = new Student("Ethan Kim", 1002, "Data Engineering");
        Student student3 = new Student("Fiona Chen", 1003, "Computer Science");

        student1.updateGpa(3.8);
        student2.updateGpa(2.7);
        student3.updateGpa(1.9);

        System.out.println(student1);
        System.out.println(student2);
        System.out.println(student3);

        // Changing one object does NOT affect the others
        student1.updateGpa(3.9);
        System.out.println("\nAfter updating student1 GPA:");
        System.out.println("student1: " + student1.gpa);
        System.out.println("student2: " + student2.gpa + " (unchanged)");

        // =========================================================
        // SECTION 4: Object references
        // =========================================================

        System.out.println("\n=== Object References ===");

        // Both variables point to the SAME object in memory
        BankAccount accountRef = account1;   // NOT a copy — a second reference
        accountRef.deposit(500.00);

        // Prove they're the same object — both reflect the deposit
        System.out.println("account1 balance: $" + account1.balance);
        System.out.println("accountRef balance: $" + accountRef.balance);
        System.out.println("Same object? " + (account1 == accountRef));  // true

        // =========================================================
        // SECTION 5: null reference
        // =========================================================

        System.out.println("\n=== Null Reference ===");
        BankAccount emptyAccount = null;  // no object assigned yet
        System.out.println("emptyAccount is null: " + (emptyAccount == null));

        // Calling a method on null throws NullPointerException!
        try {
            emptyAccount.deposit(100.00);  // NPE here
        } catch (NullPointerException e) {
            System.out.println("Caught NullPointerException — emptyAccount was null!");
        }

        // =========================================================
        // SECTION 6: Close an account — state change persists
        // =========================================================

        System.out.println("\n=== State Change ===");
        System.out.println("Before close: " + account2);
        account2.closeAccount();
        account2.withdraw(50.00);  // blocked — account is closed
        System.out.println("After close: " + account2);
    }
}
