import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaDemo {

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Predicate<T>
        // ============================================================
        System.out.println("=== Predicate<T> ===");

        // Lambda body is a single expression — no braces or return needed
        Predicate<String> isLongName = s -> s.length() > 5;

        System.out.println("Alice is long name: " + isLongName.test("Alice"));
        System.out.println("Alexander is long name: " + isLongName.test("Alexander"));

        Predicate<Integer> isEven = n -> n % 2 == 0;
        // negate() returns a new Predicate that inverts the result — no new lambda needed
        Predicate<Integer> isOdd = isEven.negate();

        System.out.println("4 is even: " + isEven.test(4) + "  |  4 is odd: " + isOdd.test(4));
        System.out.println("7 is even: " + isEven.test(7) + "  |  7 is odd: " + isOdd.test(7));
        System.out.println();

        // ============================================================
        // PART 2: Function<T, R>
        // ============================================================
        System.out.println("=== Function<T, R> ===");

        Function<String, Integer> nameLength = s -> s.length();
        // String.repeat() returns a string repeated n times
        Function<Integer, String> starRating = n -> "★".repeat(n);

        System.out.println("Length of \"Bob\": " + nameLength.apply("Bob"));
        System.out.println("Length of \"Christina\": " + nameLength.apply("Christina"));

        // andThen: nameLength runs first, then its result is fed into starRating
        Function<String, String> nameToStars = nameLength.andThen(starRating);
        System.out.println("Bob -> " + nameToStars.apply("Bob"));
        System.out.println("Christina -> " + nameToStars.apply("Christina"));
        System.out.println();

        // ============================================================
        // PART 3: Consumer<T>
        // ============================================================
        System.out.println("=== Consumer<T> ===");

        Consumer<String> printUpperCase = s -> System.out.println(s.toUpperCase());
        Consumer<String> printLength = s -> System.out.println("Length: " + s.length());

        // andThen chains consumers — both receive the same original argument
        printUpperCase.andThen(printLength).accept("lambda");
        System.out.println();

        // ============================================================
        // PART 4: Supplier<T>
        // ============================================================
        System.out.println("=== Supplier<T> ===");

        Supplier<String> greeting = () -> "Hello, Java 8!";
        Supplier<Double> randomScore = () -> Math.random() * 100;

        System.out.println(greeting.get());
        System.out.println("Random score: " + randomScore.get());
        System.out.println();

        // ============================================================
        // PART 5: Lambda in sort
        // ============================================================
        System.out.println("=== Lambda in sort ===");

        List<String> fruits = new ArrayList<>(Arrays.asList("Banana", "Apple", "Cherry", "Date"));

        // Multi-key comparator as a lambda: sort by length, break ties alphabetically
        fruits.sort((a, b) -> {
            int lenCmp = Integer.compare(a.length(), b.length());
            return lenCmp != 0 ? lenCmp : a.compareTo(b);
        });

        System.out.println("Sorted by length then alpha: " + fruits);
    }
}
