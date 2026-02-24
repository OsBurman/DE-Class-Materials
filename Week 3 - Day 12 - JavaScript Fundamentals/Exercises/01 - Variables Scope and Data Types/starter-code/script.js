// Exercise 01: Variables, Scope and Data Types
// Run with: node script.js  OR open index.html in a browser and check the console.

// ─────────────────────────────────────────────
// PART 1: Variable Declarations
// ─────────────────────────────────────────────

// TODO: Declare a const named `courseName` with the value "JavaScript Fundamentals"
//       and log it as: `courseName: JavaScript Fundamentals`


// TODO: Declare a let named `currentLesson` with the value 1,
//       then reassign it to 2 on the next line.
//       Log: `currentLesson after reassignment: 2`


// TODO: Declare a var named `legacyVar` INSIDE an `if (true) { }` block.
//       Assign it the string "I exist outside!".
//       After the block, log: `legacyVar outside block: I exist outside!`
//       (This demonstrates that var is function-scoped, not block-scoped)


// TODO: Inside another `if (true) { }` block, declare a let named `blockLet`
//       with the value "I'm block-scoped".
//       Below the block, add a COMMENT explaining why accessing blockLet
//       here would throw a ReferenceError.
//       Log the string: `blockLet would throw ReferenceError outside its block`


// ─────────────────────────────────────────────
// PART 2: Primitive Data Types
// ─────────────────────────────────────────────

console.log("\n--- Primitive Types ---");

// TODO: For each of the following, log the value AND its typeof in this format:
//       `"Hello, World!" → typeof: string`
//       Values to use:
//         1. The string  "Hello, World!"
//         2. The number  42
//         3. The bigint  9007199254740993n
//         4. The boolean true
//         5. A variable declared with let but never assigned  (typeof → "undefined")
//         6. null  (add a comment: "known JS quirk — use === null to check")
//         7. Symbol("id")


// ─────────────────────────────────────────────
// PART 3: Scope Demonstration Function
// ─────────────────────────────────────────────

console.log("\n--- scopeDemo ---");

// TODO: Write a function `scopeDemo()` that:
//   - Uses a for loop: for (let i = 0; i < 1; i++) { }
//   - Inside the loop body declares: var funcScoped = "I'm function-scoped"
//   - Also inside the loop body declares: let blockScoped = "I'm block-scoped"
//   - After the loop (still inside the function) logs funcScoped successfully
//   - Add a comment explaining that logging blockScoped here would throw ReferenceError
// Then call scopeDemo() below.


// ─────────────────────────────────────────────
// PART 4: Object Type Check
// ─────────────────────────────────────────────

console.log("\n--- Object ---");

// TODO: Declare a const `person` object with:
//   - name: "Alice"  (string)
//   - age: 30        (number)
//   - isStudent: false (boolean)
// Then log `typeof person` and log each property with its typeof in the format:
//   `  name: Alice → typeof: string`
