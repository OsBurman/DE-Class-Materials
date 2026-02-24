import java.util.*;

/**
 * DAY 6 — Collections & Generics
 * Part 2, File 2: Comparable vs Comparator
 *
 * Topics covered:
 *   - Comparable<T>: natural ordering, implements on your own class
 *   - Comparator<T>: external/custom ordering, anonymous classes
 *   - Lambda Comparators (Java 8+): Comparator.comparing(), thenComparing()
 *   - Chained comparators for multi-field sorting
 *   - Using custom Comparators with TreeSet, TreeMap, Collections.sort()
 *   - Reversed order and null-safe comparators
 */
public class ComparableAndComparator {

    // =========================================================================
    // SECTION 1: COMPARABLE — NATURAL ORDERING
    // =========================================================================
    // A class implements Comparable<T> to define its "natural" sort order.
    // The contract: implement int compareTo(T other)
    //
    //   Returns negative  → this comes BEFORE other
    //   Returns 0         → this and other are EQUAL
    //   Returns positive  → this comes AFTER other
    //
    // Once Comparable is implemented:
    //   - Collections.sort(list) works without any extra argument
    //   - TreeSet/TreeMap use it automatically
    //   - Arrays.sort() uses it
    // =========================================================================

    static class Student implements Comparable<Student> {
        String name;
        double gpa;
        int year;  // 1=Freshman, 2=Sophomore, 3=Junior, 4=Senior

        Student(String name, double gpa, int year) {
            this.name = name;
            this.gpa  = gpa;
            this.year = year;
        }

        // Natural ordering: by GPA descending (highest first)
        @Override
        public int compareTo(Student other) {
            // Descending: compare other to this (reversed)
            return Double.compare(other.gpa, this.gpa);
        }

        @Override
        public String toString() {
            return String.format("Student{%s, GPA=%.2f, year=%d}", name, gpa, year);
        }
    }

    // =========================================================================
    // SECTION 2: PRODUCT — NATURAL ORDERING BY NAME
    // =========================================================================
    static class Product implements Comparable<Product> {
        String name;
        double price;
        String category;
        int stock;

        Product(String name, double price, String category, int stock) {
            this.name = name;
            this.price = price;
            this.category = category;
            this.stock = stock;
        }

        // Natural ordering: alphabetical by name
        @Override
        public int compareTo(Product other) {
            return this.name.compareTo(other.name);
        }

        @Override
        public String toString() {
            return String.format("Product{%s, $%.2f, %s, stock=%d}",
                name, price, category, stock);
        }
    }

    // =========================================================================
    // SECTION 3: COMPARATOR — CUSTOM / EXTERNAL ORDERING
    // =========================================================================
    // Comparator<T> is a separate object (or lambda) that defines a comparison.
    // You use it when:
    //   - You don't own the class (can't implement Comparable on String)
    //   - You need a different sort order from the natural one
    //   - You need multiple sort orders for the same class
    //
    // Interface: int compare(T a, T b)
    //   Returns negative → a before b
    //   Returns 0        → equal
    //   Returns positive → a after b
    // =========================================================================

    // Named comparator class (old style — still valid, good for complex logic)
    static class ProductByPriceAsc implements Comparator<Product> {
        @Override
        public int compare(Product a, Product b) {
            return Double.compare(a.price, b.price);
        }
    }

    static class ProductByPriceDesc implements Comparator<Product> {
        @Override
        public int compare(Product a, Product b) {
            return Double.compare(b.price, a.price);  // reversed
        }
    }

    public static void main(String[] args) {

        // ---- Build a list of products for all demos ----
        List<Product> products = new ArrayList<>(List.of(
            new Product("Laptop",    1299.99, "Electronics", 15),
            new Product("Monitor",    449.00, "Electronics", 30),
            new Product("Keyboard",    89.99, "Peripherals",  50),
            new Product("Webcam",      79.00, "Peripherals",  25),
            new Product("Desk Chair", 349.00, "Furniture",    8),
            new Product("USB Hub",     29.99, "Peripherals",  100),
            new Product("Headphones", 199.00, "Electronics",  20),
            new Product("Mousepad",    14.99, "Peripherals",  200)
        ));

        List<Student> students = new ArrayList<>(List.of(
            new Student("Alice",   3.9, 3),
            new Student("Bob",     3.5, 2),
            new Student("Carol",   3.9, 4),
            new Student("Dave",    2.8, 1),
            new Student("Eve",     3.7, 3),
            new Student("Frank",   3.5, 2)
        ));

        System.out.println("=== SECTION 1: Comparable — Natural Ordering ===");
        demonstrateComparable(products, students);

        System.out.println("\n=== SECTION 2: Named Comparator Classes ===");
        demonstrateNamedComparators(products);

        System.out.println("\n=== SECTION 3: Lambda Comparators (Java 8+) ===");
        demonstrateLambdaComparators(products);

        System.out.println("\n=== SECTION 4: Comparator.comparing() + thenComparing() ===");
        demonstrateChainedComparators(products, students);

        System.out.println("\n=== SECTION 5: Comparators with TreeSet and TreeMap ===");
        demonstrateTreeCollectionsWithComparators(products);

        System.out.println("\n=== SECTION 6: Reversed and Null-Safe Comparators ===");
        demonstrateSpecialComparators(products);

        System.out.println("\n=== SECTION 7: Comparable vs Comparator — Summary ===");
        printSummaryTable();
    }

    // =========================================================================
    static void demonstrateComparable(List<Product> products, List<Student> students) {

        // Products have natural order = alphabetical by name (compareTo)
        List<Product> sortedProducts = new ArrayList<>(products);
        Collections.sort(sortedProducts);  // uses compareTo
        System.out.println("Products sorted by natural order (name alphabetically):");
        sortedProducts.forEach(p -> System.out.println("  " + p));

        // TreeSet automatically maintains natural order
        TreeSet<Product> productSet = new TreeSet<>(products);
        System.out.println("\nSame products in TreeSet (auto-sorted by name):");
        productSet.forEach(p -> System.out.println("  " + p.name + " → $" + p.price));

        // Students have natural order = GPA descending
        Collections.sort(students);
        System.out.println("\nStudents sorted by natural order (GPA descending):");
        students.forEach(s -> System.out.println("  " + s));

        // compareTo with Integer
        System.out.println("\nInteger natural ordering demo:");
        List<Integer> nums = new ArrayList<>(List.of(5, 2, 8, 1, 9, 3, 7));
        Collections.sort(nums);  // uses Integer.compareTo
        System.out.println("  Sorted: " + nums);

        System.out.println("\nString natural ordering demo:");
        List<String> words = new ArrayList<>(List.of("banana", "apple", "cherry", "date"));
        Collections.sort(words);  // uses String.compareTo (lexicographic)
        System.out.println("  Sorted: " + words);
    }

    // =========================================================================
    static void demonstrateNamedComparators(List<Product> products) {

        // Using named Comparator class
        List<Product> byPriceAsc = new ArrayList<>(products);
        byPriceAsc.sort(new ProductByPriceAsc());
        System.out.println("Products by price ascending:");
        byPriceAsc.forEach(p -> System.out.printf("  %-12s $%.2f%n", p.name, p.price));

        List<Product> byPriceDesc = new ArrayList<>(products);
        byPriceDesc.sort(new ProductByPriceDesc());
        System.out.println("\nProducts by price descending:");
        byPriceDesc.forEach(p -> System.out.printf("  %-12s $%.2f%n", p.name, p.price));

        // Anonymous class (one step cleaner — no need to name the class)
        List<Product> byCategoryAnon = new ArrayList<>(products);
        byCategoryAnon.sort(new Comparator<Product>() {
            @Override
            public int compare(Product a, Product b) {
                return a.category.compareTo(b.category);
            }
        });
        System.out.println("\nProducts by category (anonymous comparator):");
        byCategoryAnon.forEach(p ->
            System.out.printf("  %-12s (%s)%n", p.name, p.category));
    }

    // =========================================================================
    static void demonstrateLambdaComparators(List<Product> products) {

        // Lambda replaces the anonymous class completely
        // Comparator<Product> byPrice = (a, b) -> Double.compare(a.price, b.price);

        List<Product> byPriceLambda = new ArrayList<>(products);
        byPriceLambda.sort((a, b) -> Double.compare(a.price, b.price));
        System.out.println("By price (lambda comparator):");
        byPriceLambda.forEach(p -> System.out.printf("  %-12s $%.2f%n", p.name, p.price));

        // Comparator.comparing() — factory method that takes a key extractor
        // Even cleaner than writing the lambda manually
        List<Product> byCategory = new ArrayList<>(products);
        byCategory.sort(Comparator.comparing(p -> p.category));
        System.out.println("\nBy category (Comparator.comparing):");
        byCategory.forEach(p -> System.out.printf("  %-12s (%s)%n", p.name, p.category));

        // Method reference with Comparator.comparing — cleanest form
        List<Product> byName = new ArrayList<>(products);
        byName.sort(Comparator.comparing(p -> p.name));
        System.out.println("\nBy name (method ref style via lambda):");
        byName.forEach(p -> System.out.println("  " + p.name));

        // Reversed with .reversed()
        List<Product> byNameDesc = new ArrayList<>(products);
        Comparator<Product> byNameComp = Comparator.comparing(p -> p.name);
        byNameDesc.sort(byNameComp.reversed());
        System.out.println("\nBy name DESCENDING (.reversed()):");
        byNameDesc.forEach(p -> System.out.println("  " + p.name));
    }

    // =========================================================================
    static void demonstrateChainedComparators(List<Product> products, List<Student> students) {

        // thenComparing() — secondary sort when primary is a tie
        // Sort products: first by category, then by price within each category
        Comparator<Product> byCategoryThenPrice = Comparator
            .comparing((Product p) -> p.category)
            .thenComparingDouble(p -> p.price);

        List<Product> sorted = new ArrayList<>(products);
        sorted.sort(byCategoryThenPrice);
        System.out.println("Products: category (asc), then price (asc) within category:");
        String lastCategory = "";
        for (Product p : sorted) {
            if (!p.category.equals(lastCategory)) {
                System.out.println("  [" + p.category + "]");
                lastCategory = p.category;
            }
            System.out.printf("    %-12s $%.2f%n", p.name, p.price);
        }

        // Three-level sort: category asc → price asc → name asc
        Comparator<Product> threeLevel = Comparator
            .comparing((Product p) -> p.category)
            .thenComparingDouble(p -> p.price)
            .thenComparing(p -> p.name);

        List<Product> threeSort = new ArrayList<>(products);
        threeSort.sort(threeLevel);
        System.out.println("\nThree-level sort (category → price → name):");
        threeSort.forEach(p ->
            System.out.printf("  %-12s %-12s $%.2f%n", p.category, p.name, p.price));

        // Student sort: GPA descending, then name ascending on tie
        Comparator<Student> gpaThenName = Comparator
            .comparingDouble((Student s) -> s.gpa)
            .reversed()
            .thenComparing(s -> s.name);

        List<Student> sortedStudents = new ArrayList<>(students);
        sortedStudents.sort(gpaThenName);
        System.out.println("\nStudents: GPA descending, then name ascending (tie-break):");
        sortedStudents.forEach(s ->
            System.out.printf("  %-8s GPA=%.1f year=%d%n", s.name, s.gpa, s.year));
    }

    // =========================================================================
    static void demonstrateTreeCollectionsWithComparators(List<Product> products) {

        // TreeSet with custom Comparator (instead of natural order)
        // Sort by price ascending; if price tie, sort by name
        TreeSet<Product> priceOrdered = new TreeSet<>(
            Comparator.comparingDouble((Product p) -> p.price)
        );
        // NOTE: TreeSet uses the comparator for BOTH ordering AND duplicate detection
        // Two products with the same price would be treated as duplicates!
        // Add name as tie-breaker to avoid silent duplicate elimination:
        TreeSet<Product> priceThenName = new TreeSet<>(
            Comparator.comparingDouble((Product p) -> p.price)
                      .thenComparing(p -> p.name)
        );
        priceThenName.addAll(products);

        System.out.println("TreeSet sorted by price (then name as tie-breaker):");
        priceThenName.forEach(p ->
            System.out.printf("  $%7.2f  %-12s%n", p.price, p.name));

        // TreeMap with custom Comparator on keys — sorted by key length
        TreeMap<String, Integer> byKeyLength = new TreeMap<>(
            Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())
        );
        byKeyLength.put("Java",       1995);
        byKeyLength.put("Python",     1991);
        byKeyLength.put("JavaScript", 1995);
        byKeyLength.put("Go",         2009);
        byKeyLength.put("Rust",       2010);
        byKeyLength.put("TypeScript", 2012);

        System.out.println("\nTreeMap sorted by key length, then alphabetically:");
        byKeyLength.forEach((lang, year) ->
            System.out.printf("  %-12s (%d)%n", lang, year));
    }

    // =========================================================================
    static void demonstrateSpecialComparators(List<Product> products) {

        // Natural order reversed using Comparator.reverseOrder()
        List<String> names = new ArrayList<>(List.of("Mango","Apple","Papaya","Kiwi","Banana"));
        names.sort(Comparator.reverseOrder());  // reverses String's natural (alphabetical) order
        System.out.println("Names reverse alphabetical: " + names);

        // Null-safe comparators (real-world: database nulls, optional fields)
        List<String> withNulls = new ArrayList<>(Arrays.asList("Banana", null, "Apple", null, "Cherry"));

        // nullsFirst: nulls come before all non-null values
        withNulls.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        System.out.println("nullsFirst: " + withNulls);

        // nullsLast: nulls come after all non-null values
        withNulls.sort(Comparator.nullsLast(Comparator.naturalOrder()));
        System.out.println("nullsLast:  " + withNulls);

        // Practical: sort products by category, null categories at end
        // (if category field were nullable)
        List<Product> withNullCategory = new ArrayList<>(products.subList(0, 4));
        withNullCategory.add(new Product("Mystery Box", 9.99, null, 5));
        withNullCategory.sort(
            Comparator.comparing(p -> p.category,
                Comparator.nullsLast(Comparator.naturalOrder()))
        );
        System.out.println("\nProducts with null category sorted (nullsLast):");
        withNullCategory.forEach(p ->
            System.out.printf("  %-12s category=%s%n", p.name, p.category));
    }

    // =========================================================================
    static void printSummaryTable() {
        System.out.println("""
            ┌────────────────────────────────────────────────────────────────────────────┐
            │               Comparable  vs  Comparator                                  │
            ├──────────────────────────┬─────────────────────────────────────────────────┤
            │ Comparable               │ Comparator                                      │
            ├──────────────────────────┼─────────────────────────────────────────────────┤
            │ Implemented ON the class │ External to the class                           │
            │ java.lang.Comparable<T>  │ java.util.Comparator<T>                         │
            │ int compareTo(T other)   │ int compare(T a, T b)                           │
            │ One ordering per class   │ Many orderings for same class                   │
            │ "Natural" ordering       │ Custom ordering                                 │
            │ Works by default with    │ Must be passed explicitly to                    │
            │ sort, TreeSet, TreeMap   │ sort(), TreeSet(comp), TreeMap(comp)            │
            │ Best when one sort order │ Best when multiple sort orders needed           │
            │ makes sense for the type │ or class is not yours to modify                 │
            │ e.g. String, Integer,    │ e.g. Sort Employee by dept,                    │
            │      LocalDate           │      then salary, then name                    │
            └──────────────────────────┴─────────────────────────────────────────────────┘

            Modern Comparator API (Java 8+):
              Comparator.comparing(Person::getName)
                .thenComparingInt(Person::getAge)
                .reversed()
                .thenComparing(Comparator.nullsLast(Comparator.naturalOrder()))
            """);
    }
}
