# Exercise 03: For Loop Multiplication Table

## Objective
Use a standard `for` loop and a nested `for` loop to generate a formatted multiplication table.

## Background
The `for` loop is Java's most versatile loop — it bundles initialization, condition check, and increment into one line. Nested loops (a loop inside a loop) are the standard way to process two-dimensional data or generate grid-style output.

## Requirements

1. **Single for loop** — print the multiples of 7 from 7×1 through 7×10, one per line, in the format: `7 x 1 = 7`

2. **Nested for loop** — print a full multiplication table from 1×1 through 5×5:
   - The outer loop controls the first factor (rows, 1–5)
   - The inner loop controls the second factor (columns, 1–5)
   - Each row should be on its own line
   - Use `System.out.printf` or `String.format` to right-align the products so columns line up neatly (use `%3d` width for the product)

3. **Countdown for loop** — print a countdown from 10 down to 1 using a `for` loop with a **decrementing** counter (`i--`), then print `"Blast off!"` on the final line.

## Hints
- A `for` loop has three parts: `for (initialization; condition; update)`.
- To decrement: use `i--` in the update expression, and `i >= 1` as the condition.
- `System.out.printf("%3d", value)` prints an integer right-aligned in a field 3 characters wide — useful for table formatting.
- The inner loop completes all its iterations for every single iteration of the outer loop.

## Expected Output
```
=== Multiples of 7 ===
7 x 1 = 7
7 x 2 = 14
7 x 3 = 21
7 x 4 = 28
7 x 5 = 35
7 x 6 = 42
7 x 7 = 49
7 x 8 = 56
7 x 9 = 63
7 x 10 = 70

=== 5x5 Multiplication Table ===
  1  2  3  4  5
  2  4  6  8 10
  3  6  9 12 15
  4  8 12 16 20
  5 10 15 20 25

=== Countdown ===
10 9 8 7 6 5 4 3 2 1
Blast off!
```
