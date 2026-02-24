import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.Set;

public class SetDemo {
    public static void main(String[] args) {

        // ===================== PART 1: HashSet =====================
        System.out.println("=== HashSet ===");

        // TODO: Create a HashSet<String> named hashSet
        // TODO: Add "Charlie", "Alice", "Dave", "Bob", "Alice" (duplicate — silently ignored)

        // TODO: Print "Size: " + hashSet.size()  (should be 4)
        // TODO: Print "HashSet contents (order not guaranteed): " + hashSet
        // TODO: Print "Contains Alice: " + hashSet.contains("Alice")

        // TODO: Remove "Dave": hashSet.remove("Dave")
        // TODO: Print "After removing Dave: " + hashSet

        System.out.println();

        // ===================== PART 2: LinkedHashSet =====================
        System.out.println("=== LinkedHashSet (insertion order) ===");

        // TODO: Create a LinkedHashSet<String> named linkedSet
        // TODO: Add the same 5 values: "Charlie", "Alice", "Dave", "Bob", "Alice"
        // TODO: Print "LinkedHashSet: " + linkedSet  (insertion order, duplicate dropped)
        // TODO: Print "Size: " + linkedSet.size()

        System.out.println();

        // ===================== PART 3: TreeSet =====================
        System.out.println("=== TreeSet (sorted order) ===");

        // TODO: Create a TreeSet<String> named treeSet
        // TODO: Add the same 5 values
        // TODO: Print "TreeSet: " + treeSet  (alphabetically sorted)

        // TODO: Print "First: " + treeSet.first()
        // TODO: Print "Last: " + treeSet.last()

        // TODO: Print "Before Charlie (headSet): " + treeSet.headSet("Charlie")
        //       headSet(toElement) returns elements STRICTLY LESS THAN toElement

        // TODO: Print "From Charlie onward (tailSet): " + treeSet.tailSet("Charlie")
        //       tailSet(fromElement) returns elements >= fromElement

        System.out.println();

        // TODO: Print the three comparison lines:
        // "HashSet: no guaranteed order, O(1) add/contains"
        // "LinkedHashSet: insertion order preserved, slightly slower"
        // "TreeSet: always sorted, O(log n) add/contains"
    }
}
