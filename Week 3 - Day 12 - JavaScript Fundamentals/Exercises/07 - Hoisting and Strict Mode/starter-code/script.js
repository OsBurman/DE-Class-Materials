// Exercise 07: Hoisting and Strict Mode
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: var Hoisting
// ─────────────────────────────────────────────

console.log("--- var hoisting ---");

// TODO: Log hoistedVar HERE — before it is declared below.
//       Expected output: `hoistedVar before declaration: undefined`
//       (The declaration is hoisted but the assignment is not)


// TODO: Now declare and assign hoistedVar:
//   var hoistedVar = "I was hoisted";
//   Then log it again: `hoistedVar after declaration: I was hoisted`

// TODO: Add a comment explaining what JavaScript does internally with var hoisting


// ─────────────────────────────────────────────
// PART 2: Function Declaration Hoisting
// ─────────────────────────────────────────────

console.log("\n--- function declaration hoisting ---");

// TODO: Call hoistedFn() HERE — before its definition below.
//       Log the result as: `hoistedFn called before definition: Hoisted function!`
//       Add a comment: why does this work for declarations but not expressions?


// TODO: Define function hoistedFn() { return "Hoisted function!"; }


// ─────────────────────────────────────────────
// PART 3: let / const Temporal Dead Zone (TDZ)
// ─────────────────────────────────────────────

console.log("\n--- let TDZ ---");

// TODO: Write a try/catch block that tries to use `tdzVar` before it is declared.
//       The catch block should log:
//       `TDZ caught: ${error.constructor.name}: ${error.message}`
//       (This will produce: TDZ caught: ReferenceError: Cannot access 'tdzVar' before initialization)

// After the try/catch:
// TODO: Declare:  let tdzVar = "Now I exist";
// TODO: Log: `tdzVar after declaration: Now I exist`


// ─────────────────────────────────────────────
// PART 4: Strict Mode — Undeclared Variable
// ─────────────────────────────────────────────

console.log("\n--- strict mode: undeclared variable ---");

// TODO: Write a function strictTest() that:
//   - Has "use strict" as its first statement
//   - Inside a try/catch, assigns to an undeclared variable: implicitGlobal = 42
//   - Catches the ReferenceError and logs:
//     `Strict mode caught: ${error.constructor.name}: ${error.message}`

// TODO: Call strictTest()


// ─────────────────────────────────────────────
// PART 5: Strict Mode — Duplicate Parameters (comment only)
// ─────────────────────────────────────────────

// TODO: Define this non-strict function (shows duplicate params are allowed without strict):
//   function lenient(a, a) { return a; }
//   Log lenient(1, 2) — which value does it return?

// TODO: Add a comment block explaining:
//   - That function lenient(a, a) {} inside a "use strict" scope would throw a SyntaxError
//   - Why you CANNOT wrap this in try/catch to demonstrate it at runtime
//     (SyntaxErrors are caught at parse time, before any code runs)


// ─────────────────────────────────────────────
// PART 6: Strict Mode — this in a Plain Function Call
// ─────────────────────────────────────────────

console.log("\n--- strict mode: this in plain call ---");

// TODO: Write function checkThisNonStrict() (no "use strict") that logs:
//   `typeof this in non-strict: ${typeof this}`

// TODO: Write function checkThisStrict() with "use strict" as first statement
//   that logs:
//   `typeof this in strict: ${typeof this}`

// TODO: Call both functions
