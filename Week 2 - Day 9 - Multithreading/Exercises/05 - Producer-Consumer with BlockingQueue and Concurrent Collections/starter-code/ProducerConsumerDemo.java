import java.util.*;
import java.util.concurrent.*;

public class ProducerConsumerDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Producer-Consumer with LinkedBlockingQueue ───────────────
        System.out.println("=== Producer-Consumer with LinkedBlockingQueue ===");

        // TODO: Create a LinkedBlockingQueue<String> with capacity 5


        // TODO: Create a producer Thread that:
        //       1. Loops i from 1 to 8, calling queue.put("Item-" + i) and printing "Produced: Item-N"
        //       2. Sleeps 80ms between each put
        //       3. After the loop, puts the sentinel "DONE" into the queue
        //       Wrap put() in try-catch for InterruptedException


        // TODO: Create a consumer Thread that:
        //       1. Loops calling item = queue.take()
        //       2. If item.equals("DONE"), prints "Consumer received DONE signal — stopping." and breaks
        //       3. Otherwise prints "Consumed: " + item
        //       Wrap take() in try-catch for InterruptedException


        // TODO: Start producer and consumer, then join both


        // ── Part 2: ConcurrentHashMap ────────────────────────────────────────
        System.out.println("\n=== ConcurrentHashMap ===");

        // TODO: Create a ConcurrentHashMap<String, Integer>
        //       Launch 3 threads (numbered 1-3). Each thread inserts 5 entries with keys
        //       "thread-N-key-M" (N = thread number, M = 1-5) and value = key.length()
        //       Join all 3 threads, then print "ConcurrentHashMap size: " + map.size()
        //       and print any 3 sample entries using entrySet()


        // ── Part 3: Word frequency with ConcurrentHashMap.merge ─────────────
        System.out.println("\n=== Word Frequency with merge ===");

        // TODO: Create a ConcurrentHashMap<String, Integer>
        //       Split this list across 2 threads (first 3 words / last 3 words):
        //       ["apple","banana","apple","cherry","banana","apple"]
        //       Each thread calls map.merge(word, 1, Integer::sum) for each of its words
        //       Join both threads
        //       Print each entry sorted alphabetically: "word = count"
    }
}
