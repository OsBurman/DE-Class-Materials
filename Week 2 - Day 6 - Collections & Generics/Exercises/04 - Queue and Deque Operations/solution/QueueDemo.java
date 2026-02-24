import java.util.ArrayDeque;
import java.util.PriorityQueue;

public class QueueDemo {

    public static void main(String[] args) {

        // ============================================================
        // PART 1: ArrayDeque as a Queue (FIFO — First In, First Out)
        // ============================================================
        System.out.println("=== ArrayDeque as Queue (FIFO) ===");

        ArrayDeque<String> supportQueue = new ArrayDeque<>();

        // offer() adds to the TAIL of the queue — preferred over add() because
        // it returns false on failure instead of throwing an exception
        supportQueue.offer("Ticket-101");
        supportQueue.offer("Ticket-102");
        supportQueue.offer("Ticket-103");

        // peek() reads the head WITHOUT removing — returns null if empty
        System.out.println("Front of queue: " + supportQueue.peek());

        // poll() removes and returns the head — FIFO order
        while (!supportQueue.isEmpty()) {
            System.out.println("Processing: " + supportQueue.poll());
        }

        // poll() on an empty queue returns null (safe) — unlike remove() which throws
        System.out.println("Poll on empty queue: " + supportQueue.poll());
        System.out.println();

        // ============================================================
        // PART 2: ArrayDeque as a Deque (double-ended queue)
        // ============================================================
        System.out.println("=== ArrayDeque as Deque (double-ended) ===");

        ArrayDeque<String> deque = new ArrayDeque<>();

        deque.offerLast("B");   // [B]
        deque.offerFirst("A");  // [A, B]
        deque.offerLast("C");   // [A, B, C]

        // ArrayDeque.toString() displays in front-to-back order
        System.out.println("Deque: " + deque);

        // pollFirst() removes from the front (like a queue dequeue)
        System.out.println("Removed from front: " + deque.pollFirst());

        // pollLast() removes from the back (like a stack pop)
        System.out.println("Removed from back: " + deque.pollLast());

        System.out.println("Remaining: " + deque);
        System.out.println();

        // ============================================================
        // PART 3: PriorityQueue (natural alphabetical ordering)
        // ============================================================
        System.out.println("=== PriorityQueue (natural order) ===");

        PriorityQueue<String> triageQueue = new PriorityQueue<>();

        // Elements added in any order — PriorityQueue does NOT store sorted internally
        // It only guarantees poll() returns the minimum element each time (min-heap)
        triageQueue.add("PatientC");
        triageQueue.add("PatientA");
        triageQueue.add("PatientB");

        // peek() shows the minimum without removing — "PatientA" alphabetically first
        System.out.println("Highest priority peek: " + triageQueue.peek());

        // poll() always removes the current minimum — produces sorted output
        System.out.println("Polling in priority order:");
        while (!triageQueue.isEmpty()) {
            System.out.println("  " + triageQueue.poll());
        }
    }
}
