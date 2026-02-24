import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class ExecutorServiceDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // ── Part 1: Fixed thread pool with Runnable ─────────────────────────
        System.out.println("=== Fixed Thread Pool (Runnable) ===");

        // TODO: Create an ExecutorService using Executors.newFixedThreadPool(3)
        //       Submit 6 Runnable tasks (using a for loop), each printing:
        //       "Task N executed by " + Thread.currentThread().getName()
        //       Call shutdown() then awaitTermination(5, TimeUnit.SECONDS) to wait


        // ── Part 2: Callable and Future ─────────────────────────────────────
        System.out.println("\n=== Callable and Future ===");

        // TODO: Create an ExecutorService using Executors.newFixedThreadPool(2)
        //       Submit 4 Callable<Integer> tasks for numbers 2, 3, 4, 5.
        //       Each callable sleeps 100ms and returns number * number.
        //       Store the Future<Integer> results in a List<Future<Integer>>.
        //       After submitting all, iterate the list and call future.get()
        //       to print "Square of N = result". Shut down after.


        // ── Part 3: invokeAll ───────────────────────────────────────────────
        System.out.println("\n=== invokeAll ===");

        // TODO: Create an ExecutorService using Executors.newFixedThreadPool(3)
        //       Build a List<Callable<String>> with 3 callables that each return
        //       "Report-1 generated", "Report-2 generated", "Report-3 generated"
        //       Call invokeAll(callables) to get List<Future<String>>.
        //       Iterate and print each future.get(). Shut down after.


        // ── Part 4: ScheduledExecutorService ───────────────────────────────
        System.out.println("\n=== ScheduledExecutorService ===");

        // TODO: Create a ScheduledExecutorService using Executors.newScheduledThreadPool(1)
        //       Use scheduleAtFixedRate to print "Heartbeat: " + Instant.now()
        //       with an initial delay of 0, period of 200, in TimeUnit.MILLISECONDS
        //       Sleep 700ms in main, then call scheduledPool.shutdownNow()
        //       Print "Scheduled pool shut down." after shutdownNow()
    }
}
