import java.util.*;
import java.util.stream.*;

public class StreamTerminalDemo {

    public static void main(String[] args) {
        List<Integer> scores = Arrays.asList(85, 92, 78, 95, 88);

        // ── Part 1: reduce ──────────────────────────────────────────────────
        System.out.println("=== reduce ===");

        // TODO: Use reduce(0, Integer::sum) to compute and print the sum


        // TODO: Use reduce(Integer::max) — no identity, so returns Optional<Integer>
        //       Print "Max: <value>" using orElse(0)


        // TODO: Use reduce(1, (a, b) -> a * b) to compute and print the product


        // ── Part 2: count, min, max, findFirst, match ───────────────────────
        System.out.println("\n=== count / min / max / findFirst / match ===");

        // TODO: Use count() after filter(n -> n > 85) and print "Count > 85: <n>"


        // TODO: Use min(Comparator.naturalOrder()) and print "Min score: <value>"


        // TODO: Use max(Comparator.naturalOrder()) and print "Max score: <value>"


        // TODO: Use filter(n -> n > 90).findFirst() and print "First score > 90: <value>"


        // TODO: Use anyMatch(n -> n < 70) and print "Any score < 70: <result>"


        // TODO: Use allMatch(n -> n >= 70) and print "All scores >= 70: <result>"


        // TODO: Use noneMatch(n -> n > 100) and print "No score > 100: <result>"


        // ── Part 3: Collectors.joining ─────────────────────────────────────
        List<String> words = Arrays.asList("Java", "Streams", "Are", "Powerful");
        System.out.println("\n=== Collectors.joining ===");

        // TODO: Join words with ", " using Collectors.joining and print "Joined: <result>"


        // TODO: Join with " | ", prefix "[", suffix "]" and print "Wrapped: <result>"


        // ── Part 4: Collectors.groupingBy ──────────────────────────────────
        System.out.println("\n=== Collectors.groupingBy ===");

        // TODO: Group words by String::length using Collectors.groupingBy
        //       Store as Map<Integer, List<String>> and print the map


        // ── Part 5: Collectors.toMap ───────────────────────────────────────
        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
        System.out.println("\n=== Collectors.toMap ===");

        // TODO: Collect fruits into Map<String, Integer> of name → length
        //       using Collectors.toMap(s -> s, String::length)
        //       Print each entry as "FruitName -> length" using entrySet().stream()
        //       sorted by key and forEach
    }
}
