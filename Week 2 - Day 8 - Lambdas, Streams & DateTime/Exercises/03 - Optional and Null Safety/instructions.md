# Exercise 03: Optional and Null Safety

## Objective
Use `Optional<T>` to represent values that may or may not be present, eliminating `null` checks and `NullPointerException` risk with idiomatic Java 8+ patterns.

## Background
`NullPointerException` is one of the most common runtime errors in Java. `Optional<T>` is a container that either holds a non-null value or is empty — it forces the caller to explicitly handle both cases rather than forgetting a null check. It is especially useful as a return type for methods that may not find a result (e.g., database lookups, search methods).

## Requirements

1. **Creating Optionals**:
   - `Optional.of("Java")` — holds a non-null value (throws NPE if you pass null)
   - `Optional.ofNullable(null)` — safely wraps a value that might be null
   - `Optional.empty()` — explicitly empty
   - Print `isPresent()` for all three

2. **Retrieving values safely**:
   - `orElse(default)` — returns the value or a default if empty; test on a present and an empty Optional
   - `orElseGet(Supplier)` — like `orElse` but lazily evaluates; use `() -> "Generated default"`
   - `orElseThrow(Supplier)` — throws if empty; wrap in try-catch and print the exception message

3. **Transforming with map and filter**:
   - Start with `Optional<String> name = Optional.of("  alice  ")`
   - Chain `.map(String::trim).map(String::toUpperCase)` and print the result with `ifPresent()`
   - Start with `Optional<String> code = Optional.of("PROMO50")`
   - Use `.filter(s -> s.startsWith("PROMO"))` — print the filtered result; try with `"SALE30"` to show the filter returning empty

4. **Simulating a realistic lookup**: Write a method `findUserById(int id)` that returns `Optional<String>`:
   - `id == 1` → `Optional.of("Alice")`
   - `id == 2` → `Optional.of("Bob")`
   - anything else → `Optional.empty()`
   - Call it with ids `1`, `2`, and `99`; use `ifPresentOrElse()` to print `"Found: [name]"` or `"User not found"`

## Hints
- `Optional.of(null)` throws `NullPointerException` immediately — use `ofNullable` for untrusted values
- `map()` on an empty Optional stays empty — it doesn't throw; this lets you chain safely
- `filter()` returns empty if the predicate is false, or the same Optional if true
- `ifPresentOrElse(consumer, runnable)` was added in Java 9 — provide the "not found" action as a `Runnable` lambda `() -> System.out.println("...")`

## Expected Output

```
=== Creating Optionals ===
Optional.of("Java") present: true
Optional.ofNullable(null) present: false
Optional.empty() present: false

=== orElse / orElseGet / orElseThrow ===
orElse on present: Java
orElse on empty: DEFAULT
orElseGet on empty: Generated default
orElseThrow caught: No value present

=== map and filter ===
Trimmed + uppercased: ALICE
Filter matches "PROMO50": PROMO50
Filter rejects "SALE30": (empty)

=== Realistic lookup ===
Found: Alice
Found: Bob
User not found
```
