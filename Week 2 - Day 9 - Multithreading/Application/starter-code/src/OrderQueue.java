import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread-safe shared order queue.
 * Complete all TODOs to make operations thread-safe with synchronized.
 */
public class OrderQueue {

    private final LinkedList<String> orders = new LinkedList<>();

    // TODO Task 1: Add private int ordersProcessed counter
    // Add private List<String> completedOrders

    // TODO: synchronized addOrder(String order) — adds to the queue and prints
    // confirmation
    public synchronized void addOrder(String order) {
        // orders.add(order);
        // System.out.println("📥 Order queued: " + order);
    }

    // TODO: synchronized takeOrder() — polls from the queue (returns null if empty)
    public synchronized String takeOrder() {
        return null; // orders.poll()
    }

    // TODO: synchronized incrementProcessed() — increments ordersProcessed counter
    public synchronized void incrementProcessed() {
    }

    // TODO: synchronized getOrdersProcessed() — returns ordersProcessed
    public synchronized int getOrdersProcessed() {
        return 0;
    }
}
