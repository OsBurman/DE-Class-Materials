/**
 * DAY 4 — OOP Part 1 | Part 2, File 2
 * TOPIC: Non-Access Modifiers — static, final, abstract
 *
 * Topics covered:
 *  - static fields   : shared across ALL instances (class-level, not instance-level)
 *  - static methods  : called on the class itself; no 'this'; can't access instance members
 *  - final fields    : value set once, never changed (constants and immutable state)
 *  - final methods   : cannot be overridden by subclasses
 *  - final classes   : cannot be extended (String is final)
 *  - abstract        : preview only — covered fully in Day 5
 *  - Utility class pattern: all-static, private constructor
 *
 * Key vocabulary:
 *  - class member (static)   : belongs to the class, not to any one object
 *  - instance member         : belongs to a specific object
 *  - constant                : final static field — convention: ALL_CAPS_SNAKE_CASE
 */
public class NonAccessModifiers {

    // ==========================================================
    // SECTION A: static fields — shared state across all objects
    // ==========================================================

    static class BankAccount {

        // ── CLASS-LEVEL (static) fields ───────────────────────────
        // One copy shared by ALL BankAccount instances.
        private static int    totalAccounts    = 0;         // counter: increments on each new account
        private static double totalDepositsAll = 0.0;       // running total across all accounts
        public  static final double INTEREST_RATE = 0.025;  // constant — 2.5% annual rate

        // ── INSTANCE fields ───────────────────────────────────────
        // Each BankAccount object has its OWN copy of these.
        private final String  accountNumber;   // final: set once in constructor, never changes
        private String  ownerName;
        private double  balance;
        private boolean isActive;

        // ── Constructor ───────────────────────────────────────────
        public BankAccount(String ownerName, double initialBalance) {
            totalAccounts++;                           // updates the class-level counter
            this.accountNumber = "ACC-" + String.format("%04d", totalAccounts);  // final field set here
            this.ownerName     = ownerName;
            this.balance       = initialBalance;
            this.isActive      = true;
            totalDepositsAll  += initialBalance;
            System.out.printf("  Created %s for %s (total accounts: %d)%n",
                    accountNumber, ownerName, totalAccounts);
        }

        // ── Instance methods (need 'this', operate on one account) ─

        public void deposit(double amount) {
            if (amount <= 0 || !isActive) return;
            balance         += amount;
            totalDepositsAll += amount;           // static field — affects global total
        }

        public boolean withdraw(double amount) {
            if (!isActive || amount <= 0 || amount > balance) return false;
            balance -= amount;
            return true;
        }

        public void applyAnnualInterest() {
            double interest = balance * INTEREST_RATE;   // uses the static constant
            deposit(interest);
            System.out.printf("  Applied %.1f%% interest: +$%.2f to %s%n",
                    INTEREST_RATE * 100, interest, accountNumber);
        }

        // ── Static methods (class-level, no 'this') ────────────────

        public static int    getTotalAccounts()    { return totalAccounts; }
        public static double getTotalDepositsAll() { return totalDepositsAll; }

        public static void printSystemSummary() {
            System.out.println("  ── Bank System Summary ──────────────────────");
            System.out.printf("  Total accounts      : %d%n",   totalAccounts);
            System.out.printf("  Total deposits ever : $%.2f%n", totalDepositsAll);
            System.out.printf("  Current interest rate: %.1f%%%n", INTEREST_RATE * 100);
            System.out.println("  ─────────────────────────────────────────────");
        }

        // static method cannot access instance members:
        // public static void badMethod() {
        //     System.out.println(balance);  // ← compile error: non-static variable balance
        //                                   //   cannot be referenced from a static context
        // }

        public String getAccountNumber() { return accountNumber; }
        public String getOwnerName()     { return ownerName; }
        public double getBalance()       { return balance; }

        @Override
        public String toString() {
            return String.format("BankAccount{%s, owner='%s', balance=$%.2f, active=%b}",
                    accountNumber, ownerName, balance, isActive);
        }
    }

    // ==========================================================
    // SECTION B: final methods and final classes
    // ==========================================================

    static class Shape {
        protected String color;
        protected final String shapeType;  // set once per object — the type never changes

        public Shape(String shapeType, String color) {
            this.shapeType = shapeType;
            this.color     = color;
        }

        // Regular method — can be overridden
        public String describe() {
            return String.format("A %s %s", color, shapeType);
        }

        // final method — subclasses CANNOT override this
        public final String getShapeType() {
            return shapeType;
        }

        // Attempting to override a final method in a subclass would cause:
        //   @Override
        //   public final String getShapeType() { ... }
        //   ← compile error: getShapeType() in Circle cannot override getShapeType() in Shape
        //     overridden method is final
    }

    static class Circle extends Shape {
        private double radius;

        public Circle(double radius, String color) {
            super("Circle", color);
            this.radius = radius;
        }

        // ✅ Allowed — overrides a non-final method
        @Override
        public String describe() {
            return String.format("A %s circle with radius %.1f (area=%.2f)",
                    color, radius, Math.PI * radius * radius);
        }

        // ❌ NOT allowed — getShapeType() is final in Shape
        // @Override
        // public final String getShapeType() { return "oval"; }  // compile error
    }

    // A final CLASS — cannot be subclassed.
    // Java's own String class is final.
    // Attempting: class SpecialString extends String {} ← compile error: cannot inherit from final String
    static final class ImmutablePoint {
        private final double x;
        private final double y;

        public ImmutablePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        // Computed — no setters since the class is meant to be immutable
        public double getX() { return x; }
        public double getY() { return y; }

        public double distanceTo(ImmutablePoint other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        // x and y are final — set once, never changed
        // setX(double) would not compile: cannot assign a value to final variable x

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }

    // ==========================================================
    // SECTION C: Utility class pattern (all-static, no instances)
    // ==========================================================
    // A class that groups related static helper methods.
    // Java's own Math class follows this pattern.
    // Private constructor prevents accidental instantiation.

    static final class MathUtils {

        // Private constructor — cannot create a MathUtils object
        private MathUtils() {
            throw new UnsupportedOperationException("MathUtils is a utility class.");
        }

        public static final double PI        = 3.14159265358979;
        public static final double GOLDEN_RATIO = 1.61803398874989;

        public static double circleArea(double radius)       { return PI * radius * radius; }
        public static double circleCircumference(double r)   { return 2 * PI * r; }
        public static double sphereVolume(double radius)     { return (4.0 / 3) * PI * Math.pow(radius, 3); }
        public static boolean isPrime(int n) {
            if (n < 2) return false;
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) return false;
            }
            return true;
        }
        public static int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }
    }

    // ==========================================================
    // SECTION D: abstract preview (covered fully in Day 5)
    // ==========================================================
    // abstract class: cannot be instantiated directly.
    // It may contain abstract methods (no body) that subclasses MUST implement.

    static abstract class Animal {
        protected String name;

        public Animal(String name) {
            this.name = name;
        }

        // abstract method — no body; subclass must provide implementation
        public abstract String makeSound();

        // concrete method — has a body; subclasses inherit this for free
        public void sleep() {
            System.out.println(name + " is sleeping... 💤");
        }

        @Override
        public String toString() { return name + " says: " + makeSound(); }
    }

    static class Dog extends Animal {
        public Dog(String name) { super(name); }

        @Override
        public String makeSound() { return "Woof!"; }
    }

    static class Cat extends Animal {
        public Cat(String name) { super(name); }

        @Override
        public String makeSound() { return "Meow!"; }
    }

    // ==========================================================
    // MAIN — DEMONSTRATIONS
    // ==========================================================
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("SECTION 1 — static field shared across instances");
        System.out.println("============================================================");

        System.out.println("Before any accounts:");
        System.out.println("  Total accounts: " + BankAccount.getTotalAccounts());
        System.out.println();

        // Each constructor call increments the SHARED totalAccounts counter
        BankAccount alice = new BankAccount("Alice Johnson", 1500.00);
        BankAccount bob   = new BankAccount("Bob Martinez",   800.00);
        BankAccount carol = new BankAccount("Carol Williams", 2200.00);
        System.out.println();

        System.out.println("After creating 3 accounts:");
        BankAccount.printSystemSummary();   // called on the CLASS — not an instance

        System.out.println();
        alice.deposit(500.00);
        bob.deposit(200.00);
        System.out.println("After deposits:");
        BankAccount.printSystemSummary();
        System.out.println();

        System.out.println("Interest rate constant: " + BankAccount.INTEREST_RATE);
        // BankAccount.INTEREST_RATE = 0.05;  // ← compile error: cannot assign a value to final variable INTEREST_RATE
        alice.applyAnnualInterest();
        carol.applyAnnualInterest();
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 2 — final instance field (set once, never changes)");
        System.out.println("============================================================");

        System.out.println("Alice's account number: " + alice.getAccountNumber());
        System.out.println("Bob's account number  : " + bob.getAccountNumber());
        // alice.accountNumber = "ACC-9999";  ← compile error: cannot assign to final variable
        System.out.println("Account numbers are final — they can never be changed.");
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 3 — static vs instance members side-by-side");
        System.out.println("============================================================");

        System.out.println("Calling static method on class name (correct):");
        System.out.println("  BankAccount.getTotalAccounts() = " + BankAccount.getTotalAccounts());

        System.out.println();
        System.out.println("Calling instance method on an object (required):");
        System.out.println("  alice.getBalance()             = $" + alice.getBalance());
        System.out.println("  bob.getBalance()               = $" + bob.getBalance());
        System.out.println();

        //  ┌──────────────────┬────────────────────────────┬──────────────────────────────┐
        //  │                  │       STATIC member        │      INSTANCE member         │
        //  ├──────────────────┼────────────────────────────┼──────────────────────────────┤
        //  │ Belongs to       │ The CLASS itself           │ A specific OBJECT            │
        //  │ Copies in memory │ One (shared by all)        │ One per object               │
        //  │ How to call      │ ClassName.method()         │ object.method()              │
        //  │ Has 'this'?      │ No                         │ Yes                          │
        //  │ Can access       │ Only other static members  │ Both static and instance     │
        //  │ Example          │ BankAccount.getTotalAccounts()│ alice.getBalance()        │
        //  └──────────────────┴────────────────────────────┴──────────────────────────────┘

        System.out.println("============================================================");
        System.out.println("SECTION 4 — final methods and final classes");
        System.out.println("============================================================");

        Shape shape  = new Shape("Polygon", "Gray");
        Circle circle = new Circle(5.0, "Red");

        System.out.println(shape.describe());
        System.out.println(circle.describe());
        System.out.println("Shape type (final method): " + circle.getShapeType());
        System.out.println();

        ImmutablePoint p1 = new ImmutablePoint(0.0, 0.0);
        ImmutablePoint p2 = new ImmutablePoint(3.0, 4.0);
        System.out.println("P1: " + p1);
        System.out.println("P2: " + p2);
        System.out.printf("Distance from P1 to P2: %.2f%n", p1.distanceTo(p2));
        System.out.println("ImmutablePoint is a final class — cannot be subclassed.");
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 5 — Utility class (all static, no instances)");
        System.out.println("============================================================");

        // Called on the class directly — just like java.lang.Math
        System.out.printf("Circle area (r=5)    : %.4f%n",  MathUtils.circleArea(5));
        System.out.printf("Sphere volume (r=3)  : %.4f%n",  MathUtils.sphereVolume(3));
        System.out.printf("Is 17 prime?         : %b%n",    MathUtils.isPrime(17));
        System.out.printf("Is 20 prime?         : %b%n",    MathUtils.isPrime(20));
        System.out.printf("Clamp 150 to [0,100] : %d%n",    MathUtils.clamp(150, 0, 100));
        System.out.printf("Golden ratio         : %.5f%n",  MathUtils.GOLDEN_RATIO);

        // MathUtils utils = new MathUtils();   // ← throws UnsupportedOperationException
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 6 — abstract preview");
        System.out.println("============================================================");

        // Animal animal = new Animal("Generic");  // ← compile error: Animal is abstract; cannot be instantiated
        Animal dog = new Dog("Rex");
        Animal cat = new Cat("Whiskers");

        System.out.println(dog);
        System.out.println(cat);
        dog.sleep();
        cat.sleep();
        System.out.println();
        System.out.println("Both Dog and Cat share the sleep() method from Animal.");
        System.out.println("Each provides its own makeSound() — abstract contract enforced.");
        System.out.println("Full abstract/interface coverage: Day 5.");
    }
}
