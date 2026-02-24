import java.util.*;
import java.util.function.*;

/**
 * DAY 10 — PART 1 | Common Design Patterns
 * ─────────────────────────────────────────────────────────────────────────────
 * Design patterns are proven, reusable solutions to common software design problems.
 * Coined by the "Gang of Four" (GoF) — Gamma, Helm, Johnson, Vlissides (1994).
 *
 * CATEGORIES:
 *   Creational — how objects are created     (Singleton, Factory, Builder)
 *   Structural — how objects are composed    (Adapter, Decorator, Facade)
 *   Behavioral — how objects communicate     (Observer, Strategy, Command)
 *
 * TODAY: Singleton, Factory, Builder, Observer, Strategy
 */
public class DesignPatterns {

    public static void main(String[] args) {
        demonstrateSingleton();
        demonstrateFactory();
        demonstrateBuilder();
        demonstrateObserver();
        demonstrateStrategy();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PATTERN 1 — SINGLETON (Creational)
    // Ensure a class has exactly ONE instance, provide global access to it.
    // Use cases: database connection pool, config manager, logger
    // ═════════════════════════════════════════════════════════════════════════
    static void demonstrateSingleton() {
        System.out.println("=== SINGLETON ===");

        // Both calls return the SAME instance
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();

        config1.set("db.url", "jdbc:postgresql://localhost/appdb");
        System.out.println("config2 can see db.url: " + config2.get("db.url"));
        System.out.println("Same instance? " + (config1 == config2) + "\n");  // true
    }

    /**
     * Thread-safe Singleton using "initialization-on-demand holder" idiom.
     * The inner class is only loaded when getInstance() is first called —
     * so there's no synchronization overhead on subsequent calls.
     */
    static class AppConfig {
        private final Map<String, String> properties = new HashMap<>();

        // Private constructor — prevents anyone calling new AppConfig()
        private AppConfig() {}

        // Inner holder class — loaded lazily, class loading is thread-safe
        private static class Holder {
            static final AppConfig INSTANCE = new AppConfig();
        }

        public static AppConfig getInstance() {
            return Holder.INSTANCE;
        }

        public void set(String key, String value) { properties.put(key, value); }
        public String get(String key) { return properties.getOrDefault(key, "(not set)"); }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PATTERN 2 — FACTORY METHOD (Creational)
    // Define an interface for creating an object, but let subclasses (or
    // a factory method) decide which class to instantiate.
    // Use cases: payment processors, notification channels, file parsers
    // ═════════════════════════════════════════════════════════════════════════
    static void demonstrateFactory() {
        System.out.println("=== FACTORY ===");

        // Client code asks the factory for a notifier — doesn't know the concrete class
        Notifier email = NotifierFactory.create("EMAIL");
        Notifier sms   = NotifierFactory.create("SMS");
        Notifier push  = NotifierFactory.create("PUSH");

        email.send("alice@example.com", "Your order shipped!");
        sms.send("+1-555-0100",         "Your order shipped!");
        push.send("device-token-abc",   "Your order shipped!");

        System.out.println("The client code calls send() without knowing EmailNotifier or SmsNotifier.\n");
    }

    // Product interface
    interface Notifier {
        void send(String recipient, String message);
    }

    // Concrete products
    static class EmailNotifier implements Notifier {
        @Override public void send(String recipient, String message) {
            System.out.println("[EMAIL] To: " + recipient + " | " + message);
        }
    }

    static class SmsNotifier implements Notifier {
        @Override public void send(String recipient, String message) {
            System.out.println("[SMS]   To: " + recipient + " | " + message);
        }
    }

    static class PushNotifier implements Notifier {
        @Override public void send(String recipient, String message) {
            System.out.println("[PUSH]  To: " + recipient + " | " + message);
        }
    }

    // Factory — centralises the creation decision
    static class NotifierFactory {
        public static Notifier create(String type) {
            return switch (type.toUpperCase()) {
                case "EMAIL" -> new EmailNotifier();
                case "SMS"   -> new SmsNotifier();
                case "PUSH"  -> new PushNotifier();
                default      -> throw new IllegalArgumentException("Unknown notifier: " + type);
            };
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PATTERN 3 — BUILDER (Creational)
    // Construct complex objects step-by-step.
    // Solves the "telescoping constructor" anti-pattern.
    // Use cases: HTTP requests, SQL queries, domain objects with many optional fields
    // ═════════════════════════════════════════════════════════════════════════
    static void demonstrateBuilder() {
        System.out.println("=== BUILDER ===");

        // Without Builder: what do all these booleans mean?
        // new User("Alice", "alice@co.com", true, false, null, 30, "US")  // confusing!

        // With Builder: self-documenting, optional fields are omitted
        User alice = new User.Builder("Alice", "alice@example.com")
                .age(30)
                .country("US")
                .emailVerified(true)
                .build();

        User guest = new User.Builder("Guest", "")
                .build();   // only required fields

        System.out.println(alice);
        System.out.println(guest);
        System.out.println("Builder pattern: fluent, readable, easy to add optional fields.\n");
    }

    static class User {
        // Required fields
        private final String name;
        private final String email;
        // Optional fields
        private final int age;
        private final String country;
        private final boolean emailVerified;

        private User(Builder b) {
            this.name          = b.name;
            this.email         = b.email;
            this.age           = b.age;
            this.country       = b.country;
            this.emailVerified = b.emailVerified;
        }

        @Override public String toString() {
            return "User{name='" + name + "', email='" + email + "', age=" + age +
                    ", country='" + country + "', emailVerified=" + emailVerified + "}";
        }

        static class Builder {
            // Required
            private final String name;
            private final String email;
            // Optional — set defaults here
            private int age = 0;
            private String country = "UNKNOWN";
            private boolean emailVerified = false;

            Builder(String name, String email) {
                this.name  = name;
                this.email = email;
            }

            // Each setter returns 'this' to allow method chaining
            Builder age(int age)                       { this.age = age; return this; }
            Builder country(String country)             { this.country = country; return this; }
            Builder emailVerified(boolean v)            { this.emailVerified = v; return this; }

            User build() {
                // Validate before building
                if (name == null || name.isBlank()) throw new IllegalStateException("name required");
                return new User(this);
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PATTERN 4 — OBSERVER (Behavioral)
    // Define a one-to-many dependency: when one object changes state,
    // all dependents are notified automatically.
    // Use cases: event systems, UI data binding, domain events in microservices
    // ═════════════════════════════════════════════════════════════════════════
    static void demonstrateObserver() {
        System.out.println("=== OBSERVER ===");

        // Subject: the stock price feed
        StockFeed appleStock = new StockFeed("AAPL");

        // Observers: different systems that react to price changes
        PriceAlertSystem alert     = new PriceAlertSystem(180.0);  // alert if price > $180
        TradingDashboard dashboard = new TradingDashboard();
        AuditLogger auditLog       = new AuditLogger();

        // Subscribe all observers to the feed
        appleStock.subscribe(alert);
        appleStock.subscribe(dashboard);
        appleStock.subscribe(auditLog);

        // Simulate price changes — observers are notified automatically
        System.out.println("--- Price update: $175 ---");
        appleStock.updatePrice(175.0);

        System.out.println("\n--- Price update: $185 ---");
        appleStock.updatePrice(185.0);

        // Unsubscribe the dashboard
        appleStock.unsubscribe(dashboard);
        System.out.println("\n--- Price update: $190 (dashboard unsubscribed) ---");
        appleStock.updatePrice(190.0);
        System.out.println();
    }

    // Observer interface
    interface PriceObserver {
        void onPriceChange(String ticker, double newPrice);
    }

    // Concrete Subject
    static class StockFeed {
        private final String ticker;
        private double currentPrice;
        private final List<PriceObserver> observers = new ArrayList<>();

        StockFeed(String ticker) { this.ticker = ticker; }

        void subscribe(PriceObserver observer)   { observers.add(observer); }
        void unsubscribe(PriceObserver observer) { observers.remove(observer); }

        void updatePrice(double newPrice) {
            this.currentPrice = newPrice;
            notifyObservers();   // broadcast to all subscribers
        }

        private void notifyObservers() {
            for (PriceObserver o : observers) o.onPriceChange(ticker, currentPrice);
        }
    }

    // Concrete Observers
    static class PriceAlertSystem implements PriceObserver {
        private final double alertThreshold;
        PriceAlertSystem(double threshold) { this.alertThreshold = threshold; }

        @Override public void onPriceChange(String ticker, double price) {
            if (price > alertThreshold)
                System.out.printf("  🔔 ALERT:     %s hit $%.2f (threshold $%.2f)%n",
                        ticker, price, alertThreshold);
        }
    }

    static class TradingDashboard implements PriceObserver {
        @Override public void onPriceChange(String ticker, double price) {
            System.out.printf("  📊 DASHBOARD: %s = $%.2f%n", ticker, price);
        }
    }

    static class AuditLogger implements PriceObserver {
        @Override public void onPriceChange(String ticker, double price) {
            System.out.printf("  📋 AUDIT LOG: %s price changed to $%.2f%n", ticker, price);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PATTERN 5 — STRATEGY (Behavioral)
    // Define a family of algorithms, encapsulate each one, and make them
    // interchangeable. Strategy lets the algorithm vary independently from
    // clients that use it.
    // Use cases: sorting, compression, payment processing, discount calculation
    // ═════════════════════════════════════════════════════════════════════════
    static void demonstrateStrategy() {
        System.out.println("=== STRATEGY ===");

        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Laptop",   999.99);
        cart.addItem("Mouse",     29.99);
        cart.addItem("Keyboard",  79.99);

        System.out.println("Original total: $" + String.format("%.2f", cart.getTotal()));

        // Swap strategies at runtime — cart code doesn't change
        cart.setDiscountStrategy(new PercentageDiscount(10));   // 10% off
        System.out.println("After 10% off:  $" + String.format("%.2f", cart.applyDiscount()));

        cart.setDiscountStrategy(new FixedAmountDiscount(50));  // $50 off
        System.out.println("After $50 off:  $" + String.format("%.2f", cart.applyDiscount()));

        cart.setDiscountStrategy(new MemberDiscount("GOLD"));   // gold member pricing
        System.out.println("Gold member:    $" + String.format("%.2f", cart.applyDiscount()));

        // Can also use lambdas — Strategy is a functional interface!
        cart.setDiscountStrategy(total -> total > 500 ? total * 0.85 : total);  // 15% if > $500
        System.out.println("Lambda 15%:     $" + String.format("%.2f", cart.applyDiscount()));
        System.out.println();
    }

    // Strategy interface — could also be written as a @FunctionalInterface
    interface DiscountStrategy {
        double apply(double totalPrice);
    }

    // Concrete strategies
    static class PercentageDiscount implements DiscountStrategy {
        private final double percent;
        PercentageDiscount(double percent) { this.percent = percent; }
        @Override public double apply(double total) { return total * (1 - percent / 100); }
    }

    static class FixedAmountDiscount implements DiscountStrategy {
        private final double amount;
        FixedAmountDiscount(double amount) { this.amount = amount; }
        @Override public double apply(double total) { return Math.max(0, total - amount); }
    }

    static class MemberDiscount implements DiscountStrategy {
        private final String tier;
        MemberDiscount(String tier) { this.tier = tier; }
        @Override public double apply(double total) {
            return switch (tier) {
                case "GOLD"     -> total * 0.80;   // 20% off
                case "SILVER"   -> total * 0.90;   // 10% off
                default         -> total;
            };
        }
    }

    // Context class that uses a Strategy
    static class ShoppingCart {
        private final Map<String, Double> items = new LinkedHashMap<>();
        private DiscountStrategy discountStrategy = total -> total;  // default: no discount

        void addItem(String name, double price)          { items.put(name, price); }
        void setDiscountStrategy(DiscountStrategy s)     { this.discountStrategy = s; }
        double getTotal()    { return items.values().stream().mapToDouble(Double::doubleValue).sum(); }
        double applyDiscount() { return discountStrategy.apply(getTotal()); }
    }
}
