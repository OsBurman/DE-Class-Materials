# Exercise 06: Function Declarations, Expressions, and Arrow Functions

## Objective
Write functions using all three JavaScript syntaxes — declarations, expressions, and arrow functions — and understand how each handles default parameters, rest parameters, and return values.

## Background
Functions are the primary unit of code reuse in JavaScript. ES6 introduced arrow function syntax as a concise alternative for callbacks and short functions. Each syntax has different behaviours around hoisting and `this` binding, so knowing when to choose each is an important skill.

## Requirements

1. **Function declaration — `greet`:**
   - Write a function declaration `greet(name, greeting = "Hello")` that returns the string `"Hello, Ada!"` (using the default greeting when none is provided).
   - Call it twice: once with only a name, once with both name and a custom greeting (e.g., `"Hi"`).
   - Log both results.
   - Demonstrate that function declarations are hoisted: call `greet("Hoisted")` **before** the function definition in your file and show it works.

2. **Function expression — `calculateArea`:**
   - Write a function expression (assigned to a `const`) that calculates the area of a rectangle: `width * height`.
   - Default `height` to `width` (square) when not provided.
   - Call it as: `calculateArea(5)` → 25, `calculateArea(4, 6)` → 24.

3. **Arrow function — `double` and `square`:**
   - Write a one-liner arrow function `double` that returns `n * 2`.
   - Write a one-liner arrow function `square` that returns `n ** 2`.
   - Log `double(7)` → 14 and `square(5)` → 25.

4. **Arrow function as callback:**
   - Given `const numbers = [1, 2, 3, 4, 5]`, use `.map()` with an arrow function to produce an array of cubes. Log it.
   - Use `.filter()` with an arrow function to keep only even numbers. Log it.

5. **Rest parameters — `sum`:**
   - Write a function `sum(...nums)` that accepts any number of arguments and returns their total.
   - Call it as: `sum(1, 2, 3)` → 6, `sum(10, 20, 30, 40)` → 100.

6. **Function returning a function:**
   - Write a function `multiplier(factor)` that returns an arrow function which multiplies its argument by `factor`.
   - Use it to create `triple = multiplier(3)` and `half = multiplier(0.5)`.
   - Log `triple(10)` → 30 and `half(20)` → 10.

7. **Immediately Invoked Function Expression (IIFE):**
   - Write an IIFE that logs `"IIFE executed: scope is contained"`.
   - Explain in a comment why variables declared inside an IIFE don't pollute the outer scope.

8. **Difference summary (comment only — no code required):**
   - Add a comment block explaining: (a) which syntax is hoisted, (b) which syntax does NOT have its own `this`, (c) when you would choose each.

## Hints
- Function **declarations** (`function foo() {}`) are hoisted — the entire definition moves to the top of the scope. Function **expressions** and arrow functions are NOT hoisted.
- Arrow functions **cannot** be used as constructors and do not have their own `arguments` object.
- Rest parameters (`...nums`) must be the **last** parameter. They collect all remaining arguments into an array.
- An IIFE is written as `(function() { ... })()` or `(() => { ... })()`.

## Expected Output

```
--- hoisted call (before declaration) ---
Hello, Hoisted!

--- function declaration ---
Hello, Ada!
Hi, Ada!

--- function expression ---
calculateArea(5) → 25
calculateArea(4, 6) → 24

--- arrow functions ---
double(7) → 14
square(5) → 25

--- arrow as callback ---
cubes: [ 1, 8, 27, 64, 125 ]
evens: [ 2, 4 ]

--- rest parameters ---
sum(1,2,3) → 6
sum(10,20,30,40) → 100

--- function returning function ---
triple(10) → 30
half(20) → 10

--- IIFE ---
IIFE executed: scope is contained
```
