public class SynchronizationDemo {

    // ── Unsafe counter (no synchronization) ────────────────────────────────
    // TODO: Create a static inner class UnsafeCounter with a plain int field 'count'
    //       and a method increment() that does count++  (no synchronized keyword)

    // ── Safe counter using synchronized method ──────────────────────────────
    // TODO: Create a static inner class SafeCounter with a private int field 'count'
    //       and a synchronized void increment() method that does count++

    // ── Safe counter using synchronized block ───────────────────────────────
    // TODO: Create a static inner class BlockCounter with:
    //       - private int count
    //       - private final Object lock = new Object()
    //       - void increment() that uses a synchronized(lock) block to do count++

    // ── Volatile flag demo ──────────────────────────────────────────────────
    // TODO: Create a static inner class WorkerThread that extends Thread.
    //       Give it a field: volatile boolean running = true;
    //       Its run() should loop while running is true, incrementing a local
    //       int 'iterations' counter, then print "Worker stopped after N iterations"

    // ── Helper: run N threads each calling counter.increment() 1000 times ──
    // TODO: Create a static helper method runThreads(Runnable incrementTask, int threadCount)
    //       that creates threadCount Thread objects (each running a loop of 1000 calls
    //       to the provided task), starts them all, and joins them all.
    //       Hint: the lambda () -> { for(int i=0;i<1000;i++) incrementTask.run(); }
    //       passes the increment call as a Runnable.

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Race condition ──────────────────────────────────────────
        System.out.println("=== Race Condition Demo (Unsafe) ===");
        // TODO: Create an UnsafeCounter, run 5 threads each calling increment 1000 times,
        //       then print "Unsafe final count (expected 5000): <count>"


        // ── Part 2: Synchronized method ────────────────────────────────────
        System.out.println("\n=== Synchronized Method Demo (Safe) ===");
        // TODO: Create a SafeCounter, run 5 threads each calling increment 1000 times,
        //       then print "Safe final count (expected 5000): <count>"


        // ── Part 3: Synchronized block ──────────────────────────────────────
        System.out.println("\n=== Synchronized Block Demo (BlockCounter) ===");
        // TODO: Create a BlockCounter, run 5 threads each calling increment 1000 times,
        //       then print "BlockCounter final count (expected 5000): <count>"


        // ── Part 4: Volatile flag ───────────────────────────────────────────
        System.out.println("\n=== Volatile Flag Demo ===");
        // TODO: Create and start a WorkerThread.
        //       Sleep 50ms in main, then set worker.running = false, then join the worker.
    }
}
