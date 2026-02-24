# Exercise 02: Type Coercion and Type Conversion

## Objective
Understand the difference between implicit type coercion and explicit type conversion, and learn why `==` vs `===` matters in JavaScript.

## Background
JavaScript is a loosely typed language — it will silently convert types to make operations work, sometimes producing surprising results. This automatic conversion is called **implicit coercion**. **Explicit conversion** is when you intentionally convert a type using `Number()`, `String()`, `Boolean()`, etc. Knowing the difference is essential for avoiding subtle bugs.

## Requirements

1. **Implicit coercion — log each expression and its result:**
   - `"5" + 3` → string concatenation (why?)
   - `"5" - 3` → numeric subtraction (why different from `+`?)
   - `"5" * "2"` → numeric result
   - `true + 1` → boolean coerced to number
   - `false + "value"` → boolean coerced to string
   - `null + 1` → null coerced to 0
   - `undefined + 1` → produces `NaN`
   - For each, add an inline comment explaining what coercion happened.

2. **Loose equality (`==`) vs strict equality (`===`):**
   - Log the result of these six comparisons **and** explain each with a comment:
     - `0 == false`
     - `0 === false`
     - `"" == false`
     - `null == undefined`
     - `null === undefined`
     - `NaN == NaN`
   - Write a brief comment block (3–4 lines) summarising the rule: when to prefer `===`.

3. **Explicit type conversion:**
   - Use `Number()` to convert: `"42"`, `"3.14"`, `""`, `"abc"`, `true`, `false`, `null`, `undefined`.
   - Use `String()` to convert: `42`, `true`, `null`, `undefined`.
   - Use `Boolean()` to convert: `0`, `""`, `null`, `undefined`, `NaN`, `"hello"`, `1`, `[]`, `{}`.
   - Log each in the format: `Number("42") → 42`

4. **`parseInt` and `parseFloat`:**
   - Show the difference between `Number("42px")` (NaN) and `parseInt("42px")` (42).
   - Show `parseFloat("3.14abc")` → 3.14.
   - Show `parseInt("0xFF", 16)` → 255.

5. **`isNaN` vs `Number.isNaN`:**
   - Log `isNaN("hello")`, `isNaN(undefined)`, `isNaN(NaN)`.
   - Log `Number.isNaN("hello")`, `Number.isNaN(undefined)`, `Number.isNaN(NaN)`.
   - Add a comment explaining why `Number.isNaN` is safer.

## Hints
- The `+` operator is overloaded: if **either** operand is a string, it concatenates. All other arithmetic operators (`-`, `*`, `/`) force numeric coercion.
- The falsy values are: `false`, `0`, `""`, `null`, `undefined`, `NaN`. Everything else is truthy — including `[]` and `{}`.
- `Boolean([])` returns `true` — empty arrays are truthy! This surprises most new JS developers.
- `Number.isNaN()` only returns `true` for the actual `NaN` value; the older `isNaN()` coerces its argument to a number first, giving misleading results for strings.

## Expected Output

```
--- Implicit Coercion ---
"5" + 3 → 53          (+ with a string triggers concatenation)
"5" - 3 → 2           (- forces numeric coercion)
"5" * "2" → 10
true + 1 → 2          (true coerces to 1)
false + "value" → falsevalue
null + 1 → 1          (null coerces to 0)
undefined + 1 → NaN

--- Loose vs Strict Equality ---
0 == false  → true
0 === false → false
"" == false → true
null == undefined  → true
null === undefined → false
NaN == NaN → false

--- Explicit Conversion: Number() ---
Number("42") → 42
Number("3.14") → 3.14
Number("") → 0
Number("abc") → NaN
Number(true) → 1
Number(false) → 0
Number(null) → 0
Number(undefined) → NaN

--- Explicit Conversion: String() ---
String(42) → "42"
String(true) → "true"
String(null) → "null"
String(undefined) → "undefined"

--- Explicit Conversion: Boolean() ---
Boolean(0) → false
Boolean("") → false
Boolean(null) → false
Boolean(undefined) → false
Boolean(NaN) → false
Boolean("hello") → true
Boolean(1) → true
Boolean([]) → true
Boolean({}) → true

--- parseInt / parseFloat ---
Number("42px") → NaN
parseInt("42px") → 42
parseFloat("3.14abc") → 3.14
parseInt("0xFF", 16) → 255

--- isNaN vs Number.isNaN ---
isNaN("hello") → true
isNaN(undefined) → true
isNaN(NaN) → true
Number.isNaN("hello") → false
Number.isNaN(undefined) → false
Number.isNaN(NaN) → true
```
