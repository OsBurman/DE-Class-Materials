import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        // TODO: Sort nums using Collections.sort() and print "After sort:    [...]"


        // TODO: Reverse nums using Collections.reverse() and print "After reverse: [...]"


        // TODO: Shuffle nums with Collections.shuffle(nums, new Random(42)) and print "After shuffle: [...]"


        // TODO: Print min and max using Collections.min() and Collections.max()
        //       Format: "Min: X  |  Max: Y"


        // TODO: Print frequency of 5 using Collections.frequency()
        //       Format: "Frequency of 5: N"


        System.out.println();

        // ============================================================
        // PART 2: Unmodifiable views and filled lists
        // ============================================================
        System.out.println("=== Unmodifiable and Filled Lists ===");

        // TODO: Create an unmodifiable view of nums
        // TODO: Try to add 99 to it inside a try/catch(UnsupportedOperationException)
        //       Print: "Unmodifiable list caught: " + exception class and message


        // TODO: Create a list with Collections.nCopies(5, "Java") and print it
        //       Format: "nCopies(5, \"Java\"): [...]"


        // TODO: Create an ArrayList<String> of 5 "_" elements
        //       Call Collections.fill(list, "done") and print the result
        //       Format: "After fill with \"done\": [...]"


        System.out.println();

        // ============================================================
        // PART 3: Capstone — Word Frequency Counter
        // ============================================================
        System.out.println("=== Capstone: Word Frequency Counter ===");

        String sentence = "the quick brown fox jumps over the lazy dog the fox";

        // TODO: Split sentence into words using split(" ")

        // TODO: Count each word's frequency using a HashMap<String, Integer>
        //       (Hint: use getOrDefault(word, 0) + 1)

        // TODO: Collect map entries into a List<Map.Entry<String, Integer>>

        // TODO: Sort entries by value descending, then by key ascending for ties
        //       (Hint: Map.Entry.comparingByValue(Comparator.reverseOrder())
        //               .thenComparing(Map.Entry.comparingByKey()))

        // TODO: Print the top 5 entries in format "word: count"

    }
}
