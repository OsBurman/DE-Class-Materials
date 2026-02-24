// Exercise 06: Function Declarations, Expressions, and Arrow Functions
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Function Declaration (hoisting demo)
// ─────────────────────────────────────────────

console.log("--- hoisted call (before declaration) ---");
// TODO: Call greet("Hoisted") HERE — before the function definition below.
//       This should work because function declarations are hoisted.
//       Log the result.


console.log("\n--- function declaration ---");
// TODO: Write a function declaration `greet(name, greeting = "Hello")`
//       that returns the string `${greeting}, ${name}!`
//       Call it twice:
//         1. greet("Ada")          → "Hello, Ada!"
//         2. greet("Ada", "Hi")    → "Hi, Ada!"
//       Log both results.


// ─────────────────────────────────────────────
// PART 2: Function Expression
// ─────────────────────────────────────────────

console.log("\n--- function expression ---");
// TODO: Write a function expression (const calculateArea = function(...) { ... })
//       Parameters: width, height = width  (default height to width for squares)
//       Returns: width * height
//       Log calculateArea(5)    → 25
//       Log calculateArea(4, 6) → 24


// ─────────────────────────────────────────────
// PART 3: Arrow Functions
// ─────────────────────────────────────────────

console.log("\n--- arrow functions ---");
// TODO: Write a concise arrow function `double` that returns n * 2
// TODO: Write a concise arrow function `square` that returns n ** 2
// Log double(7) → 14 and square(5) → 25


// ─────────────────────────────────────────────
// PART 4: Arrow Function as Callback
// ─────────────────────────────────────────────

console.log("\n--- arrow as callback ---");
const numbers = [1, 2, 3, 4, 5];

// TODO: Use .map() with an arrow function to create an array of cubes (n ** 3)
//       Log: `cubes: [...]`

// TODO: Use .filter() with an arrow function to keep only even numbers
//       Log: `evens: [...]`


// ─────────────────────────────────────────────
// PART 5: Rest Parameters
// ─────────────────────────────────────────────

console.log("\n--- rest parameters ---");
// TODO: Write a function `sum(...nums)` that:
//       - Uses rest syntax to accept any number of arguments
//       - Returns the sum of all of them (hint: use .reduce)
// Log sum(1, 2, 3) → 6
// Log sum(10, 20, 30, 40) → 100


// ─────────────────────────────────────────────
// PART 6: Function Returning a Function
// ─────────────────────────────────────────────

console.log("\n--- function returning function ---");
// TODO: Write a function `multiplier(factor)` that returns an arrow function
//       The returned arrow function takes one number and multiplies it by factor.
// Create: const triple = multiplier(3)
// Create: const half = multiplier(0.5)
// Log triple(10) → 30
// Log half(20) → 10


// ─────────────────────────────────────────────
// PART 7: IIFE
// ─────────────────────────────────────────────

console.log("\n--- IIFE ---");
// TODO: Write an IIFE (Immediately Invoked Function Expression) that logs:
//       "IIFE executed: scope is contained"
// Add a comment explaining why variables inside an IIFE don't pollute the outer scope.


// ─────────────────────────────────────────────
// PART 8: Difference Summary (comment block)
// ─────────────────────────────────────────────

// TODO: Add a comment block that explains:
//   a) Which syntax is hoisted (and why this matters)
//   b) Which syntax does NOT have its own `this` binding
//   c) When you would choose declaration vs expression vs arrow
