# Exercise 04: Type Conversion, Casting, Autoboxing, and Unboxing

## Objective
Perform widening and narrowing type conversions, explicit casting between numeric types, and demonstrate autoboxing and unboxing between primitive types and their wrapper classes.

## Background
Java performs **widening conversions** automatically (e.g., `int` → `long`) because no data is lost. **Narrowing conversions** require an explicit cast (e.g., `double` → `int`) because precision or range may be lost. **Autoboxing** is the automatic conversion Java performs when a primitive is assigned to its corresponding wrapper class (e.g., `int` → `Integer`). **Unboxing** is the reverse. The `parseXxx` methods (e.g., `Integer.parseInt()`) convert Strings to primitives.

## Requirements

1. **Widening Conversions** — demonstrate each of the following assignments (no cast needed):
   - Assign a `byte` value of `42` to an `int` variable
   - Assign that `int` to a `long` variable
   - Assign that `long` to a `double` variable
   - Print each variable and its type label

2. **Narrowing Conversions** — demonstrate each with an explicit cast:
   - Cast the `double` value `9.99` to an `int` (notice the decimal is truncated, not rounded)
   - Cast the `int` value `130` to a `byte` (notice overflow wrapping occurs: result is `-126`)
   - Cast the `int` value `65` to a `char` (prints the character `A`)
   - Print each result with a label explaining what happened

3. **Autoboxing** — demonstrate:
   - Assign `int` literal `100` to an `Integer` variable (autoboxing)
   - Assign `double` literal `3.14` to a `Double` variable (autoboxing)
   - Print both wrapper objects

4. **Unboxing** — demonstrate:
   - Unbox the `Integer` from step 3 back into a plain `int`
   - Perform arithmetic with it (`unboxedInt + 50`) and print the result

5. **String → Primitive conversion** using `parseXxx`:
   - Parse the String `"42"` to an `int` using `Integer.parseInt()`
   - Parse the String `"3.14"` to a `double` using `Double.parseDouble()`
   - Print both parsed values

## Hints
- Widening happens automatically — you never need a cast for `byte → short → int → long → float → double`.
- Narrowing always needs an explicit cast: `(int) someDouble`. The decimal part is **truncated** (not rounded).
- Casting `130` to `byte`: 130 exceeds `byte`'s max (127), so it wraps around. The formula is `130 - 256 = -126`.
- Autoboxing was introduced in Java 5 — the compiler inserts `Integer.valueOf(x)` behind the scenes.
- `Integer.parseInt("42")` throws a `NumberFormatException` if the String is not a valid integer.

## Expected Output
```
=== Widening Conversions ===
byte  42   → int    : 42
int   42   → long   : 42
long  42   → double : 42.0

=== Narrowing Conversions (explicit cast required) ===
(int) 9.99           : 9    (truncated, not rounded)
(byte) 130           : -126 (overflow wrap-around)
(char) 65            : A    (code point 65 = 'A')

=== Autoboxing (primitive → wrapper) ===
Integer autoboxed    : 100
Double  autoboxed    : 3.14

=== Unboxing (wrapper → primitive) ===
Unboxed int          : 100
Unboxed int + 50     : 150

=== String → Primitive (parseXxx) ===
Integer.parseInt("42")       : 42
Double.parseDouble("3.14")   : 3.14
```
