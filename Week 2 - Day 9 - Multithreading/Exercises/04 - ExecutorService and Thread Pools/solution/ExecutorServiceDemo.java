import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class ExecutorServiceDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // ── Part 1: Fixed thread pool with Runnable ─────────────────────────
        System.out.println("=== Fixed Thread Pool (Runnable) ===");

        // 3 threads service 6 tasks — threads are reused as tasks complete
        ExecutorService pool1 = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 6; i++) {
            final int taskNum = i;
            pool1.submit(() ->
                System.out.println("Task " + taskNum + " executed by " + Thread.currentThread().getName()));
        }
        pool1.shutdown();
        pool1.awaitTermination(5, TimeUnit.SECONDS);

        // ── Part 2: Callable and Future ─────────────────────────────────────
        System.out.println("\n=== Callable and Future ===");

        ExecutorService pool2 = Executors.newFixedThreadPool(2);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int n : new int[]{2, 3, 4, 5}) {
            final int num = n;
            // submit() accepts Callable<T> and returns Future<T>
            futures.add(pool2.submit(() -> {
                Thread.sleep(100);  // simulate computation delay
                return num * num;
            }));
        }

        // Collect results — future.get() blocks until the task finishes
        int[] nums = {2, 3, 4, 5};
        for (int i = 0; i < futures.size(); i++) {
            System.out.println("Square of " + nums[i] + " = " + futures.get(i).get());
        }
        pool2.shutdown();

        // ── Part 3: invokeAll ───────────────────────────────────────────────
        System.out.println("\n=== invokeAll ===");

        ExecutorService pool3 = Executors.newFixedThreadPool(3);
        List<Callable<String>> reportTasks = Arrays.asList(
            () -> "Report-1 generated",
            () -> "Report-2 generated",
            () -> "Report-3 generated"
        );
        // invokeAll blocks until ALL callables complete, returns List<Future<T>>
        List<Future<String>> reports = pool3.invokeAll(reportTasks);
        for (Future<String> report : reports) {
            System.out.println(report.get());
        }
        pool3.shutdown();

        // ── Part 4: ScheduledExecutorService ───────────────────────────────
        System.out.println("\n=== ScheduledExecutorService ===");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // scheduleAtFixedRate: first run at initialDelay=0, then every 200ms
        scheduler.scheduleAtFixedRate(
            () -> System.out.println("Heartbeat: " + Instant.now()),
            0, 200, TimeUnit.MILLISECONDS
        );

        Thread.sleep(700); // let the heartbeat fire ~3-4 times
        scheduler.shutdownNow(); // cancel any pending/running tasks immediately
        System.out.println("Scheduled pool shut down.");
    }
}
