import java.util.ArrayList;
import java.util.List;

public class BuilderObserverDemo {

    // ── Builder Pattern: Pizza ────────────────────────────────────────────────
    static class Pizza {
        private final String  size;
        private final String  crust;
        private final boolean cheese;
        private final boolean pepperoni;
        private final boolean mushrooms;

        // Private constructor — only the Builder can call it
        private Pizza(Builder b) {
            this.size      = b.size;
            this.crust     = b.crust;
            this.cheese    = b.cheese;
            this.pepperoni = b.pepperoni;
            this.mushrooms = b.mushrooms;
        }

        @Override
        public String toString() {
            return "Pizza[size=" + size + ", crust=" + crust +
                   ", cheese=" + cheese + ", pepperoni=" + pepperoni +
                   ", mushrooms=" + mushrooms + "]";
        }

        // Static nested Builder — fluent setters return 'this' for chaining
        static class Builder {
            private final String size;   // required
            private final String crust;  // required
            private boolean cheese    = false;
            private boolean pepperoni = false;
            private boolean mushrooms = false;

            Builder(String size, String crust) {
                this.size  = size;
                this.crust = crust;
            }

            Builder cheese(boolean val)    { this.cheese    = val; return this; }
            Builder pepperoni(boolean val) { this.pepperoni = val; return this; }
            Builder mushrooms(boolean val) { this.mushrooms = val; return this; }
            Pizza build()                  { return new Pizza(this); }
        }
    }

    // ── Observer Pattern: interface ───────────────────────────────────────────
    interface StockObserver {
        void update(String symbol, double price);
    }

    // ── Observer Pattern: Subject ─────────────────────────────────────────────
    static class StockMarket {
        private final String symbol;
        private double price;
        private final List<StockObserver> observers = new ArrayList<>();

        StockMarket(String symbol, double initialPrice) {
            this.symbol = symbol;
            this.price  = initialPrice;
        }

        void addObserver(StockObserver o)    { observers.add(o); }
        void removeObserver(StockObserver o) { observers.remove(o); }

        void setPrice(double price) {
            this.price = price;
            notifyObservers();
        }

        private void notifyObservers() {
            // Iterate over a copy to safely support remove-during-notification
            new ArrayList<>(observers).forEach(o -> o.update(symbol, price));
        }
    }

    // ── Concrete observers ────────────────────────────────────────────────────
    static class PriceAlertObserver implements StockObserver {
        @Override
        public void update(String symbol, double price) {
            System.out.printf("ALERT: %s hit $%.2f%n", symbol, price);
        }
    }

    static class LoggingObserver implements StockObserver {
        @Override
        public void update(String symbol, double price) {
            System.out.printf("LOG: %s price updated to $%.2f%n", symbol, price);
        }
    }

    public static void main(String[] args) {

        // ── Builder demo ────────────────────────────────────────────────────
        System.out.println("=== Builder: Pizza ===");
        Pizza loaded = new Pizza.Builder("large", "thin")
                .cheese(true).pepperoni(true).mushrooms(true)
                .build();
        System.out.println(loaded);

        Pizza plain = new Pizza.Builder("medium", "thick")
                .build();  // no topping setters called — defaults to false
        System.out.println(plain);

        // ── Observer demo ───────────────────────────────────────────────────
        System.out.println("\n=== Observer: Stock Market ===");
        StockMarket aapl = new StockMarket("AAPL", 150.0);

        StockObserver alertObserver  = new PriceAlertObserver();
        StockObserver logObserver    = new LoggingObserver();

        aapl.addObserver(alertObserver);
        aapl.addObserver(logObserver);
        aapl.setPrice(175.50);  // triggers both observers

        aapl.removeObserver(alertObserver); // deregister the alert
        aapl.setPrice(180.00);  // only the logger fires now
    }
}
