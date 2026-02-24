// Exercise 07: Hoisting and Strict Mode — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: var Hoisting
// ─────────────────────────────────────────────

console.log("--- var hoisting ---");

// Accessing hoistedVar BEFORE the declaration prints undefined.
// Behind the scenes the engine transforms:
//   var hoistedVar = "I was hoisted";
// into:
//   var hoistedVar;            ← hoisted to the top of the scope
//   hoistedVar = "I was hoisted"; ← stays in place
// So at this point hoistedVar exists but has not been assigned yet.
console.log(`hoistedVar before declaration: ${hoistedVar}`); // undefined

var hoistedVar = "I was hoisted"; // assignment happens here

console.log(`hoistedVar after declaration: ${hoistedVar}`);

// ─────────────────────────────────────────────
// PART 2: Function Declaration Hoisting
// ─────────────────────────────────────────────

console.log("\n--- function declaration hoisting ---");

// Works! Function declarations are fully hoisted — name AND body move to the top.
// A function EXPRESSION like: const hoistedFn = function() {} would NOT work here
// because only the variable declaration (const hoistedFn) would be in TDZ, not the value.
console.log(`hoistedFn called before definition: ${hoistedFn()}`);

function hoistedFn() {
  return "Hoisted function!";
}

// ─────────────────────────────────────────────
// PART 3: let / const Temporal Dead Zone (TDZ)
// ─────────────────────────────────────────────

console.log("\n--- let TDZ ---");

// let (and const) bindings ARE hoisted but placed in a "temporal dead zone" —
// accessing them before the declaration throws a ReferenceError.
try {
  console.log(tdzVar); // ← throws ReferenceError
} catch (error) {
  console.log(`TDZ caught: ${error.constructor.name}: ${error.message}`);
}

let tdzVar = "Now I exist"; // declaration + initialisation
console.log(`tdzVar after declaration: ${tdzVar}`);

// ─────────────────────────────────────────────
// PART 4: Strict Mode — Undeclared Variable
// ─────────────────────────────────────────────

console.log("\n--- strict mode: undeclared variable ---");

function strictTest() {
  "use strict"; // activates strict mode for this function only
  try {
    implicitGlobal = 42; // in non-strict this would silently create a global; strict throws
  } catch (error) {
    console.log(`Strict mode caught: ${error.constructor.name}: ${error.message}`);
  }
}

strictTest();

// ─────────────────────────────────────────────
// PART 5: Strict Mode — Duplicate Parameters (comment only)
// ─────────────────────────────────────────────

// Non-strict: duplicate parameter names are allowed (last one wins)
function lenient(a, a) {
  return a; // returns the second `a` argument
}
console.log(`lenient(1, 2) in non-strict: ${lenient(1, 2)}`); // → 2

/*
 * In strict mode the same definition would throw a SyntaxError:
 *
 *   function strictDup(a, a) { "use strict"; return a; }
 *     ↑ SyntaxError: Duplicate parameter name not allowed in this context
 *
 * WHY YOU CANNOT CATCH THIS WITH try/catch:
 *   SyntaxErrors are detected by the JavaScript PARSER before the engine runs
 *   any code at all. The entire script/function is rejected immediately.
 *   try/catch only handles runtime errors — errors that occur WHILE code executes.
 *   Because parsing happens before execution, a SyntaxError can never be
 *   caught by a try/catch block.
 */

// ─────────────────────────────────────────────
// PART 6: Strict Mode — this in a Plain Function Call
// ─────────────────────────────────────────────

console.log("\n--- strict mode: this in plain call ---");

function checkThisNonStrict() {
  // In a plain function call (not as a method, constructor, or with call/apply/bind),
  // non-strict mode sets `this` to the global object (globalThis in Node, window in browser).
  console.log(`typeof this in non-strict: ${typeof this}`); // "object"
}

function checkThisStrict() {
  "use strict";
  // In strict mode a plain function call sets `this` to undefined — safer behaviour.
  console.log(`typeof this in strict: ${typeof this}`); // "undefined"
}

checkThisNonStrict();
checkThisStrict();
