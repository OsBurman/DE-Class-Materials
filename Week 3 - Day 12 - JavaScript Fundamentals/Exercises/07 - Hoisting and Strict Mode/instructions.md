# Exercise 07: Hoisting and Strict Mode

## Objective
Observe how JavaScript hoists `var` declarations and function declarations, understand the Temporal Dead Zone (TDZ) for `let`/`const`, and see how `"use strict"` changes runtime behaviour.

## Background
JavaScript's engine processes source files in two passes: first it hoists declarations, then it executes code. This means `var` variables and function declarations are available before the lines that define them — but function expressions and `let`/`const` variables are NOT. Strict mode (`"use strict"`) was introduced in ES5 to catch common mistakes like undeclared variables and forbid deprecated features.

## Requirements

1. **`var` hoisting:**
   - Access a `var` variable named `hoistedVar` before it is declared and log it.
   - Then declare and assign it: `var hoistedVar = "I was hoisted";`
   - Log it again after the declaration.
   - Expected: first log prints `undefined` (declaration hoisted, assignment is not), second prints the value.
   - Add a comment explaining what the engine did behind the scenes.

2. **Function declaration hoisting:**
   - Call `hoistedFn()` **before** the `function hoistedFn() { return "Hoisted function!" }` declaration.
   - Show it works and log the return value.
   - Add a comment explaining why this works but a function expression would not.

3. **`let`/`const` Temporal Dead Zone:**
   - Write a `try/catch` block that:
     - Tries to access a `let` variable named `tdzVar` before its declaration.
     - Catches the `ReferenceError` and logs: `TDZ caught: ReferenceError: Cannot access 'tdzVar' before initialization`
   - Then declare `let tdzVar = "Now I exist"` and log it successfully.

4. **Strict mode — undeclared variable:**
   - Write a function `strictTest()` with `"use strict"` as its first statement.
   - Inside it, try to assign to an undeclared variable `implicitGlobal = 42` inside a `try/catch`.
   - Catch the `ReferenceError` and log: `Strict mode caught: ReferenceError: implicitGlobal is not defined`.
   - Call `strictTest()`.

5. **Strict mode — duplicate parameters:**
   - Outside of strict mode, show that `function lenient(a, a) { return a; }` is valid (returns the last `a`).
   - Show that the same function definition inside a `"use strict"` scope causes a `SyntaxError` at parse time (explain this in a comment — you cannot catch SyntaxErrors at runtime, so just document the behaviour).

6. **Strict mode — `this` in a plain function call:**
   - In non-strict mode, a plain function call's `this` is the global object (`globalThis`).
   - In strict mode, `this` is `undefined`.
   - Write a non-strict `function checkThisNonStrict()` that logs `typeof this`.
   - Write a strict `function checkThisStrict()` with `"use strict"` that logs `typeof this`.
   - Call both and show the difference.

## Hints
- `var` hoisting: only the **declaration** (`var x`) is hoisted, not the **assignment** (`x = value`). Think of the engine splitting `var x = 5` into `var x` (hoisted) and `x = 5` (stays in place).
- The Temporal Dead Zone: `let` and `const` are hoisted too, but accessing them before their declaration throws a `ReferenceError` rather than returning `undefined`.
- `"use strict"` applies to the entire script if at the top, or just to a specific function if inside the function body.
- You cannot recover from a `SyntaxError` with `try/catch` — the engine refuses to run the script at all if it contains a syntax error.

## Expected Output

```
--- var hoisting ---
hoistedVar before declaration: undefined
hoistedVar after declaration: I was hoisted

--- function declaration hoisting ---
hoistedFn called before definition: Hoisted function!

--- let TDZ ---
TDZ caught: ReferenceError: Cannot access 'tdzVar' before initialization
tdzVar after declaration: Now I exist

--- strict mode: undeclared variable ---
Strict mode caught: ReferenceError: implicitGlobal is not defined

--- strict mode: this in plain call ---
typeof this in non-strict: object
typeof this in strict: undefined
```
