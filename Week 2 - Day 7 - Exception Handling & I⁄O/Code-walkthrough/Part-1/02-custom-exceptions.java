/**
 * Day 7 - Part 1: Custom Exceptions
 *
 * WHY WRITE CUSTOM EXCEPTIONS?
 *   - Built-in exceptions like IllegalArgumentException and IOException are generic.
 *   - Custom exceptions give your error messages DOMAIN MEANING:
 *       InsufficientFundsException is far more descriptive than RuntimeException.
 *   - They let callers catch specific exception types and handle them differently.
 *   - They can carry extra fields (e.g., error code, account number, retry flag).
 *
 * TWO KINDS:
 *   1. Checked   — extend Exception. Caller MUST handle or re-declare.
 *                  Use when the caller can reasonably recover (e.g., prompt retry).
 *   2. Unchecked — extend RuntimeException. Caller CAN handle but isn't forced to.
 *                  Use for programming errors or conditions the caller can't recover from.
 */

// =============================================================================
// CUSTOM EXCEPTION 1 — Checked Exception (extends Exception)
// =============================================================================

/**
 * InsufficientFundsException is a CHECKED exception.
 * It extends Exception, so any method that throws it MUST declare 'throws InsufficientFundsException'.
 * Callers must either catch it or re-declare it.
 *
 * Best Practice: Provide at least these three constructors so the exception
 * plays well with the rest of the Java ecosystem.
 */
class InsufficientFundsException extends Exception {

    // Custom field: how much was requested vs how much was available
    private final double requested;
    private final double available;

    // Constructor 1: just a message
    public InsufficientFundsException(String message) {
        super(message);
        this.requested = 0;
        this.available = 0;
    }

    // Constructor 2: message + domain-specific context
    public InsufficientFundsException(double requested, double available) {
        super(String.format("Insufficient funds: requested $%.2f but only $%.2f available.",
                requested, available));
        this.requested = requested;
        this.available = available;
    }

    // Constructor 3: message + cause (for chaining)
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
        this.requested = 0;
        this.available = 0;
    }

    // Custom getters — callers can inspect the details
    public double getRequested() { return requested; }
    public double getAvailable() { return available; }
    public double getShortfall()  { return requested - available; }
}


// =============================================================================
// CUSTOM EXCEPTION 2 — Unchecked Exception (extends RuntimeException)
// =============================================================================

/**
 * InvalidProductException is an UNCHECKED exception.
 * Extends RuntimeException — no 'throws' declaration required.
 * Use this for programming errors: bad product IDs, null names, etc.
 * These represent bugs or invalid inputs that should never reach production.
 */
class InvalidProductException extends RuntimeException {

    private final String productId;
    private final String reason;

    public InvalidProductException(String productId, String reason) {
        super("Invalid product [" + productId + "]: " + reason);
        this.productId = productId;
        this.reason = reason;
    }

    // Chain the original cause
    public InvalidProductException(String productId, String reason, Throwable cause) {
        super("Invalid product [" + productId + "]: " + reason, cause);
        this.productId = productId;
        this.reason = reason;
    }

    public String getProductId() { return productId; }
    public String getReason()    { return reason; }
}


// =============================================================================
// CUSTOM EXCEPTION 3 — Exception with Error Code (common in APIs)
// =============================================================================

/**
 * ApiException carries an HTTP-style status code.
 * This pattern is common in REST APIs and service layers.
 * Checked exception because the caller (controller layer) must always handle it.
 */
class ApiException extends Exception {

    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return statusCode; }

    @Override
    public String toString() {
        return "ApiException [HTTP " + statusCode + "]: " + getMessage();
    }
}


// =============================================================================
// DOMAIN CLASSES that USE the custom exceptions
// =============================================================================

class BankAccount {
    private final String owner;
    private double balance;

    public BankAccount(String owner, double initialBalance) {
        this.owner = owner;
        this.balance = initialBalance;
    }

    /**
     * withdraw() throws our CHECKED InsufficientFundsException.
     * The 'throws' keyword in the signature is part of the public contract —
     * it tells every caller "you MUST handle this case".
     */
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            // Unchecked — this is a programming error, not a business scenario
            throw new IllegalArgumentException("Withdrawal amount must be positive: " + amount);
        }
        if (amount > balance) {
            // Checked — this is a real business scenario the caller should handle
            throw new InsufficientFundsException(amount, balance);
        }
        balance -= amount;
        System.out.printf("  Withdrew $%.2f. New balance: $%.2f%n", amount, balance);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive: " + amount);
        }
        balance += amount;
        System.out.printf("  Deposited $%.2f. New balance: $%.2f%n", amount, balance);
    }

    public double getBalance() { return balance; }
    public String getOwner()   { return owner; }
}


class ProductService {

    /**
     * Demonstrates an UNCHECKED custom exception.
     * No 'throws' declaration needed — but we document it with @throws in Javadoc.
     *
     * @throws InvalidProductException if productId is null/empty or price is negative
     */
    public void addProduct(String productId, String name, double price) {
        if (productId == null || productId.isBlank()) {
            throw new InvalidProductException("null/blank", "Product ID cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidProductException(productId, "Product name cannot be null or empty");
        }
        if (price < 0) {
            throw new InvalidProductException(productId,
                    "Price cannot be negative — got: " + price);
        }
        System.out.println("  Product added: [" + productId + "] " + name + " @ $" + price);
    }
}


class UserApiService {

    /**
     * Demonstrates a checked exception with an error code.
     * The service layer throws ApiException; the controller layer catches it
     * and maps it to an HTTP response.
     */
    public String getUser(int userId) throws ApiException {
        if (userId <= 0) {
            throw new ApiException(400, "User ID must be a positive integer, got: " + userId);
        }
        if (userId == 999) {
            // Simulate a "not found" scenario
            throw new ApiException(404, "User with ID " + userId + " not found");
        }
        return "User{id=" + userId + ", name='Jane Doe'}";
    }
}


// =============================================================================
// MAIN — putting it all together
// =============================================================================

public class CustomExceptions {

    // -------------------------------------------------------------------------
    // Demo 1: Checked exception — InsufficientFundsException
    // -------------------------------------------------------------------------

    static void demonstrateBankAccount() {
        System.out.println("=== Demo 1: Checked Exception — InsufficientFundsException ===");
        BankAccount account = new BankAccount("Alice", 500.00);
        System.out.println("  Account: " + account.getOwner() + ", Balance: $" + account.getBalance());

        // Successful withdrawal
        try {
            account.withdraw(200.00);
        } catch (InsufficientFundsException e) {
            System.out.println("  Should not happen here.");
        }

        // Failed withdrawal — insufficient funds
        try {
            account.withdraw(400.00); // only $300 left
        } catch (InsufficientFundsException e) {
            System.out.println("  Caught: " + e.getMessage());
            System.out.printf("  Shortfall: $%.2f%n", e.getShortfall());
            System.out.println("  → Suggest the user deposit at least $" + e.getShortfall() + " first.");
        }

        // Bad argument — unchecked
        try {
            account.withdraw(-50);
        } catch (IllegalArgumentException e) {
            System.out.println("  Caught IAE: " + e.getMessage());
        }

        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Demo 2: Unchecked exception — InvalidProductException
    // -------------------------------------------------------------------------

    static void demonstrateProductService() {
        System.out.println("=== Demo 2: Unchecked Exception — InvalidProductException ===");
        ProductService service = new ProductService();

        // Valid product
        service.addProduct("SKU-001", "Wireless Headphones", 79.99);

        // Bad product ID
        try {
            service.addProduct("", "Widget", 9.99);
        } catch (InvalidProductException e) {
            System.out.println("  Caught: " + e.getMessage());
            System.out.println("  Product ID was: \"" + e.getProductId() + "\"");
        }

        // Negative price
        try {
            service.addProduct("SKU-002", "Gadget", -5.00);
        } catch (InvalidProductException e) {
            System.out.println("  Caught: " + e.getMessage());
            System.out.println("  Reason: " + e.getReason());
        }

        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Demo 3: Exception with error code — ApiException
    // -------------------------------------------------------------------------

    static void demonstrateApiExceptions() {
        System.out.println("=== Demo 3: Checked Exception with Error Code — ApiException ===");
        UserApiService apiService = new UserApiService();

        int[] userIds = {1, -5, 999};
        for (int id : userIds) {
            try {
                String user = apiService.getUser(id);
                System.out.println("  Found: " + user);
            } catch (ApiException e) {
                System.out.println("  " + e); // uses our custom toString()
                if (e.getStatusCode() == 404) {
                    System.out.println("    → Return a 'not found' response to the client.");
                } else if (e.getStatusCode() == 400) {
                    System.out.println("    → Return a 'bad request' response to the client.");
                }
            }
        }

        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Demo 4: Re-throwing — catching one exception, wrapping in a custom one
    // -------------------------------------------------------------------------

    static void demonstrateReThrowingAsCustom() {
        System.out.println("=== Demo 4: Re-throwing as Custom Exception (chaining) ===");

        // Simulates a service that wraps a low-level exception in a domain exception
        try {
            loadConfig("config.properties");
        } catch (ApiException e) {
            System.out.println("  High-level error: " + e);
            System.out.println("  Root cause: " + e.getCause().getClass().getSimpleName()
                    + " — " + e.getCause().getMessage());
        }

        System.out.println();
    }

    /**
     * Loads a config file. Wraps any low-level IOException in an ApiException
     * so the rest of the application only needs to deal with ApiException.
     */
    static void loadConfig(String filename) throws ApiException {
        try {
            // Simulate a file-not-found IOException
            java.io.FileReader fr = new java.io.FileReader("/nonexistent/" + filename);
        } catch (java.io.FileNotFoundException e) {
            // Re-throw as our domain exception, chaining the original cause
            throw new ApiException(500,
                    "Application startup failed: config file '" + filename + "' not found.", e);
        }
    }

    // -------------------------------------------------------------------------
    // Demo 5: Exception hierarchy — catch parent catches all children
    // -------------------------------------------------------------------------

    static void demonstrateHierarchyCatching() {
        System.out.println("=== Demo 5: Catching Parent Exception Type ===");
        System.out.println("  InsufficientFundsException IS-A Exception → can be caught by catch(Exception e)");

        BankAccount acct = new BankAccount("Bob", 100.00);
        try {
            acct.withdraw(999.00);
        } catch (Exception e) {
            // Catches InsufficientFundsException because it extends Exception
            System.out.println("  Caught as Exception: " + e.getMessage());
            System.out.println("  Actual type: " + e.getClass().getSimpleName());

            // We can downcast to access custom fields if needed
            if (e instanceof InsufficientFundsException ife) { // Java 16+ pattern matching
                System.out.printf("  Shortfall (via instanceof check): $%.2f%n", ife.getShortfall());
            }
        }

        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║    Day 7 - Part 1: Custom Exceptions             ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        demonstrateBankAccount();
        demonstrateProductService();
        demonstrateApiExceptions();
        demonstrateReThrowingAsCustom();
        demonstrateHierarchyCatching();
    }
}
