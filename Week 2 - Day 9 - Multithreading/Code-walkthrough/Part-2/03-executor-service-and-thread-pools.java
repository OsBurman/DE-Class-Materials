import java.util.*;
import java.util.concurrent.*;

/**
 * DAY 9 — PART 2 | ExecutorService, Thread Pools & ScheduledExecutorService
 * ─────────────────────────────────────────────────────────────────────────────
 * Creating a new Thread for every task is expensive:
 *   - Thread creation = OS resource allocation (stack memory, kernel structures)
 *   - Unbounded threads = memory exhaustion
 *   - No way to limit concurrency
 *
 * ExecutorService solves this: maintain a POOL of reusable threads.
 * Tasks are queued and executed by threads from the pool.
 *
 * POOL TYPES (from Executors factory):
 * ┌─────────────────────────────────┬──────────────────────────────────────────┐
 * │ newFixedThreadPool(n)           │ n threads, unbounded queue               │
 * │ newCachedThreadPool()           │ Grows/shrinks dynamically (60s TTL)      │
 * │ newSingleThreadExecutor()       │ One thread, tasks run sequentially       │
 * │ newScheduledThreadPool(n)       │ Supports delayed / periodic tasks        │
 * │ newVirtualThreadPerTaskExecutor │ Java 21+ — one virtual thread per task   │
 * └─────────────────────────────────┴──────────────────────────────────────────┘
 *
 * Runnable vs Callable:
 *   Runnable:  void run()          — no return value, cannot throw checked exceptions
 *   Callable:  V    call()         — returns a value, can throw checked exceptions
 *   Future<V>: the handle for a Callable's result — get() blocks until done
 */
public class ExecutorServiceAndThreadPools {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        demonstrateFixedThreadPool();
        demonstrateCachedThreadPool();
        demonstrateSingleThreadExecutor();
        demonstrateCallableAndFuture();
        demonstrateSubmitMultipleTasks();
        demonstrateScheduledExecutorService();
        demonstrateShutdownPatterns();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Fixed Thread Pool
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateFixedThreadPool() throws InterruptedException {
        System.out.println("=== Fixed Thread Pool ===");

        // 3 threads serve up to N queued tasks
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // Submit 8 tasks to a pool of 3 — the pool queues the extras
        for (int i = 1; i <= 8; i++) {
            final int taskId = i;
            pool.submit(() -> {
                System.out.printf("[%s] Executing task %d%n",
                        Thread.currentThread().getName(), taskId);
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }

        pool.shutdown();                  // no new tasks accepted
        pool.awaitTermination(5, TimeUnit.SECONDS);  // wait for all tasks to finish
        System.out.println("All tasks complete\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Cached Thread Pool
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCachedThreadPool() throws InterruptedException {
        System.out.println("=== Cached Thread Pool ===");

        // Creates new threads as needed, reuses idle ones (idle > 60s are terminated)
        // Good for: many short-lived tasks
        // Danger:   can create thousands of threads if tasks arrive faster than they complete
        ExecutorService pool = Executors.newCachedThreadPool();

        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            pool.submit(() -> {
                System.out.printf("[%s] Cached task %d%n",
                        Thread.currentThread().getName(), taskId);
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }

        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
        System.out.println("Cached pool done\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Single Thread Executor
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSingleThreadExecutor() throws InterruptedException {
        System.out.println("=== Single Thread Executor ===");

        // All tasks run sequentially on one thread — ORDER is guaranteed
        ExecutorService single = Executors.newSingleThreadExecutor();

        String[] steps = {"Step 1: Validate", "Step 2: Process", "Step 3: Persist", "Step 4: Notify"};
        for (String step : steps) {
            single.submit(() -> {
                System.out.println("[" + Thread.currentThread().getName() + "] " + step);
                try { Thread.sleep(30); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }

        single.shutdown();
        single.awaitTermination(3, TimeUnit.SECONDS);
        System.out.println("All steps done (in order)\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Callable and Future
    // Runnable: void — fire and forget
    // Callable<V>: returns a value — use when you need a result
    // Future<V>: the promise of a future result; get() blocks until available
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCallableAndFuture() throws InterruptedException, ExecutionException {
        System.out.println("=== Callable and Future ===");

        ExecutorService pool = Executors.newFixedThreadPool(2);

        // Submit a Callable — returns a Future
        Callable<Integer> factorialTask = () -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Computing factorial(10)");
            Thread.sleep(200);  // simulate computation
            int result = 1;
            for (int i = 2; i <= 10; i++) result *= i;
            return result;
        };

        Future<Integer> future = pool.submit(factorialTask);

        // Do other work while the task runs
        System.out.println("Main thread doing other work...");
        Thread.sleep(50);
        System.out.println("Main thread still working...");

        // get() blocks until the result is ready
        System.out.println("Waiting for factorial result...");
        Integer result = future.get();   // blocks here
        System.out.println("Factorial(10) = " + result);  // 3628800

        // Future methods:
        System.out.println("isDone():      " + future.isDone());       // true
        System.out.println("isCancelled(): " + future.isCancelled());  // false

        // get() with timeout — don't wait more than N seconds
        Callable<String> slowTask = () -> {
            Thread.sleep(2000);
            return "slow result";
        };
        Future<String> slowFuture = pool.submit(slowTask);
        try {
            String slowResult = slowFuture.get(500, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println("Timed out waiting for slow task — cancelling");
            slowFuture.cancel(true);  // true = interrupt if running
        }

        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Submitting Multiple Callables: invokeAll, invokeAny
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSubmitMultipleTasks() throws InterruptedException, ExecutionException {
        System.out.println("=== invokeAll and invokeAny ===");

        ExecutorService pool = Executors.newFixedThreadPool(4);

        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(() -> { Thread.sleep(100); return "Result from Task-1"; });
        tasks.add(() -> { Thread.sleep(200); return "Result from Task-2"; });
        tasks.add(() -> { Thread.sleep(50);  return "Result from Task-3"; });
        tasks.add(() -> { Thread.sleep(150); return "Result from Task-4"; });

        // invokeAll — run all, wait for all to complete, return all futures
        System.out.println("-- invokeAll --");
        List<Future<String>> futures = pool.invokeAll(tasks);
        for (Future<String> f : futures) {
            System.out.println("  " + f.get());  // each get() returns immediately (all done)
        }

        // invokeAny — run all, return the result of the FIRST to complete (cancels rest)
        System.out.println("-- invokeAny --");
        String fastest = pool.invokeAny(tasks);
        System.out.println("  Fastest result: " + fastest);  // Task-3 (50ms)

        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — ScheduledExecutorService
    // Run tasks after a delay or repeatedly at a fixed rate/interval
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateScheduledExecutorService() throws InterruptedException {
        System.out.println("=== ScheduledExecutorService ===");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // ----- 6a. schedule — run ONCE after a delay -----
        scheduler.schedule(() ->
                System.out.println("[Scheduled] One-time task after 300ms"),
                300, TimeUnit.MILLISECONDS);

        // ----- 6b. scheduleAtFixedRate — run every N ms (fixed start-to-start) -----
        // If task takes longer than period, next run starts immediately after previous ends
        ScheduledFuture<?> fixedRate = scheduler.scheduleAtFixedRate(() ->
                System.out.println("[FixedRate]  Heartbeat at " + System.currentTimeMillis() % 100000),
                100,    // initial delay
                200,    // period
                TimeUnit.MILLISECONDS);

        // ----- 6c. scheduleWithFixedDelay — N ms gap between END of one run and START of next -----
        ScheduledFuture<?> fixedDelay = scheduler.scheduleWithFixedDelay(() ->
                System.out.println("[FixedDelay] Polling at " + System.currentTimeMillis() % 100000),
                100,    // initial delay
                150,    // delay between runs
                TimeUnit.MILLISECONDS);

        // Let both run for a bit
        Thread.sleep(800);

        fixedRate.cancel(false);    // stop the fixed-rate task
        fixedDelay.cancel(false);   // stop the fixed-delay task

        scheduler.shutdown();
        scheduler.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("Scheduler stopped\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 — Shutdown Patterns
    // Always shut down your executor — otherwise threads keep the JVM alive
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateShutdownPatterns() throws InterruptedException {
        System.out.println("=== Shutdown Patterns ===");

        ExecutorService pool = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 5; i++) {
            final int id = i;
            pool.submit(() -> {
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("Task " + id + " done");
            });
        }

        // Pattern 1: shutdown() + awaitTermination()
        // shutdown() = no new tasks, let existing tasks finish
        pool.shutdown();
        boolean finished = pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Clean shutdown successful: " + finished);

        // Pattern 2: shutdownNow() — interrupt running tasks, return unstarted ones
        ExecutorService pool2 = Executors.newFixedThreadPool(2);
        pool2.submit(() -> { while (!Thread.currentThread().isInterrupted()) {} });  // infinite task
        pool2.submit(() -> { while (!Thread.currentThread().isInterrupted()) {} });
        Thread.sleep(50);
        List<Runnable> unstarted = pool2.shutdownNow();  // interrupts running, returns unstarted
        System.out.println("shutdownNow — unstarted tasks returned: " + unstarted.size());

        // Pattern 3: try-finally for guaranteed shutdown
        ExecutorService safePool = Executors.newFixedThreadPool(2);
        try {
            safePool.submit(() -> System.out.println("Safe pool task"));
        } finally {
            safePool.shutdown();
            safePool.awaitTermination(3, TimeUnit.SECONDS);
        }

        System.out.println();
    }
}
