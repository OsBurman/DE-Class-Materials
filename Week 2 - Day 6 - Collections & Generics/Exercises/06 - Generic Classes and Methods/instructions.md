# Exercise 06: Generic Classes and Methods

## Objective
Write your own generic classes and methods to understand how Java generics enable type-safe, reusable code without duplication.

## Background
Before generics (Java 5), collections stored `Object` — you could accidentally put a `String` into a list meant for `Integer`s and only discover the error at runtime with a `ClassCastException`. Generics move that error to **compile time** and eliminate the need for casting. The same principle applies to your own classes: a `Box<T>` works for any type `T` while remaining type-safe.

## Requirements

1. **Define a `Box<T>` class** (in the same file as `main`, as a top-level or static inner class) with:
   - A private field `T value`
   - A constructor `Box(T value)`
   - `T getValue()` getter
   - `void setValue(T value)` setter
   - `boolean isEmpty()` — returns `true` if value is `null`
   - `String toString()` — returns `"Box[value]"` or `"Box[empty]"` if null

2. **Define a `Pair<A, B>` class** with:
   - Private fields `A first` and `B second`
   - A constructor `Pair(A first, B second)`
   - Getters `getFirst()` and `getSecond()`
   - `String toString()` — returns `"(first, second)"`

3. **Define a generic static method** `max()` with signature:
   ```
   static <T extends Comparable<T>> T max(T a, T b)
   ```
   that returns whichever argument is larger.

4. In `main`, demonstrate all three:
   - Create a `Box<String>` with value `"Hello, Generics!"`, print it, change the value to `"Updated"`, print again
   - Create a `Box<Integer>` with value `42`, print it, then set it to `null` and call `isEmpty()` to confirm
   - Create a `Pair<String, Integer>` representing a student name and age, print it
   - Call `max(10, 37)` and print the result
   - Call `max("Zebra", "Apple")` and print the result

## Hints
- You can define `Box` and `Pair` as `static` nested classes inside `GenericsDemo` or as separate top-level classes in the same file (only one can be `public` if in the same file — make `GenericsDemo` public)
- `<T extends Comparable<T>>` is an **upper-bounded type parameter** — it constrains `T` to types that have a natural ordering (String, Integer, etc.)
- Type parameters are erased at runtime (type erasure) — generics are a compile-time feature

## Expected Output

```
=== Generic Box<T> ===
Box<String>: Box[Hello, Generics!]
After setValue: Box[Updated]
Box<Integer>: Box[42]
Box is empty: true

=== Generic Pair<A, B> ===
Student pair: (Alice, 21)

=== Generic Method max() ===
max(10, 37): 37
max("Zebra", "Apple"): Zebra
```
