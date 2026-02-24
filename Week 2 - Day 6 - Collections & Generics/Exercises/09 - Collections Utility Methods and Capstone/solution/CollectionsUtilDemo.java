import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CollectionsUtilDemo {

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Core utility methods
        // ============================================================
        System.out.println("=== Collections Utility Methods ===");

        ArrayList<Integer> nums = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3));

        Collections.sort(nums);
        System.out.println("After sort:    " + nums);

        Collections.reverse(nums);
        System.out.println("After reverse: " + nums);

        // Seeded Random makes shuffle deterministic — same output every run
        Collections.shuffle(nums, new Random(42));
        System.out.println("After shuffle: " + nums);

        // min/max scan the entire collection — O(n)
        System.out.println("Min: " + Collections.min(nums) + "  |  Max: " + Collections.max(nums));

        // frequency() counts elements using .equals() — O(n)
        System.out.println("Frequency of 5: " + Collections.frequency(nums, 5));
        System.out.println();

        // ============================================================
        // PART 2: Unmodifiable views and filled lists
        // ============================================================
        System.out.println("=== Unmodifiable and Filled Lists ===");

        // unmodifiableList returns a WRAPPER — reads are live; writes are blocked
        List<Integer> readOnly = Collections.unmodifiableList(nums);
        try {
            readOnly.add(99);   // throws UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            // Print class name + message (message is null for UnsupportedOperationException,
            // so we use the class name to show students what was thrown)
            System.out.println("Unmodifiable list caught: " + e.getClass().getName());
        }

        // nCopies creates an immutable fixed-size list — useful for pre-filling or testing
        List<String> copies = Collections.nCopies(5, "Java");
        System.out.println("nCopies(5, \"Java\"): " + copies);

        // fill overwrites every element in a mutable list — list size stays the same
        ArrayList<String> slots = new ArrayList<>(Arrays.asList("_", "_", "_", "_", "_"));
        Collections.fill(slots, "done");
        System.out.println("After fill with \"done\": " + slots);
        System.out.println();

        // ============================================================
        // PART 3: Capstone — Word Frequency Counter
        // ============================================================
        System.out.println("=== Capstone: Word Frequency Counter ===");

        String sentence = "the quick brown fox jumps over the lazy dog the fox";
        String[] words = sentence.split(" ");

        // Build frequency map using getOrDefault — avoids a null check
        HashMap<String, Integer> freqMap = new HashMap<>();
        for (String word : words) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }

        // Collect entries into a mutable list so we can sort them
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(freqMap.entrySet());

        // Sort: primary = value descending, secondary = key ascending (alphabetical tie-break)
        entries.sort(
            Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                     .thenComparing(Map.Entry.comparingByKey())
        );

        // Print top 5
        int limit = Math.min(5, entries.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = entries.get(i);
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
