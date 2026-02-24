# Exercise 01: Lambda Expressions and Functional Interfaces

## Objective
Write lambda expressions and use Java's four core functional interfaces (`Predicate`, `Function`, `Consumer`, `Supplier`) to replace anonymous class boilerplate with concise, readable code.

## Background
Before Java 8, passing behavior required anonymous inner classes — verbose and hard to read. Lambda expressions provide a shorthand for implementing any **functional interface** (an interface with exactly one abstract method). Java ships four general-purpose functional interfaces in `java.util.function` that cover the most common patterns: test a value (`Predicate`), transform a value (`Function`), consume a value (`Consumer`), and produce a value (`Supplier`).

## Requirements

1. **Predicate<T>** — tests a condition, returns `boolean`:
   - Create `Predicate<String> isLongName` that returns `true` if the string length > 5
   - Test with `"Alice"` (false) and `"Alexander"` (true), printing: `"Alice is long name: false"` / `"Alexander is long name: true"`
   - Create `Predicate<Integer> isEven` that returns `true` for even numbers
   - Use `isEven.negate()` to create `isOdd` and test with `4` and `7`

2. **Function<T, R>** — transforms a value from type T to type R:
   - Create `Function<String, Integer> nameLength` that returns the length of a string
   - Create `Function<Integer, String> starRating` that returns a string of `"★"` repeated N times
   - Use `nameLength.andThen(starRating)` to compose them into a single function, and apply it to `"Bob"` and `"Christina"`

3. **Consumer<T>** — accepts a value, returns nothing:
   - Create `Consumer<String> printUpperCase` that prints the string in upper case
   - Create `Consumer<String> printLength` that prints `"Length: N"`
   - Use `printUpperCase.andThen(printLength)` to chain them, and apply to `"lambda"`

4. **Supplier<T>** — takes no input, produces a value:
   - Create `Supplier<String> greeting` that returns `"Hello, Java 8!"`
   - Create `Supplier<Double> randomScore` that returns `Math.random() * 100`
   - Print the greeting and print `"Random score: [value]"` (no need for deterministic output here)

5. **Inline lambda in sort**: Sort a `List<String>` containing `"Banana"`, `"Apple"`, `"Cherry"`, `"Date"` using `list.sort()` with a lambda comparator (by length, then alphabetically). Print the sorted list.

## Hints
- Functional interface lambda syntax: `(params) -> expression` or `(params) -> { body; }`
- `Predicate` has `and()`, `or()`, `negate()` for composing predicates
- `Function.andThen(f2)` applies `this` first, then `f2`; `Function.compose(f2)` applies `f2` first
- `Consumer.andThen(c2)` chains two consumers — both are called in order

## Expected Output

```
=== Predicate<T> ===
Alice is long name: false
Alexander is long name: true
4 is even: true  |  4 is odd: false
7 is even: false  |  7 is odd: true

=== Function<T, R> ===
Length of "Bob": 3
Length of "Christina": 9
Bob -> ★★★
Christina -> ★★★★★★★★★

=== Consumer<T> ===
LAMBDA
Length: 6

=== Supplier<T> ===
Hello, Java 8!
Random score: [some value]

=== Lambda in sort ===
Sorted: [Date, Bob is not in list — see: Apple, Banana, Cherry, Date]
```

> Note: sort by length then alphabetically: `Date`(4), `Apple`(5), `Banana`(6), `Cherry`(6) → `[Date, Apple, Banana, Cherry]`

```
Sorted by length then alpha: [Date, Apple, Banana, Cherry]
```
