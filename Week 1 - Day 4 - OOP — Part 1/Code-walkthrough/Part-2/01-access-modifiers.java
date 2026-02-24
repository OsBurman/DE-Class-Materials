/**
 * DAY 4 — OOP Part 1 | Part 2, File 1
 * TOPIC: Access Modifiers
 *
 * Topics covered:
 *  - The four access levels: public, private, protected, default (package-private)
 *  - Why direct field access is dangerous (encapsulation violation)
 *  - Encapsulation with private fields + public getters/setters
 *  - Visibility table (in comments)
 *
 * Key vocabulary:
 *  - access modifier  : keyword that controls where a class member is visible
 *  - encapsulation    : bundling data + behavior AND controlling external access
 *  - getter           : public method that reads a private field
 *  - setter           : public method that writes a private field (with validation)
 */
public class AccessModifiers {

    // ─────────────────────────────────────────────────
    // VISIBILITY REFERENCE TABLE
    // ─────────────────────────────────────────────────
    //
    //  Modifier       │ Same Class │ Same Package │ Subclass │ Everywhere
    //  ───────────────┼────────────┼──────────────┼──────────┼───────────
    //  public         │     ✅     │      ✅      │    ✅    │    ✅
    //  protected      │     ✅     │      ✅      │    ✅    │    ❌
    //  (default)      │     ✅     │      ✅      │    ❌    │    ❌
    //  private        │     ✅     │      ❌      │    ❌    │    ❌
    //
    //  "(default)" means no keyword at all — package-private.
    // ─────────────────────────────────────────────────

    // ==========================================================
    // SECTION A: The "Open Book" Anti-Pattern (public fields)
    // ==========================================================
    // This class has ALL fields public — nothing is protected.
    // We will use it to show why that's a problem.

    static class UnsafeBankAccount {
        public String ownerName;      // anyone can read AND overwrite
        public String accountNumber;
        public double balance;        // no validation — set to any value
        public boolean isActive;

        public UnsafeBankAccount(String ownerName, String accountNumber, double initialBalance) {
            this.ownerName = ownerName;
            this.accountNumber = accountNumber;
            this.balance = initialBalance;
            this.isActive = true;
        }

        @Override
        public String toString() {
            return String.format("UnsafeBankAccount{owner='%s', acct='%s', balance=$%.2f, active=%b}",
                    ownerName, accountNumber, balance, isActive);
        }
    }

    // ==========================================================
    // SECTION B: Properly Encapsulated Bank Account
    // ==========================================================
    // All fields are PRIVATE — only this class can read/write them.
    // External code must go through the public getter/setter interface.

    static class SafeBankAccount {

        // ── private fields ─────────────────────────────────────────
        private String ownerName;
        private String accountNumber;
        private double balance;
        private boolean isActive;
        private int failedWithdrawalAttempts;

        // ── constructor (public — callers need this) ───────────────
        public SafeBankAccount(String ownerName, String accountNumber, double initialBalance) {
            if (ownerName == null || ownerName.isBlank()) {
                throw new IllegalArgumentException("Owner name cannot be blank.");
            }
            if (initialBalance < 0) {
                throw new IllegalArgumentException("Initial balance cannot be negative.");
            }
            this.ownerName = ownerName;
            this.accountNumber = accountNumber;
            this.balance = initialBalance;
            this.isActive = true;
            this.failedWithdrawalAttempts = 0;
        }

        // ── public getters (read access) ───────────────────────────

        public String getOwnerName()    { return ownerName; }
        public String getAccountNumber(){ return accountNumber; }
        public double getBalance()      { return balance; }
        public boolean isActive()       { return isActive; }

        // failedWithdrawalAttempts is an internal metric; no getter exposed

        // ── public setters (write access WITH validation) ──────────

        public void setOwnerName(String ownerName) {
            if (ownerName == null || ownerName.isBlank()) {
                System.out.println("  [REJECTED] Owner name cannot be blank.");
                return;
            }
            this.ownerName = ownerName;
        }

        // accountNumber should never change after creation — no setter

        // balance is controlled only through deposit/withdraw — no direct setter

        // ── public behaviour methods ───────────────────────────────

        public void deposit(double amount) {
            if (!isActive) {
                System.out.println("  [REJECTED] Account is closed.");
                return;
            }
            if (amount <= 0) {
                System.out.println("  [REJECTED] Deposit amount must be positive.");
                return;
            }
            balance += amount;
            System.out.printf("  [DEPOSIT]  +$%.2f  →  new balance: $%.2f%n", amount, balance);
        }

        public boolean withdraw(double amount) {
            if (!isActive) {
                System.out.println("  [REJECTED] Account is closed.");
                return false;
            }
            if (amount <= 0) {
                System.out.println("  [REJECTED] Withdrawal must be positive.");
                return false;
            }
            if (amount > balance) {
                failedWithdrawalAttempts++;
                System.out.printf("  [REJECTED] Insufficient funds. Attempt #%d%n",
                        failedWithdrawalAttempts);
                if (failedWithdrawalAttempts >= 3) {
                    isActive = false;   // internal rule: lock after 3 failures
                    System.out.println("  [LOCKED]   Account locked due to repeated failures.");
                }
                return false;
            }
            balance -= amount;
            System.out.printf("  [WITHDRAW] -$%.2f  →  new balance: $%.2f%n", amount, balance);
            return true;
        }

        public void closeAccount() {
            isActive = false;
            System.out.printf("  Account %s closed.%n", accountNumber);
        }

        @Override
        public String toString() {
            return String.format(
                    "SafeBankAccount{owner='%s', acct='%s', balance=$%.2f, active=%b, failedAttempts=%d}",
                    ownerName, accountNumber, balance, isActive, failedWithdrawalAttempts);
        }
    }

    // ==========================================================
    // SECTION C: Default (package-private) visibility example
    // ==========================================================
    // Members with no modifier are visible only within the same PACKAGE.
    // Useful for "internal helpers" that only sibling classes should use.

    static class PackageHelper {

        // package-private — no modifier → only classes in the same package can see this
        static String formatCurrency(double amount) {
            return String.format("$%.2f", amount);
        }

        // private — only PackageHelper itself can call this
        private static String maskAccountNumber(String acctNum) {
            if (acctNum == null || acctNum.length() < 4) return "****";
            return "****-" + acctNum.substring(acctNum.length() - 4);
        }

        // public — available everywhere
        public static String getSafeDisplay(String acctNum, double balance) {
            return String.format("Account: %s | Balance: %s",
                    maskAccountNumber(acctNum),      // calls private helper — allowed (same class)
                    formatCurrency(balance));         // calls package-private helper — allowed (same class)
        }
    }

    // ==========================================================
    // SECTION D: Protected visibility (inheritance preview)
    // ==========================================================
    // protected means: same class + same package + any subclass (even in another package).
    // Full demonstration comes in Day 5 (Inheritance). This shows the declaration pattern.

    static class Vehicle {
        private String make;           // only Vehicle can access
        private String model;
        protected int year;            // Vehicle + subclasses can access
        protected double engineSize;   // Vehicle + subclasses can access
        public String color;           // everyone can access

        public Vehicle(String make, String model, int year, double engineSize, String color) {
            this.make = make;
            this.model = model;
            this.year = year;
            this.engineSize = engineSize;
            this.color = color;
        }

        public String getMake()  { return make; }
        public String getModel() { return model; }

        @Override
        public String toString() {
            return String.format("Vehicle{%d %s %s, %.1fL, %s}", year, make, model, engineSize, color);
        }
    }

    // A subclass of Vehicle — can access protected members but NOT private ones
    static class ElectricCar extends Vehicle {
        private double batteryCapacityKwh;
        private int rangeKm;

        public ElectricCar(String make, String model, int year,
                           double batteryCapacityKwh, int rangeKm, String color) {
            super(make, model, year, 0.0, color);  // electric — engine size = 0
            this.batteryCapacityKwh = batteryCapacityKwh;
            this.rangeKm = rangeKm;
        }

        public String getSpec() {
            // ✅ year and engineSize are protected — accessible here
            return String.format("%s %s (%d) | %.1f kWh | %d km range | %s",
                    getMake(), getModel(), year, batteryCapacityKwh, rangeKm, color);
            // ❌ make and model are private to Vehicle — we must use the getters
            // ❌ this.make  → compile error: make has private access in Vehicle
        }

        @Override
        public String toString() { return getSpec(); }
    }

    // ==========================================================
    // MAIN — DEMONSTRATIONS
    // ==========================================================
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("SECTION 1 — Why public fields are dangerous");
        System.out.println("============================================================");

        UnsafeBankAccount hackedAccount = new UnsafeBankAccount("Dana Lee", "ACC-999", 5000.00);
        System.out.println("Before: " + hackedAccount);

        // Anyone can do this — no validation, no audit, no control:
        hackedAccount.balance = 999999.99;   // ← direct write to a financial field
        hackedAccount.isActive = false;      // ← can lock the account from outside
        hackedAccount.ownerName = "";        // ← invalid name, allowed silently

        System.out.println("After  (malicious/accidental modification): " + hackedAccount);
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 2 — Encapsulation with private fields");
        System.out.println("============================================================");

        SafeBankAccount alice = new SafeBankAccount("Alice Johnson", "ACC-1001", 1500.00);
        System.out.println("Created: " + alice);
        System.out.println();

        // Reading via getters
        System.out.println("Owner   : " + alice.getOwnerName());
        System.out.println("Balance : $" + alice.getBalance());
        System.out.println("Active  : " + alice.isActive());
        System.out.println();

        // Direct field access — WOULD NOT COMPILE:
        // alice.balance = 999999.99;   // ← compile error: balance has private access in SafeBankAccount
        // alice.isActive = false;      // ← compile error: isActive has private access in SafeBankAccount

        // Must go through controlled methods
        alice.deposit(200.00);
        alice.withdraw(50.00);
        alice.withdraw(5000.00);  // rejected — insufficient funds (attempt 1)
        alice.withdraw(5000.00);  // rejected (attempt 2)
        alice.withdraw(5000.00);  // rejected (attempt 3 → LOCKED)
        alice.deposit(100.00);    // rejected — account locked
        System.out.println();
        System.out.println("Final state: " + alice);
        System.out.println();

        System.out.println("Setter validation demo:");
        SafeBankAccount bob = new SafeBankAccount("Bob Martinez", "ACC-1002", 800.00);
        bob.setOwnerName("");     // rejected — blank
        bob.setOwnerName(null);   // rejected — null
        bob.setOwnerName("Robert Martinez");  // accepted
        System.out.println("Updated owner: " + bob.getOwnerName());
        System.out.println();

        System.out.println("Constructor validation demo:");
        try {
            SafeBankAccount bad = new SafeBankAccount("", "ACC-9999", 100.00);
        } catch (IllegalArgumentException e) {
            System.out.println("  Caught exception: " + e.getMessage());
        }
        try {
            SafeBankAccount bad2 = new SafeBankAccount("Eve", "ACC-0001", -50.00);
        } catch (IllegalArgumentException e) {
            System.out.println("  Caught exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 3 — Package-private and private helpers");
        System.out.println("============================================================");

        // PackageHelper.formatCurrency()   — package-private, accessible here (same file/package)
        // PackageHelper.maskAccountNumber()— private, NOT accessible here
        //   PackageHelper.maskAccountNumber("ACC-1001"); ← compile error

        System.out.println(PackageHelper.getSafeDisplay("ACC-1001", 1500.00));
        System.out.println(PackageHelper.formatCurrency(299.99));  // package-private — works in same package
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 4 — protected (inheritance preview)");
        System.out.println("============================================================");

        Vehicle car = new Vehicle("Toyota", "Corolla", 2022, 1.8, "Silver");
        System.out.println("Vehicle : " + car);

        ElectricCar tesla = new ElectricCar("Tesla", "Model 3", 2023, 75.0, 500, "White");
        System.out.println("Electric: " + tesla);

        // ElectricCar can access color and year (protected) but NOT make/model (private):
        System.out.println("Color (protected, via field): " + tesla.color);
        System.out.println("Year  (protected, via field): " + tesla.year);
        System.out.println("Make  (private,  via getter): " + tesla.getMake());
        System.out.println();

        System.out.println("============================================================");
        System.out.println("ACCESS MODIFIER SUMMARY");
        System.out.println("============================================================");
        System.out.println("private   → tightest. Fields almost always private.");
        System.out.println("(default) → package scope. Good for internal utilities.");
        System.out.println("protected → package + subclasses. Used in inheritance.");
        System.out.println("public    → open to all. Methods/constructors usually public.");
        System.out.println();
        System.out.println("Golden Rule: make everything as private as possible.");
        System.out.println("Expose only what external code genuinely needs.");
    }
}
