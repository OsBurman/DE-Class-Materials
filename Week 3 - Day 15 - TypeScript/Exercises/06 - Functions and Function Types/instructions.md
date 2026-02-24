# Exercise 06: Functions and Function Types

## Objective
Write typed TypeScript functions covering optional parameters, default values, rest parameters, function type expressions, overloads, and higher-order functions.

## Background
TypeScript adds types to every part of a function: parameters, return values, and even the function itself as a value. Properly typed functions catch argument mismatches at compile time and serve as self-documenting contracts.

## Requirements

1. Write a function `multiply(a: number, b: number = 2): number` with a default parameter. Call it with one argument and with two, log both results.

2. Write a function `joinStrings(separator: string, ...words: string[]): string` using a rest parameter. Call it with `"-"` and three words. Log the result.

3. Declare a **function type** alias: `type MathOperation = (a: number, b: number) => number`. Assign `add` and `subtract` function expressions to variables of this type. Call each and log.

4. Write a function `applyOperation(a: number, b: number, op: MathOperation): number` that takes a function as a parameter (higher-order function). Call it with `add` and `subtract`. Log results.

5. Write a function `findFirst<T>(arr: T[], predicate: (item: T) => boolean): T | undefined` that returns the first matching element or `undefined`. Test it with a number array (find first > 10) and a string array (find first starting with "A"). Log results.

6. Write **overloaded** function signatures for `format`:
   - `format(value: string): string` — returns `"str: " + value`
   - `format(value: number): string` — returns `"num: " + value.toFixed(2)`
   Call with a string and a number, log both.

7. Write a function `makeAdder(n: number): (x: number) => number` that returns a closure. Create `add5 = makeAdder(5)` and call it with `10`. Log the result.

## Hints
- Default parameters come after required ones: `function f(x: number, y = 10)`
- Rest parameters must be last: `function f(...args: string[])`
- Function type expressions describe the full call signature: `(a: number) => number`
- For overloads, write the overload signatures first, then a single implementation signature

## Expected Output
```
multiply(4): 8
multiply(3, 5): 15
joinStrings: one-two-three
add(10, 3): 13
subtract(10, 3): 7
applyOperation add: 20
applyOperation subtract: 4
findFirst > 10: 15
findFirst starts A: Alice
makeAdder(5)(10): 15
format("hello"): str: hello
format(3.14159): num: 3.14
```
