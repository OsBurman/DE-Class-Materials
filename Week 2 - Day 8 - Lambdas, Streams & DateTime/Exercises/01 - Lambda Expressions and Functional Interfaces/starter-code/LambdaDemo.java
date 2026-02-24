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
        // PART 1: Predicate<T> — tests a condition, returns boolean
        // ============================================================
        System.out.println("=== Predicate<T> ===");

        // TODO: Create Predicate<String> isLongName — true if string length > 5
        // Predicate<String> isLongName = ...

        // TODO: Test with "Alice" and "Alexander", print:
        //   "Alice is long name: [result]"
        //   "Alexander is long name: [result]"


        // TODO: Create Predicate<Integer> isEven — true if number % 2 == 0
        // Predicate<Integer> isEven = ...

        // TODO: Use isEven.negate() to create isOdd
        // TODO: Test both with 4 and 7, printing:
        //   "4 is even: [result]  |  4 is odd: [result]"
        //   "7 is even: [result]  |  7 is odd: [result]"


        System.out.println();

        // ============================================================
        // PART 2: Function<T, R> — transforms T into R
        // ============================================================
        System.out.println("=== Function<T, R> ===");

        // TODO: Create Function<String, Integer> nameLength — returns string.length()
        // Function<String, Integer> nameLength = ...

        // TODO: Create Function<Integer, String> starRating — returns "★" repeated n times
        //       Hint: "★".repeat(n)
        // Function<Integer, String> starRating = ...

        // TODO: Print nameLength applied to "Bob" and "Christina":
        //   "Length of \"Bob\": 3"   etc.

        // TODO: Compose nameLength.andThen(starRating) into a single Function
        //       Apply to "Bob" and "Christina", printing:
        //   "Bob -> ★★★"
        //   "Christina -> ★★★★★★★★★"


        System.out.println();

        // ============================================================
        // PART 3: Consumer<T> — accepts a value, returns nothing
        // ============================================================
        System.out.println("=== Consumer<T> ===");

        // TODO: Create Consumer<String> printUpperCase — prints s.toUpperCase()
        // Consumer<String> printUpperCase = ...

        // TODO: Create Consumer<String> printLength — prints "Length: " + s.length()
        // Consumer<String> printLength = ...

        // TODO: Chain them with printUpperCase.andThen(printLength), apply to "lambda"


        System.out.println();

        // ============================================================
        // PART 4: Supplier<T> — no input, produces a value
        // ============================================================
        System.out.println("=== Supplier<T> ===");

        // TODO: Create Supplier<String> greeting that returns "Hello, Java 8!"
        // Supplier<String> greeting = ...

        // TODO: Create Supplier<Double> randomScore that returns Math.random() * 100
        // Supplier<Double> randomScore = ...

        // TODO: Print greeting.get()
        // TODO: Print "Random score: " + randomScore.get()


        System.out.println();

        // ============================================================
        // PART 5: Lambda in sort
        // ============================================================
        System.out.println("=== Lambda in sort ===");

        List<String> fruits = new ArrayList<>(Arrays.asList("Banana", "Apple", "Cherry", "Date"));

        // TODO: Sort fruits using list.sort() with a lambda Comparator
        //       Primary: by length ascending
        //       Secondary: alphabetically (for same-length strings)
        //       Hint: Integer.compare(a.length(), b.length()) then String.compareTo()

        // TODO: Print "Sorted by length then alpha: " + fruits
    }
}
