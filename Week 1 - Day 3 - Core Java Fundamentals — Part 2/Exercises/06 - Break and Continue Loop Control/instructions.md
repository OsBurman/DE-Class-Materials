# Exercise 06 - Break and Continue Loop Control

## Objective
Understand how `break` and `continue` alter the normal flow of a loop, and use a **labeled break** to exit nested loops.

---

## Background
- **`break`** immediately terminates the enclosing loop and moves execution to the first statement after the loop.
- **`continue`** skips the rest of the current iteration and jumps back to the loop's update/condition check.
- **Labeled break** (`break outerLoop;`) lets you break out of a specific outer loop from inside a nested loop — useful when searching a 2D structure.

---

## Requirements

### Part 1 — `break`: Find the First Prime
- Create an `int[] numbers = {4, 6, 9, 13, 18, 21, 25, 29}`.
- Loop through the array and find the **first prime number**.
- Print `"First prime found: [value]"` and immediately `break`.
- A prime is a number greater than 1 with no divisors other than 1 and itself. Use a helper boolean `isPrime` or a short inner divisibility check (you decide).

### Part 2 — `continue`: Print Only Odd Numbers
- Loop from 1 to 15 (inclusive) using a `for` loop.
- If the current number is **even**, use `continue` to skip it.
- Print each odd number on the same line, space-separated.
- Print a newline at the end.

### Part 3 — Labeled Break: Search a 2D Array
- Create `int[][] grid = {{3, 7, 2}, {8, 1, 9}, {4, 6, 5}}`.
- Search for the value `9`.
- Use a labeled break (`outer:`) to exit both loops as soon as the target is found.
- Print `"Found 9 at row [r], col [c]"` after the loops finish.

---

## Hints
- For Part 1, a simple primality check: try dividing the candidate by every integer from 2 up to its square root. If none divide evenly, it's prime.
- For Part 2, `if (i % 2 == 0) continue;` is all you need.
- For Part 3, place the label `outer:` on the line immediately before the outer `for` statement.
- Declare row/col variables *before* the loops so they are accessible in the print statement afterward.

---

## Expected Output
```
=== Break: First Prime ===
First prime found: 13

=== Continue: Odd Numbers 1-15 ===
1 3 5 7 9 11 13 15 

=== Labeled Break: 2D Search ===
Found 9 at row 1, col 2
```
