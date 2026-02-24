import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class IteratorDemo {

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Explicit Iterator — print all elements
        // ============================================================
        System.out.println("=== Iterator: Print all elements ===");

        ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));

        // Explicit Iterator — equivalent to what for-each uses internally
        Iterator<Integer> it = numbers.iterator();
        while (it.hasNext()) {
            System.out.print(it.next());
            if (it.hasNext()) System.out.print(" ");
        }
        System.out.println();  // newline after the row
        System.out.println();

        // ============================================================
        // PART 2: Safe removal using iterator.remove()
        // ============================================================
        System.out.println("=== Iterator: Safe removal of even numbers ===");

        // A fresh iterator — must NOT reuse the one above after the list is unchanged
        Iterator<Integer> removeIt = numbers.iterator();
        while (removeIt.hasNext()) {
            int val = removeIt.next();      // must call next() before remove()
            if (val % 2 == 0) {
                removeIt.remove();          // safe: removes the element last returned by next()
            }
        }
        System.out.println("After removing evens: " + numbers);
        System.out.println();

        // ============================================================
        // PART 3: ConcurrentModificationException — what NOT to do
        // ============================================================
        System.out.println("=== ConcurrentModificationException Warning ===");

        /*
         * WRONG — do NOT do this:
         *
         * for (Integer n : numbers) {
         *     if (n % 2 == 0) numbers.remove(n);  // throws ConcurrentModificationException!
         * }
         *
         * Why: the for-each loop uses an Iterator internally. ArrayList tracks a
         * "modCount" (modification count). When you call numbers.remove() directly,
         * modCount increments. On the next hasNext()/next() call, the Iterator compares
         * its saved modCount to the current one — mismatch → ConcurrentModificationException.
         *
         * Always use iterator.remove() or removeIf() when removing during iteration.
         */
        System.out.println("// See comment in code — removing via list.remove() inside for-each throws CME");
        System.out.println();

        // ============================================================
        // PART 4: ListIterator — bidirectional traversal
        // ============================================================
        System.out.println("=== ListIterator: Forward traversal ===");

        ArrayList<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E"));

        // ListIterator extends Iterator — adds previous(), hasPrevious(), nextIndex(), etc.
        ListIterator<String> listIt = letters.listIterator();

        while (listIt.hasNext()) {
            int index = listIt.nextIndex();  // index of what next() will return
            String elem = listIt.next();
            System.out.println("[" + index + "] " + elem);
        }

        System.out.println();
        System.out.println("=== ListIterator: Backward traversal ===");

        // listIt is now positioned AFTER the last element — we can walk backward
        while (listIt.hasPrevious()) {
            int index = listIt.previousIndex();  // index of what previous() will return
            String elem = listIt.previous();
            System.out.println("[" + index + "] " + elem);
        }
        System.out.println();

        // ============================================================
        // PART 5: removeIf with a Predicate (modern approach)
        // ============================================================
        System.out.println("=== removeIf: Remove fruits starting with 'a' ===");

        ArrayList<String> fruits = new ArrayList<>(
                Arrays.asList("apple", "banana", "avocado", "cherry", "apricot"));

        // removeIf internally uses an Iterator safely — clean, readable one-liner
        fruits.removeIf(fruit -> fruit.startsWith("a"));
        System.out.println("After removeIf: " + fruits);
    }
}
