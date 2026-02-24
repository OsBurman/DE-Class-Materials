import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * DAY 9 — PART 2 | Core Concurrency Utilities
 * ─────────────────────────────────────────────────────────────────────────────
 * Beyond synchronized blocks, Java provides high-level coordination tools:
 *
 * COORDINATION BARRIERS:
 *   CountDownLatch   — one-time gate: all threads wait until count reaches 0
 *   CyclicBarrier    — reusable rendezvous: all threads wait until all arrive
 *
 * RESOURCE LIMITING:
 *   Semaphore        — controls how many threads access a resource concurrently
 *
 * EXPLICIT LOCKS:
 *   ReentrantLock          — same semantics as synchronized, but more control
 *   ReentrantReadWriteLock — many concurrent readers OR one exclusive writer
 *   StampedLock            — optimistic reads for even better read throughput
 *
 * ADVANCED (AWARENESS):
 *   ForkJoinPool     — divide-and-conquer parallel tasks (used by parallel streams)
 *   Virtual Threads  — Java 21+, lightweight threads for I/O-bound workloads
 */
public class ConcurrencyUtilities {

    public static void main(String[] args) throws Exception {
        demonstrateCountDownLatch();
        demonstrateCyclicBarrier();
        demonstrateSemaphore();
        demonstrateReentrantLock();
        demonstrateReadWriteLock();
        demonstrateStampedLock();
        demonstrateForkJoinPoolAwareness();
        demonstrateVirtualThreadsAwareness();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — CountDownLatch
    // Use case: wait for N independent events to happen, then proceed
    // One-time use — cannot be reset
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCountDownLatch() throws InterruptedException {
        System.out.println("=== CountDownLatch ===");

        int serviceCount = 3;
        CountDownLatch startLatch = new CountDownLatch(serviceCount);

        // Simulate 3 microservices starting up
        String[] services = {"DatabaseService", "CacheService", "MessageBroker"};
        for (String service : services) {
            new Thread(() -> {
                try {
                    int delay = 100 + new Random().nextInt(200);
                    Thread.sleep(delay);
                    System.out.println(service + " ready after " + delay + "ms");
                    startLatch.countDown();   // decrement the count
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, service).start();
        }

        System.out.println("Application waiting for all services...");
        startLatch.await();   // blocks until count reaches 0
        System.out.println("All services ready — application starting!\n");

        // await with timeout — don't wait forever
        CountDownLatch timeoutLatch = new CountDownLatch(1);
        boolean completed = timeoutLatch.await(200, TimeUnit.MILLISECONDS);
        System.out.println("Latch completed within timeout: " + completed + "\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — CyclicBarrier
    // Use case: multiple threads work on a phase, then ALL wait at a checkpoint
    //           before proceeding to the next phase
    // Key difference from CountDownLatch: REUSABLE — resets automatically
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCyclicBarrier() throws InterruptedException {
        System.out.println("=== CyclicBarrier ===");

        int workerCount = 3;
        int roundCount  = 2;

        // Optional barrier action — runs ONCE when all threads arrive
        CyclicBarrier barrier = new CyclicBarrier(workerCount,
                () -> System.out.println("  *** All workers reached barrier — starting next phase ***"));

        for (int w = 1; w <= workerCount; w++) {
            final int workerId = w;
            new Thread(() -> {
                try {
                    for (int round = 1; round <= roundCount; round++) {
                        int work = 50 + workerId * 30;
                        Thread.sleep(work);
                        System.out.printf("  Worker-%d finished round %d (took %dms)%n", workerId, round, work);
                        barrier.await();  // wait for all workers to finish this round
                    }
                    System.out.println("  Worker-" + workerId + " all done");
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        Thread.sleep(1500);   // let the simulation complete
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Semaphore
    // Use case: limit concurrent access to a shared resource
    //   - Database connection pool: max 3 connections
    //   - Rate limiter: max 5 concurrent API calls
    //   - Parking lot: N spaces available
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSemaphore() throws InterruptedException {
        System.out.println("=== Semaphore — Connection Pool ===");

        int maxConnections = 3;
        Semaphore connectionPool = new Semaphore(maxConnections);
        AtomicInteger activeConnections = new AtomicInteger(0);

        // 7 threads compete for 3 connections
        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            final int threadId = i;
            threads.add(new Thread(() -> {
                try {
                    System.out.println("Thread-" + threadId + " requesting connection...");
                    connectionPool.acquire();   // blocks if no permits available
                    int current = activeConnections.incrementAndGet();
                    System.out.printf("Thread-%d GOT connection (active: %d, available: %d)%n",
                            threadId, current, connectionPool.availablePermits());

                    Thread.sleep(100 + new Random().nextInt(150));  // use connection

                    activeConnections.decrementAndGet();
                    connectionPool.release();   // return permit
                    System.out.println("Thread-" + threadId + " released connection");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        // tryAcquire — non-blocking attempt
        boolean acquired = connectionPool.tryAcquire();
        System.out.println("tryAcquire (non-blocking): " + acquired);
        if (acquired) connectionPool.release();
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — ReentrantLock
    // Same mutual exclusion as synchronized, but with:
    //   - tryLock()         — attempt lock without blocking
    //   - lockInterruptibly()— can be interrupted while waiting
    //   - Conditions        — like wait/notify but named and multiple per lock
    //   - Fairness option   — longest-waiting thread gets lock first
    // ALWAYS unlock in finally block to prevent lock leaks
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateReentrantLock() throws InterruptedException {
        System.out.println("=== ReentrantLock ===");

        ReentrantLock lock = new ReentrantLock();
        int[] sharedCounter = {0};

        // Basic lock/unlock — always in try/finally
        Runnable incrementTask = () -> {
            for (int i = 0; i < 1000; i++) {
                lock.lock();
                try {
                    sharedCounter[0]++;
                } finally {
                    lock.unlock();   // ALWAYS in finally — runs even if exception thrown
                }
            }
        };

        Thread t1 = new Thread(incrementTask);
        Thread t2 = new Thread(incrementTask);
        t1.start(); t2.start();
        t1.join();  t2.join();
        System.out.println("Counter (should be 2000): " + sharedCounter[0]);

        // tryLock — non-blocking, avoid spinning
        ReentrantLock tryLockDemo = new ReentrantLock();
        tryLockDemo.lock();   // held by main thread
        Thread tryThread = new Thread(() -> {
            boolean got = tryLockDemo.tryLock();  // returns immediately if unavailable
            System.out.println("tryLock acquired: " + got);
            if (got) tryLockDemo.unlock();
        });
        tryThread.start();
        tryThread.join();
        tryLockDemo.unlock();

        // tryLock with timeout
        ReentrantLock timedLock = new ReentrantLock();
        timedLock.lock();
        Thread timedThread = new Thread(() -> {
            try {
                boolean got = timedLock.tryLock(50, TimeUnit.MILLISECONDS);
                System.out.println("tryLock with timeout acquired: " + got);
                if (got) timedLock.unlock();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        timedThread.start();
        timedThread.join();
        timedLock.unlock();

        // Condition — replaces wait()/notify() with named conditions
        ReentrantLock condLock = new ReentrantLock();
        Condition notEmpty = condLock.newCondition();
        Queue<Integer> queue = new LinkedList<>();

        Thread producer = new Thread(() -> {
            condLock.lock();
            try {
                queue.add(42);
                System.out.println("Producer: added item, signalling consumer");
                notEmpty.signal();   // like notify() but on a named Condition
            } finally { condLock.unlock(); }
        });

        Thread consumer = new Thread(() -> {
            condLock.lock();
            try {
                while (queue.isEmpty()) {
                    System.out.println("Consumer: queue empty, waiting...");
                    notEmpty.await();  // releases lock and waits (like wait())
                }
                System.out.println("Consumer: got item " + queue.poll());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally { condLock.unlock(); }
        });

        consumer.start();
        Thread.sleep(50);
        producer.start();
        consumer.join(); producer.join();
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — ReentrantReadWriteLock
    // Read-heavy data structures: many threads can read simultaneously
    // but writes need exclusive access
    //   readLock()  — multiple concurrent holders allowed
    //   writeLock() — exclusive, blocks all readers and other writers
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateReadWriteLock() throws InterruptedException {
        System.out.println("=== ReentrantReadWriteLock ===");

        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        Lock readLock  = rwLock.readLock();
        Lock writeLock = rwLock.writeLock();
        Map<String, String> cache = new HashMap<>();

        // Simulate a cached config store
        Runnable readTask = () -> {
            readLock.lock();
            try {
                String val = cache.getOrDefault("config.maxRetries", "(not set)");
                System.out.println("[" + Thread.currentThread().getName() + "] Read: " + val);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally { readLock.unlock(); }
        };

        Runnable writeTask = () -> {
            writeLock.lock();
            try {
                String val = "5-" + Thread.currentThread().getName();
                System.out.println("[" + Thread.currentThread().getName() + "] Writing: " + val);
                cache.put("config.maxRetries", val);
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally { writeLock.unlock(); }
        };

        // Start 3 readers and 1 writer
        Thread w = new Thread(writeTask, "Writer");
        Thread r1 = new Thread(readTask, "Reader-1");
        Thread r2 = new Thread(readTask, "Reader-2");
        Thread r3 = new Thread(readTask, "Reader-3");

        w.start(); Thread.sleep(10);
        r1.start(); r2.start(); r3.start();
        w.join(); r1.join(); r2.join(); r3.join();

        System.out.println("ReadWriteLock rules:");
        System.out.println("  readLock holders  now: " + rwLock.getReadLockCount());
        System.out.println("  writeLock held now: " + rwLock.isWriteLocked());
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — StampedLock (awareness)
    // Optimistic reads: read WITHOUT acquiring a lock, then validate
    // If validation fails (write happened during read), fall back to full read lock
    // Best for read-heavy workloads where conflicts are rare
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateStampedLock() {
        System.out.println("=== StampedLock (optimistic read) ===");

        StampedLock sl = new StampedLock();
        double[] point = {1.0, 2.0};   // x, y

        // Optimistic read — no lock acquired
        long stamp = sl.tryOptimisticRead();
        double x = point[0];
        double y = point[1];

        if (!sl.validate(stamp)) {
            // A write happened between our reads — fall back to read lock
            stamp = sl.readLock();
            try {
                x = point[0];
                y = point[1];
            } finally {
                sl.unlockRead(stamp);
            }
        }
        System.out.println("Optimistic read: x=" + x + ", y=" + y);

        // Write path
        long writeStamp = sl.writeLock();
        try {
            point[0] = 10.0;
            point[1] = 20.0;
            System.out.println("Write: new point=" + Arrays.toString(point));
        } finally {
            sl.unlockWrite(writeStamp);
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 — ForkJoinPool (awareness)
    // Divide-and-conquer parallel processing
    // Used internally by: parallel streams, CompletableFuture.supplyAsync
    // RecursiveTask<T>  — returns a value
    // RecursiveAction   — no return value
    // Work-stealing: idle threads steal tasks from busy threads' queues
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateForkJoinPoolAwareness() throws Exception {
        System.out.println("=== ForkJoinPool (awareness) ===");

        // RecursiveTask example: parallel sum of an array
        long[] data = new long[1_000_000];
        for (int i = 0; i < data.length; i++) data[i] = i + 1L;

        ForkJoinPool pool = ForkJoinPool.commonPool();
        System.out.println("ForkJoinPool parallelism: " + pool.getParallelism());

        Long sum = pool.invoke(new ParallelSum(data, 0, data.length));
        System.out.println("Parallel sum of 1..1,000,000 = " + sum);

        // You already use ForkJoin without knowing it!
        long streamSum = java.util.stream.LongStream.rangeClosed(1, 1_000_000).parallel().sum();
        System.out.println("parallel stream sum: " + streamSum + " (uses ForkJoinPool.commonPool)");
        System.out.println();
    }

    static class ParallelSum extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;
        private final long[] data;
        private final int start, end;

        ParallelSum(long[] data, int start, int end) {
            this.data  = data;
            this.start = start;
            this.end   = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                // Small enough: compute directly (base case)
                long sum = 0;
                for (int i = start; i < end; i++) sum += data[i];
                return sum;
            }
            // Divide: split the work
            int mid = (start + end) / 2;
            ParallelSum left  = new ParallelSum(data, start, mid);
            ParallelSum right = new ParallelSum(data, mid, end);
            left.fork();               // submit left to pool (async)
            long rightResult = right.compute();  // compute right on this thread
            long leftResult  = left.join();      // wait for left
            return leftResult + rightResult;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 — Virtual Threads (Java 21+, Project Loom) — awareness
    // Platform threads (OS threads): ~1MB stack, OS scheduler, expensive
    // Virtual threads: ~KB stack, JVM scheduler, maps to carrier threads
    //
    // KEY POINTS:
    //   - One virtual thread per task — millions possible
    //   - Ideal for I/O-bound work (HTTP, DB, file)
    //   - NOT faster for CPU-bound — still needs real CPU time
    //   - AVOID synchronized blocks — they "pin" the carrier thread
    //     Use ReentrantLock instead inside virtual threads
    //   - Thread.isVirtual() — check if a thread is virtual
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateVirtualThreadsAwareness() throws Exception {
        System.out.println("=== Virtual Threads (Java 21+) ===");

        // Create and start a virtual thread (Java 21+)
        Thread vt = Thread.ofVirtual().name("my-virtual-thread").start(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] "
                    + "isVirtual=" + Thread.currentThread().isVirtual()
                    + " running task");
        });
        vt.join();

        // Virtual thread per task executor — ideal replacement for newCachedThreadPool on I/O work
        try (ExecutorService vtExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                final int id = i;
                futures.add(vtExecutor.submit(() -> {
                    Thread.sleep(50);   // simulate I/O (blocking is cheap on virtual threads)
                    return "Virtual thread task " + id
                            + " (isVirtual=" + Thread.currentThread().isVirtual() + ")";
                }));
            }
            for (Future<String> f : futures) System.out.println(f.get());
        }  // executor auto-closes with try-with-resources (Java 19+)

        System.out.println();
        System.out.println("Key Virtual Thread Rules:");
        System.out.println("  ✅ Use for I/O-bound tasks (HTTP, DB, file)");
        System.out.println("  ✅ Use ReentrantLock instead of synchronized inside VT");
        System.out.println("  ❌ Not faster for CPU-bound work — same CPU time needed");
        System.out.println("  ❌ Avoid ThreadLocal with large values — millions of threads!");
        System.out.println();
    }
}
