import java.util.ArrayDeque;
import java.util.PriorityQueue;

public class QueueDemo {

    public static void main(String[] args) {

        // ============================================================
        // PART 1: ArrayDeque as a Queue (FIFO — First In, First Out)
        // ============================================================
        System.out.println("=== ArrayDeque as Queue (FIFO) ===");

        ArrayDeque<String> supportQueue = new ArrayDeque<>();

        // TODO: Enqueue "Ticket-101", "Ticket-102", "Ticket-103" using offer()


        // TODO: Peek at the front (without removing) and print it


        // TODO: Process all tickets with poll() in a while loop, printing "Processing: [ticket]"


        // TODO: Poll the now-empty queue and print the result (should be null)


        System.out.println();

        // ============================================================
        // PART 2: ArrayDeque as a Deque (double-ended queue)
        // ============================================================
        System.out.println("=== ArrayDeque as Deque (double-ended) ===");

        ArrayDeque<String> deque = new ArrayDeque<>();

        // TODO: Add "B" to the back using offerLast()
        // TODO: Add "A" to the front using offerFirst()
        // TODO: Add "C" to the back using offerLast()

        // TODO: Print the deque


        // TODO: Remove and print from the front using pollFirst()


        // TODO: Remove and print from the back using pollLast()


        // TODO: Print the remaining deque


        System.out.println();

        // ============================================================
        // PART 3: PriorityQueue (natural alphabetical ordering)
        // ============================================================
        System.out.println("=== PriorityQueue (natural order) ===");

        PriorityQueue<String> triageQueue = new PriorityQueue<>();

        // TODO: Add "PatientC", "PatientA", "PatientB" to triageQueue


        // TODO: Peek at the highest-priority patient (alphabetically first) and print it


        // TODO: Poll all patients in priority order with a while loop, printing each one

    }
}
