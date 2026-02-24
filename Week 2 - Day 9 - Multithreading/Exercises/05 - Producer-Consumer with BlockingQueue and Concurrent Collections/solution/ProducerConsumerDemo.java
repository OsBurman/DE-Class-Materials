import java.util.*;
import java.util.concurrent.*;

public class ProducerConsumerDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Producer-Consumer with LinkedBlockingQueue ───────────────
        System.out.println("=== Producer-Consumer with LinkedBlockingQueue ===");

        // Bounded queue: put() blocks when full, take() blocks when empty
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 8; i++) {
                    queue.put("Item-" + i);           // blocks if queue is full
                    System.out.println("Produced: Item-" + i);
                    Thread.sleep(80);
                }
                queue.put("DONE");                    // sentinel to signal consumer to stop
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String item = queue.take();        // blocks if queue is empty
                    if (item.equals("DONE")) {
                        System.out.println("Consumer received DONE signal — stopping.");
                        break;
                    }
                    System.out.println("Consumed: " + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        // ── Part 2: ConcurrentHashMap ────────────────────────────────────────
        System.out.println("\n=== ConcurrentHashMap ===");

        // ConcurrentHashMap: thread-safe HashMap — no external synchronization needed
        ConcurrentHashMap<String, Integer> concMap = new ConcurrentHashMap<>();

        Thread[] mapThreads = new Thread[3];
        for (int t = 1; t <= 3; t++) {
            final int threadNum = t;
            mapThreads[t - 1] = new Thread(() -> {
                for (int k = 1; k <= 5; k++) {
                    String key = "thread-" + threadNum + "-key-" + k;
                    concMap.put(key, key.length());
                }
            });
        }
        for (Thread t : mapThreads) t.start();
        for (Thread t : mapThreads) t.join();

        System.out.println("ConcurrentHashMap size: " + concMap.size());
        // Print 3 sample entries (sorted for reproducibility)
        concMap.entrySet().stream()
               .sorted(Map.Entry.comparingByKey())
               .limit(3)
               .forEach(e -> System.out.println(e.getKey() + " = " + e.getValue()));

        // ── Part 3: Word frequency with ConcurrentHashMap.merge ─────────────
        System.out.println("\n=== Word Frequency with merge ===");

        ConcurrentHashMap<String, Integer> freq = new ConcurrentHashMap<>();
        String[] words = {"apple", "banana", "apple", "cherry", "banana", "apple"};

        // Split the array across 2 threads — merge() is atomic in ConcurrentHashMap
        Thread freqT1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) freq.merge(words[i], 1, Integer::sum);
        });
        Thread freqT2 = new Thread(() -> {
            for (int i = 3; i < 6; i++) freq.merge(words[i], 1, Integer::sum);
        });
        freqT1.start();
        freqT2.start();
        freqT1.join();
        freqT2.join();

        new TreeMap<>(freq).forEach((word, count) ->
            System.out.println(word + " = " + count));
    }
}
