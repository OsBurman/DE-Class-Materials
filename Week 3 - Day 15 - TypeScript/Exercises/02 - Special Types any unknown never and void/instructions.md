# Exercise 02: Special Types — any, unknown, never, and void

## Objective
Understand when and why to use TypeScript's four special types: `any`, `unknown`, `never`, and `void`.

## Background
TypeScript provides escape hatches and utility types for situations where a value's type is truly indeterminate, where a function returns nothing, or where a code path should never be reached. Knowing the difference prevents unsafe `any` usage and enables exhaustive pattern matching.

## Requirements

1. Declare a variable `flexible` of type `any`. Assign it a string, then reassign it to a number, then to a boolean. Log each value. Observe that TypeScript does **not** complain.
2. Declare a variable `mystery` of type `unknown`. Assign it the string `"42"`.  
   - Try to call `mystery.toUpperCase()` — comment it out and note the error.  
   - Add a `typeof` check so that inside the check you can safely call `.toUpperCase()` and log the result.
3. Write a function `logMessage(msg: string): void` that logs the message. Call it. Observe the return type is `void` (no return value expected).
4. Write a function `throwError(message: string): never` that always throws a `new Error(message)`. Call it inside a `try/catch` and log `"Caught: " + err.message`.
5. Write an exhaustive switch helper using `never`:  
   Create a type `Direction = "north" | "south" | "east" | "west"`.  
   Write a function `describeDirection(d: Direction): string` that handles all four cases and has a `default` branch that passes the value to `function assertNever(x: never): never { throw new Error("Unexpected: " + x); }`.  
   Call it with each direction and log the result.

## Hints
- `any` disables type checking entirely — use it only as a last resort
- `unknown` is the safe alternative to `any` — you must narrow it with `typeof` or `instanceof` before using it
- A `void` function may still `return;` (with no value), but TypeScript won't let you use the return value
- The `never` return type tells TypeScript "this path never completes normally" — useful for exhaustive checks

## Expected Output
```
flexible as string: hello
flexible as number: 42
flexible as boolean: true
mystery uppercased: 42
logMessage: TypeScript is great
Caught: Something went wrong
north → Go north
south → Go south
east → Go east
west → Go west
```
