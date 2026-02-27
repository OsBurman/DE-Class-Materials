/**
 * 02-constructors.java
 * Day 4 — OOP Part 1
 *
 * Topics covered:
 *   - Default constructor (compiler-provided)
 *   - No-arg constructor (explicitly written)
 *   - Parameterized constructor
 *   - Multiple constructors (constructor overloading)
 *   - Constructor chaining with this()
 *   - Copy constructor
 *   - Constructor vs method: key differences
 */
public class Constructors {

    // =========================================================
    // CLASS: Product — demonstrates all constructor types
    // =========================================================

    static class Product {

        // Fields
        String name;
        String category;
        double price;
        int stockQuantity;
        boolean isAvailable;

        // =========================================================
        // DEFAULT / NO-ARG CONSTRUCTOR
        // =========================================================
        // If you write NO constructors at all, Java provides a default
        // no-arg constructor automatically. Once you write ANY constructor,
        // that auto-generated one disappears.
        //
        // Explicitly written no-arg constructor:
        Product() {
            this.name = "Unnamed Product";
            this.category = "General";
            this.price = 0.0;
            this.stockQuantity = 0;
            this.isAvailable = false;
        }

        // =========================================================
        // PARAMETERIZED CONSTRUCTOR (basic — name + price only)
        // =========================================================
        // Overloading: same constructor name, different parameter lists
        Product(String name, double price) {
            this.name = name;
            this.category = "General";   // default category
            this.price = price;
            this.stockQuantity = 0;
            this.isAvailable = false;
        }

        // =========================================================
        // PARAMETERIZED CONSTRUCTOR (full — all fields)
        // =========================================================
        Product(String name, String category, double price, int stockQuantity) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.isAvailable = stockQuantity > 0;
        }

        // =========================================================
        // CONSTRUCTOR CHAINING with this()
        // =========================================================
        // The most important technique: one constructor calls another
        // via this(...). Eliminates duplication and centralises logic.
        // this() MUST be the FIRST statement in the constructor body.

        // Convenience constructor: name + category + price  (no stock yet)
        Product(String name, String category, double price) {
            this(name, category, price, 0);  // ← delegates to the 4-param constructor
            // Any extra logic specific to this constructor goes BELOW this() call
            System.out.println("  [3-param constructor called → chained to 4-param]");
        }

        // =========================================================
        // COPY CONSTRUCTOR
        // =========================================================
        // Creates a NEW object with the same state as the provided object.
        // Important: this is a SHALLOW copy for primitive/String fields —
        // sufficient for this class because all fields are value types.
        Product(Product other) {
            this(other.name, other.category, other.price, other.stockQuantity);
            System.out.println("  [Copy constructor called]");
        }

        // --- Methods ---
        void restock(int quantity) {
            stockQuantity += quantity;
            isAvailable = true;
        }

        void applyDiscount(double percent) {
            price = price * (1 - percent / 100.0);
        }

        @Override
        public String toString() {
            return String.format("Product{name='%s', cat='%s', price=$%.2f, stock=%d, available=%b}",
                    name, category, price, stockQuantity, isAvailable);
        }
    }

    // =========================================================
    // CLASS: Employee — demonstrates constructor chaining in depth
    // =========================================================

    static class Employee {
        String firstName;
        String lastName;
        String department;
        double salary;
        String employeeId;
        int yearsOfExperience;

        // Full constructor — the "master" that all others chain to
        Employee(String firstName, String lastName, String department,
                 double salary, int yearsOfExperience) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.department = department;
            this.salary = salary;
            this.yearsOfExperience = yearsOfExperience;
            // Generate employee ID from name initials + salary hash
            this.employeeId = "EMP-" + firstName.charAt(0) + lastName.charAt(0)
                    + "-" + (int)(salary / 1000);
        }

        // Convenience: name + department only (default salary and experience)
        Employee(String firstName, String lastName, String department) {
            this(firstName, lastName, department, 50000.0, 0);
            // New hire defaults: $50k base, 0 years experience
        }

        // Convenience: name only (department TBD, defaults for rest)
        Employee(String firstName, String lastName) {
            this(firstName, lastName, "Unassigned");
        }

        String getFullName() { return firstName + " " + lastName; }

        @Override
        public String toString() {
            return String.format("Employee{id='%s', name='%s', dept='%s', salary=$%.0f, yrs=%d}",
                    employeeId, getFullName(), department, salary, yearsOfExperience);
        }
    }

    // =========================================================
    // MAIN METHOD
    // =========================================================

    public static void main(String[] args) {

        // =========================================================
        // SECTION 1: No-arg constructor
        // =========================================================
        System.out.println("=== No-Arg Constructor ===");
        Product p1 = new Product();
        System.out.println(p1);

        // =========================================================
        // SECTION 2: Parameterized constructor (name + price)
        // =========================================================
        System.out.println("\n=== 2-Param Constructor ===");
        Product p2 = new Product("Wireless Headphones", 79.99);
        System.out.println(p2);

        // =========================================================
        // SECTION 3: Parameterized constructor (full)
        // =========================================================
        System.out.println("\n=== 4-Param Constructor ===");
        Product p3 = new Product("Standing Desk", "Furniture", 549.00, 12);
        System.out.println(p3);

        // =========================================================
        // SECTION 4: Constructor chaining with this()
        // =========================================================
        System.out.println("\n=== 3-Param Constructor (chains to 4-param) ===");
        Product p4 = new Product("USB-C Hub", "Electronics", 39.99);
        System.out.println(p4);  // stockQuantity = 0 (set by the 4-param constructor)
        p4.restock(50);
        System.out.println("After restock: " + p4);

        // =========================================================
        // SECTION 5: Copy constructor
        // =========================================================
        System.out.println("\n=== Copy Constructor ===");
        Product original = new Product("Mechanical Keyboard", "Electronics", 129.99, 25);
        System.out.println("Original: " + original);

        Product copy = new Product(original);
        System.out.println("Copy:     " + copy);

        // Modify the copy — original is unaffected
        copy.applyDiscount(10);
        copy.name = "Mechanical Keyboard (Refurb)";
        System.out.println("\nAfter modifying copy:");
        System.out.println("Original: " + original);
        System.out.println("Copy:     " + copy);

        // =========================================================
        // SECTION 6: Constructor chaining — Employee cascade
        // =========================================================
        System.out.println("\n=== Employee Constructor Cascade ===");

        // Uses 5-param constructor directly
        Employee senior = new Employee("Sarah", "Williams", "Engineering", 110000.0, 8);
        System.out.println("Senior: " + senior);

        // Uses 3-param → chains to 5-param
        Employee newHire = new Employee("James", "Brown", "Marketing");
        System.out.println("New hire: " + newHire);

        // Uses 2-param → chains to 3-param → chains to 5-param
        Employee unassigned = new Employee("Chris", "Taylor");
        System.out.println("Unassigned: " + unassigned);

        // =========================================================
        // SECTION 7: Constructor vs method — key differences
        // =========================================================

        // Comparison as comments — for discussion during walkthrough
        //
        // CONSTRUCTOR                     | METHOD
        // --------------------------------+----------------------------------
        // Same name as the class          | Any name (verb convention)
        // No return type (not even void)  | Always has a return type
        // Called only with 'new'          | Called on existing objects
        // Runs once at object creation    | Can be called many times
        // Initialises object state        | Performs operations

        System.out.println("\n=== Constructor vs Method demonstration ===");
        Product desk = new Product("Office Chair", "Furniture", 299.99, 5);  // constructor
        desk.applyDiscount(15);   // method call
        desk.restock(10);         // method call
        System.out.println(desk);
    }
}
