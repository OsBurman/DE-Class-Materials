import java.util.*;
import java.util.stream.*;

public class StreamTerminalDemo {

    public static void main(String[] args) {
        List<Integer> scores = Arrays.asList(85, 92, 78, 95, 88);

        // ── Part 1: reduce ──────────────────────────────────────────────────
        System.out.println("=== reduce ===");

        int sum = scores.stream()
                        .reduce(0, Integer::sum);
        System.out.println("Sum: " + sum);

        int max = scores.stream()
                        .reduce(Integer::max)
                        .orElse(0);
        System.out.println("Max: " + max);

        long product = scores.stream()
                             .mapToLong(Integer::longValue)
                             .reduce(1L, (a, b) -> a * b);
        System.out.printf("Product: %,d%n", product);

        // ── Part 2: count, min, max, findFirst, match ───────────────────────
        System.out.println("\n=== count / min / max / findFirst / match ===");

        long countAbove85 = scores.stream()
                                  .filter(n -> n > 85)
                                  .count();
        System.out.println("Count > 85: " + countAbove85);

        int minScore = scores.stream()
                             .min(Comparator.naturalOrder())
                             .orElse(0);
        System.out.println("Min score: " + minScore);

        int maxScore = scores.stream()
                             .max(Comparator.naturalOrder())
                             .orElse(0);
        System.out.println("Max score: " + maxScore);

        int firstAbove90 = scores.stream()
                                 .filter(n -> n > 90)
                                 .findFirst()
                                 .orElse(-1);
        System.out.println("First score > 90: " + firstAbove90);

        boolean anyBelow70 = scores.stream().anyMatch(n -> n < 70);
        System.out.println("Any score < 70: " + anyBelow70);

        boolean allAtLeast70 = scores.stream().allMatch(n -> n >= 70);
        System.out.println("All scores >= 70: " + allAtLeast70);

        boolean noneAbove100 = scores.stream().noneMatch(n -> n > 100);
        System.out.println("No score > 100: " + noneAbove100);

        // ── Part 3: Collectors.joining ─────────────────────────────────────
        List<String> words = Arrays.asList("Java", "Streams", "Are", "Powerful");
        System.out.println("\n=== Collectors.joining ===");

        String joined = words.stream()
                             .collect(Collectors.joining(", "));
        System.out.println("Joined: " + joined);

        String wrapped = words.stream()
                              .collect(Collectors.joining(" | ", "[", "]"));
        System.out.println("Wrapped: " + wrapped);

        // ── Part 4: Collectors.groupingBy ──────────────────────────────────
        System.out.println("\n=== Collectors.groupingBy ===");

        Map<Integer, List<String>> byLength = words.stream()
                .collect(Collectors.groupingBy(String::length));

        // Print in a deterministic key order
        new TreeMap<>(byLength).forEach((len, group) ->
                System.out.println(len + " -> " + group));

        // ── Part 5: Collectors.toMap ───────────────────────────────────────
        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
        System.out.println("\n=== Collectors.toMap ===");

        Map<String, Integer> fruitLengths = fruits.stream()
                .collect(Collectors.toMap(s -> s, String::length));

        fruitLengths.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));
    }
}
