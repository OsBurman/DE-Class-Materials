public class DeadlockDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Deadlock demonstration ──────────────────────────────────
        System.out.println("=== Deadlock Demonstration ===");

        final Object lockA = new Object();
        final Object lockB = new Object();

        // TODO: Create Thread t1 ("Thread-1") that:
        //       1. Acquires lockA (synchronized block), prints "[Thread-1] acquired LockA"
        //       2. Sleeps 50ms
        //       3. Attempts to acquire lockB inside the lockA block
        //       4. Catches InterruptedException and prints "[Thread-1] interrupted while waiting for LockB"
        //       (Wrap the inner synchronized(lockB) attempt in try-catch InterruptedException)

        // TODO: Create Thread t2 ("Thread-2") that:
        //       1. Acquires lockB (synchronized block), prints "[Thread-2] acquired LockB"
        //       2. Sleeps 50ms
        //       3. Attempts to acquire lockA inside the lockB block
        //       4. Catches InterruptedException and prints "[Thread-2] interrupted while waiting for LockA"

        // TODO: Create a watchdog Thread that:
        //       1. Sleeps 500ms
        //       2. Prints "Watchdog: detected deadlock — interrupting threads"
        //       3. Calls t1.interrupt() and t2.interrupt()
        //       Set the watchdog as a daemon thread (watchdog.setDaemon(true))

        // TODO: Start t1, t2, and the watchdog, then join t1 and t2


        // ── Part 2: Fixed — consistent lock ordering ─────────────────────────
        System.out.println("\n=== Deadlock Fixed (Consistent Lock Ordering) ===");

        // TODO: Create Thread fixed1 ("Thread-1") that acquires lockA first, then lockB.
        //       Print "[Thread-1] acquired LockA", "[Thread-1] acquired LockB",
        //       then "[Thread-1] completed successfully"

        // TODO: Create Thread fixed2 ("Thread-2") that also acquires lockA first, then lockB.
        //       Print "[Thread-2] acquired LockA", "[Thread-2] acquired LockB",
        //       then "[Thread-2] completed successfully"

        // TODO: Start fixed1 and fixed2, then join both


        // ── Part 3: Livelock explanation ─────────────────────────────────────
        System.out.println("\n=== What is Livelock? ===");
        // TODO: Print a multi-line explanation of livelock — what it is,
        //       how it differs from deadlock, and how to fix it.
    }
}
