# Exercise 07 - Single Dimensional Array Operations

## Objective
Practice declaring, initializing, accessing, modifying, and iterating over single-dimensional arrays in Java, and use `Arrays.toString()` to print an array cleanly.

---

## Background
- **Declaration**: `int[] arr;` — declares a reference, not an array yet.
- **Literal init**: `int[] primes = {2, 3, 5, 7, 11};` — size inferred from values.
- **Sized init**: `String[] names = new String[4];` — allocates 4 slots, filled with `null`.
- **Index access**: zero-based — `arr[0]` is the first element, `arr[arr.length - 1]` is the last.
- **`Arrays.toString(arr)`** returns a bracketed, comma-separated string (requires `import java.util.Arrays;`).
- Iterating with a **standard `for` loop** gives you the index; a **for-each** loop gives you only the value.

---

## Requirements

### Part 1 — Literal Initialization and Access
- Declare `int[] primes = {2, 3, 5, 7, 11, 13}`.
- Print the array using `Arrays.toString(primes)`.
- Print the first element and the last element.
- Print the length of the array.

### Part 2 — Sized Initialization and Assignment
- Declare `String[] days = new String[5]`.
- Assign the five weekday names (`"Monday"` … `"Friday"`) to indices 0–4.
- Print the array using `Arrays.toString(days)`.
- Change index 2 to `"Midweek"` and print the array again.

### Part 3 — Standard For Loop: Double Every Element
- Declare `int[] values = {3, 6, 9, 12, 15}`.
- Use a **standard for loop** (`for (int i = 0; ...)`) to multiply every element by 2 in place.
- Print the array before and after (use `Arrays.toString()`).

### Part 4 — Copy an Array (Manual)
- Declare `int[] original = {10, 20, 30, 40, 50}`.
- Create `int[] copy = new int[original.length]`.
- Use a for loop to copy each element.
- Change `copy[0] = 99` and show that `original[0]` is still `10` (print both).

---

## Hints
- `import java.util.Arrays;` must appear at the top of the file.
- `arr.length` is a field, not a method — no parentheses.
- To access the last element: `arr[arr.length - 1]`.
- A simple assignment `int[] copy = original` does **not** copy the data — it copies the reference. That's why Part 4 uses a manual loop.

---

## Expected Output
```
=== Part 1: Literal Init ===
Primes  : [2, 3, 5, 7, 11, 13]
First   : 2
Last    : 13
Length  : 6

=== Part 2: Sized Init ===
Days    : [Monday, Tuesday, Wednesday, Thursday, Friday]
Updated : [Monday, Tuesday, Midweek, Thursday, Friday]

=== Part 3: Double Elements ===
Before  : [3, 6, 9, 12, 15]
After   : [6, 12, 18, 24, 30]

=== Part 4: Array Copy ===
Copy[0] changed to 99
copy    : [99, 20, 30, 40, 50]
original: [10, 20, 30, 40, 50]
```
