// Exercise 06: Function Declarations, Expressions, and Arrow Functions — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Function Declaration (hoisting demo)
// ─────────────────────────────────────────────

console.log("--- hoisted call (before declaration) ---");
// This call appears BEFORE the function definition — works because declarations are hoisted
console.log(greet("Hoisted"));

console.log("\n--- function declaration ---");

// Function declarations are hoisted: the entire function is moved to the top of its scope
// at parse time, so it can be called anywhere in the same scope.
function greet(name, greeting = "Hello") {
  return `${greeting}, ${name}!`;
}

console.log(greet("Ada"));          // uses default greeting
console.log(greet("Ada", "Hi"));    // custom greeting overrides default

// ─────────────────────────────────────────────
// PART 2: Function Expression
// ─────────────────────────────────────────────

console.log("\n--- function expression ---");

// Function expressions are NOT hoisted — they cannot be called before this line
const calculateArea = function(width, height = width) { // default height = width (square)
  return width * height;
};

console.log(`calculateArea(5) → ${calculateArea(5)}`);     // square: 5 * 5 = 25
console.log(`calculateArea(4, 6) → ${calculateArea(4, 6)}`); // rectangle: 4 * 6 = 24

// ─────────────────────────────────────────────
// PART 3: Arrow Functions
// ─────────────────────────────────────────────

console.log("\n--- arrow functions ---");

// Concise body (no braces, no return keyword) — expression is implicitly returned
const double = n => n * 2;
const square = n => n ** 2; // ** is the exponentiation operator

console.log(`double(7) → ${double(7)}`);
console.log(`square(5) → ${square(5)}`);

// ─────────────────────────────────────────────
// PART 4: Arrow Function as Callback
// ─────────────────────────────────────────────

console.log("\n--- arrow as callback ---");

const numbers = [1, 2, 3, 4, 5];

// Arrow functions are ideal as callbacks — concise and don't need their own `this`
const cubes = numbers.map(n => n ** 3);
console.log(`cubes:`, cubes);

const evens = numbers.filter(n => n % 2 === 0);
console.log(`evens:`, evens);

// ─────────────────────────────────────────────
// PART 5: Rest Parameters
// ─────────────────────────────────────────────

console.log("\n--- rest parameters ---");

// ...nums collects all arguments into a real array; reduce sums them
function sum(...nums) {
  return nums.reduce((acc, n) => acc + n, 0);
}

console.log(`sum(1,2,3) → ${sum(1, 2, 3)}`);
console.log(`sum(10,20,30,40) → ${sum(10, 20, 30, 40)}`);

// ─────────────────────────────────────────────
// PART 6: Function Returning a Function
// ─────────────────────────────────────────────

console.log("\n--- function returning function ---");

// multiplier is a factory: it captures `factor` in its closure and returns
// a new function each time — see Exercise 09 for a deep dive on closures
function multiplier(factor) {
  return n => n * factor; // returned arrow function closes over `factor`
}

const triple = multiplier(3);
const half   = multiplier(0.5);

console.log(`triple(10) → ${triple(10)}`);
console.log(`half(20) → ${half(20)}`);

// ─────────────────────────────────────────────
// PART 7: IIFE
// ─────────────────────────────────────────────

console.log("\n--- IIFE ---");

// An IIFE is a function that is defined and immediately invoked.
// Variables declared inside are scoped to the function body —
// they are invisible outside, preventing pollution of the global/module scope.
(function() {
  const iifeScopedVar = "only visible inside"; // not accessible outside
  console.log("IIFE executed: scope is contained");
})();

// console.log(iifeScopedVar); // ← would throw ReferenceError

// ─────────────────────────────────────────────
// PART 8: Difference Summary
// ─────────────────────────────────────────────

/*
 * a) HOISTING:
 *    - Function DECLARATIONS are fully hoisted — both the name and body are
 *      available at the top of their scope before any code runs.
 *    - Function EXPRESSIONS and arrow functions are NOT hoisted; the variable
 *      binding exists (TDZ for let/const, undefined for var) but the value
 *      is not assigned until that line executes.
 *
 * b) `this` BINDING:
 *    - Arrow functions do NOT have their own `this`. They capture `this` from
 *      the enclosing lexical scope at definition time.
 *    - Declarations and expressions each have their own `this` that depends
 *      on how they are called (see Exercise 10).
 *
 * c) WHEN TO USE EACH:
 *    - Declaration → use for top-level utility functions that need to be callable
 *      anywhere in the file (hoisting helps readability).
 *    - Expression  → use when you need to assign a function to a variable,
 *      return it from another function, or conditionally define it.
 *    - Arrow       → use for callbacks (map/filter/reduce), short inline functions,
 *      and any context where you want to inherit `this` from the outer scope.
 */
