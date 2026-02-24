# Exercise 03: Custom Exceptions in a Product Inventory System

## Objective
Design and use custom checked and unchecked exceptions to model domain-specific error conditions in a product inventory system.

## Background
Real applications don't just throw generic Java exceptions — they define their own exception types that carry meaningful business context. A well-named custom exception like `OutOfStockException` immediately tells the next developer (or the API consumer) exactly what went wrong. This exercise covers both a **checked** custom exception (the compiler forces callers to handle it) and an **unchecked** one (a programming contract violation).

## Requirements

1. **Define `InvalidProductException`** — an **unchecked** exception (extends `RuntimeException`):
   - Constructor accepts a `String productId`
   - Message: `"Product ID '[productId]' is invalid or does not exist"`

2. **Define `OutOfStockException`** — a **checked** exception (extends `Exception`):
   - Constructor accepts `String productName` and `int requested`
   - Message: `"Cannot fulfill order: '[productName]' is out of stock (requested [N] units)"`

3. **Define a `ProductInventory` class** with:
   - A `HashMap<String, Integer>` mapping product ID → quantity (e.g., `"P001" → 10`, `"P002" → 2`, `"P003" → 0`)
   - Method `getStock(String productId)`: returns quantity; throws `InvalidProductException` if the ID is not in the map
   - Method `fulfillOrder(String productId, int quantity) throws OutOfStockException`: calls `getStock()` first (letting `InvalidProductException` propagate unchecked); throws `OutOfStockException` if available stock < requested quantity; otherwise reduces stock and prints `"Order fulfilled: [N] x [productId]"`

4. In `main`, perform these calls with appropriate try-catch blocks:
   - `fulfillOrder("P001", 5)` — success
   - `fulfillOrder("P002", 5)` — triggers `OutOfStockException` (only 2 in stock)
   - `fulfillOrder("INVALID", 1)` — triggers `InvalidProductException`
   - After all three, call `getStock("P001")` and print the remaining stock

## Hints
- `OutOfStockException` extends `Exception` (checked) — any method that might throw it must declare `throws OutOfStockException` in its signature
- `InvalidProductException` extends `RuntimeException` (unchecked) — no `throws` declaration needed
- In `main`, you only need to catch `OutOfStockException` explicitly; `InvalidProductException` is unchecked so you can catch it separately or let it propagate
- Use `HashMap.containsKey()` to check if a product ID exists

## Expected Output

```
=== Order 1: P001, quantity 5 ===
Order fulfilled: 5 x P001

=== Order 2: P002, quantity 5 (only 2 in stock) ===
OutOfStockException: Cannot fulfill order: 'P002' is out of stock (requested 5 units)

=== Order 3: INVALID product ID ===
InvalidProductException: Product ID 'INVALID' is invalid or does not exist

=== Remaining stock for P001 ===
P001 remaining stock: 5
```
