import java.util.concurrent.*;

public class ConcurrencyUtilitiesDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: CountDownLatch — starting gate ───────────────────────────
        System.out.println("=== CountDownLatch: Starting Gate ===");

        // TODO: Create CountDownLatch startSignal = new CountDownLatch(1)


        // TODO: Create 4 runner threads (Runner-1 to Runner-4).
        //       Each thread should call startSignal.await() to wait for the gun,
        //       then print "Runner-N is running!"
        //       Start all 4 threads.


        System.out.println("All runners ready. Ready... Set...");
        Thread.sleep(200); // simulate pre-race countdown

        // TODO: Call startSignal.countDown() to release all runners at once
        System.out.println("GO!");


        // TODO: Join all 4 runner threads


        // ── Part 2: CountDownLatch — completion barrier ──────────────────────
        System.out.println("\n=== CountDownLatch: Completion Barrier ===");

        // TODO: Create CountDownLatch done = new CountDownLatch(3)


        // TODO: Create 3 worker threads (Worker-1 to Worker-3).
        //       Each thread sleeps 100ms * threadNum, prints "Worker-N finished",
        //       then calls done.countDown()
        //       Start all 3 threads.


        System.out.println("Waiting for workers...");

        // TODO: Call done.await() to block until all 3 workers have counted down


        System.out.println("All workers done — proceeding.");

        // ── Part 3: Semaphore — connection pool ──────────────────────────────
        System.out.println("\n=== Semaphore: Connection Pool (max 2) ===");

        // TODO: Create Semaphore connectionPool = new Semaphore(2)


        // TODO: Create 5 client threads (Client-1 to Client-5).
        //       Each thread:
        //       1. Calls connectionPool.acquire() — blocks if no permits available
        //       2. Prints "Client-N: connected (permits left: " + connectionPool.availablePermits() + ")"
        //       3. Sleeps 150ms to simulate database work
        //       4. Calls connectionPool.release()
        //       5. Prints "Client-N: disconnected"
        //       Start all 5 threads and join them all.


        // ── Part 4: Awareness note ───────────────────────────────────────────
        System.out.println("\n=== Awareness: ForkJoinPool & Virtual Threads ===");
        // TODO: Print a brief explanation of ForkJoinPool and Virtual Threads
        //       (see instructions.md for the text to use)
    }
}
