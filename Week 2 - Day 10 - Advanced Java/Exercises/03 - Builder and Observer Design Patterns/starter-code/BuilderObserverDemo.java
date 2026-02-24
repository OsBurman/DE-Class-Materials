import java.util.ArrayList;
import java.util.List;

public class BuilderObserverDemo {

    // ── Builder Pattern: Pizza ────────────────────────────────────────────────
    // TODO: Create a static inner class Pizza with fields:
    //       String size, String crust, boolean cheese, boolean pepperoni, boolean mushrooms
    //       Add a private constructor that takes a Builder and copies all fields.
    //       Add toString() returning "Pizza[size=..., crust=..., cheese=..., pepperoni=..., mushrooms=...]"
    //
    //       Inside Pizza, create static inner class Builder with:
    //       - Fields: same as Pizza
    //       - Constructor: Builder(String size, String crust)  ← required fields
    //       - Fluent setters: cheese(boolean), pepperoni(boolean), mushrooms(boolean)
    //         Each returns 'this' (the Builder) for chaining
    //       - build() method that returns new Pizza(this)


    // ── Observer Pattern: StockObserver interface ─────────────────────────────
    // TODO: Create an interface StockObserver with:
    //       void update(String symbol, double price)


    // ── Observer Pattern: StockMarket (subject) ───────────────────────────────
    // TODO: Create a class StockMarket with:
    //       - String symbol, double price, List<StockObserver> observers (ArrayList)
    //       - Constructor StockMarket(String symbol, double initialPrice)
    //       - addObserver(StockObserver o)
    //       - removeObserver(StockObserver o)
    //       - setPrice(double price) — updates price, then calls notifyObservers()
    //       - private notifyObservers() — iterates observers and calls update(symbol, price)


    // ── Observer implementations ──────────────────────────────────────────────
    // TODO: Create class PriceAlertObserver implementing StockObserver
    //       update() prints: "ALERT: [symbol] hit $" + String.format("%.2f", price)

    // TODO: Create class LoggingObserver implementing StockObserver
    //       update() prints: "LOG: [symbol] price updated to $" + String.format("%.2f", price)


    public static void main(String[] args) {

        // ── Builder demo ────────────────────────────────────────────────────
        System.out.println("=== Builder: Pizza ===");
        // TODO: Build a fully-loaded large thin-crust pizza with cheese, pepperoni, mushrooms
        //       and print it

        // TODO: Build a plain medium thick-crust pizza with no toppings and print it


        // ── Observer demo ───────────────────────────────────────────────────
        System.out.println("\n=== Observer: Stock Market ===");
        // TODO: Create StockMarket("AAPL", 150.0)
        //       Create a PriceAlertObserver and a LoggingObserver
        //       Add both observers to the stock market
        //       Call setPrice(175.50) — both observers should fire

        // TODO: Remove the PriceAlertObserver
        //       Call setPrice(180.00) — only the LoggingObserver should fire
    }
}
