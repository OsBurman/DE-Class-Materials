/**
 * Day 8 - Part 1: Functional Interfaces
 *
 * WHAT IS A FUNCTIONAL INTERFACE?
 * ────────────────────────────────
 * An interface with EXACTLY ONE abstract method.
 * The @FunctionalInterface annotation is optional but recommended — it causes
 * a compile error if you accidentally add a second abstract method.
 *
 * Java 8 introduced java.util.function package with ~40 built-in functional interfaces.
 * The four most important ones:
 *
 *   Interface          Method Signature      Description
 *   ─────────────────────────────────────────────────────────────────────────
 *   Predicate<T>       boolean test(T t)      Test a condition → true/false
 *   Function<T, R>     R apply(T t)           Transform T into R
 *   Consumer<T>        void accept(T t)       Consume T, produce nothing
 *   Supplier<T>        T get()                Produce T, consume nothing
 *   ─────────────────────────────────────────────────────────────────────────
 *
 * MEMORY TRICK: Think of them in terms of data flow:
 *   Supplier  =  () → T          "gives you something"
 *   Consumer  =  T → ()          "takes something, does something with it"
 *   Function  =  T → R           "transforms something into something else"
 *   Predicate =  T → boolean     "tests something"
 */

import java.util.*;
import java.util.function.*;

public class FunctionalInterfaces {

    // =========================================================================
    // SECTION 1 — Predicate<T>: boolean test(T t)
    // =========================================================================

    static void demonstratePredicate() {
        System.out.println("=== Predicate<T> — Testing Conditions ===");

        // Basic predicates
        Predicate<Integer> isEven        = n -> n % 2 == 0;
        Predicate<Integer> isPositive    = n -> n > 0;
        Predicate<String>  isLongWord    = s -> s.length() > 5;
        Predicate<String>  startsWithA   = s -> s.startsWith("A");

        System.out.println("  isEven.test(4):   " + isEven.test(4));
        System.out.println("  isEven.test(7):   " + isEven.test(7));
        System.out.println("  isLongWord(\"elephant\"): " + isLongWord.test("elephant"));
        System.out.println("  startsWithA(\"Apple\"): " + startsWithA.test("Apple"));

        System.out.println();

        // --- Predicate COMPOSITION ---
        System.out.println("  --- Predicate Composition ---");

        Predicate<Integer> isPositiveAndEven = isPositive.and(isEven);   // AND
        Predicate<Integer> isEvenOrNegative  = isEven.or(n -> n < 0);    // OR
        Predicate<Integer> isOdd             = isEven.negate();           // NOT

        int[] testNums = {-4, -3, 0, 1, 2, 6};
        for (int n : testNums) {
            System.out.printf("    n=%-3d  positiveAndEven=%-5b  evenOrNegative=%-5b  isOdd=%b%n",
                    n, isPositiveAndEven.test(n), isEvenOrNegative.test(n), isOdd.test(n));
        }

        System.out.println();

        // --- Real-world: filtering a list ---
        System.out.println("  --- Predicate in practice: filtering employees ---");

        List<Employee> employees = List.of(
            new Employee("Alice",   "Engineering", 95000, true),
            new Employee("Bob",     "Marketing",   72000, true),
            new Employee("Carol",   "Engineering", 88000, false),
            new Employee("David",   "HR",          65000, true),
            new Employee("Eve",     "Engineering", 105000, true)
        );

        Predicate<Employee> isEngineer      = e -> "Engineering".equals(e.department);
        Predicate<Employee> isHighEarner    = e -> e.salary >= 90000;
        Predicate<Employee> isActive        = e -> e.active;

        Predicate<Employee> isActiveEngineerHighEarner = isEngineer.and(isHighEarner).and(isActive);

        System.out.println("  Active engineers earning ≥ $90k:");
        employees.stream()
                 .filter(isActiveEngineerHighEarner)
                 .forEach(e -> System.out.printf("    %-6s $%.0f%n", e.name, e.salary));

        System.out.println();
    }

    // =========================================================================
    // SECTION 2 — Function<T, R>: R apply(T t)
    // =========================================================================

    static void demonstrateFunction() {
        System.out.println("=== Function<T, R> — Transforming Values ===");

        // Basic transformations
        Function<String, Integer>  strToLength = String::length;    // (method ref preview)
        Function<Integer, String>  intToHex    = Integer::toHexString;
        Function<String, String>   trim        = String::trim;

        System.out.println("  \"hello\".length(): " + strToLength.apply("hello"));
        System.out.println("  255 in hex: " + intToHex.apply(255));
        System.out.println("  trimmed: \"" + trim.apply("  spaces  ") + "\"");

        System.out.println();

        // --- Function COMPOSITION: andThen and compose ---
        System.out.println("  --- Function Composition ---");

        Function<Double, Double> celsiusToFahrenheit = c -> c * 9.0 / 5.0 + 32;
        Function<Double, String> formatTemp          = t -> String.format("%.1f°F", t);

        // andThen: apply THIS function first, then the NEXT function
        Function<Double, String> celsiusToDisplay = celsiusToFahrenheit.andThen(formatTemp);
        System.out.println("  100°C = " + celsiusToDisplay.apply(100.0));
        System.out.println("  0°C   = " + celsiusToDisplay.apply(0.0));
        System.out.println("  37°C  = " + celsiusToDisplay.apply(37.0));

        // compose: apply the ARGUMENT function first, then THIS function
        // andThen(g): f.andThen(g) = g(f(x))
        // compose(g): f.compose(g) = f(g(x))
        Function<String, String> removeSpaces = s -> s.replace(" ", "");
        Function<String, String> toLowerCase  = String::toLowerCase;
        Function<String, String> toSlug       = removeSpaces.andThen(toLowerCase);
        System.out.println("\n  'Hello World' slug: " + toSlug.apply("Hello World"));

        System.out.println();

        // --- BiFunction: two input parameters ---
        System.out.println("  --- BiFunction<T, U, R> ---");
        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
        System.out.println("  \"ha\".repeat(3): " + repeat.apply("ha", 3));

        BiFunction<Double, Double, Double> hypotenuse =
                (a, b) -> Math.sqrt(a * a + b * b);
        System.out.printf("  Hypotenuse(3,4): %.2f%n", hypotenuse.apply(3.0, 4.0));

        // --- UnaryOperator: same type in and out ---
        System.out.println();
        System.out.println("  --- UnaryOperator<T> (Function<T,T> shorthand) ---");
        UnaryOperator<String>  shout  = s -> s.toUpperCase() + "!";
        UnaryOperator<Integer> triple = n -> n * 3;

        List<String> words = new ArrayList<>(List.of("hello", "world", "java"));
        words.replaceAll(shout);  // replaceAll takes UnaryOperator
        System.out.println("  Shouted words: " + words);

        System.out.println();
    }

    // =========================================================================
    // SECTION 3 — Consumer<T>: void accept(T t)
    // =========================================================================

    static void demonstrateConsumer() {
        System.out.println("=== Consumer<T> — Consuming Values (side effects) ===");

        // Basic consumers
        Consumer<String>  printLine  = s -> System.out.println("  " + s);
        Consumer<Integer> printSquare = n -> System.out.println("  " + n + "² = " + (n * n));

        printLine.accept("Hello from a Consumer");
        printSquare.accept(7);

        System.out.println();

        // forEach uses Consumer — you've been using it!
        List<String> fruits = List.of("apple", "banana", "cherry", "date");
        System.out.println("  forEach with Consumer:");
        fruits.forEach(printLine);  // passing a Consumer variable

        System.out.println();

        // --- Consumer COMPOSITION: andThen ---
        System.out.println("  --- Consumer.andThen() ---");

        Consumer<Employee> printName     = e -> System.out.print("  Name: " + e.name);
        Consumer<Employee> printDept     = e -> System.out.println(" | Dept: " + e.department);
        Consumer<Employee> printFullInfo = printName.andThen(printDept);

        List<Employee> staff = List.of(
            new Employee("Alice", "Engineering", 95000, true),
            new Employee("Bob",   "Marketing",   72000, true)
        );
        staff.forEach(printFullInfo);

        System.out.println();

        // --- BiConsumer: accepts two arguments ---
        System.out.println("  --- BiConsumer<T, U> ---");
        BiConsumer<String, Integer> printEntry = (key, val) ->
                System.out.printf("  %-15s = %d%n", key, val);

        Map<String, Integer> inventory = Map.of("Laptops", 12, "Monitors", 8, "Keyboards", 35);
        inventory.forEach(printEntry);  // Map.forEach uses BiConsumer

        System.out.println();
    }

    // =========================================================================
    // SECTION 4 — Supplier<T>: T get()
    // =========================================================================

    static void demonstrateSupplier() {
        System.out.println("=== Supplier<T> — Producing Values (no input) ===");

        // Basic suppliers
        Supplier<String>  greeting  = () -> "Hello, World!";
        Supplier<Double>  random    = Math::random;                    // method ref
        Supplier<List<String>> newList = ArrayList::new;               // constructor ref

        System.out.println("  greeting.get(): " + greeting.get());
        System.out.printf("  random.get():   %.4f%n", random.get());
        System.out.println("  newList.get():  " + newList.get().getClass().getSimpleName());

        System.out.println();

        // --- Real-world pattern: lazy initialization ---
        System.out.println("  --- Lazy initialization with Supplier ---");

        // Without supplier: the expensive computation happens EAGERLY
        // String config = loadConfig();  // runs immediately, even if not needed

        // With supplier: computation is deferred until .get() is called
        Supplier<String> lazyConfig = () -> {
            System.out.println("    [Loading config now...]");
            return "host=localhost;port=5432;db=myapp";
        };

        System.out.println("  Supplier created — nothing computed yet.");
        System.out.println("  Calling get() now:");
        String config = lazyConfig.get(); // runs only when we actually need it
        System.out.println("  Config: " + config);

        System.out.println();

        // --- orElseGet vs orElse with Supplier ---
        System.out.println("  --- Supplier in Optional.orElseGet() ---");

        // orElse(T) evaluates the default EAGERLY — always computed
        // orElseGet(Supplier<T>) evaluates the default LAZILY — only if needed
        String maybeName = null;

        // ⚠️ orElse: the fallback string is constructed even if not needed
        String result1 = Optional.ofNullable(maybeName)
                                 .orElse(generateDefaultName()); // runs ALWAYS

        // ✅ orElseGet: the supplier only runs if value is absent
        String result2 = Optional.ofNullable(maybeName)
                                 .orElseGet(() -> generateDefaultName()); // runs only if needed

        System.out.println("  orElse result:    " + result1);
        System.out.println("  orElseGet result: " + result2);

        System.out.println();
    }

    static String generateDefaultName() {
        System.out.println("    [generateDefaultName() was called]");
        return "Guest-" + (int)(Math.random() * 1000);
    }

    // =========================================================================
    // SECTION 5 — Writing Your Own Functional Interface
    // =========================================================================

    /**
     * @FunctionalInterface — your own custom functional interface.
     * Any interface with ONE abstract method qualifies.
     * The annotation just enforces this and makes the intent explicit.
     */
    @FunctionalInterface
    interface EmailValidator {
        boolean validate(String email);

        // ✓ You CAN have default methods — they don't count as abstract
        default boolean validateAndLog(String email) {
            boolean valid = validate(email);
            System.out.println("    Validating '" + email + "': " + (valid ? "VALID" : "INVALID"));
            return valid;
        }
    }

    @FunctionalInterface
    interface Transformer<A, B> {
        B transform(A input);
    }

    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    static void demonstrateCustomFunctionalInterface() {
        System.out.println("=== Custom @FunctionalInterface ===");

        // EmailValidator — implemented as a lambda
        EmailValidator simpleValidator = email -> email != null
                && email.contains("@")
                && email.contains(".");

        EmailValidator strictValidator = email -> email != null
                && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

        String[] emails = {"alice@example.com", "not-an-email", "bob@co.uk", "@bad.com"};
        System.out.println("  Simple validator:");
        for (String e : emails) simpleValidator.validateAndLog(e);

        System.out.println("  Strict validator (regex):");
        for (String e : emails) strictValidator.validateAndLog(e);

        System.out.println();

        // Transformer — generic custom interface
        Transformer<String, Integer> wordCount = text -> text.split("\\s+").length;
        System.out.println("  Word count of sentence: "
                + wordCount.transform("The quick brown fox jumps over the lazy dog"));

        // TriFunction — three parameters, one result
        TriFunction<String, Integer, Boolean, String> formatUser =
                (name, age, premium) -> name + " (age " + age + ")" + (premium ? " ★" : "");
        System.out.println("  Formatted: " + formatUser.apply("Alice", 30, true));
        System.out.println("  Formatted: " + formatUser.apply("Bob", 25, false));

        System.out.println();
    }

    // =========================================================================
    // SECTION 6 — Summary: Choosing the Right Functional Interface
    // =========================================================================

    static void summarize() {
        System.out.println("=== Summary: Which Functional Interface to Use? ===");
        System.out.println();
        System.out.println("  Use case                          → Interface");
        System.out.println("  ──────────────────────────────────────────────────────────────");
        System.out.println("  Check if something is true/false  → Predicate<T>");
        System.out.println("  Transform T into R                → Function<T,R>");
        System.out.println("  Transform T into T (same type)    → UnaryOperator<T>");
        System.out.println("  Transform T, U into R             → BiFunction<T,U,R>");
        System.out.println("  Perform an action on T (no return)→ Consumer<T>");
        System.out.println("  Produce a T with no input         → Supplier<T>");
        System.out.println("  Run some code with no params/return→ Runnable");
        System.out.println();
        System.out.println("  Specialised primitives (skip boxing overhead):");
        System.out.println("    IntPredicate, LongPredicate, DoublePredicate");
        System.out.println("    IntFunction<R>, IntToDoubleFunction, IntUnaryOperator");
        System.out.println("    IntConsumer, IntSupplier, etc.");
        System.out.println();
    }

    // =========================================================================
    // HELPER CLASSES
    // =========================================================================

    static class Employee {
        String name, department;
        double salary;
        boolean active;

        Employee(String name, String department, double salary, boolean active) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.active = active;
        }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║    Day 8 - Part 1: Functional Interfaces         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        demonstratePredicate();
        demonstrateFunction();
        demonstrateConsumer();
        demonstrateSupplier();
        demonstrateCustomFunctionalInterface();
        summarize();
    }
}
