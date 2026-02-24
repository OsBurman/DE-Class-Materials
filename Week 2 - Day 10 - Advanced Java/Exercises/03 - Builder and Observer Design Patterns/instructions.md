# Exercise 03: Builder and Observer Design Patterns

## Objective
Use the Builder pattern to construct complex objects with optional fields fluently, and implement the Observer pattern to broadcast state changes to multiple listeners.

## Background
The **Builder** pattern addresses the "telescoping constructor" problem — when a class has many optional fields, a single constructor with all parameters becomes unreadable. A nested static `Builder` class lets callers set only the fields they need using a fluent method chain ending in `build()`. The **Observer** pattern (also called Publish-Subscribe) lets a *subject* maintain a list of *observers* and notify them all when its state changes — fundamental to event systems, GUI frameworks, and reactive programming.

## Requirements

1. **Builder pattern — Pizza**:
   - Create a class `Pizza` with fields: `String size` (required), `String crust` (required), `boolean cheese`, `boolean pepperoni`, `boolean mushrooms`
   - Create a static inner class `Pizza.Builder` with setters for each field that return `this`, and a `build()` method that returns a `Pizza`
   - `Pizza` should have a `toString()` method printing: `"Pizza[size=..., crust=..., cheese=..., pepperoni=..., mushrooms=...]"`
   - In `main`, build two pizzas:
     - A fully-loaded pizza: large, thin crust, cheese + pepperoni + mushrooms
     - A plain pizza: medium, thick crust, no toppings

2. **Observer pattern — Stock price alerts**:
   - Create an interface `StockObserver` with method `void update(String symbol, double price)`
   - Create a class `StockMarket` (the subject) that holds a `String symbol`, a `double price`, and a `List<StockObserver> observers`
   - Add methods: `addObserver(StockObserver o)`, `removeObserver(StockObserver o)`, and `setPrice(double price)` — `setPrice` should update the price and call `notifyObservers()`
   - `notifyObservers()` iterates the observer list and calls `update(symbol, price)` on each
   - Create two observer implementations: `PriceAlertObserver` (prints `"ALERT: [symbol] hit $price"`) and `LoggingObserver` (prints `"LOG: [symbol] price updated to $price"`)
   - In `main`: create a `StockMarket("AAPL", 150.0)`, add both observers, call `setPrice(175.50)`, remove the `PriceAlertObserver`, call `setPrice(180.00)` — the second price change should only trigger the logger

## Hints
- The Builder's setter methods must return `this` (the Builder reference, not the Pizza) to enable chaining
- The `build()` method creates and returns a `new Pizza(this)` — use a private Pizza constructor that takes a Builder
- For Observer: `List<StockObserver>` can be an `ArrayList`; iterate with a copy to avoid `ConcurrentModificationException` if observers remove themselves during notification
- `String.format("%.2f", price)` formats the price to 2 decimal places

## Expected Output

```
=== Builder: Pizza ===
Pizza[size=large, crust=thin, cheese=true, pepperoni=true, mushrooms=true]
Pizza[size=medium, crust=thick, cheese=false, pepperoni=false, mushrooms=false]

=== Observer: Stock Market ===
ALERT: AAPL hit $175.50
LOG: AAPL price updated to $175.50
LOG: AAPL price updated to $180.00
```
