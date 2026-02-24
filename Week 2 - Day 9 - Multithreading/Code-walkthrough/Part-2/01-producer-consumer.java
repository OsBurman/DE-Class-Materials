import java.util.concurrent.*;

/**
 * DAY 9 — PART 2 | Producer-Consumer Problem
 * ─────────────────────────────────────────────────────────────────────────────
 * The Producer-Consumer pattern is one of the most common concurrent patterns:
 *   - PRODUCER: generates data and puts it in a shared buffer
 *   - CONSUMER: takes data from the buffer and processes it
 *   - BUFFER:   the shared queue between them (must be thread-safe)
 *
 * KEY CHALLENGE: The buffer has a fixed capacity.
 *   - If full:  producer must WAIT (not overwrite)
 *   - If empty: consumer must WAIT (not spin endlessly)
 *
 * This file shows TWO approaches:
 *   APPROACH 1 — Low-level: wait() / notify() (educational — understand the pattern)
 *   APPROACH 2 — High-level: BlockingQueue   (production-ready — always prefer this)
 */
public class ProducerConsumer {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== APPROACH 1: wait()/notify() ===");
        runWithWaitNotify();

        System.out.println();
        System.out.println("=== APPROACH 2: BlockingQueue ===");
        runWithBlockingQueue();

        System.out.println();
        System.out.println("=== APPROACH 2b: Multiple Producers & Consumers ===");
        runMultipleProducersConsumers();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // APPROACH 1 — Manual wait()/notify()
    // Shows the pattern explicitly — demonstrates the mechanics
    // ─────────────────────────────────────────────────────────────────────────
    static void runWithWaitNotify() throws InterruptedException {
        ManualBuffer buffer = new ManualBuffer(3);   // capacity = 3

        Thread producer = new Thread(() -> {
            String[] items = {"OrderA", "OrderB", "OrderC", "OrderD", "OrderE"};
            for (String item : items) {
                buffer.put(item);
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            buffer.put(null);   // sentinel value signals "no more items"
        }, "Producer");

        Thread consumer = new Thread(() -> {
            while (true) {
                String item = buffer.take();
                if (item == null) {
                    System.out.println("[Consumer] Received end-of-stream — stopping");
                    break;
                }
                System.out.println("[Consumer] Processed: " + item);
                try { Thread.sleep(120); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "Consumer");

        consumer.start();
        producer.start();
        producer.join();
        consumer.join();
    }

    // Fixed-capacity buffer using synchronized + wait/notify
    static class ManualBuffer {
        private final Object[] items;
        private int head = 0, tail = 0, count = 0;
        private final int capacity;

        ManualBuffer(int capacity) {
            this.capacity = capacity;
            this.items    = new Object[capacity];
        }

        // Producer calls this
        synchronized void put(Object item) {
            while (count == capacity) {          // buffer full — wait
                System.out.println("[Producer] Buffer full — waiting");
                try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
            items[tail] = item;
            tail        = (tail + 1) % capacity;
            count++;
            System.out.println("[Producer] Put: " + item + " | buffer size: " + count);
            notifyAll();    // wake up any waiting consumers
        }

        // Consumer calls this
        synchronized Object take() {
            while (count == 0) {                 // buffer empty — wait
                System.out.println("[Consumer] Buffer empty — waiting");
                try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return null; }
            }
            Object item = items[head];
            items[head] = null;
            head        = (head + 1) % capacity;
            count--;
            notifyAll();    // wake up any waiting producers
            return item;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // APPROACH 2 — BlockingQueue (always use this in production)
    // BlockingQueue handles all the wait/notify logic internally
    // put() blocks if full; take() blocks if empty — automatically
    // ─────────────────────────────────────────────────────────────────────────
    static void runWithBlockingQueue() throws InterruptedException {
        // ArrayBlockingQueue: bounded, FIFO, backed by an array
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

        Thread producer = new Thread(() -> {
            String[] orders = {"OrderA", "OrderB", "OrderC", "OrderD", "OrderE"};
            try {
                for (String order : orders) {
                    queue.put(order);   // blocks if queue is full
                    System.out.println("[Producer] Queued: " + order +
                            " | queue size: " + queue.size());
                    Thread.sleep(50);
                }
                queue.put("DONE");   // sentinel
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String order = queue.take();   // blocks if queue is empty
                    if ("DONE".equals(order)) {
                        System.out.println("[Consumer] Done signal received");
                        break;
                    }
                    System.out.println("[Consumer] Processing: " + order);
                    Thread.sleep(120);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        consumer.start();
        producer.start();
        producer.join();
        consumer.join();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // APPROACH 2b — Multiple Producers and Multiple Consumers
    // BlockingQueue shines here — handles contention automatically
    // ─────────────────────────────────────────────────────────────────────────
    static void runMultipleProducersConsumers() throws InterruptedException {
        BlockingQueue<String> queue  = new LinkedBlockingQueue<>(10);
        int numProducers  = 2;
        int numConsumers  = 3;
        int itemsEach     = 4;

        // Producers
        Thread[] producers = new Thread[numProducers];
        for (int p = 0; p < numProducers; p++) {
            final int producerId = p + 1;
            producers[p] = new Thread(() -> {
                try {
                    for (int i = 1; i <= itemsEach; i++) {
                        String item = "P" + producerId + "-Item" + i;
                        queue.put(item);
                        System.out.println("[Producer-" + producerId + "] put: " + item);
                        Thread.sleep(40);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Producer-" + producerId);
        }

        // Consumers
        // Use a PoisonPill per consumer to shut down cleanly
        Thread[] consumers = new Thread[numConsumers];
        for (int c = 0; c < numConsumers; c++) {
            final int consumerId = c + 1;
            consumers[c] = new Thread(() -> {
                try {
                    while (true) {
                        String item = queue.poll(500, TimeUnit.MILLISECONDS);
                        if (item == null || "POISON".equals(item)) {
                            System.out.println("[Consumer-" + consumerId + "] stopping");
                            break;
                        }
                        System.out.println("[Consumer-" + consumerId + "] processed: " + item);
                        Thread.sleep(80);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Consumer-" + consumerId);
        }

        // Start everyone
        for (Thread c : consumers) c.start();
        for (Thread p : producers) p.start();

        // Wait for producers to finish
        for (Thread p : producers) p.join();

        // Send poison pills to consumers
        for (int c = 0; c < numConsumers; c++) queue.put("POISON");

        // Wait for consumers to finish
        for (Thread c : consumers) c.join();
        System.out.println("All done.");
    }
}
