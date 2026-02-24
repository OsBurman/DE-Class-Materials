public class SynchronizationDemo {

    // ── Unsafe counter: no synchronization — demonstrates race condition ────
    static class UnsafeCounter {
        int count = 0;
        void increment() { count++; } // read-modify-write: NOT atomic
    }

    // ── Safe counter: synchronized method — only one thread at a time ───────
    static class SafeCounter {
        private int count = 0;
        synchronized void increment() { count++; } // 'this' is the monitor
    }

    // ── Safe counter: explicit lock object inside a synchronized block ───────
    static class BlockCounter {
        private int count = 0;
        private final Object lock = new Object(); // dedicated lock object

        void increment() {
            synchronized (lock) { count++; }
        }
    }

    // ── Volatile flag: guarantees visibility of the flag across threads ──────
    static class WorkerThread extends Thread {
        volatile boolean running = true; // volatile = changes visible to all threads immediately

        @Override
        public void run() {
            int iterations = 0;
            while (running) {
                iterations++;
            }
            System.out.println("Worker stopped after " + iterations + " iterations");
        }
    }

    // ── Helper: start threadCount threads each looping 1000 increments ───────
    static void runThreads(Runnable incrementTask, int threadCount) throws InterruptedException {
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) incrementTask.run();
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Race condition ──────────────────────────────────────────
        System.out.println("=== Race Condition Demo (Unsafe) ===");
        UnsafeCounter unsafe = new UnsafeCounter();
        runThreads(unsafe::increment, 5);
        System.out.println("Unsafe final count (expected 5000): " + unsafe.count);

        // ── Part 2: Synchronized method ────────────────────────────────────
        System.out.println("\n=== Synchronized Method Demo (Safe) ===");
        SafeCounter safe = new SafeCounter();
        runThreads(safe::increment, 5);
        System.out.println("Safe final count (expected 5000): " + safe.count);

        // ── Part 3: Synchronized block ──────────────────────────────────────
        System.out.println("\n=== Synchronized Block Demo (BlockCounter) ===");
        BlockCounter block = new BlockCounter();
        runThreads(block::increment, 5);
        System.out.println("BlockCounter final count (expected 5000): " + block.count);

        // ── Part 4: Volatile flag ───────────────────────────────────────────
        System.out.println("\n=== Volatile Flag Demo ===");
        WorkerThread worker = new WorkerThread();
        worker.start();
        Thread.sleep(50);          // let the worker run for 50ms
        worker.running = false;    // signal the worker to stop (volatile write)
        worker.join();             // wait for the worker to notice and exit
    }
}
