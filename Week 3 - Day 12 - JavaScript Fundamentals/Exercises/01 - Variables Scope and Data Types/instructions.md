# Exercise 01: Variables, Scope and Data Types

## Objective
Practice declaring variables with `var`, `let`, and `const`, understand how each scopes to blocks or functions, and explore every JavaScript primitive data type using `typeof`.

## Background
JavaScript has three ways to declare variables, each with different scoping rules. Understanding when to use each — and why — is foundational to writing correct JavaScript. JS also has seven primitive types; recognising them and how `typeof` reports them prevents many common bugs.

## Requirements

1. **Variable declarations:**
   - Declare a `const` named `courseName` with the value `"JavaScript Fundamentals"`.
   - Declare a `let` named `currentLesson` with the value `1`, then reassign it to `2` on the next line.
   - Attempt to declare a `var` named `legacyVar` inside an `if (true) { }` block and log it *outside* the block — observe that it is accessible (function-scoped).
   - Declare a `let` named `blockLet` inside an `if (true) { }` block; comment out any attempt to access it outside and explain in a comment why that would throw a `ReferenceError`.

2. **Primitive data types — log the value AND its `typeof` for each:**
   - A `string`: `"Hello, World!"`
   - A `number`: `42`
   - A `bigint`: `9007199254740993n`
   - A `boolean`: `true`
   - `undefined` (declared but not assigned)
   - `null` (note: `typeof null` returns `"object"` — add a comment explaining this is a known quirk)
   - A `symbol`: `Symbol("id")`

3. **Scope demonstration:**
   - Write a function `scopeDemo()` that declares `var funcScoped = "I'm function-scoped"` and `let blockScoped = "I'm block-scoped"` inside a `for` loop body `for (let i = 0; i < 1; i++) { }`.
   - Inside the function but outside the loop, log `funcScoped` (works) and add a comment that `blockScoped` would throw.
   - Call `scopeDemo()`.

4. **Object type check:**
   - Declare a `const` named `person` with properties `name` (string), `age` (number), `isStudent` (boolean).
   - Log `typeof person` and log each property with its `typeof`.

## Hints
- `const` prevents reassignment but does **not** make objects immutable — you can still change properties.
- `var` is **function-scoped**, not block-scoped, which is why the `if`-block `var` leaks out.
- `typeof null === "object"` is a historical bug in JS — use `=== null` to check for null explicitly.
- Every `Symbol()` call creates a **unique** value even if the description string is the same.

## Expected Output

```
courseName: JavaScript Fundamentals
currentLesson after reassignment: 2
legacyVar outside block: I exist outside!
blockLet would throw ReferenceError outside its block

--- Primitive Types ---
"Hello, World!" → typeof: string
42 → typeof: number
9007199254740993n → typeof: bigint
true → typeof: boolean
undefined → typeof: undefined
null → typeof: object  (known JS quirk — use === null to check)
Symbol(id) → typeof: symbol

--- scopeDemo ---
funcScoped inside function: I'm function-scoped

--- Object ---
person → typeof: object
  name: Alice → typeof: string
  age: 30 → typeof: number
  isStudent: false → typeof: boolean
```
