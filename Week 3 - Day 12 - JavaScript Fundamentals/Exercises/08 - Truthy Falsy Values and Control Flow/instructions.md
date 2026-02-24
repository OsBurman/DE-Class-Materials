# Exercise 08: Truthy, Falsy Values, and Control Flow

## Objective
Identify all falsy values in JavaScript, use short-circuit operators for concise conditional logic, and implement `if/else`, `switch`, `for`/`while`/`for...of`/`for...in` loops correctly.

## Background
JavaScript coerces values to boolean in conditional contexts. Knowing exactly which values are falsy lets you write concise guards and defaults. Combined with the full suite of control-flow statements and loops, these are the building blocks of every algorithm.

## Requirements

1. **Falsy values inventory:**
   - Log `Boolean(value)` for all six falsy values: `false`, `0`, `""`, `null`, `undefined`, `NaN`.
   - Log `Boolean(value)` for four truthy edge cases: `[]`, `{}`, `"0"`, `-1`.
   - Format each as: `Boolean(0) → false`.

2. **Short-circuit operators:**
   - `&&` — log `null && "never"` → `null` and `"hello" && "world"` → `"world"`. Explain short-circuit in a comment.
   - `||` — log `null || "default"` → `"default"` and `"value" || "default"` → `"value"`. Explain in a comment.
   - `??` (nullish coalescing) — log `null ?? "fallback"` → `"fallback"` and `0 ?? "fallback"` → `0`. Explain why `??` differs from `||`.
   - `?.` (optional chaining) — given `const user = { profile: { name: "Alice" } }`, log `user?.profile?.name` and `user?.address?.city` (should be `undefined`, not an error).

3. **`if / else if / else` — grade classifier:**
   - Write a function `getGrade(score)` that returns:
     - `"A"` for 90–100, `"B"` for 80–89, `"C"` for 70–79, `"D"` for 60–69, `"F"` for below 60.
   - Call it with scores: 95, 82, 71, 65, 45. Log each result.

4. **`switch` statement — day type:**
   - Write a function `getDayType(day)` that accepts a day name and returns `"Weekday"` or `"Weekend"`.
   - Use a `switch` with fall-through for Saturday/Sunday → `"Weekend"`, and a `default` for weekdays.
   - Call it with `"Monday"`, `"Saturday"`, `"Sunday"`, `"Wednesday"`.

5. **`for` loop — multiplication table:**
   - Print the 7× multiplication table (1–10) using a `for` loop: `7 × 1 = 7`, `7 × 2 = 14` … `7 × 10 = 70`.

6. **`while` loop — countdown:**
   - Use a `while` loop to count down from 5 to 1, logging each number. Then log `"Liftoff!"`.

7. **`for...of` — iterate an array:**
   - Given `const colours = ["red", "green", "blue"]`, iterate with `for...of` and log each colour in uppercase.

8. **`for...in` — iterate object keys:**
   - Given `const config = { host: "localhost", port: 3000, debug: true }`, use `for...in` to log each key and value as `host: localhost`.

9. **`break` and `continue`:**
   - Use a `for` loop over `[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]`.
   - `continue` (skip) odd numbers.
   - `break` when the value exceeds 8.
   - Log each included number.

## Hints
- `&&` returns the **first falsy** value, or the **last** value if all are truthy.
- `||` returns the **first truthy** value, or the **last** value if all are falsy.
- `??` only short-circuits on `null` or `undefined` — it does NOT treat `0`, `""`, or `false` as "missing".
- `for...in` iterates **keys** (property names); `for...of` iterates **values** of iterables (arrays, strings, etc.).

## Expected Output

```
--- falsy values ---
Boolean(false) → false
Boolean(0) → false
Boolean("") → false
Boolean(null) → false
Boolean(undefined) → false
Boolean(NaN) → false
Boolean([]) → true
Boolean({}) → true
Boolean("0") → true
Boolean(-1) → true

--- short-circuit ---
null && "never" → null
"hello" && "world" → world
null || "default" → default
"value" || "default" → value
null ?? "fallback" → fallback
0 ?? "fallback" → 0
user?.profile?.name → Alice
user?.address?.city → undefined

--- grades ---
95 → A
82 → B
71 → C
65 → D
45 → F

--- day type ---
Monday → Weekday
Saturday → Weekend
Sunday → Weekend
Wednesday → Weekday

--- 7× table ---
7 × 1 = 7
7 × 2 = 14
...
7 × 10 = 70

--- countdown ---
5 4 3 2 1 Liftoff!

--- for...of ---
RED GREEN BLUE

--- for...in ---
host: localhost
port: 3000
debug: true

--- break and continue ---
2 4 6 8
```
