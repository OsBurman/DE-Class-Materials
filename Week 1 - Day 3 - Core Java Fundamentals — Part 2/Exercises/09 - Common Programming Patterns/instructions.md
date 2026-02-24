# Exercise 09 - Common Programming Patterns

## Objective
Implement four classic algorithmic patterns that appear repeatedly across real-world programs: running total accumulator, variable swap, find min/max, and count occurrences.

---

## Background
These patterns are the building blocks of almost every program you'll write:

| Pattern | Core Idea |
|---|---|
| **Accumulator** | Start at 0 (or 1 for product), add (or multiply) each element |
| **Swap** | Use a temporary variable to exchange two values without losing either |
| **Min/Max** | Seed from the first real element, update whenever a smaller/larger value is found |
| **Count occurrences** | Increment a counter whenever the condition matches |

---

## Requirements

### Part 1 — Running Total Accumulator
- Given `int[] sales = {120, 340, 210, 450, 180, 390, 275}`.
- Compute the **total** (sum of all elements).
- Compute the **product** (multiply all elements together — use a `long` to avoid overflow).
- Print both results.

### Part 2 — Variable Swap
- Declare `int a = 15, b = 42`.
- Print the values before the swap.
- Swap `a` and `b` using a **temporary variable** (`int temp`).
- Print the values after the swap.

### Part 3 — Find Min and Max
- Given `int[] temps = {72, 68, 85, 91, 63, 78, 88, 74}`.
- Find the minimum and maximum temperatures **without** using `Math.min` / `Math.max`.
- Print `"Min temp: [value]"` and `"Max temp: [value]"`.

### Part 4 — Count Occurrences
- Given `int[] rolls = {3, 5, 2, 6, 3, 1, 3, 4, 6, 3}`.
- Count how many times the value `3` appears.
- Count how many values are greater than `3`.
- Print both counts.

---

## Hints
- For the product, initialize `long product = 1L` and use `product *= sales[i]`.
- Swap pattern: `temp = a; a = b; b = temp;` — three lines, always.
- Seed min and max from the first element of the array, not from `0` or `Integer.MAX_VALUE`.
- Count occurrences with a simple `if (rolls[i] == target) count++` inside a loop.

---

## Expected Output
```
=== Part 1: Accumulator ===
Sales   : [120, 340, 210, 450, 180, 390, 275]
Total   : 1965
Product : 269168640000000

=== Part 2: Variable Swap ===
Before  : a = 15, b = 42
After   : a = 42, b = 15

=== Part 3: Min and Max ===
Temps   : [72, 68, 85, 91, 63, 78, 88, 74]
Min temp: 63
Max temp: 91

=== Part 4: Count Occurrences ===
Rolls   : [3, 5, 2, 6, 3, 1, 3, 4, 6, 3]
Count of 3  : 4
Greater than 3: 4
```
