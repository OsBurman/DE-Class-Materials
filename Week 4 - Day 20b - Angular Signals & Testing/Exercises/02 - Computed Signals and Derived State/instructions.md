# Exercise 02: Computed Signals and Derived State

## Objective
Practice deriving state from signals using `computed()` to create automatically-updated, memoised values.

## Background
A `computed` signal derives its value from one or more other signals. Angular tracks which signals are read during the computation and automatically re-runs the computation whenever any dependency changes. Computed signals are **read-only** — you cannot call `.set()` on them.

## Requirements
1. Create three writable signals:
   - `unitPrice` (number) initialised to `25`
   - `quantity` (number) initialised to `1`
   - `discountPercent` (number) initialised to `0`
2. Create a `computed` signal called `subtotal` that multiplies `unitPrice()` by `quantity()`.
3. Create a `computed` signal called `discountAmount` that calculates the discount: `subtotal() * discountPercent() / 100`.
4. Create a `computed` signal called `total` that subtracts `discountAmount()` from `subtotal()`.
5. Create a `computed` signal called `summary` that returns a formatted string:
   `"Qty: N × $P = $S  |  Discount: D%  |  Total: $T"`
   where N = quantity, P = unitPrice, S = subtotal, D = discountPercent, T = total (all rounded to 2 decimal places).
6. In the template, display each computed value individually **and** display the `summary()` string.
7. Add three number inputs (bound to the writable signals via setter methods or `[(ngModel)]`-compatible approach) so the user can change price, quantity, and discount. All computed values must update automatically.

## Hints
- Import both `signal` and `computed` from `@angular/core`.
- `computed()` accepts a function — Angular tracks every signal read inside that function.
- You **cannot** bind `[(ngModel)]` directly to a signal. Use `(input)` events with `.set()` or separate bound properties.
- Use `toFixed(2)` in the `summary` computed string to format currency.

## Expected Output
At initial load (price=25, qty=1, discount=0):
```
Subtotal:        $25.00
Discount Amount: $0.00
Total:           $25.00
Summary: Qty: 1 × $25 = $25.00  |  Discount: 0%  |  Total: $25.00
```
After changing qty to 4 and discount to 10:
```
Subtotal:        $100.00
Discount Amount: $10.00
Total:           $90.00
Summary: Qty: 4 × $25 = $100.00  |  Discount: 10%  |  Total: $90.00
```
