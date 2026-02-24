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

        // TODO: Obtain an Iterator<Integer> from numbers
        // TODO: Use hasNext() / next() to print all values space-separated on one line
        // (Hint: use System.out.print() and only print a newline at the end)


        System.out.println();

        // ============================================================
        // PART 2: Safe removal using iterator.remove()
        // ============================================================
        System.out.println("=== Iterator: Safe removal of even numbers ===");

        // TODO: Obtain a NEW Iterator<Integer> from numbers
        // TODO: Use hasNext() / next() to iterate; call iterator.remove() on even numbers
        // TODO: Print the resulting list


        System.out.println();

        // ============================================================
        // PART 3: ConcurrentModificationException — what NOT to do
        // ============================================================
        System.out.println("=== ConcurrentModificationException Warning ===");

        /*
         * TODO: Add a comment here showing the WRONG way to remove during iteration:
         *
         * for (Integer n : numbers) {
         *     if (n % 2 == 0) numbers.remove(n);  // throws ConcurrentModificationException!
         * }
         *
         * Explain: the for-each loop uses an Iterator internally. When the underlying list
         * is structurally modified (size changed) outside of the iterator, the iterator
         * detects it via a "modification count" and throws ConcurrentModificationException.
         */
        System.out.println("// See comment in code — removing via list.remove() inside for-each throws CME");
        System.out.println();

        // ============================================================
        // PART 4: ListIterator — bidirectional traversal
        // ============================================================
        System.out.println("=== ListIterator: Forward traversal ===");

        ArrayList<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E"));

        // TODO: Obtain a ListIterator<String> from letters
        // TODO: Traverse forward with hasNext() / next(), printing "[index] element"
        //       (Hint: use listIterator.nextIndex() to get the current index BEFORE calling next())


        System.out.println();
        System.out.println("=== ListIterator: Backward traversal ===");

        // TODO: The ListIterator is now at the end — traverse BACKWARD
        //       using hasPrevious() / previous(), printing "[index] element"
        //       (Hint: use listIterator.previousIndex() to get the index BEFORE calling previous())


        System.out.println();

        // ============================================================
        // PART 5: removeIf with a Predicate (modern approach)
        // ============================================================
        System.out.println("=== removeIf: Remove fruits starting with 'a' ===");

        ArrayList<String> fruits = new ArrayList<>(
                Arrays.asList("apple", "banana", "avocado", "cherry", "apricot"));

        // TODO: Use removeIf() with a lambda to remove all strings starting with "a"
        // TODO: Print the resulting list

    }
}
