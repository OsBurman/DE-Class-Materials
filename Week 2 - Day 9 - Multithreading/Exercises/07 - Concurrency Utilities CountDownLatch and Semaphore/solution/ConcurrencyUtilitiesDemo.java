import java.util.concurrent.*;

public class ConcurrencyUtilitiesDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: CountDownLatch — starting gate ───────────────────────────
        System.out.println("=== CountDownLatch: Starting Gate ===");

        // One latch that all runners wait on; the main thread fires it with countDown()
        CountDownLatch startSignal = new CountDownLatch(1);

        Thread[] runners = new Thread[4];
        for (int i = 1; i <= 4; i++) {
            final int num = i;
            runners[i - 1] = new Thread(() -> {
                try {
                    startSignal.await(); // block until count reaches 0
                    System.out.println("Runner-" + num + " is running!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Runner-" + i);
            runners[i - 1].start();
        }

        System.out.println("All runners ready. Ready... Set...");
        Thread.sleep(200);
        startSignal.countDown(); // releases ALL waiting threads simultaneously
        System.out.println("GO!");

        for (Thread r : runners) r.join();

        // ── Part 2: CountDownLatch — completion barrier ──────────────────────
        System.out.println("\n=== CountDownLatch: Completion Barrier ===");

        // Latch initialized with 3 — main blocks at await() until all 3 workers call countDown()
        CountDownLatch done = new CountDownLatch(3);

        Thread[] workers = new Thread[3];
        for (int i = 1; i <= 3; i++) {
            final int num = i;
            workers[i - 1] = new Thread(() -> {
                try {
                    Thread.sleep(num * 100L); // stagger completion times
                    System.out.println("Worker-" + num + " finished");
                    done.countDown();         // signal one unit of work is complete
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Worker-" + i);
            workers[i - 1].start();
        }

        System.out.println("Waiting for workers...");
        done.await(); // main thread blocks here until count == 0
        System.out.println("All workers done — proceeding.");

        // ── Part 3: Semaphore — connection pool ──────────────────────────────
        System.out.println("\n=== Semaphore: Connection Pool (max 2) ===");

        // Only 2 permits — at most 2 threads can hold a permit simultaneously
        Semaphore connectionPool = new Semaphore(2);

        Thread[] clients = new Thread[5];
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            clients[i - 1] = new Thread(() -> {
                try {
                    connectionPool.acquire();  // blocks if both permits are taken
                    System.out.println("Client-" + num + ": connected (permits left: "
                            + connectionPool.availablePermits() + ")");
                    Thread.sleep(150);         // simulate database work
                    connectionPool.release();  // return the permit
                    System.out.println("Client-" + num + ": disconnected");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Client-" + i);
        }
        for (Thread c : clients) c.start();
        for (Thread c : clients) c.join();

        // ── Part 4: Awareness note ───────────────────────────────────────────
        System.out.println("\n=== Awareness: ForkJoinPool & Virtual Threads ===");
        System.out.println("ForkJoinPool: work-stealing pool used by parallel streams and CompletableFuture.");
        System.out.println("  Ideal for divide-and-conquer CPU-bound tasks.");
        System.out.println("  Common pool: ForkJoinPool.commonPool()");
        System.out.println();
        System.out.println("Virtual Threads (Java 21+ / Project Loom):");
        System.out.println("  Lightweight threads managed by the JVM — not OS threads.");
        System.out.println("  Can scale to millions. Created with: Thread.ofVirtual().start(task)");
        System.out.println("  Ideal for high-throughput I/O-bound workloads.");
    }
}
