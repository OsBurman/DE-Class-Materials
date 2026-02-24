import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * DAY 8 — PART 2 | Stream API: Basics and Operations
 * ─────────────────────────────────────────────────────────────────────────────
 * A Stream<T> is a sequence of elements that supports functional-style
 * operations. It is NOT a data structure — it does not store data.
 *
 * KEY CONCEPTS:
 *  1. Streams are LAZY — intermediate operations do nothing until a terminal
 *     operation is invoked.
 *  2. Streams are SINGLE-USE — once consumed by a terminal operation, they
 *     cannot be reused.
 *  3. Streams leave the source unchanged — the original collection is untouched.
 *
 * PIPELINE STRUCTURE:
 *   source → [intermediate op]* → terminal op
 *
 * OPERATION TYPES:
 * ┌────────────────┬──────────────────────────────────────────────────────────┐
 * │ Intermediate   │ Lazy. Returns a new Stream. Zero or more in a pipeline.  │
 * │ (transforms)   │ filter, map, flatMap, distinct, sorted, peek, limit, skip│
 * ├────────────────┼──────────────────────────────────────────────────────────┤
 * │ Terminal       │ Eager. Triggers execution. Exactly ONE per pipeline.      │
 * │ (consumes)     │ collect, forEach, reduce, count, min, max, findFirst,     │
 * │                │ findAny, anyMatch, allMatch, noneMatch, toArray, toList    │
 * └────────────────┴──────────────────────────────────────────────────────────┘
 */
public class StreamApiBasicsAndOperations {

    public static void main(String[] args) {
        demonstrateStreamCreation();
        demonstrateIntermediateOperations();
        demonstrateTerminalOperations();
        demonstrateCollectors();
        demonstratePrimitiveStreams();
        demonstrateRealWorldPipelines();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Creating Streams
    // Multiple ways to create a stream from different sources
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateStreamCreation() {
        System.out.println("=== Stream Creation ===");

        // ----- 1a. Stream.of() — from individual elements -----
        Stream<String> fromValues = Stream.of("Alice", "Bob", "Charlie");
        fromValues.forEach(System.out::println);

        // ----- 1b. Collection.stream() — from a list or set -----
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Stream<Integer> fromList = numbers.stream();
        System.out.println("Sum: " + fromList.mapToInt(Integer::intValue).sum());

        // ----- 1c. Arrays.stream() — from an array -----
        String[] namesArray = {"Diana", "Eve", "Frank"};
        Stream<String> fromArray = Arrays.stream(namesArray);
        fromArray.map(String::toUpperCase).forEach(System.out::println);

        // ----- 1d. Stream.generate(Supplier) — infinite stream -----
        // Must be limited — generate produces values indefinitely
        Stream<Double> randoms = Stream.generate(Math::random);
        System.out.println("5 random doubles:");
        randoms.limit(5).forEach(d -> System.out.printf("  %.4f%n", d));

        // Generate IDs
        int[] counter = {1000};
        Stream<String> ids = Stream.generate(() -> "ID-" + counter[0]++);
        List<String> fiveIds = ids.limit(5).collect(Collectors.toList());
        System.out.println("Generated IDs: " + fiveIds);

        // ----- 1e. Stream.iterate(seed, UnaryOperator) — structured infinite stream -----
        // Produces: seed, f(seed), f(f(seed)), ...
        Stream<Integer> evens = Stream.iterate(0, n -> n + 2);
        System.out.println("First 6 even numbers:");
        evens.limit(6).forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Powers of 2
        Stream<Long> powersOfTwo = Stream.iterate(1L, n -> n * 2);
        System.out.println("Powers of 2 up to 1024:");
        powersOfTwo.limit(11).forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Java 9+ — Stream.iterate with a predicate (like a for loop)
        Stream.iterate(1, n -> n <= 100, n -> n * 2)
              .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // ----- 1f. IntStream.range / rangeClosed -----
        System.out.println("range(1, 6):       ");
        IntStream.range(1, 6).forEach(n -> System.out.print(n + " "));   // 1 2 3 4 5
        System.out.println();
        System.out.println("rangeClosed(1, 5): ");
        IntStream.rangeClosed(1, 5).forEach(n -> System.out.print(n + " ")); // 1 2 3 4 5
        System.out.println();

        // ----- 1g. String.chars() — stream of characters -----
        "Hello".chars()
               .forEach(c -> System.out.print((char) c + " "));
        System.out.println();

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Intermediate Operations (LAZY)
    // Each returns a new Stream — nothing runs until a terminal op is called
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateIntermediateOperations() {
        System.out.println("=== Intermediate Operations ===");

        List<String> words = Arrays.asList(
                "stream", "lambda", "java", "functional", "stream", "api", "java", "optional"
        );
        List<Integer> numbers = Arrays.asList(5, 3, 8, 1, 9, 2, 7, 4, 6, 10, 3, 7);

        // ----- 2a. filter(Predicate) — keep elements that match -----
        System.out.println("filter — words longer than 5 chars:");
        words.stream()
             .filter(w -> w.length() > 5)
             .forEach(w -> System.out.print(w + " "));
        System.out.println();

        // ----- 2b. map(Function) — transform each element -----
        System.out.println("map — to uppercase:");
        words.stream()
             .map(String::toUpperCase)
             .forEach(w -> System.out.print(w + " "));
        System.out.println();

        // map with a different output type:
        System.out.println("map — to word lengths:");
        words.stream()
             .map(String::length)
             .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // ----- 2c. flatMap(Function<T, Stream<R>>) — flatten nested streams -----
        // Use case: each element maps to multiple elements
        List<List<Integer>> nestedLists = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5),
                Arrays.asList(6, 7, 8, 9)
        );
        System.out.println("flatMap — flatten nested lists:");
        nestedLists.stream()
                   .flatMap(Collection::stream)   // each List<Integer> -> Stream<Integer>
                   .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // flatMap with strings — split sentences into words
        List<String> sentences = Arrays.asList("hello world", "streams are fun", "java rocks");
        System.out.println("flatMap — all words from sentences:");
        sentences.stream()
                 .flatMap(s -> Arrays.stream(s.split(" ")))
                 .forEach(w -> System.out.print(w + " "));
        System.out.println();

        // ----- 2d. distinct() — remove duplicates -----
        System.out.println("distinct:");
        words.stream()
             .distinct()
             .forEach(w -> System.out.print(w + " "));
        System.out.println();

        // ----- 2e. sorted() — natural order; sorted(Comparator) — custom order -----
        System.out.println("sorted (natural):");
        words.stream()
             .distinct()
             .sorted()
             .forEach(w -> System.out.print(w + " "));
        System.out.println();

        System.out.println("sorted (by length):");
        words.stream()
             .distinct()
             .sorted(Comparator.comparingInt(String::length))
             .forEach(w -> System.out.print(w + " "));
        System.out.println();

        // ----- 2f. peek(Consumer) — side-effect without transforming; great for debugging -----
        System.out.println("peek — trace through pipeline:");
        List<Integer> result = numbers.stream()
                .filter(n -> n > 5)
                .peek(n -> System.out.print("[after filter: " + n + "] "))
                .map(n -> n * 2)
                .peek(n -> System.out.print("[after map: " + n + "] "))
                .collect(Collectors.toList());
        System.out.println("\nfinal: " + result);

        // ----- 2g. limit(n) — take at most n elements -----
        System.out.println("limit(4):");
        numbers.stream()
               .limit(4)
               .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // ----- 2h. skip(n) — skip the first n elements -----
        System.out.println("skip(3):");
        numbers.stream()
               .skip(3)
               .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // limit + skip = pagination
        int page = 1;        // 0-indexed
        int pageSize = 3;
        System.out.println("Page " + page + " (size " + pageSize + "):");
        numbers.stream()
               .skip((long) page * pageSize)
               .limit(pageSize)
               .forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Terminal Operations (EAGER — trigger the pipeline)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateTerminalOperations() {
        System.out.println("=== Terminal Operations ===");

        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve");
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);

        // ----- 3a. forEach — consume each element -----
        System.out.println("forEach:");
        names.stream().forEach(System.out::println);

        // ----- 3b. count() — number of elements -----
        long count = names.stream().filter(n -> n.length() > 3).count();
        System.out.println("Names longer than 3 chars: " + count);  // 3

        // ----- 3c. min / max with Comparator -----
        Optional<String> shortest = names.stream()
                .min(Comparator.comparingInt(String::length));
        System.out.println("Shortest name: " + shortest.orElse("none"));  // Bob or Eve

        Optional<Integer> max = numbers.stream().max(Integer::compare);
        System.out.println("Max number: " + max.orElse(0));  // 9

        // ----- 3d. findFirst / findAny -----
        Optional<String> firstLong = names.stream()
                .filter(n -> n.length() > 4)
                .findFirst();  // deterministic — always first in encounter order
        System.out.println("findFirst (>4 chars): " + firstLong);  // Optional[Alice]

        Optional<String> anyLong = names.stream()
                .filter(n -> n.length() > 4)
                .findAny();    // non-deterministic in parallel streams; sequential: same as findFirst
        System.out.println("findAny (>4 chars): " + anyLong);

        // ----- 3e. anyMatch / allMatch / noneMatch -----
        boolean anyStartsA = names.stream().anyMatch(n -> n.startsWith("A"));
        boolean allStartsA = names.stream().allMatch(n -> n.startsWith("A"));
        boolean noneStartZ = names.stream().noneMatch(n -> n.startsWith("Z"));

        System.out.println("anyMatch(A): " + anyStartsA);  // true
        System.out.println("allMatch(A): " + allStartsA);  // false
        System.out.println("noneMatch(Z): " + noneStartZ); // true

        // ----- 3f. reduce — fold all elements into one value -----
        // reduce(identity, BinaryOperator) — starts with identity, combines elements
        int sum = numbers.stream()
                .reduce(0, Integer::sum);  // 0 + 3 + 1 + 4 + ... = 44
        System.out.println("Sum (reduce): " + sum);

        int product = numbers.stream()
                .reduce(1, (a, b) -> a * b);
        System.out.println("Product (reduce): " + product);

        // reduce without identity returns Optional (stream might be empty)
        Optional<Integer> max2 = numbers.stream()
                .reduce(Integer::max);
        System.out.println("Max (reduce): " + max2);  // Optional[9]

        // String concatenation with reduce
        String joined = names.stream()
                .reduce("", (acc, s) -> acc.isEmpty() ? s : acc + ", " + s);
        System.out.println("Joined (reduce): " + joined);  // Alice, Bob, ...

        // ----- 3g. collect — gather into a collection (see Section 4) -----
        List<String> longNames = names.stream()
                .filter(n -> n.length() > 3)
                .collect(Collectors.toList());
        System.out.println("Long names list: " + longNames);

        // ----- 3h. toList() — Java 16+ unmodifiable list (no Collectors needed) -----
        List<String> immutableList = names.stream()
                .filter(n -> n.length() > 3)
                .toList();   // Java 16+
        System.out.println("toList() (Java 16+): " + immutableList);

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Collectors (the most powerful terminal collector)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCollectors() {
        System.out.println("=== Collectors ===");

        List<Product> products = Arrays.asList(
                new Product("Laptop",    "Electronics", 999.99),
                new Product("Phone",     "Electronics", 699.99),
                new Product("Desk",      "Furniture",   349.99),
                new Product("Chair",     "Furniture",   249.99),
                new Product("Keyboard",  "Electronics", 129.99),
                new Product("Notebook",  "Stationery",   4.99),
                new Product("Pen",       "Stationery",   1.99)
        );

        // ----- 4a. toList, toSet, toUnmodifiableList -----
        List<String> nameList  = products.stream().map(Product::getName).collect(Collectors.toList());
        Set<String>  nameSet   = products.stream().map(Product::getCategory).collect(Collectors.toSet());
        List<String> nameImm   = products.stream().map(Product::getName).collect(Collectors.toUnmodifiableList());
        System.out.println("Names:       " + nameList);
        System.out.println("Categories:  " + nameSet);

        // ----- 4b. joining -----
        String csv = products.stream()
                .map(Product::getName)
                .collect(Collectors.joining(", "));
        System.out.println("CSV: " + csv);

        String withBrackets = products.stream()
                .map(Product::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Brackets: " + withBrackets);

        // ----- 4c. counting -----
        long count = products.stream().collect(Collectors.counting());
        System.out.println("Total products: " + count);

        // ----- 4d. groupingBy — the workhorse -----
        // Group products by category → Map<String, List<Product>>
        Map<String, List<Product>> byCategory = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        byCategory.forEach((cat, prods) -> {
            System.out.print(cat + ": ");
            prods.forEach(p -> System.out.print(p.getName() + " "));
            System.out.println();
        });

        // groupingBy with downstream collector — count per category
        Map<String, Long> countByCategory = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));
        System.out.println("Count by category: " + countByCategory);

        // groupingBy with downstream collector — sum price per category
        Map<String, Double> totalByCategory = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.summingDouble(Product::getPrice)
                ));
        System.out.println("Total price by category: " + totalByCategory);

        // ----- 4e. summarizingInt / summarizingDouble -----
        DoubleSummaryStatistics stats = products.stream()
                .collect(Collectors.summarizingDouble(Product::getPrice));
        System.out.printf("Price stats — min=%.2f, max=%.2f, avg=%.2f, sum=%.2f, count=%d%n",
                stats.getMin(), stats.getMax(), stats.getAverage(),
                stats.getSum(), stats.getCount());

        // ----- 4f. partitioningBy — split into two groups (true/false) -----
        Map<Boolean, List<Product>> expensiveVsCheap = products.stream()
                .collect(Collectors.partitioningBy(p -> p.getPrice() > 100));
        System.out.println("Expensive (>100): " +
                expensiveVsCheap.get(true).stream().map(Product::getName).collect(Collectors.toList()));
        System.out.println("Cheap     (≤100): " +
                expensiveVsCheap.get(false).stream().map(Product::getName).collect(Collectors.toList()));

        // ----- 4g. toMap -----
        // Create a Map<name, price> for quick lookup
        Map<String, Double> priceMap = products.stream()
                .collect(Collectors.toMap(Product::getName, Product::getPrice));
        System.out.println("Laptop price: " + priceMap.get("Laptop"));  // 999.99

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Primitive Streams (IntStream, LongStream, DoubleStream)
    // Use these to avoid boxing overhead and to access numeric aggregation
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstratePrimitiveStreams() {
        System.out.println("=== Primitive Streams ===");

        // IntStream
        int sum     = IntStream.rangeClosed(1, 100).sum();
        double avg  = IntStream.rangeClosed(1, 100).average().orElse(0.0);
        int max     = IntStream.of(3, 1, 4, 1, 5, 9).max().orElse(0);
        System.out.println("Sum 1-100: " + sum);        // 5050
        System.out.println("Avg 1-100: " + avg);        // 50.5
        System.out.println("Max:       " + max);        // 9

        // mapToInt — go from Stream<T> to IntStream (avoids boxing)
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date");
        IntStream lengths = words.stream().mapToInt(String::length);
        System.out.println("Total chars: " + lengths.sum());  // 5+6+6+4 = 21

        // boxed() — convert back to Stream<Integer>
        List<Integer> boxed = IntStream.range(1, 6)
                .boxed()
                .collect(Collectors.toList());
        System.out.println("Boxed: " + boxed);  // [1, 2, 3, 4, 5]

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — Real-World Pipelines
    // Combining multiple operations in realistic scenarios
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateRealWorldPipelines() {
        System.out.println("=== Real-World Pipelines ===");

        List<Employee> employees = Arrays.asList(
                new Employee("Alice",   "Engineering", 90_000, true),
                new Employee("Bob",     "Marketing",   65_000, true),
                new Employee("Charlie", "Engineering", 80_000, false),
                new Employee("Diana",   "HR",          60_000, true),
                new Employee("Eve",     "Engineering", 95_000, true),
                new Employee("Frank",   "Marketing",   70_000, false),
                new Employee("Grace",   "Engineering", 85_000, true)
        );

        // Pipeline 1: Top 3 active Engineering salaries
        System.out.println("Top 3 active Engineering salaries:");
        employees.stream()
                 .filter(Employee::isActive)
                 .filter(e -> "Engineering".equals(e.getDepartment()))
                 .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                 .limit(3)
                 .map(e -> e.getName() + " ($" + String.format("%.0f", e.getSalary()) + ")")
                 .forEach(System.out::println);

        // Pipeline 2: Average salary by department (active only)
        System.out.println("\nAverage salary by department (active):");
        employees.stream()
                 .filter(Employee::isActive)
                 .collect(Collectors.groupingBy(
                         Employee::getDepartment,
                         Collectors.averagingDouble(Employee::getSalary)
                 ))
                 .forEach((dept, avg) ->
                         System.out.printf("  %-15s $%.0f%n", dept, avg));

        // Pipeline 3: Comma-separated list of names per department
        System.out.println("\nNames by department:");
        employees.stream()
                 .collect(Collectors.groupingBy(
                         Employee::getDepartment,
                         Collectors.mapping(Employee::getName, Collectors.joining(", "))
                 ))
                 .forEach((dept, names) -> System.out.println("  " + dept + ": " + names));

        // Pipeline 4: Word frequency from a paragraph
        String paragraph = "to be or not to be that is the question to be";
        System.out.println("\nWord frequency:");
        Arrays.stream(paragraph.split(" "))
              .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
              .entrySet().stream()
              .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
              .forEach(e -> System.out.printf("  %-10s %d%n", e.getKey(), e.getValue()));

        // Pipeline 5: Check if any/all conditions hold
        boolean anyHighEarner  = employees.stream().anyMatch(e -> e.getSalary() > 90_000);
        boolean allAboveMinWage = employees.stream().allMatch(e -> e.getSalary() > 30_000);
        System.out.println("\nAny earning > 90k: " + anyHighEarner);    // true
        System.out.println("All earning > 30k: " + allAboveMinWage);    // true

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INNER CLASSES
    // ─────────────────────────────────────────────────────────────────────────
    static class Product {
        private final String name;
        private final String category;
        private final double price;

        Product(String name, String category, double price) {
            this.name     = name;
            this.category = category;
            this.price    = price;
        }

        String getName()     { return name; }
        String getCategory() { return category; }
        double getPrice()    { return price; }

        @Override
        public String toString() {
            return name + "($" + String.format("%.2f", price) + ")";
        }
    }

    static class Employee {
        private final String  name;
        private final String  department;
        private final double  salary;
        private final boolean active;

        Employee(String name, String department, double salary, boolean active) {
            this.name       = name;
            this.department = department;
            this.salary     = salary;
            this.active     = active;
        }

        String  getName()       { return name; }
        String  getDepartment() { return department; }
        double  getSalary()     { return salary; }
        boolean isActive()      { return active; }

        @Override
        public String toString() { return name + "(" + department + ")"; }
    }
}
