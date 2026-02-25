package com.academy;

import java.util.*;

/**
 * Day 10 Part 1 — Big O, Design Patterns: Singleton, Factory, Builder, Observer, Strategy
 *
 * Theme: Coffee Shop Management System
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  Day 10 Part 1 — Design Patterns Demo               ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        demoBigO();
        demoSingleton();
        demoFactory();
        demoBuilder();
        demoObserver();
        demoStrategy();
    }

    static void demoBigO() {
        System.out.println("=== Big O Notation ===");
        System.out.println("  O(1)      — constant:    array[5] access");
        System.out.println("  O(log n)  — logarithmic: binary search");
        System.out.println("  O(n)      — linear:      linear search, single loop");
        System.out.println("  O(n log n)— linearithmic: merge sort, quick sort (avg)");
        System.out.println("  O(n²)     — quadratic:   nested loops, bubble sort");
        System.out.println("  O(2ⁿ)     — exponential: recursive fibonacci (naive)");
        System.out.println();
    }

    static void demoSingleton() {
        System.out.println("=== 1. Singleton Pattern ===");
        System.out.println("  Purpose: Ensure only ONE instance of a class exists");
        CoffeeShopConfig cfg1 = CoffeeShopConfig.getInstance();
        CoffeeShopConfig cfg2 = CoffeeShopConfig.getInstance();
        cfg1.set("shopName", "Academy Coffee");
        System.out.println("  cfg1 == cfg2: " + (cfg1 == cfg2) + "  (same instance)");
        System.out.println("  cfg2.get(shopName): " + cfg2.get("shopName") + "  (shared state)");
        System.out.println();
    }

    static void demoFactory() {
        System.out.println("=== 2. Factory Pattern ===");
        System.out.println("  Purpose: Create objects without exposing creation logic");
        Drink espresso  = DrinkFactory.create("ESPRESSO");
        Drink latte     = DrinkFactory.create("LATTE");
        Drink tea       = DrinkFactory.create("TEA");
        espresso.prepare(); latte.prepare(); tea.prepare();
        System.out.println();
    }

    static void demoBuilder() {
        System.out.println("=== 3. Builder Pattern ===");
        System.out.println("  Purpose: Build complex objects step-by-step");
        Order order = new Order.Builder("Americano")
            .size("Large").milk("Oat").shots(2).temperature("Hot").sugar(1).build();
        System.out.println("  " + order);

        Order simple = new Order.Builder("Espresso").build();
        System.out.println("  " + simple);
        System.out.println();
    }

    static void demoObserver() {
        System.out.println("=== 4. Observer Pattern ===");
        System.out.println("  Purpose: Notify multiple observers when state changes");
        OrderBoard board = new OrderBoard();
        board.addObserver(name -> System.out.println("  [Barista Display] New order: " + name));
        board.addObserver(name -> System.out.println("  [Kitchen Printer] Ticket: " + name));
        board.addObserver(name -> System.out.println("  [Mobile App]      Push: Your order is ready: " + name));

        board.placeOrder("Latte for Alice");
        board.placeOrder("Espresso for Bob");
        System.out.println();
    }

    static void demoStrategy() {
        System.out.println("=== 5. Strategy Pattern ===");
        System.out.println("  Purpose: Define a family of algorithms, make them interchangeable");
        PriceCalculator calc = new PriceCalculator();

        calc.setStrategy(price -> price);                              // no discount
        System.out.printf("  Regular price:    $%.2f%n", calc.calculate(10.00));

        calc.setStrategy(price -> price * 0.90);                      // 10% off
        System.out.printf("  Member price:     $%.2f%n", calc.calculate(10.00));

        calc.setStrategy(price -> price > 20 ? price * 0.80 : price); // bulk discount
        System.out.printf("  Bulk price ($25): $%.2f%n", calc.calculate(25.00));
        System.out.println("\n✓ Design Patterns demo complete.");
    }
}

// ── Singleton ───────────────────────────────────────────────
class CoffeeShopConfig {
    private static CoffeeShopConfig INSTANCE;
    private final Map<String, String> config = new HashMap<>();
    private CoffeeShopConfig() {}
    public static synchronized CoffeeShopConfig getInstance() {
        if (INSTANCE == null) INSTANCE = new CoffeeShopConfig();
        return INSTANCE;
    }
    public void set(String k, String v) { config.put(k, v); }
    public String get(String k)         { return config.get(k); }
}

// ── Factory ──────────────────────────────────────────────────
interface Drink { void prepare(); }
class Espresso implements Drink { public void prepare() { System.out.println("  Preparing: Espresso — grind 18g, extract 30s"); } }
class Latte     implements Drink { public void prepare() { System.out.println("  Preparing: Latte — espresso + steam 150ml milk"); } }
class Tea       implements Drink { public void prepare() { System.out.println("  Preparing: Tea — steep 90°C for 3 min"); } }
class DrinkFactory {
    static Drink create(String type) {
        return switch (type) {
            case "ESPRESSO" -> new Espresso();
            case "LATTE"    -> new Latte();
            case "TEA"      -> new Tea();
            default         -> throw new IllegalArgumentException("Unknown drink: " + type);
        };
    }
}

// ── Builder ──────────────────────────────────────────────────
class Order {
    private final String name; private final String size; private final String milk;
    private final int shots; private final String temp; private final int sugar;
    private Order(Builder b) { name=b.name; size=b.size; milk=b.milk; shots=b.shots; temp=b.temp; sugar=b.sugar; }
    public String toString() { return "Order[" + name + " " + size + " " + temp + ", " + shots + " shots, " + milk + " milk, " + sugar + " sugar]"; }
    static class Builder {
        private final String name; private String size="Regular"; private String milk="None";
        private int shots=1; private String temp="Hot"; private int sugar=0;
        public Builder(String name) { this.name = name; }
        public Builder size(String s)        { this.size=s;   return this; }
        public Builder milk(String m)        { this.milk=m;   return this; }
        public Builder shots(int s)          { this.shots=s;  return this; }
        public Builder temperature(String t) { this.temp=t;   return this; }
        public Builder sugar(int s)          { this.sugar=s;  return this; }
        public Order build()                 { return new Order(this); }
    }
}

// ── Observer ─────────────────────────────────────────────────
class OrderBoard {
    private final List<java.util.function.Consumer<String>> observers = new ArrayList<>();
    void addObserver(java.util.function.Consumer<String> o) { observers.add(o); }
    void placeOrder(String name) { observers.forEach(o -> o.accept(name)); }
}

// ── Strategy ─────────────────────────────────────────────────
class PriceCalculator {
    private java.util.function.Function<Double, Double> strategy;
    void setStrategy(java.util.function.Function<Double, Double> s) { this.strategy = s; }
    double calculate(double price) { return strategy.apply(price); }
}
