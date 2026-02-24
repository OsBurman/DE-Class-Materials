# Exercise 06: StringBuilder and StringBuffer — Mutability and Performance

## Objective
Understand why `StringBuilder` is preferred for string building in loops, practice its key methods, and learn when to use `StringBuffer` for thread-safety.

## Background
`String` objects in Java are **immutable** — every `+` concatenation inside a loop creates a brand-new `String` object and discards the old one, causing many unnecessary object allocations. `StringBuilder` solves this by maintaining a mutable internal buffer that you `append()` to directly. `StringBuffer` works identically but is synchronized (thread-safe), making it appropriate when multiple threads share the same builder. In single-threaded code, always prefer `StringBuilder`.

## Requirements

1. **Mutability comparison** — using a loop that runs 5 iterations:
   - Build a String using `+=` (the inefficient way): start with `String result = "";` and append `"item" + i + " "` in each iteration.
   - Build a String using `StringBuilder`: start with `StringBuilder sb = new StringBuilder();` and call `sb.append("item" + i + " ")` in each iteration.
   - After both loops, print both results (they should be identical).

2. **StringBuilder method practice** — given `StringBuilder sb2 = new StringBuilder("Hello World")`:
   - `append("!")` — print the result
   - `insert(5, ",")` — insert a comma at index 5, print the result
   - `delete(0, 6)` — delete characters from index 0 to 5 (exclusive of 6), print the result
   - `reverse()` — reverse the remaining content, print the result
   - `length()` — print the current length
   - `toString()` — print the final String conversion

3. **Explain thread safety** — after all StringBuilder operations, add a multi-line comment (not printed output) explaining in 2–3 sentences when you would use `StringBuffer` instead of `StringBuilder`.

4. **StringBuffer demo** — create a `StringBuffer` named `safeSb` with initial value `"Thread"`, append `"-Safe"`, and print the result using `toString()`.

## Hints
- `StringBuilder` and `StringBuffer` share the same API — you can swap one for the other with just a type change.
- `delete(start, end)` deletes characters from `start` (inclusive) to `end` (exclusive).
- `insert(index, str)` inserts before the character currently at that index.
- `reverse()` reverses the contents **in place** — it modifies the same `StringBuilder` object.
- `length()` on a `StringBuilder` works just like on a `String`.

## Expected Output
```
=== Mutability: String vs StringBuilder ===
String (+=) result    : item0 item1 item2 item3 item4 
StringBuilder result  : item0 item1 item2 item3 item4 

=== StringBuilder Method Practice ===
After append("!")     : Hello World!
After insert(5, ",")  : Hello, World!
After delete(0, 6)    : World!
After reverse()       : !dlroW
length()              : 6
toString()            : !dlroW

=== StringBuffer Demo ===
StringBuffer result   : Thread-Safe
```
