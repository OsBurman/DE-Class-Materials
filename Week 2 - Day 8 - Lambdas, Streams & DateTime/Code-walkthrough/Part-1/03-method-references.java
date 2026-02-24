import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * DAY 8 — PART 1 | Method References
 * ─────────────────────────────────────
 * A method reference is a shorthand for a lambda that calls a single existing method.
 *
 * THERE ARE EXACTLY FOUR KINDS:
 * ┌──────────────────────────────┬──────────────────────────────┬──────────────────────────────────────┐
 * │ Kind                         │ Syntax                       │ Lambda Equivalent                    │
 * ├──────────────────────────────┼──────────────────────────────┼──────────────────────────────────────┤
 * │ 1. Static method             │ ClassName::staticMethod      │ (args) -> ClassName.staticMethod(args)│
 * │ 2. Instance (specific obj)   │ instance::instanceMethod     │ (args) -> instance.method(args)      │
 * │ 3. Instance (arbitrary obj)  │ ClassName::instanceMethod    │ (obj, args) -> obj.method(args)      │
 * │ 4. Constructor               │ ClassName::new               │ (args) -> new ClassName(args)        │
 * └──────────────────────────────┴──────────────────────────────┴──────────────────────────────────────┘
 *
 * KEY IDEA: Method references don't call the method — they describe which method to call.
 *           The functional interface determines how and when it is invoked.
 */
public class MethodReferences {

    public static void main(String[] args) {
        demonstrateStaticMethodReferences();
        demonstrateInstanceMethodReferencesSpecificObject();
        demonstrateInstanceMethodReferencesArbitraryObject();
        demonstrateConstructorReferences();
        demonstrateSideBySideComparisons();
        demonstrateInStreamPipelines();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Static Method References  (ClassName::staticMethod)
    // Use when the lambda just delegates to a static method.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateStaticMethodReferences() {
        System.out.println("=== Static Method References ===");

        // ----- 1a. Integer::parseInt -----
        // Lambda:           s -> Integer.parseInt(s)
        // Method reference: Integer::parseInt
        Function<String, Integer> parseIntLambda = s -> Integer.parseInt(s);
        Function<String, Integer> parseIntRef    = Integer::parseInt;         // same thing

        System.out.println(parseIntLambda.apply("42"));   // 42
        System.out.println(parseIntRef.apply("42"));       // 42

        // ----- 1b. Math::abs -----
        // Lambda:           n -> Math.abs(n)
        // Method reference: Math::abs
        Function<Integer, Integer> absLambda = n -> Math.abs(n);
        Function<Integer, Integer> absRef    = Math::abs;

        List<Integer> numbers = Arrays.asList(-5, 3, -1, 8, -12);
        List<Integer> absolutes = numbers.stream()
                .map(absRef)          // <-- method reference replaces n -> Math.abs(n)
                .collect(Collectors.toList());
        System.out.println("Absolutes: " + absolutes);  // [5, 3, 1, 8, 12]

        // ----- 1c. Math::max as BinaryOperator -----
        BinaryOperator<Integer> maxLambda = (a, b) -> Math.max(a, b);
        BinaryOperator<Integer> maxRef    = Math::max;

        System.out.println("Max(3,7): " + maxRef.apply(3, 7));  // 7

        // ----- 1d. Our own static helper -----
        // Lambda:           s -> isValidEmail(s)
        // Method reference: MethodReferences::isValidEmail
        Predicate<String> emailCheckLambda = s -> isValidEmail(s);
        Predicate<String> emailCheckRef    = MethodReferences::isValidEmail;

        List<String> emails = Arrays.asList("alice@example.com", "not-valid", "bob@corp.org", "missing-at");
        System.out.println("Valid emails:");
        emails.stream()
              .filter(emailCheckRef)           // MethodReferences::isValidEmail
              .forEach(System.out::println);   // System.out::println (preview of section 2)

        System.out.println();
    }

    static boolean isValidEmail(String s) {
        return s != null && s.contains("@") && s.contains(".");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Instance Method References: Specific Object  (instance::method)
    // Use when you have a particular object whose method you want to call.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateInstanceMethodReferencesSpecificObject() {
        System.out.println("=== Instance Method References — Specific Object ===");

        // ----- 2a. System.out::println  (the most common one) -----
        // Lambda:           s -> System.out.println(s)
        // Method reference: System.out::println
        Consumer<String> printLambda = s -> System.out.println(s);
        Consumer<String> printRef    = System.out::println;

        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
        System.out.println("-- Lambda forEach --");
        fruits.forEach(printLambda);
        System.out.println("-- Method ref forEach --");
        fruits.forEach(printRef);

        // ----- 2b. A specific StringBuilder instance -----
        StringBuilder sb = new StringBuilder();
        // Lambda:           s -> sb.append(s)
        // Method reference: sb::append
        Consumer<String> appendToSb = sb::append;

        List<String> words = Arrays.asList("Hello", ", ", "World", "!");
        words.forEach(appendToSb);  // each word gets appended to our specific sb
        System.out.println("Built string: " + sb);  // Hello, World!

        // ----- 2c. A custom Printer instance -----
        Printer myPrinter = new Printer("[LOG] ");
        Consumer<String> logRef = myPrinter::print;  // bound to *this* Printer object

        Arrays.asList("Server started", "Listening on port 8080")
              .forEach(logRef);  // prints "[LOG] Server started", "[LOG] Listening on port 8080"

        System.out.println();
    }

    // Helper for 2c
    static class Printer {
        private final String prefix;
        Printer(String prefix) { this.prefix = prefix; }
        void print(String message) { System.out.println(prefix + message); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Instance Method References: Arbitrary Object  (ClassName::instanceMethod)
    // Use when you want to call an instance method, but the instance itself is the parameter.
    // The first argument becomes the target of the method call.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateInstanceMethodReferencesArbitraryObject() {
        System.out.println("=== Instance Method References — Arbitrary Object ===");

        // ----- 3a. String::toUpperCase -----
        // Lambda:           s -> s.toUpperCase()
        // Method reference: String::toUpperCase
        // The String in the stream becomes "the object" on which toUpperCase() is called.
        Function<String, String> toUpperLambda = s -> s.toUpperCase();
        Function<String, String> toUpperRef    = String::toUpperCase;

        List<String> names = Arrays.asList("alice", "bob", "charlie");
        List<String> upper = names.stream()
                .map(toUpperRef)              // each element is the "instance"
                .collect(Collectors.toList());
        System.out.println("Upper: " + upper);  // [ALICE, BOB, CHARLIE]

        // ----- 3b. String::length -----
        // Lambda:           s -> s.length()
        // Method reference: String::length
        Function<String, Integer> lengthRef = String::length;
        names.stream()
             .map(lengthRef)
             .forEach(System.out::println);   // 5  3  7

        // ----- 3c. String::compareTo — sorting with method reference -----
        // Comparator<String> expects (a, b) -> a.compareTo(b)
        // String::compareTo matches: (a, b) -> a.compareTo(b)  ← first arg is the instance
        List<String> sortable = new ArrayList<>(Arrays.asList("banana", "apple", "date", "cherry"));

        sortable.sort(String::compareTo);      // same as (a, b) -> a.compareTo(b)
        System.out.println("Sorted: " + sortable);  // [apple, banana, cherry, date]

        // ----- 3d. String::isEmpty as Predicate -----
        // Lambda:           s -> s.isEmpty()
        // Method reference: String::isEmpty
        List<String> mixed = Arrays.asList("hello", "", "world", "", "java");
        long blanks = mixed.stream()
                .filter(String::isEmpty)
                .count();
        System.out.println("Blank count: " + blanks);  // 2

        // ----- 3e. Our own domain class -----
        List<Employee> employees = Arrays.asList(
                new Employee("Alice",   "Engineering", 90_000),
                new Employee("Bob",     "Marketing",   65_000),
                new Employee("Charlie", "Engineering", 80_000),
                new Employee("Diana",   "HR",          60_000)
        );

        // Employee::getDepartment  ≡  e -> e.getDepartment()
        employees.stream()
                 .map(Employee::getName)          // e -> e.getName()
                 .sorted()
                 .forEach(System.out::println);   // Alice, Bob, Charlie, Diana

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Constructor References  (ClassName::new)
    // Use to create objects via a functional interface that matches the constructor.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateConstructorReferences() {
        System.out.println("=== Constructor References ===");

        // ----- 4a. No-arg constructor -----
        // Lambda:           () -> new ArrayList<>()
        // Method reference: ArrayList::new
        Supplier<ArrayList<String>> listMakerLambda = () -> new ArrayList<>();
        Supplier<ArrayList<String>> listMakerRef    = ArrayList::new;

        ArrayList<String> list1 = listMakerRef.get();
        list1.add("Created via constructor reference");
        System.out.println(list1);  // [Created via constructor reference]

        // ----- 4b. Single-arg constructor -----
        // Lambda:           s -> new StringBuilder(s)
        // Method reference: StringBuilder::new
        Function<String, StringBuilder> sbMaker = StringBuilder::new;
        StringBuilder sb = sbMaker.apply("Hello World");
        System.out.println("SB length: " + sb.length());  // 11

        // ----- 4c. Custom class constructor -----
        // Employee(String name) — one-arg constructor
        // Lambda:           name -> new Employee(name, "", 0)
        // Method reference: doesn't work unless signature matches exactly
        //
        // Two-arg constructor: Employee(String name, String dept)
        // BiFunction<String, String, Employee> ≡ (name, dept) -> new Employee(name, dept, 0)
        // This matches the two-arg Employee constructor.
        BiFunction<String, String, Employee> twoArgEmployeeRef = Employee::new;
        Employee emp1 = twoArgEmployeeRef.apply("Eve", "Finance");
        System.out.println("Created: " + emp1.getName() + " / " + emp1.getDepartment());

        // ----- 4d. Converting a list of strings into a list of objects -----
        List<String> employeeNames = Arrays.asList("Frank", "Grace", "Heidi");

        // Lambda version:
        List<Employee> empListLambda = employeeNames.stream()
                .map(name -> new Employee(name, "Unassigned", 0))
                .collect(Collectors.toList());

        // There's no direct single-arg constructor here, so we show the pattern clearly:
        System.out.println("Employees created:");
        empListLambda.forEach(e -> System.out.println("  " + e.getName()));

        // ----- 4e. Supplier for dependency injection pattern -----
        // Common in frameworks: pass a Supplier<T> so the caller creates instances on demand
        Supplier<List<String>> freshList = ArrayList::new;
        List<String> a = freshList.get();
        List<String> b = freshList.get();
        a.add("I'm list A");
        b.add("I'm list B");
        System.out.println(a);  // [I'm list A]   — independent instances
        System.out.println(b);  // [I'm list B]

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Side-by-Side Comparison (Lambda vs Method Reference)
    // See the pattern: lambda just calls one method → method reference applies
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSideBySideComparisons() {
        System.out.println("=== Side-by-Side: Lambda vs Method Reference ===");

        List<String> items = Arrays.asList("three", "one", "four", "one", "five", "nine");

        System.out.println("-- Print with forEach --");
        // Lambda:
        items.forEach(s -> System.out.println(s));
        // Method reference:
        items.forEach(System.out::println);

        System.out.println("-- Convert to Integer --");
        List<String> numberStrings = Arrays.asList("1", "2", "3", "4", "5");
        // Lambda:
        List<Integer> numbersLambda = numberStrings.stream()
                .map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());
        // Method reference:
        List<Integer> numbersRef = numberStrings.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        System.out.println(numbersLambda.equals(numbersRef) ? "Both lists match ✓" : "Mismatch");

        System.out.println("-- Sort strings --");
        List<String> toSort = new ArrayList<>(items);
        // Lambda:
        toSort.sort((a, b) -> a.compareToIgnoreCase(b));
        // Method reference:
        toSort.sort(String::compareToIgnoreCase);
        System.out.println("Sorted: " + toSort);

        System.out.println("-- Collect to new list --");
        // Lambda:
        Supplier<List<String>> lambdaSupplier = () -> new ArrayList<>();
        // Method reference:
        Supplier<List<String>> refSupplier    = ArrayList::new;
        List<String> newList = refSupplier.get();
        newList.add("Works!");
        System.out.println(newList);

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — Method References in Stream Pipelines
    // Real-world pipelines composed mostly or entirely of method references
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateInStreamPipelines() {
        System.out.println("=== Method References in Stream Pipelines ===");

        List<Employee> employees = Arrays.asList(
                new Employee("Alice",   "Engineering", 90_000),
                new Employee("Bob",     "Marketing",   65_000),
                new Employee("Charlie", "Engineering", 80_000),
                new Employee("Diana",   "HR",          60_000),
                new Employee("Eve",     "Engineering", 95_000),
                new Employee("Frank",   "Marketing",   70_000)
        );

        // Pipeline 1: Get sorted names of all employees
        System.out.println("All employees (sorted):");
        employees.stream()
                 .map(Employee::getName)           // arbitrary-instance method ref
                 .sorted(String::compareToIgnoreCase)  // static? No — arbitrary instance
                 .forEach(System.out::println);    // specific-object method ref

        // Pipeline 2: Get engineering salaries
        System.out.println("\nEngineering salaries:");
        employees.stream()
                 .filter(Employee::isEngineering)  // custom predicate via method ref
                 .map(Employee::getSalary)
                 .map(Object::toString)            // double -> String
                 .forEach(System.out::println);

        // Pipeline 3: Collect names to a list — all method references
        List<String> names = employees.stream()
                .map(Employee::getName)
                .sorted()
                .collect(Collectors.toList());
        System.out.println("\nNames list: " + names);

        // Pipeline 4: Parse CSV strings into Employees using constructor reference
        List<String> csv = Arrays.asList(
                "Zara,Sales",
                "Yuki,Engineering",
                "Xander,HR"
        );
        System.out.println("\nFrom CSV:");
        csv.stream()
           .map(MethodReferences::parseEmployee)   // static method reference
           .map(Employee::getName)
           .forEach(System.out::println);

        System.out.println();
    }

    static Employee parseEmployee(String csv) {
        String[] parts = csv.split(",");
        return new Employee(parts[0].trim(), parts[1].trim(), 0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INNER CLASS — Employee
    // ─────────────────────────────────────────────────────────────────────────
    static class Employee {
        private final String name;
        private final String department;
        private final double salary;

        Employee(String name, String department, double salary) {
            this.name       = name;
            this.department = department;
            this.salary     = salary;
        }

        // Two-arg constructor for BiFunction::new demo
        Employee(String name, String department) {
            this(name, department, 0);
        }

        String getName()       { return name; }
        String getDepartment() { return department; }
        double getSalary()     { return salary; }
        boolean isEngineering() { return "Engineering".equals(department); }

        @Override
        public String toString() {
            return name + " (" + department + ", $" + String.format("%.0f", salary) + ")";
        }
    }
}
