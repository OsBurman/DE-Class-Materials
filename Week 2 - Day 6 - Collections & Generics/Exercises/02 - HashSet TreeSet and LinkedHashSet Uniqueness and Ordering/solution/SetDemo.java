import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.Set;

public class SetDemo {
    public static void main(String[] args) {

        // ===================== PART 1: HashSet =====================
        System.out.println("=== HashSet ===");

        // HashSet uses a hash table — O(1) operations, no ordering guarantee
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("Charlie");
        hashSet.add("Alice");
        hashSet.add("Dave");
        hashSet.add("Bob");
        hashSet.add("Alice");   // duplicate — add() returns false, element not added

        System.out.println("Size: " + hashSet.size());   // 4, not 5
        // Note: actual printed order may differ on different JVMs/runs
        System.out.println("HashSet contents (order not guaranteed): " + hashSet);
        System.out.println("Contains Alice: " + hashSet.contains("Alice"));

        hashSet.remove("Dave");
        System.out.println("After removing Dave: " + hashSet);

        System.out.println();

        // ===================== PART 2: LinkedHashSet =====================
        System.out.println("=== LinkedHashSet (insertion order) ===");

        // LinkedHashSet maintains a doubly-linked list alongside the hash table
        // to preserve the order elements were first inserted
        LinkedHashSet<String> linkedSet = new LinkedHashSet<>();
        linkedSet.add("Charlie");
        linkedSet.add("Alice");
        linkedSet.add("Dave");
        linkedSet.add("Bob");
        linkedSet.add("Alice");   // duplicate — dropped, insertion order for "Alice" stays at position 2
        System.out.println("LinkedHashSet: " + linkedSet);
        System.out.println("Size: " + linkedSet.size());

        System.out.println();

        // ===================== PART 3: TreeSet =====================
        System.out.println("=== TreeSet (sorted order) ===");

        // TreeSet stores elements in a Red-Black tree — always sorted (natural ordering by default)
        TreeSet<String> treeSet = new TreeSet<>();
        treeSet.add("Charlie");
        treeSet.add("Alice");
        treeSet.add("Dave");
        treeSet.add("Bob");
        treeSet.add("Alice");   // duplicate — dropped
        System.out.println("TreeSet: " + treeSet);

        System.out.println("First: " + treeSet.first());
        System.out.println("Last: " + treeSet.last());

        // headSet: elements strictly less than "Charlie"
        System.out.println("Before Charlie (headSet): " + treeSet.headSet("Charlie"));

        // tailSet: elements >= "Charlie"
        System.out.println("From Charlie onward (tailSet): " + treeSet.tailSet("Charlie"));

        System.out.println();
        System.out.println("HashSet: no guaranteed order, O(1) add/contains");
        System.out.println("LinkedHashSet: insertion order preserved, slightly slower");
        System.out.println("TreeSet: always sorted, O(log n) add/contains");
    }
}
