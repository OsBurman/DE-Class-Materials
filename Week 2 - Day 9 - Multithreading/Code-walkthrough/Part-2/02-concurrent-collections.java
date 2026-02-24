import java.util.*;
import java.util.concurrent.*;

/**
 * DAY 9 — PART 2 | Concurrent Collections
 * ─────────────────────────────────────────────────────────────────────────────
 * Regular Java collections (ArrayList, HashMap, etc.) are NOT thread-safe.
 * Multiple threads accessing them concurrently causes corruption or exceptions.
 *
 * THE SOLUTIONS:
 *
 * ┌──────────────────────────────┬────────────────────────────────────────────┐
 * │ Class                        │ Use For                                    │
 * ├──────────────────────────────┼────────────────────────────────────────────┤
 * │ ConcurrentHashMap            │ High-concurrency key-value map             │
 * │ CopyOnWriteArrayList         │ Read-heavy list (rare writes)              │
 * │ ArrayBlockingQueue           │ Bounded FIFO queue (producer-consumer)     │
 * │ LinkedBlockingQueue          │ Unbounded (or bounded) FIFO queue          │
 * │ PriorityBlockingQueue        │ Priority-ordered blocking queue            │
 * │ ConcurrentLinkedQueue        │ Non-blocking unbounded FIFO queue          │
 * │ Collections.synchronizedXxx  │ Wrap any collection (coarse-grained lock)  │
 * └──────────────────────────────┴────────────────────────────────────────────┘
 *
 * PREFER java.util.concurrent classes over Collections.synchronized* —
 * they have better performance through fine-grained or lock-free algorithms.
 */
public class ConcurrentCollections {

    public static void main(String[] args) throws InterruptedException {
        demonstrateUnsafeHashMap();
        demonstrateConcurrentHashMap();
        demonstrateCopyOnWriteArrayList();
        demonstrateBlockingQueues();
        demonstrateConcurrentLinkedQueue();
        demonstrateSynchronizedWrappers();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Why Regular HashMap is Dangerous
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateUnsafeHashMap() throws InterruptedException {
        System.out.println("=== Unsafe HashMap (Thread UNSAFE) ===");

        Map<Integer, Integer> unsafeMap = new HashMap<>();

        Thread writer1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                unsafeMap.put(i, i);   // concurrent puts cause corruption
            }
        }, "Writer-1");

        Thread writer2 = new Thread(() -> {
            for (int i = 1000; i < 2000; i++) {
                unsafeMap.put(i, i);
            }
        }, "Writer-2");

        writer1.start(); writer2.start();
        writer1.join();  writer2.join();

        // May be < 2000 due to lost puts, or may throw ConcurrentModificationException
        System.out.println("HashMap size (expected 2000, may be less): " + unsafeMap.size());
        System.out.println("⚠️  HashMap is NOT thread-safe. Use ConcurrentHashMap instead.");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — ConcurrentHashMap
    // Thread-safe, high-performance map. Uses segment-level locking (Java 7)
    // or compare-and-swap on individual buckets (Java 8+).
    // Allows full concurrent reads and fine-grained concurrent writes.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateConcurrentHashMap() throws InterruptedException {
        System.out.println("=== ConcurrentHashMap ===");

        ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();

        // Multiple writers updating different keys — no locking needed by caller
        Thread writer1 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                scores.put("player-" + i, i * 10);
            }
        }, "Writer-1");

        Thread writer2 = new Thread(() -> {
            for (int i = 500; i < 1000; i++) {
                scores.put("player-" + i, i * 10);
            }
        }, "Writer-2");

        writer1.start(); writer2.start();
        writer1.join();  writer2.join();

        System.out.println("ConcurrentHashMap size (expected 1000): " + scores.size());

        // ----- Atomic operations on ConcurrentHashMap -----
        // putIfAbsent — add only if key doesn't exist
        scores.putIfAbsent("newPlayer", 0);
        System.out.println("putIfAbsent result: " + scores.get("newPlayer"));  // 0

        // computeIfAbsent — compute and insert if absent
        scores.computeIfAbsent("highScore", k -> 9999);
        System.out.println("computeIfAbsent: " + scores.get("highScore"));  // 9999

        // compute — atomic read-modify-write
        scores.put("alice", 100);
        scores.compute("alice", (key, val) -> val == null ? 1 : val + 50);
        System.out.println("compute (alice +50): " + scores.get("alice"));   // 150

        // merge — combine existing value with new value
        scores.merge("bob", 200, Integer::sum);   // bob=200 (not present)
        scores.merge("bob", 300, Integer::sum);   // bob=200+300=500
        System.out.println("merge (bob): " + scores.get("bob"));   // 500

        // getOrDefault
        System.out.println("getOrDefault: " + scores.getOrDefault("unknown", -1));  // -1

        // ⚠️ NOT atomic — compound check-then-act still needs external sync:
        // if (!map.containsKey(key)) { map.put(key, value); }  ← NOT safe
        // Use putIfAbsent / computeIfAbsent instead

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — CopyOnWriteArrayList
    // Thread-safe list for READ-HEAVY workloads.
    // Every WRITE creates a full copy of the array — expensive for writes,
    // but reads are completely unsynchronized (no locks — very fast).
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCopyOnWriteArrayList() throws InterruptedException {
        System.out.println("=== CopyOnWriteArrayList ===");

        CopyOnWriteArrayList<String> listeners = new CopyOnWriteArrayList<>();

        // Register listeners (writes are rare)
        listeners.add("EmailNotifier");
        listeners.add("SMSNotifier");
        listeners.add("PushNotifier");

        // Multiple threads reading simultaneously — completely safe, no locks
        Thread eventThread = new Thread(() -> {
            System.out.println("Event fired — notifying all listeners:");
            for (String listener : listeners) {   // safe to iterate without ConcurrentModificationException
                System.out.println("  → " + listener);
            }
        }, "Event-Thread");

        // Another thread adds a listener while event fires — safe (copy-on-write)
        Thread registerThread = new Thread(() -> {
            listeners.add("LogNotifier");   // creates a new array copy internally
            System.out.println("Registered: LogNotifier");
        }, "Register-Thread");

        eventThread.start();
        registerThread.start();
        eventThread.join();
        registerThread.join();

        // Regular ArrayList would throw ConcurrentModificationException if modified during iteration
        System.out.println("Final listeners: " + listeners);

        // ⚠️ Use CopyOnWriteArrayList when:
        //   - Reads FAR outnumber writes
        //   - Iteration is the primary operation
        //   - NOT suitable for large lists with frequent writes (memory/CPU overhead)
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Blocking Queues
    // ArrayBlockingQueue vs LinkedBlockingQueue vs PriorityBlockingQueue
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateBlockingQueues() throws InterruptedException {
        System.out.println("=== BlockingQueue Variants ===");

        // ----- 4a. ArrayBlockingQueue — bounded, backed by array -----
        System.out.println("-- ArrayBlockingQueue (capacity=3) --");
        ArrayBlockingQueue<String> arrayQ = new ArrayBlockingQueue<>(3);
        arrayQ.put("A");
        arrayQ.put("B");
        arrayQ.put("C");
        System.out.println("Queue: " + arrayQ);

        // offer() — returns false instead of blocking if full
        boolean added = arrayQ.offer("D");
        System.out.println("offer('D') on full queue: " + added);  // false

        // offer with timeout
        boolean addedWithTimeout = arrayQ.offer("D", 100, TimeUnit.MILLISECONDS);
        System.out.println("offer('D') with 100ms timeout: " + addedWithTimeout);  // false

        // poll() — returns null instead of blocking if empty
        String polled = arrayQ.poll();
        System.out.println("poll(): " + polled);   // A

        // peek() — look at head without removing
        System.out.println("peek(): " + arrayQ.peek());  // B

        // ----- 4b. LinkedBlockingQueue — optionally bounded -----
        System.out.println("-- LinkedBlockingQueue --");
        LinkedBlockingQueue<Integer> linkedQ = new LinkedBlockingQueue<>(100);  // bounded
        for (int i = 1; i <= 5; i++) linkedQ.offer(i);
        System.out.println("LinkedQ: " + linkedQ + " | size: " + linkedQ.size());

        // drainTo — batch remove
        List<Integer> batch = new ArrayList<>();
        linkedQ.drainTo(batch, 3);   // drain at most 3 elements
        System.out.println("Drained: " + batch + " | Remaining: " + linkedQ);

        // ----- 4c. PriorityBlockingQueue — priority-ordered -----
        System.out.println("-- PriorityBlockingQueue --");
        PriorityBlockingQueue<Task> priorityQ = new PriorityBlockingQueue<>();
        priorityQ.put(new Task("Low priority task",  3));
        priorityQ.put(new Task("High priority task", 1));
        priorityQ.put(new Task("Med priority task",  2));

        // Elements come out in priority order (lowest number = highest priority here)
        while (!priorityQ.isEmpty()) {
            System.out.println("  Processing: " + priorityQ.take().name);
        }

        System.out.println();
    }

    static class Task implements Comparable<Task> {
        final String name;
        final int priority;
        Task(String name, int priority) { this.name = name; this.priority = priority; }
        @Override
        public int compareTo(Task other) { return Integer.compare(this.priority, other.priority); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — ConcurrentLinkedQueue
    // Non-blocking, unbounded, lock-free FIFO queue
    // Best for: high-throughput work queues where blocking is undesirable
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateConcurrentLinkedQueue() throws InterruptedException {
        System.out.println("=== ConcurrentLinkedQueue (non-blocking) ===");

        ConcurrentLinkedQueue<String> workQueue = new ConcurrentLinkedQueue<>();

        // Producers — no blocking, just add
        Thread p1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) workQueue.offer("P1-task-" + i);
        }, "P1");
        Thread p2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) workQueue.offer("P2-task-" + i);
        }, "P2");

        // Consumer — non-blocking poll (returns null if empty)
        Thread consumer = new Thread(() -> {
            int processed = 0;
            while (processed < 10) {
                String task = workQueue.poll();   // non-blocking — returns null if empty
                if (task != null) {
                    System.out.println("Processed: " + task);
                    processed++;
                }
                // In real code, yield or sleep briefly to avoid spin-loop
            }
        }, "Consumer");

        p1.start(); p2.start(); consumer.start();
        p1.join();  p2.join();  consumer.join();

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — Collections.synchronized* wrappers (legacy approach)
    // Wraps any collection with coarse-grained synchronization.
    // WORSE than java.util.concurrent classes — kept for legacy code awareness
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSynchronizedWrappers() {
        System.out.println("=== Collections.synchronized* (Legacy) ===");

        List<String> syncList = Collections.synchronizedList(new ArrayList<>());
        Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());

        syncList.add("A");
        syncList.add("B");
        syncMap.put("key", 1);

        System.out.println("synchronizedList: " + syncList);
        System.out.println("synchronizedMap:  " + syncMap);

        // ⚠️ CRITICAL: Even with synchronizedList, iteration still needs external sync!
        // Without this, a ConcurrentModificationException can occur:
        synchronized (syncList) {
            for (String s : syncList) {
                System.out.println("  item: " + s);
            }
        }

        System.out.println();
        System.out.println("Summary: Prefer ConcurrentHashMap over synchronizedMap");
        System.out.println("         Prefer CopyOnWriteArrayList for read-heavy lists");
        System.out.println("         Prefer BlockingQueue over synchronized(list)+wait/notify");
    }
}
