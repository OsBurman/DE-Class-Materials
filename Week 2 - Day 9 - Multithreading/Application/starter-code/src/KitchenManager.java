import java.util.concurrent.*;

/**
 * Manages the kitchen using an ExecutorService thread pool.
 * Complete Tasks 4, 5, and 6.
 */
public class KitchenManager {

    private final OrderQueue queue;

    public KitchenManager(OrderQueue queue) {
        this.queue = queue;
    }

    // TODO Task 4: runKitchen()
    // - Create ExecutorService with Executors.newFixedThreadPool(3)
    // - Submit 1 OrderProducer and 3 ChefWorker tasks
    // - Call shutdown() then awaitTermination(10, TimeUnit.SECONDS)
    public void runKitchen() {
    }

    // TODO Task 5: runWithLatch()
    // - Create a CountDownLatch(3) for 3 chefs
    // - Pass latch to ChefWorkers; they call latch.countDown() when done
    // - After submitting tasks, call latch.await() then print "All orders complete!"
    public void runWithLatch() throws InterruptedException {
    }

    // TODO Task 6: prepareSpecialDish(String dish)
    // Return a CompletableFuture<String>:
    //   .supplyAsync(() -> { Thread.sleep(500); return "Prepared: " + dish; })
    //   .thenApply(result -> result + " with garnish")
    //   .thenAccept(result -> System.out.println("🍽️ " + result))
    // In Main: call this twice and combine with CompletableFuture.allOf()
    public CompletableFuture<Void> prepareSpecialDish(String dish) {
        return CompletableFuture.completedFuture(null); // replace with your implementation
    }
}
