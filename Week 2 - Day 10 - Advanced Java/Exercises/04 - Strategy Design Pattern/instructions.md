# Exercise 04: Strategy Design Pattern

## Objective
Apply the Strategy pattern to select and swap sorting and discount algorithms at runtime without changing the code that uses them.

## Background
The **Strategy** pattern defines a family of algorithms, encapsulates each one in a class, and makes them interchangeable. The object that *uses* the algorithm (the **context**) holds a reference to a `Strategy` interface and delegates work to it — swapping the strategy changes the behaviour without touching the context class. This is one of the most practical patterns: it directly embodies the Open/Closed Principle (open for extension, closed for modification).

## Requirements

1. **Sorting strategies**:
   - Create an interface `SortStrategy` with method `void sort(int[] arr)`
   - Create three implementations: `BubbleSortStrategy`, `SelectionSortStrategy`, and `InsertionSortStrategy` — each implements the respective sorting algorithm
   - Create a class `Sorter` with a `SortStrategy strategy` field, a setter `setStrategy(SortStrategy s)`, and a method `sort(int[] arr)` that delegates to the strategy
   - In `main`, create a `Sorter`. Sort `{5, 2, 9, 1, 7}` with each strategy and print the strategy name and result each time (reset the array before each sort)

2. **Discount strategies**:
   - Create an interface `DiscountStrategy` with method `double apply(double price)`
   - Create three implementations:
     - `NoDiscountStrategy` — returns `price` unchanged
     - `PercentageDiscountStrategy(double percent)` — returns `price * (1 - percent/100)`
     - `FlatDiscountStrategy(double amount)` — returns `Math.max(0, price - amount)`
   - Create a class `PriceCalculator` with a `DiscountStrategy strategy` field and a method `calculate(double price)` that returns `strategy.apply(price)`
   - In `main`, price = `120.00`. Print the result for all three discount strategies:
     - No discount → `$120.00`
     - 20% off → `$96.00`
     - $30 flat → `$90.00`

## Hints
- The Strategy interface is small — just one method. Java functional interfaces (`@FunctionalInterface`) would allow lambdas, but named classes make the pattern more explicit for learning
- `Sorter.setStrategy()` allows swapping the algorithm after construction — this is the key insight of the pattern
- Copy the array before each sort so you compare strategies fairly: `int[] copy = Arrays.copyOf(original, original.length)`
- The context (`Sorter`, `PriceCalculator`) never imports or references concrete strategy classes — it only knows the interface

## Expected Output

```
=== Strategy: Sorting ===
BubbleSortStrategy:    [1, 2, 5, 7, 9]
SelectionSortStrategy: [1, 2, 5, 7, 9]
InsertionSortStrategy: [1, 2, 5, 7, 9]

=== Strategy: Discount ===
No discount:     $120.00
20% off:         $96.00
$30 flat off:    $90.00
```
