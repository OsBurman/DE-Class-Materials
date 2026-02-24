import java.util.HashMap;

public class ProductInventory {

    // ============================================================
    // UNCHECKED custom exception — no throws declaration needed
    // Use when the error is a programming/contract violation
    // ============================================================
    static class InvalidProductException extends RuntimeException {
        public InvalidProductException(String productId) {
            super("Product ID '" + productId + "' is invalid or does not exist");
        }
    }

    // ============================================================
    // CHECKED custom exception — callers MUST handle or rethrow
    // Use when the error is a recoverable business condition
    // ============================================================
    static class OutOfStockException extends Exception {
        public OutOfStockException(String productName, int requested) {
            super("Cannot fulfill order: '" + productName + "' is out of stock (requested " + requested + " units)");
        }
    }

    // ============================================================
    // Inventory domain class
    // ============================================================
    private final HashMap<String, Integer> stock = new HashMap<>();

    public ProductInventory() {
        // Seed inventory: P001 has plenty, P002 is low, P003 is empty
        stock.put("P001", 10);
        stock.put("P002", 2);
        stock.put("P003", 0);
    }

    // Returns current quantity — throws unchecked if ID unknown
    public int getStock(String productId) {
        if (!stock.containsKey(productId)) {
            throw new InvalidProductException(productId);
        }
        return stock.get(productId);
    }

    // Fulfills an order — throws checked OutOfStockException, unchecked InvalidProductException
    public void fulfillOrder(String productId, int quantity) throws OutOfStockException {
        int available = getStock(productId);  // InvalidProductException propagates unchecked
        if (available < quantity) {
            throw new OutOfStockException(productId, quantity);
        }
        stock.put(productId, available - quantity);
        System.out.println("Order fulfilled: " + quantity + " x " + productId);
    }

    public static void main(String[] args) {

        ProductInventory inventory = new ProductInventory();

        // ---- Order 1: P001, quantity 5 ----
        System.out.println("=== Order 1: P001, quantity 5 ===");
        try {
            inventory.fulfillOrder("P001", 5);
        } catch (OutOfStockException e) {
            System.out.println("OutOfStockException: " + e.getMessage());
        }
        System.out.println();

        // ---- Order 2: P002, quantity 5 (only 2 in stock) ----
        System.out.println("=== Order 2: P002, quantity 5 (only 2 in stock) ===");
        try {
            inventory.fulfillOrder("P002", 5);
        } catch (OutOfStockException e) {
            System.out.println("OutOfStockException: " + e.getMessage());
        }
        System.out.println();

        // ---- Order 3: Invalid product ID ----
        System.out.println("=== Order 3: INVALID product ID ===");
        try {
            inventory.fulfillOrder("INVALID", 1);
        } catch (InvalidProductException e) {
            // Catching unchecked first (more specific) before the checked one
            System.out.println("InvalidProductException: " + e.getMessage());
        } catch (OutOfStockException e) {
            System.out.println("OutOfStockException: " + e.getMessage());
        }
        System.out.println();

        // ---- Remaining stock check ----
        System.out.println("=== Remaining stock for P001 ===");
        System.out.println("P001 remaining stock: " + inventory.getStock("P001"));
    }
}
