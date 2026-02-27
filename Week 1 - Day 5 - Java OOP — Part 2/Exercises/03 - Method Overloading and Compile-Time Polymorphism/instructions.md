# Exercise 03: Method Overloading and Compile-Time Polymorphism

## Objective
Practice method overloading — defining multiple methods with the same name but different parameter lists — and understand how the compiler selects the correct version at compile time.

## Background
A `MathUtils` utility class needs to support the same operation (like `add` or `multiply`) on different numbers and types of arguments. Rather than naming every variation differently (`addTwoInts`, `addThreeInts`, `addDoubles`), Java lets you use the same method name with different signatures — the compiler picks the right one based on the arguments you pass.

## Requirements

1. Create a class `MathUtils` with the following **overloaded** method groups:

   **`add` — three overloads:**
   - `int add(int a, int b)` — returns the sum of two ints
   - `int add(int a, int b, int c)` — returns the sum of three ints
   - `double add(double a, double b)` — returns the sum of two doubles

   **`multiply` — two overloads:**
   - `int multiply(int a, int b)` — returns the product of two ints
   - `double multiply(double a, double b)` — returns the product of two doubles

   **`describe` — two overloads:**
   - `String describe(int n)` — returns `"Integer: [n]"`
   - `String describe(double n)` — returns `"Double: [n]"`
   - `String describe(String s)` — returns `"String: \"[s]\" (length [s.length()])"`

2. In `main`, demonstrate each overload:
   - Call `add(3, 7)` and print the result
   - Call `add(3, 7, 2)` and print the result
   - Call `add(1.5, 2.3)` and print the result
   - Call `multiply(4, 5)` and print the result
   - Call `multiply(2.5, 4.0)` and print the result
   - Call `describe(42)`, `describe(3.14)`, and `describe("hello")` and print each

3. Add a method `print` with two overloads to show the difference between overloading and overriding:
   - `void print(int value)` — prints `"Printing int: [value]"`
   - `void print(String value)` — prints `"Printing String: [value]"`
   - Demonstrate both in `main`

## Hints
- Overloading is resolved at **compile time** based on the declared parameter types — the compiler looks at which `add(...)` signature matches the arguments you wrote
- Two methods are overloaded if they have the same name but **different parameter lists** (different number of parameters, or different parameter types at the same position)
- Return type alone is NOT enough to overload — `int foo()` and `double foo()` do NOT compile
- Don't confuse overloading (same class, different signatures) with overriding (subclass, same signature)

## Expected Output

```
=== Method Overloading Demo ===

add(3, 7)       = 10
add(3, 7, 2)    = 12
add(1.5, 2.3)   = 3.8

multiply(4, 5)     = 20
multiply(2.5, 4.0) = 10.0

describe(42)      → Integer: 42
describe(3.14)    → Double: 3.14
describe("hello") → String: "hello" (length 5)

print(99)       → Printing int: 99
print("world")  → Printing String: world
```
