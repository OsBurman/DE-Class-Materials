import java.util.HashMap;

public class ProductInventory {

    // ============================================================
    // TODO 1: Define InvalidProductException (extends RuntimeException — unchecked)
    //   Constructor: InvalidProductException(String productId)
    //   Message: "Product ID '[productId]' is invalid or does not exist"
    // ============================================================


    // ============================================================
    // TODO 2: Define OutOfStockException (extends Exception — CHECKED)
    //   Constructor: OutOfStockException(String productName, int requested)
    //   Message: "Cannot fulfill order: '[productName]' is out of stock (requested N units)"
    // ============================================================


    // ============================================================
    // TODO 3: Implement the ProductInventory class
    //   - Field: HashMap<String, Integer> stock
    //   - Constructor: initialize stock with "P001"->10, "P002"->2, "P003"->0
    //   - getStock(String productId): returns quantity
    //       throws InvalidProductException if productId not in map
    //   - fulfillOrder(String productId, int quantity) throws OutOfStockException:
    //       calls getStock() (let InvalidProductException propagate)
    //       throws OutOfStockException if available < quantity
    //       otherwise: reduce stock, print "Order fulfilled: N x productId"
    // ============================================================


    public static void main(String[] args) {

        // TODO 4: Create a ProductInventory instance

        // ---- Order 1: P001, quantity 5 ----
        System.out.println("=== Order 1: P001, quantity 5 ===");
        // TODO: Call fulfillOrder("P001", 5)
        //       Catch OutOfStockException and print "OutOfStockException: " + e.getMessage()
        System.out.println();

        // ---- Order 2: P002, quantity 5 (only 2 in stock) ----
        System.out.println("=== Order 2: P002, quantity 5 (only 2 in stock) ===");
        // TODO: Call fulfillOrder("P002", 5) — should trigger OutOfStockException
        //       Catch OutOfStockException and print "OutOfStockException: " + e.getMessage()
        System.out.println();

        // ---- Order 3: Invalid product ID ----
        System.out.println("=== Order 3: INVALID product ID ===");
        // TODO: Call fulfillOrder("INVALID", 1)
        //       Catch InvalidProductException (unchecked) and print "InvalidProductException: " + e.getMessage()
        //       Also catch OutOfStockException (checked, required by signature)
        System.out.println();

        // ---- Remaining stock check ----
        System.out.println("=== Remaining stock for P001 ===");
        // TODO: Call getStock("P001") and print "P001 remaining stock: " + quantity
        //       InvalidProductException is unchecked — no catch required, but add one to be safe
    }
}
