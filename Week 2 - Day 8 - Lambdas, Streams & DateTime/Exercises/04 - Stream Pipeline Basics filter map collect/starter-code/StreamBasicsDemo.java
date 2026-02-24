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
        // TODO: Print "List stream count: " + seasons.stream().count()

        int[] scores = {85, 92, 78};
        // TODO: Print "Array stream count: " + Arrays.stream(scores).count()

        // TODO: Print "Stream.of count: " + Stream.of("a", "b", "c").count()


        System.out.println();

        // ============================================================
        // PART 2: filter + collect
        // ============================================================
        System.out.println("=== filter + collect ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // TODO: Use numbers.stream().filter(...).collect(Collectors.toList())
        //       to collect all even numbers into a List<Integer>
        //       Print "Even numbers: " + evenNumbers


        // TODO: Collect all numbers > 5 and print "Numbers > 5: " + result


        System.out.println();

        // ============================================================
        // PART 3: map + collect
        // ============================================================
        System.out.println("=== map + collect ===");

        List<String> names = Arrays.asList("alice", "bob", "carol", "dave", "eve");

        // TODO: Map each name to uppercase, collect to list
        //       Print "Uppercase names: " + result


        // TODO: Map each name to its length, collect to List<Integer>
        //       Print "Name lengths: " + result


        System.out.println();

        // ============================================================
        // PART 4: filter + map chained
        // ============================================================
        System.out.println("=== filter + map chained ===");

        List<String> emails = Arrays.asList(
                "alice@example.com", "invalid-email",
                "bob@example.com", "not-an-email", "carol@example.com");

        // TODO: Filter emails that contain "@", then map to extract the part before "@"
        //       (Hint: s.split("@")[0])
        //       Collect to list and print "Email usernames: " + result


        System.out.println();

        // ============================================================
        // PART 5: sorted and distinct
        // ============================================================
        System.out.println("=== sorted and distinct ===");

        List<Integer> dupes = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);

        // TODO: distinct() then sorted(), collect to list
        //       Print "Unique sorted: " + result


        // TODO: distinct() then sorted(Comparator.reverseOrder()), collect to list
        //       Print "Unique reverse sorted: " + result

    }
}
