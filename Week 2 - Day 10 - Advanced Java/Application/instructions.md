# Day 10 Application — Advanced Java: Coffee Shop Order System

## Overview

You'll build a **Coffee Shop Order System** that applies design patterns (Singleton, Builder, Factory, Observer, Strategy), analyzes algorithm complexity, and uses Java serialization. Each pattern solves a real problem in the coffee shop domain.

---

## Learning Goals

- Apply Singleton, Factory, Builder, Observer, and Strategy patterns
- Analyze time and space complexity with Big O
- Use Java serialization and deserialization
- Understand Stack vs Heap memory distinctions
- Practice debugging techniques

---

## Project Structure

```
starter-code/
└── src/
    ├── Main.java
    ├── patterns/
    │   ├── CoffeeShop.java           ← TODO: Singleton
    │   ├── DrinkFactory.java         ← TODO: Factory
    │   ├── Order.java                ← TODO: Builder
    │   ├── OrderObserver.java        ← TODO: Observer interface
    │   ├── BaristaNotifier.java      ← TODO: Observer impl
    │   └── PricingStrategy.java      ← TODO: Strategy interface + impls
    └── utils/
        ├── OrderSerializer.java      ← TODO: Serialization
        └── ComplexityDemo.java       ← TODO: Big O examples
```

---

## Part 1 — Singleton: `CoffeeShop`

**Task 1**  
Make `CoffeeShop` a Singleton — private constructor, private static instance, `public static CoffeeShop getInstance()`.  
Add a `private List<OrderObserver> observers` and methods `addObserver()`, `notifyObservers(Order order)`.  
Add `placeOrder(Order order)` which notifies all observers.

---

## Part 2 — Factory: `DrinkFactory`

**Task 2**  
Create an interface `Drink` with `getName()`, `getBasePrice()`.  
Implement: `Espresso`, `Latte`, `Cappuccino`.  
`DrinkFactory.createDrink(String type)` returns the correct implementation. Throw `IllegalArgumentException` for unknown types.

---

## Part 3 — Builder: `Order`

**Task 3**  
`Order` has: `drink` (`Drink`), `size` (`String`), `extras` (`List<String>`), `customerName` (`String`), `totalPrice` (`double`).  
Use the **Builder pattern** — inner static class `Order.Builder` with fluent methods: `.withDrink()`, `.withSize()`, `.addExtra()`, `.forCustomer()`. `.build()` calculates `totalPrice` based on size (small+0, medium+0.50, large+1.00) plus extras (+0.25 each).

---

## Part 4 — Observer: `OrderObserver`

**Task 4**  
`OrderObserver` interface: `void onOrderPlaced(Order order)`.  
`BaristaNotifier` implements it — prints `"🔔 Barista notified: [customerName]'s [drink] is up!"`.  
`ReceiptPrinter` implements it — prints a full formatted receipt.

---

## Part 5 — Strategy: `PricingStrategy`

**Task 5**  
`PricingStrategy` interface: `double applyDiscount(double price)`.  
Implement: `RegularPricing` (no discount), `HappyHourPricing` (20% off), `LoyaltyPricing` (10% off).  
Add `setPricingStrategy(PricingStrategy s)` to `CoffeeShop` and use it in `placeOrder`.

---

## Part 6 — Big O & Serialization

**Task 6 — `ComplexityDemo.java`**  
Write 3 methods with different complexities and add Javadoc comments noting the Big O:
- `findOrder(List<Order> orders, String name)` — O(n) linear search  
- `hasDuplicate(List<String> items)` — O(n²) nested loop approach  
- `binaryFindSorted(String[] sorted, String target)` — O(log n) binary search

**Task 7 — `OrderSerializer.java`**  
Make `Order` implement `Serializable`. Write `serialize(Order order, String filePath)` and `deserialize(String filePath)` using `ObjectOutputStream` / `ObjectInputStream` in try-with-resources.

---

## Submission Checklist

- [ ] Singleton with `getInstance()` — only one instance created
- [ ] Factory returns correct concrete type
- [ ] Builder creates `Order` with fluent chaining
- [ ] Observer notified on every `placeOrder()` call
- [ ] Strategy swappable at runtime
- [ ] Big O noted in comments on complexity methods
- [ ] Serialization and deserialization both work
