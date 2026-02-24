// Exercise 08: Truthy, Falsy Values, and Control Flow
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Falsy Values Inventory
// ─────────────────────────────────────────────

console.log("--- falsy values ---");

// TODO: Log Boolean(value) for all 6 falsy values:
//       false, 0, "", null, undefined, NaN
//       Format: `Boolean(false) → false`

// TODO: Log Boolean(value) for 4 truthy edge cases:
//       [], {}, "0", -1
//       (These surprise many students — they are all truthy!)


// ─────────────────────────────────────────────
// PART 2: Short-Circuit Operators
// ─────────────────────────────────────────────

console.log("\n--- short-circuit ---");

// TODO: && — log null && "never" and "hello" && "world"
//       Add a comment explaining: && returns first falsy, or last value if all truthy

// TODO: || — log null || "default" and "value" || "default"
//       Add a comment explaining: || returns first truthy, or last value if all falsy

// TODO: ?? — log null ?? "fallback" and 0 ?? "fallback"
//       Add a comment explaining why ?? differs from || (only nullish, not falsy)

// TODO: ?. — given the user object below, log user?.profile?.name and user?.address?.city
const user = { profile: { name: "Alice" } };
// user?.address?.city should be undefined (not a TypeError)


// ─────────────────────────────────────────────
// PART 3: if / else if / else — Grade Classifier
// ─────────────────────────────────────────────

console.log("\n--- grades ---");

// TODO: Write function getGrade(score) using if/else if/else:
//   90-100 → "A", 80-89 → "B", 70-79 → "C", 60-69 → "D", <60 → "F"

// TODO: Call getGrade() with 95, 82, 71, 65, 45 and log: `95 → A`


// ─────────────────────────────────────────────
// PART 4: switch — Day Type
// ─────────────────────────────────────────────

console.log("\n--- day type ---");

// TODO: Write function getDayType(day) using switch with fall-through:
//   "Saturday" and "Sunday" fall through to return "Weekend"
//   default → return "Weekday"

// TODO: Call with "Monday", "Saturday", "Sunday", "Wednesday" and log results


// ─────────────────────────────────────────────
// PART 5: for loop — 7× Multiplication Table
// ─────────────────────────────────────────────

console.log("\n--- 7× table ---");

// TODO: Use a for loop (i from 1 to 10 inclusive) to log: `7 × 1 = 7` through `7 × 10 = 70`


// ─────────────────────────────────────────────
// PART 6: while loop — Countdown
// ─────────────────────────────────────────────

console.log("\n--- countdown ---");

// TODO: Use a while loop to count down from 5 to 1.
//       Log each number on the same line (hint: process.stdout.write or collect then log)
//       After the loop log "Liftoff!" on its own line.
//       Expected: 5 4 3 2 1 Liftoff!


// ─────────────────────────────────────────────
// PART 7: for...of — Iterate Array
// ─────────────────────────────────────────────

console.log("\n--- for...of ---");

const colours = ["red", "green", "blue"];

// TODO: Use for...of to iterate colours and log each in uppercase
//       Expected: RED GREEN BLUE  (on one line, or each on its own line is fine)


// ─────────────────────────────────────────────
// PART 8: for...in — Iterate Object Keys
// ─────────────────────────────────────────────

console.log("\n--- for...in ---");

const config = { host: "localhost", port: 3000, debug: true };

// TODO: Use for...in to iterate the keys of config
//       Log each as: `host: localhost`


// ─────────────────────────────────────────────
// PART 9: break and continue
// ─────────────────────────────────────────────

console.log("\n--- break and continue ---");

// TODO: Loop over [1..10].
//       Use `continue` to skip odd numbers.
//       Use `break` to stop when the value exceeds 8.
//       Log each included number.
//       Expected output: 2 4 6 8
