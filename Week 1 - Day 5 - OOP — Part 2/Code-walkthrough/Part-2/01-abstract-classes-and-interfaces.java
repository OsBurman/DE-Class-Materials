/**
 * DAY 5 — OOP Part 2 | Part 2, File 1
 * TOPIC: Abstraction — Abstract Classes and Interfaces
 *
 * Topics covered:
 *  A. Abstract classes
 *     - abstract keyword on class and method
 *     - cannot instantiate abstract class
 *     - abstract method: no body; subclass MUST implement
 *     - concrete method in abstract class: shared default behavior
 *     - constructor in abstract class (called via super())
 *  B. Interfaces
 *     - interface keyword: pure contract
 *     - implements keyword
 *     - all interface methods are implicitly public abstract (before Java 8)
 *     - default methods (Java 8+): provide a body in the interface
 *     - static methods in interfaces
 *     - constants in interfaces (public static final)
 *     - a class can implement MULTIPLE interfaces (Java's answer to multiple inheritance)
 *  C. Abstract class vs Interface — when to choose which
 *  D. Polymorphism through interfaces
 *
 * Key vocabulary:
 *  - abstraction     : hiding implementation details, exposing only what's needed
 *  - abstract class  : partial implementation; cannot be instantiated; extends with one parent
 *  - interface       : pure contract; no fields (except constants); a class can implement many
 *  - default method  : interface method with a default implementation (Java 8+)
 */
public class AbstractClassesAndInterfaces {

    // ==========================================================
    // SECTION A: Abstract Class
    // ==========================================================
    // An abstract class defines a partial implementation.
    // It declares WHAT subclasses must do (abstract methods)
    // while providing COMMON behavior (concrete methods).

    static abstract class Employee {

        // Concrete fields — shared by every Employee type
        private   String employeeId;
        protected String firstName;
        protected String lastName;
        protected double baseSalary;

        // Constructor — even abstract classes have constructors (called via super())
        public Employee(String firstName, String lastName, double baseSalary) {
            this.employeeId = "EMP-" + (int)(Math.random() * 90000 + 10000);
            this.firstName  = firstName;
            this.lastName   = lastName;
            this.baseSalary = baseSalary;
        }

        // ── Abstract methods — subclasses MUST implement these ────
        // No body here — just the contract.

        public abstract double calculateBonus();    // each employee type has a different bonus formula
        public abstract String getRole();           // "Full-Time", "Part-Time", "Contractor"
        public abstract String getDepartment();

        // ── Concrete methods — shared behavior for ALL employees ──

        public double calculateTotalCompensation() {
            return baseSalary + calculateBonus();   // calls abstract calculateBonus() — polymorphism!
        }

        public void printPayslip() {
            System.out.println("  ── Pay Slip ───────────────────────────────────");
            System.out.printf("  Employee  : %s %s (%s)%n", firstName, lastName, employeeId);
            System.out.printf("  Role      : %s | %s%n", getRole(), getDepartment());
            System.out.printf("  Base      : $%.2f%n", baseSalary);
            System.out.printf("  Bonus     : $%.2f%n", calculateBonus());
            System.out.printf("  Total     : $%.2f%n", calculateTotalCompensation());
            System.out.println("  ───────────────────────────────────────────────");
        }

        public String getFullName() { return firstName + " " + lastName; }
        public String getEmployeeId() { return employeeId; }

        @Override
        public String toString() {
            return String.format("%s{name='%s', role='%s', baseSalary=$%.2f}",
                    getClass().getSimpleName(), getFullName(), getRole(), baseSalary);
        }
    }

    // Concrete subclass 1 — implements all abstract methods
    static class FullTimeEmployee extends Employee {
        private int performanceRating;  // 1-5

        public FullTimeEmployee(String firstName, String lastName,
                                double baseSalary, int performanceRating) {
            super(firstName, lastName, baseSalary);
            this.performanceRating = performanceRating;
        }

        @Override
        public double calculateBonus() {
            // Performance-based: 5% per rating point
            return baseSalary * (0.05 * performanceRating);
        }

        @Override
        public String getRole()       { return "Full-Time"; }

        @Override
        public String getDepartment() { return "Engineering"; }
    }

    // Concrete subclass 2
    static class Contractor extends Employee {
        private int hoursWorked;
        private double hourlyRate;

        public Contractor(String firstName, String lastName,
                          int hoursWorked, double hourlyRate) {
            super(firstName, lastName, hoursWorked * hourlyRate);
            this.hoursWorked = hoursWorked;
            this.hourlyRate  = hourlyRate;
        }

        @Override
        public double calculateBonus() {
            // Contractors get no bonus — bonus = 0
            return 0.0;
        }

        @Override
        public String getRole()       { return "Contractor"; }

        @Override
        public String getDepartment() { return "External"; }
    }

    // Concrete subclass 3
    static class Manager extends Employee {
        private int teamSize;
        private double bonusPercentage;

        public Manager(String firstName, String lastName,
                       double baseSalary, int teamSize, double bonusPercentage) {
            super(firstName, lastName, baseSalary);
            this.teamSize        = teamSize;
            this.bonusPercentage = bonusPercentage;
        }

        @Override
        public double calculateBonus() {
            // Flat percentage bonus + $500 per team member
            return (baseSalary * bonusPercentage) + (500.0 * teamSize);
        }

        @Override
        public String getRole()       { return "Manager"; }

        @Override
        public String getDepartment() { return "Leadership"; }

        public int getTeamSize() { return teamSize; }
    }

    // ==========================================================
    // SECTION B: Interfaces
    // ==========================================================
    // An interface is a PURE CONTRACT — it says "any class that
    // implements me promises to provide these methods."
    // No fields (except public static final constants).
    // A class can implement MULTIPLE interfaces.

    // Interface 1: something that can be serialized to JSON
    interface JsonSerializable {
        String toJson();   // every implementing class must provide this

        // static constant — implicitly public static final
        String JSON_VERSION = "1.0";

        // default method — provides a default implementation
        // Implementing classes can override it or accept the default
        default String toJsonWithVersion() {
            return "{\"version\": \"" + JSON_VERSION + "\", \"data\": " + toJson() + "}";
        }

        // static utility method in the interface (Java 8+)
        static String wrap(String key, String value) {
            return "\"" + key + "\": \"" + value + "\"";
        }
    }

    // Interface 2: something that can be compared and sorted
    interface Comparable<T> {
        int compareTo(T other);   // returns negative, 0, or positive
    }

    // Interface 3: something that can generate a report
    interface Reportable {
        String generateReport();

        default void printReport() {
            System.out.println("  ── Report ─────────────────────────────────────");
            System.out.println(generateReport());
            System.out.println("  ───────────────────────────────────────────────");
        }
    }

    // Interface 4: audit trail — tracks when something was created/modified
    interface Auditable {
        String getCreatedBy();
        String getCreatedAt();
        default String getAuditInfo() {
            return "Created by: " + getCreatedBy() + " at " + getCreatedAt();
        }
    }

    // ==========================================================
    // SECTION C: A class implementing MULTIPLE interfaces
    // ==========================================================

    static class Product implements JsonSerializable, Reportable, Auditable {

        private String  name;
        private String  category;
        private double  price;
        private int     stockQuantity;
        private String  createdBy;
        private String  createdAt;

        public Product(String name, String category, double price,
                       int stockQuantity, String createdBy) {
            this.name          = name;
            this.category      = category;
            this.price         = price;
            this.stockQuantity = stockQuantity;
            this.createdBy     = createdBy;
            this.createdAt     = "2026-02-22T09:00:00";
        }

        // ── JsonSerializable contract ──────────────────────────────
        @Override
        public String toJson() {
            return String.format(
                "{\"name\": \"%s\", \"category\": \"%s\", \"price\": %.2f, \"stock\": %d}",
                name, category, price, stockQuantity);
        }

        // inherits default toJsonWithVersion() from JsonSerializable

        // ── Reportable contract ────────────────────────────────────
        @Override
        public String generateReport() {
            return String.format("  Product: %s | %s | $%.2f | In stock: %d",
                    name, category, price, stockQuantity);
        }

        // inherits default printReport() from Reportable

        // ── Auditable contract ─────────────────────────────────────
        @Override
        public String getCreatedBy() { return createdBy; }

        @Override
        public String getCreatedAt() { return createdAt; }

        // inherits default getAuditInfo() from Auditable

        public String getName()  { return name; }
        public double getPrice() { return price; }
        public int    getStock() { return stockQuantity; }

        @Override
        public String toString() {
            return String.format("Product{name='%s', price=$%.2f, stock=%d}", name, price, stockQuantity);
        }
    }

    // ==========================================================
    // SECTION D: Polymorphism through interfaces
    // ==========================================================
    // Just like Animal/Dog/Cat, you can hold objects by their interface type.

    // A functional interface — only ONE abstract method
    // Commonly used with lambdas (Week 2)
    interface Discountable {
        double applyDiscount(double originalPrice);

        default String describeDiscount() { return "Some discount"; }
    }

    // Different discount strategies
    static class PercentageDiscount implements Discountable {
        private double percent;
        public PercentageDiscount(double percent) { this.percent = percent; }

        @Override
        public double applyDiscount(double originalPrice) {
            return originalPrice * (1 - percent / 100);
        }

        @Override
        public String describeDiscount() { return percent + "% off"; }
    }

    static class FlatDiscount implements Discountable {
        private double amount;
        public FlatDiscount(double amount) { this.amount = amount; }

        @Override
        public double applyDiscount(double originalPrice) {
            return Math.max(0, originalPrice - amount);
        }

        @Override
        public String describeDiscount() { return "$" + amount + " off"; }
    }

    static class BuyOneGetOneDiscount implements Discountable {
        @Override
        public double applyDiscount(double originalPrice) {
            return originalPrice / 2;  // effectively 50% off per unit
        }

        @Override
        public String describeDiscount() { return "Buy 1 Get 1 Free (50% effective)"; }
    }

    // ==========================================================
    // MAIN — DEMONSTRATIONS
    // ==========================================================
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("SECTION 1 — Abstract class: cannot instantiate directly");
        System.out.println("============================================================");

        // Employee employee = new Employee(...);  // ← compile error:
        //   Employee is abstract; cannot be instantiated

        FullTimeEmployee alice   = new FullTimeEmployee("Alice", "Johnson", 85000.0, 4);
        Contractor       bob     = new Contractor("Bob", "Martinez", 160, 75.0);
        Manager          carol   = new Manager("Carol", "Williams", 120000.0, 8, 0.15);
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 2 — Abstract methods and concrete methods");
        System.out.println("============================================================");

        alice.printPayslip();    // concrete method in Employee — shared by all
        bob.printPayslip();
        carol.printPayslip();
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 3 — Polymorphism through abstract class");
        System.out.println("============================================================");

        java.util.List<Employee> employees = new java.util.ArrayList<>();
        employees.add(alice);
        employees.add(bob);
        employees.add(carol);
        employees.add(new FullTimeEmployee("Dave", "Brown", 70000.0, 5));
        employees.add(new Manager("Eve", "Davis", 95000.0, 5, 0.10));

        System.out.println("All employee total compensations:");
        for (Employee e : employees) {
            System.out.printf("  %-20s %-12s $%.2f%n",
                    e.getFullName(), e.getRole(), e.calculateTotalCompensation());
        }
        System.out.println();

        // Total payroll via polymorphism:
        double totalPayroll = employees.stream()
                .mapToDouble(Employee::calculateTotalCompensation)
                .sum();
        System.out.printf("  Total payroll: $%.2f%n", totalPayroll);
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 4 — Interfaces: contract + multiple implementation");
        System.out.println("============================================================");

        Product laptop = new Product("MacBook Pro", "Electronics", 2499.99, 15, "admin");
        Product coffee = new Product("Colombian Roast", "Food & Drink", 14.99, 200, "inventory-bot");
        System.out.println();

        // JsonSerializable interface
        System.out.println("Product JSON:");
        System.out.println("  " + laptop.toJson());
        System.out.println("  Versioned: " + laptop.toJsonWithVersion());
        System.out.println();

        // Reportable interface
        System.out.println("Product reports:");
        laptop.printReport();   // default method from Reportable
        coffee.printReport();
        System.out.println();

        // Auditable interface
        System.out.println("Audit info:");
        System.out.println("  Laptop: " + laptop.getAuditInfo());   // default method from Auditable
        System.out.println("  Coffee: " + coffee.getAuditInfo());
        System.out.println();

        // Static interface method
        System.out.println("Interface static method:");
        System.out.println("  " + JsonSerializable.wrap("status", "available"));
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 5 — Polymorphism through interfaces");
        System.out.println("============================================================");

        Discountable[] discounts = {
            new PercentageDiscount(20),       // 20% off
            new FlatDiscount(50),             // $50 off
            new BuyOneGetOneDiscount()        // 50% effective
        };

        double originalPrice = 199.99;
        System.out.printf("Original price: $%.2f%n", originalPrice);
        for (Discountable d : discounts) {
            System.out.printf("  %-40s → $%.2f%n",
                    d.describeDiscount(), d.applyDiscount(originalPrice));
        }
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 6 — Abstract class vs Interface: when to use which");
        System.out.println("============================================================");
        System.out.println();
        System.out.println("  ABSTRACT CLASS                      │ INTERFACE");
        System.out.println("  ────────────────────────────────────┼──────────────────────────────────────");
        System.out.println("  Can have instance fields            │ No instance fields (constants only)");
        System.out.println("  Can have constructors               │ No constructors");
        System.out.println("  Can have any access modifier        │ Methods implicitly public");
        System.out.println("  A class can extend ONLY ONE         │ A class can implement MANY");
        System.out.println("  Can have concrete methods           │ Default methods (Java 8+) only");
        System.out.println("  Use for: shared state + behavior    │ Use for: capability contract");
        System.out.println("  Example: Employee, Shape, Animal    │ Example: Serializable, Comparable");
        System.out.println();
        System.out.println("  Rule of thumb:");
        System.out.println("    Abstract class → IS-A with shared state (Employee has a baseSalary)");
        System.out.println("    Interface      → CAN-DO capability (Product CAN BE serialized)");
    }
}
