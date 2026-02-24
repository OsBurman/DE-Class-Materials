import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamBasicsDemo {

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Stream creation
        // ============================================================
        System.out.println("=== Stream Creation ===");

        List<String> seasons = Arrays.asList("Spring", "Summer", "Autumn", "Winter");
        // count() is a terminal operation — triggers the pipeline
        System.out.println("List stream count: " + seasons.stream().count());

        int[] scores = {85, 92, 78};
        // Arrays.stream works on primitive arrays too — returns IntStream
        System.out.println("Array stream count: " + Arrays.stream(scores).count());

        // Stream.of creates a stream directly from varargs
        System.out.println("Stream.of count: " + Stream.of("a", "b", "c").count());
        System.out.println();

        // ============================================================
        // PART 2: filter + collect
        // ============================================================
        System.out.println("=== filter + collect ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // filter keeps only elements for which the predicate returns true
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("Even numbers: " + evens);

        List<Integer> greaterThan5 = numbers.stream()
                .filter(n -> n > 5)
                .collect(Collectors.toList());
        System.out.println("Numbers > 5: " + greaterThan5);
        System.out.println();

        // ============================================================
        // PART 3: map + collect
        // ============================================================
        System.out.println("=== map + collect ===");

        List<String> names = Arrays.asList("alice", "bob", "carol", "dave", "eve");

        // map transforms each element — String → String here
        List<String> uppercased = names.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("Uppercase names: " + uppercased);

        // map can change the type — String → Integer
        List<Integer> lengths = names.stream()
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println("Name lengths: " + lengths);
        System.out.println();

        // ============================================================
        // PART 4: filter + map chained
        // ============================================================
        System.out.println("=== filter + map chained ===");

        List<String> emails = Arrays.asList(
                "alice@example.com", "invalid-email",
                "bob@example.com", "not-an-email", "carol@example.com");

        // filter first — reduces the set before map does any work
        List<String> usernames = emails.stream()
                .filter(s -> s.contains("@"))
                .map(s -> s.split("@")[0])   // extract username part
                .collect(Collectors.toList());
        System.out.println("Email usernames: " + usernames);
        System.out.println();

        // ============================================================
        // PART 5: sorted and distinct
        // ============================================================
        System.out.println("=== sorted and distinct ===");

        List<Integer> dupes = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);

        // distinct removes duplicates (uses equals()); sorted uses natural order
        List<Integer> uniqueSorted = dupes.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Unique sorted: " + uniqueSorted);

        List<Integer> uniqueReversed = dupes.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        System.out.println("Unique reverse sorted: " + uniqueReversed);
    }
}
