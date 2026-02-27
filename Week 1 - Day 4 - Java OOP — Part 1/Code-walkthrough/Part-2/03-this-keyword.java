/**
 * DAY 4 — OOP Part 1 | Part 2, File 3
 * TOPIC: The 'this' Keyword
 *
 * Topics covered:
 *  1. this.field  — disambiguates instance field from constructor/method parameter of the same name
 *  2. this()      — constructor chaining (delegates to another constructor in the same class)
 *                   cross-reference: see 02-constructors.java in Part-1 for extended examples
 *  3. this as argument — passing the current object to another method
 *  4. this as return value — fluent/builder pattern (method chaining)
 *
 * Key vocabulary:
 *  - this         : reference to the current object (the instance the method is running on)
 *  - fluent API   : a style where methods return 'this', enabling call chains: obj.a().b().c()
 *  - builder pattern : accumulate settings via method chaining, then .build() for the final object
 */
public class ThisKeyword {

    // ==========================================================
    // SECTION A: this.field — name disambiguation
    // ==========================================================

    static class Employee {

        // Instance fields — same names we'll use in the constructor
        private String firstName;
        private String lastName;
        private String department;
        private double salary;
        private int    yearsOfExperience;

        // ── Constructor: parameter names shadow field names ────────
        // Without 'this.', the assignment would be self-assignment (salary = salary → no-op).
        public Employee(String firstName, String lastName, String department,
                        double salary, int yearsOfExperience) {

            // 'firstName' refers to the PARAMETER (local scope takes priority)
            // 'this.firstName' refers to the FIELD on this object
            this.firstName         = firstName;         // field  ← parameter
            this.lastName          = lastName;
            this.department        = department;
            this.salary            = salary;
            this.yearsOfExperience = yearsOfExperience;
        }

        // ── Setters also need 'this.' when parameter names match ───

        public void setSalary(double salary) {
            if (salary < 0) {
                System.out.println("  [REJECTED] Salary cannot be negative.");
                return;
            }
            this.salary = salary;   // 'this.salary' = field; 'salary' = parameter
        }

        public void setDepartment(String department) {
            if (department == null || department.isBlank()) {
                System.out.println("  [REJECTED] Department cannot be blank.");
                return;
            }
            this.department = department;
        }

        // ── When 'this.' is NOT required ──────────────────────────
        // If the local variable has a DIFFERENT name than the field,
        // 'this.' is optional — Java finds the field without ambiguity.
        public double calculateAnnualBonus() {
            double baseBonus = salary * 0.10;          // 'salary' unambiguous — no this. needed
            double experienceBonus = yearsOfExperience * 500.0;
            return baseBonus + experienceBonus;
        }

        public String getFullName()    { return firstName + " " + lastName; }
        public double getSalary()      { return salary; }
        public String getDepartment()  { return department; }

        @Override
        public String toString() {
            return String.format("Employee{name='%s %s', dept='%s', salary=$%.2f, exp=%d yrs}",
                    firstName, lastName, department, salary, yearsOfExperience);
        }
    }

    // ==========================================================
    // SECTION B: this() — constructor chaining
    // ==========================================================

    static class Product {

        private String  name;
        private String  category;
        private double  price;
        private int     stockQuantity;
        private boolean discontinued;

        // ── "Master" constructor — does all the real work ─────────
        public Product(String name, String category, double price,
                       int stockQuantity, boolean discontinued) {
            this.name          = name;
            this.category      = category;
            this.price         = price;
            this.stockQuantity = stockQuantity;
            this.discontinued  = discontinued;
            System.out.printf("  [CONSTRUCTED] %s (%s) | $%.2f | qty=%d | disc=%b%n",
                    name, category, price, stockQuantity, discontinued);
        }

        // ── Shorter constructors chain to the master via this() ───
        // Rule: this() MUST be the FIRST statement in the constructor body.

        // 4-param: fills in discontinued=false
        public Product(String name, String category, double price, int stockQuantity) {
            this(name, category, price, stockQuantity, false);  // ← this() call
        }

        // 3-param: fills in stockQuantity=0, discontinued=false
        public Product(String name, String category, double price) {
            this(name, category, price, 0, false);
        }

        // 2-param: fills in category="General", stockQuantity=0, discontinued=false
        public Product(String name, double price) {
            this(name, "General", price, 0, false);
        }

        // 1-param: skeleton product, price=0
        public Product(String name) {
            this(name, "General", 0.0, 0, false);
        }

        public String getName()  { return name; }
        public double getPrice() { return price; }

        @Override
        public String toString() {
            return String.format("Product{name='%s', cat='%s', price=$%.2f, qty=%d, disc=%b}",
                    name, category, price, stockQuantity, discontinued);
        }
    }

    // ==========================================================
    // SECTION C: 'this' as a method argument
    // ==========================================================
    // Sometimes you need to pass the current object to another method or class.
    // 'this' is the reference to the current object — you can pass it just like any variable.

    static class EventLogger {
        private java.util.List<String> log = new java.util.ArrayList<>();

        public void record(String event) {
            log.add(event);
        }

        public void printLog() {
            System.out.println("  ── Event Log ────────────────────────────────");
            for (int i = 0; i < log.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, log.get(i));
            }
            System.out.println("  ─────────────────────────────────────────────");
        }
    }

    static class SavingsAccount {
        private String ownerName;
        private double balance;
        private EventLogger logger;

        public SavingsAccount(String ownerName, double initialBalance, EventLogger logger) {
            this.ownerName = ownerName;
            this.balance   = initialBalance;
            this.logger    = logger;
            // Pass 'this' (the SavingsAccount being created) to the logger
            logger.record("Account opened for " + ownerName + " | initial balance: $" + initialBalance);
        }

        public void deposit(double amount) {
            balance += amount;
            logger.record(ownerName + " deposited $" + amount + " | new balance: $" + balance);
        }

        public void transfer(SavingsAccount destination, double amount) {
            if (amount > balance) {
                logger.record("Transfer FAILED from " + ownerName + " to " + destination.ownerName);
                return;
            }
            balance             -= amount;
            destination.balance += amount;

            // 'this' passed to a helper — the helper can inspect the sender
            logTransfer(this, destination, amount);
        }

        // Static helper receives both accounts via parameters
        private static void logTransfer(SavingsAccount from, SavingsAccount to, double amount) {
            from.logger.record(String.format(
                    "Transfer: %s → %s | $%.2f | from balance now: $%.2f",
                    from.ownerName, to.ownerName, amount, from.balance));
        }

        public String getOwnerName() { return ownerName; }
        public double getBalance()   { return balance; }

        @Override
        public String toString() {
            return String.format("SavingsAccount{owner='%s', balance=$%.2f}", ownerName, balance);
        }
    }

    // ==========================================================
    // SECTION D: 'this' as return value — fluent/builder pattern
    // ==========================================================
    // Returning 'this' from a setter-like method lets the caller chain calls:
    //     account.setName("Alice").setRate(0.05).setMinBalance(100);

    static class SavingsAccountBuilder {

        private String ownerName    = "Unknown";
        private double initialBalance = 0.0;
        private double interestRate = 0.01;
        private double minBalance   = 0.0;
        private boolean overdraftProtection = false;
        private String accountTier  = "Standard";

        // Each 'setter' returns 'this' — enabling chaining
        public SavingsAccountBuilder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;                    // ← return this
        }

        public SavingsAccountBuilder initialBalance(double amount) {
            if (amount < 0) {
                System.out.println("  [BUILDER] Warning: negative initial balance ignored.");
                return this;
            }
            this.initialBalance = amount;
            return this;
        }

        public SavingsAccountBuilder interestRate(double rate) {
            this.interestRate = rate;
            return this;
        }

        public SavingsAccountBuilder minBalance(double min) {
            this.minBalance = min;
            return this;
        }

        public SavingsAccountBuilder overdraftProtection(boolean enabled) {
            this.overdraftProtection = enabled;
            return this;
        }

        public SavingsAccountBuilder premiumTier() {
            this.accountTier = "Premium";
            return this;
        }

        // Terminal method — stops the chain and produces the final result
        public BuiltAccount build() {
            return new BuiltAccount(ownerName, initialBalance, interestRate,
                    minBalance, overdraftProtection, accountTier);
        }
    }

    // The final product returned by the builder
    static class BuiltAccount {
        private final String  ownerName;
        private final double  initialBalance;
        private final double  interestRate;
        private final double  minBalance;
        private final boolean overdraftProtection;
        private final String  accountTier;

        public BuiltAccount(String ownerName, double initialBalance, double interestRate,
                            double minBalance, boolean overdraftProtection, String accountTier) {
            this.ownerName           = ownerName;
            this.initialBalance      = initialBalance;
            this.interestRate        = interestRate;
            this.minBalance          = minBalance;
            this.overdraftProtection = overdraftProtection;
            this.accountTier         = accountTier;
        }

        @Override
        public String toString() {
            return String.format(
                    "BuiltAccount{owner='%s', balance=$%.2f, rate=%.1f%%, " +
                    "minBal=$%.2f, overdraft=%b, tier='%s'}",
                    ownerName, initialBalance, interestRate * 100,
                    minBalance, overdraftProtection, accountTier);
        }
    }

    // ==========================================================
    // MAIN — DEMONSTRATIONS
    // ==========================================================
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("SECTION 1 — this.field: disambiguating name collisions");
        System.out.println("============================================================");

        Employee emp1 = new Employee("Alice", "Johnson", "Engineering", 85000.0, 5);
        Employee emp2 = new Employee("Bob",   "Martinez", "Marketing",   72000.0, 3);

        System.out.println(emp1);
        System.out.println(emp2);
        System.out.println();

        System.out.printf("Alice's bonus: $%.2f%n", emp1.calculateAnnualBonus());

        emp1.setSalary(90000.0);
        System.out.println("After raise: " + emp1.getSalary());
        emp1.setSalary(-1000);        // rejected
        emp2.setDepartment("Sales");
        emp2.setDepartment("");       // rejected
        System.out.println();

        // What happens WITHOUT this. ?
        // Imagine the constructor said:  salary = salary;
        // That assigns the PARAMETER to itself — the field is never updated.
        // The object would always show salary = 0.0 (default double).
        // 'this.' makes the intent explicit and correct.

        System.out.println("============================================================");
        System.out.println("SECTION 2 — this(): constructor chaining");
        System.out.println("============================================================");
        System.out.println("All constructors delegate to the 5-param master:\n");

        Product p1 = new Product("Laptop", "Electronics", 1299.99, 25, false);  // 5-param
        Product p2 = new Product("Mouse",  "Electronics", 29.99,   100);         // 4-param → 5-param
        Product p3 = new Product("Notebook", "Office",    4.99);                 // 3-param → 5-param
        Product p4 = new Product("USB Cable", 9.99);                             // 2-param → 5-param
        Product p5 = new Product("Mystery Item");                                 // 1-param → 5-param
        System.out.println();

        // this() must be FIRST — the following would NOT compile:
        // public Product(String name, double price) {
        //     System.out.println("Creating product...");   // ← statement before this()
        //     this(name, "General", price, 0, false);      // compile error!
        // }

        System.out.println("============================================================");
        System.out.println("SECTION 3 — 'this' as method argument");
        System.out.println("============================================================");

        EventLogger logger = new EventLogger();

        SavingsAccount carol = new SavingsAccount("Carol", 1000.0, logger);
        SavingsAccount dave  = new SavingsAccount("Dave",   500.0, logger);
        System.out.println();

        carol.deposit(250.0);
        dave.deposit(100.0);
        carol.transfer(dave, 300.0);   // 'this' (carol) passed internally to logTransfer
        carol.transfer(dave, 5000.0);  // fails — insufficient funds

        System.out.println();
        System.out.println("Final states:");
        System.out.println("  " + carol);
        System.out.println("  " + dave);
        System.out.println();
        System.out.println("Event log:");
        logger.printLog();
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 4 — 'this' as return value: fluent builder");
        System.out.println("============================================================");

        // Each method returns 'this' → we can chain the calls
        BuiltAccount standardAccount = new SavingsAccountBuilder()
                .ownerName("Eve Thompson")
                .initialBalance(500.0)
                .interestRate(0.02)
                .build();

        BuiltAccount premiumAccount = new SavingsAccountBuilder()
                .ownerName("Frank Wilson")
                .initialBalance(10000.0)
                .interestRate(0.045)
                .minBalance(1000.0)
                .overdraftProtection(true)
                .premiumTier()
                .build();

        System.out.println("Standard: " + standardAccount);
        System.out.println("Premium : " + premiumAccount);
        System.out.println();

        // Compare: without builder pattern, you'd need:
        //   new SavingsAccount("Frank Wilson", 10000.0, 0.045, 1000.0, true, "Premium")
        // — parameter order is hard to remember, easy to swap values accidentally.
        // Builder makes each assignment explicit and readable.

        System.out.println("============================================================");
        System.out.println("'this' KEYWORD SUMMARY");
        System.out.println("============================================================");
        System.out.println("  this.field    → refers to the instance field (disambiguates from parameter)");
        System.out.println("  this()        → calls another constructor in the same class (must be line 1)");
        System.out.println("  method(this)  → passes the current object as an argument");
        System.out.println("  return this;  → enables method chaining (fluent/builder pattern)");
    }
}
