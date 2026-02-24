/**
 * Day 8 - Part 1: Lambda Expressions
 *
 * WHAT IS A LAMBDA?
 * ─────────────────
 * A lambda expression is an anonymous function — a block of code that:
 *   1. Can be passed as an argument to a method
 *   2. Can be stored in a variable
 *   3. Can be returned from a method
 *
 * Before lambdas (pre-Java 8), you had to write anonymous inner classes to pass
 * behavior as an argument. Lambdas are the concise modern replacement.
 *
 * SYNTAX:
 *   (parameters) -> expression
 *   (parameters) -> { statements; }
 *
 * RULES:
 *   - Parentheses around parameters are optional when there is exactly one parameter
 *   - Curly braces around the body are optional for single expressions
 *   - 'return' keyword is omitted when using expression form (result is implicit)
 *   - A lambda can only be used where a FUNCTIONAL INTERFACE is expected
 *     (any interface with exactly one abstract method)
 */

import java.util.*;
import java.util.function.*;

public class LambdaExpressions {

    // =========================================================================
    // SECTION 1 — Lambda Syntax Progression
    // =========================================================================

    static void demonstrateSyntax() {
        System.out.println("=== Lambda Syntax — From Verbose to Concise ===");

        // --- 1a. Anonymous inner class (pre-lambda) ---
        Runnable oldWay = new Runnable() {
            @Override
            public void run() {
                System.out.println("  Running via anonymous inner class");
            }
        };

        // --- 1b. Lambda expression — same thing, 3 lines vs 5 ---
        Runnable lambdaWay = () -> System.out.println("  Running via lambda");

        oldWay.run();
        lambdaWay.run();

        System.out.println();
        System.out.println("  --- Syntax forms ---");

        // Zero parameters
        Runnable noParams = () -> System.out.println("  No parameters");
        noParams.run();

        // One parameter — parentheses OPTIONAL
        Consumer<String> oneParam = name -> System.out.println("  Hello, " + name);
        oneParam.accept("Alice");

        // One parameter — with explicit type (also valid)
        Consumer<String> oneParamTyped = (String name) -> System.out.println("  Hello again, " + name);
        oneParamTyped.accept("Bob");

        // Two parameters — parentheses REQUIRED
        Comparator<Integer> twoParams = (a, b) -> a - b;
        System.out.println("  Compare 5 and 3: " + twoParams.compare(5, 3));

        // Block body — use when you need multiple statements
        Comparator<String> blockBody = (s1, s2) -> {
            int lenDiff = s1.length() - s2.length();
            if (lenDiff != 0) return lenDiff;
            return s1.compareTo(s2); // tie-break alphabetically
        };
        System.out.println("  Compare 'apple' and 'fig': " + blockBody.compare("apple", "fig"));

        // Expression that returns a value (return is implicit)
        Function<Integer, Integer> square = n -> n * n;
        System.out.println("  Square of 7: " + square.apply(7));

        System.out.println();
    }

    // =========================================================================
    // SECTION 2 — Lambdas as Comparators (most common real-world use)
    // =========================================================================

    static void demonstrateComparatorLambdas() {
        System.out.println("=== Lambdas as Comparators ===");

        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "David", "Bob", "Eve"));

        // Sort alphabetically — traditional Comparator.compare
        names.sort((a, b) -> a.compareTo(b));
        System.out.println("  Alphabetical:   " + names);

        // Sort by length
        names.sort((a, b) -> a.length() - b.length());
        System.out.println("  By length:      " + names);

        // Sort by length then alphabetical (block body lambda)
        names.sort((a, b) -> {
            if (a.length() != b.length()) return a.length() - b.length();
            return a.compareTo(b);
        });
        System.out.println("  Length+alpha:   " + names);

        // Reverse alphabetical (negate the compareTo)
        names.sort((a, b) -> b.compareTo(a));
        System.out.println("  Reverse alpha:  " + names);

        System.out.println();

        // Sorting a list of custom objects
        List<Product> products = new ArrayList<>(List.of(
            new Product("Laptop",   1299.99, "Electronics"),
            new Product("Keyboard",   89.99, "Electronics"),
            new Product("Notebook",    4.99, "Stationery"),
            new Product("Monitor",   449.99, "Electronics"),
            new Product("Pen",         1.99, "Stationery")
        ));

        // Sort products by price
        products.sort((p1, p2) -> Double.compare(p1.price, p2.price));
        System.out.println("  Products by price:");
        products.forEach(p -> System.out.printf("    %-10s $%.2f%n", p.name, p.price));

        // Sort by category then price
        products.sort((p1, p2) -> {
            int catCmp = p1.category.compareTo(p2.category);
            if (catCmp != 0) return catCmp;
            return Double.compare(p1.price, p2.price);
        });
        System.out.println("\n  Products by category then price:");
        products.forEach(p -> System.out.printf("    %-12s %-10s $%.2f%n",
                p.category, p.name, p.price));

        System.out.println();
    }

    // =========================================================================
    // SECTION 3 — Lambdas with Collections: forEach, removeIf, replaceAll
    // =========================================================================

    static void demonstrateCollectionMethods() {
        System.out.println("=== Lambdas with Collection Methods ===");

        List<String> cities = new ArrayList<>(List.of(
                "New York", "London", "Tokyo", "Paris", "Sydney", "Rio"));

        // forEach — iterate with a Consumer lambda
        System.out.println("  All cities:");
        cities.forEach(city -> System.out.println("    " + city));

        // removeIf — remove elements matching a Predicate lambda
        cities.removeIf(city -> city.length() > 6);
        System.out.println("\n  Cities with 6 or fewer characters:");
        cities.forEach(city -> System.out.println("    " + city));

        // replaceAll — transform each element with a UnaryOperator lambda
        cities.replaceAll(city -> city.toUpperCase());
        System.out.println("\n  Uppercased:");
        cities.forEach(city -> System.out.println("    " + city));

        // Map.forEach — iterate key-value pairs
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("Alice", 92);
        scores.put("Bob", 87);
        scores.put("Carol", 95);
        scores.put("David", 78);

        System.out.println("\n  Score board:");
        scores.forEach((name, score) -> System.out.printf("    %-6s: %d%s%n",
                name, score, score >= 90 ? " ★" : ""));

        System.out.println();
    }

    // =========================================================================
    // SECTION 4 — Lambdas as Thread / Runnable
    // =========================================================================

    static void demonstrateRunnableLambdas() {
        System.out.println("=== Lambdas as Runnable ===");

        // Creating a thread with an anonymous Runnable — old way
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("  Thread 1 (anonymous class): " + Thread.currentThread().getName());
            }
        });

        // Creating a thread with a lambda — new way (same interface, less ceremony)
        Thread t2 = new Thread(() ->
                System.out.println("  Thread 2 (lambda): " + Thread.currentThread().getName()));

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        System.out.println();
    }

    // =========================================================================
    // SECTION 5 — Variable Capture (Closures): effectively final
    // =========================================================================

    static void demonstrateVariableCapture() {
        System.out.println("=== Variable Capture — Effectively Final ===");

        String greeting = "Hello"; // effectively final — never reassigned after this

        // Lambda captures 'greeting' from the enclosing scope
        Consumer<String> greeter = name -> System.out.println("  " + greeting + ", " + name + "!");
        greeter.accept("World");
        greeter.accept("Java");

        // ⚠️ WATCH OUT: you CANNOT modify a captured variable inside a lambda.
        // The code below would NOT compile:
        //   int count = 0;
        //   Runnable r = () -> count++;  // ERROR: count must be effectively final

        // WORKAROUND: Use an array of size 1 (reference is final, contents can change)
        int[] counter = {0};
        List<String> tags = List.of("lambda", "stream", "optional", "functional");
        tags.forEach(tag -> {
            counter[0]++;
            System.out.println("  Tag " + counter[0] + ": " + tag);
        });

        System.out.println("  Total tags processed: " + counter[0]);
        System.out.println();
    }

    // =========================================================================
    // SECTION 6 — Storing Lambdas in Variables and Passing as Arguments
    // =========================================================================

    /**
     * Lambdas are objects — they can be stored, passed, and returned.
     * The type is always a functional interface.
     */
    static void processNumbers(List<Integer> numbers, Predicate<Integer> filter,
                                Function<Integer, Integer> transform) {
        System.out.print("  Result: [");
        numbers.stream()
               .filter(filter)
               .map(transform)
               .forEach(n -> System.out.print(n + " "));
        System.out.println("]");
    }

    static void demonstrateLambdasAsArguments() {
        System.out.println("=== Lambdas as Arguments to Methods ===");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Pass different lambda combinations to the same method
        System.out.print("  Even numbers doubled:      ");
        processNumbers(numbers, n -> n % 2 == 0, n -> n * 2);

        System.out.print("  Odd numbers squared:       ");
        processNumbers(numbers, n -> n % 2 != 0, n -> n * n);

        System.out.print("  Numbers > 5, tripled:      ");
        processNumbers(numbers, n -> n > 5, n -> n * 3);

        // Store lambda in a variable and reuse it
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Function<Integer, Integer> doubled = n -> n * 2;
        Function<Integer, String> toLabel = n -> "item-" + n;

        System.out.println("\n  Reusable lambdas:");
        System.out.println("  isEven.test(4): " + isEven.test(4));
        System.out.println("  doubled.apply(7): " + doubled.apply(7));
        System.out.println("  toLabel.apply(3): " + toLabel.apply(3));

        System.out.println();
    }

    // =========================================================================
    // HELPER CLASS
    // =========================================================================

    static class Product {
        String name;
        double price;
        String category;

        Product(String name, double price, String category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║    Day 8 - Part 1: Lambda Expressions            ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        demonstrateSyntax();
        demonstrateComparatorLambdas();
        demonstrateCollectionMethods();
        demonstrateRunnableLambdas();
        demonstrateVariableCapture();
        demonstrateLambdasAsArguments();
    }
}
