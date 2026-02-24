public class ThreadBasicsDemo {

    // ── Thread subclass ─────────────────────────────────────────────────────
    // TODO: Create a static inner class CountdownThread that extends Thread.
    //       Its run() method should print "[CountdownThread] T-minus N" for N
    //       from 5 down to 1, sleeping 100ms between each print.
    //       Catch InterruptedException inside run().


    // ── Runnable implementation ─────────────────────────────────────────────
    // TODO: Create a static inner class MessagePrinter that implements Runnable.
    //       Store a String prefix in a constructor field.
    //       Its run() method should print "[prefix] Message N" for N from 1 to 4,
    //       sleeping 80ms between each print.
    //       Catch InterruptedException inside run().


    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Thread subclass ─────────────────────────────────────────
        // TODO: Create a CountdownThread instance and set its name to "CountdownThread"


        // TODO: Print the thread's state BEFORE calling start() — should be NEW


        // TODO: Call start() on the CountdownThread


        // TODO: Print the thread's state AFTER calling start() — should be RUNNABLE


        // ── Part 2: Runnable threads ────────────────────────────────────────
        // TODO: Create two Thread objects wrapping MessagePrinter instances
        //       with prefixes "Alpha" and "Beta". Set their names accordingly.
        //       Start both threads.


        // ── Part 3: join() — wait for all threads to complete ───────────────
        // TODO: Call join() on all three threads (countdownThread, alphaThread, betaThread)


        System.out.println("All threads finished.");
    }
}
