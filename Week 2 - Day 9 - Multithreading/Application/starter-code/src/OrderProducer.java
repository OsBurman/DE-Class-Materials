/**
 * Produces orders and adds them to the shared queue.
 * TODO Task 2: Implement the Runnable interface.
 * In run(): loop 5 times, generate an order string, call queue.addOrder(), sleep 200ms.
 */
public class OrderProducer implements Runnable {

    private final OrderQueue queue;
    private final String[] menuItems = {"Espresso", "Latte", "Cappuccino", "Mocha", "Americano"};

    public OrderProducer(OrderQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        // TODO: loop 5 times
        //   String order = "Order-" + i + ": " + menuItems[i % menuItems.length];
        //   queue.addOrder(order);
        //   Thread.sleep(200); (wrap in try-catch InterruptedException)
    }
}
