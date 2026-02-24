// Exercise 08: Truthy, Falsy Values, and Control Flow — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Falsy Values Inventory
// ─────────────────────────────────────────────

console.log("--- falsy values ---");

// The only falsy values in JavaScript:
console.log(`Boolean(false) → ${Boolean(false)}`);
console.log(`Boolean(0) → ${Boolean(0)}`);
console.log(`Boolean("") → ${Boolean("")}`);
console.log(`Boolean(null) → ${Boolean(null)}`);
console.log(`Boolean(undefined) → ${Boolean(undefined)}`);
console.log(`Boolean(NaN) → ${Boolean(NaN)}`);

// Truthy edge cases that commonly trip up new developers:
console.log(`Boolean([]) → ${Boolean([])}`);    // empty array IS truthy
console.log(`Boolean({}) → ${Boolean({})}`);    // empty object IS truthy
console.log(`Boolean("0") → ${Boolean("0")}`);  // non-empty string IS truthy
console.log(`Boolean(-1) → ${Boolean(-1)}`);    // any non-zero number IS truthy

// ─────────────────────────────────────────────
// PART 2: Short-Circuit Operators
// ─────────────────────────────────────────────

console.log("\n--- short-circuit ---");

// && returns the FIRST FALSY operand, or the LAST operand if all are truthy
// (short-circuits: stops evaluating as soon as it finds a falsy value)
console.log(`null && "never" → ${null && "never"}`);       // null is falsy → returns null
console.log(`"hello" && "world" → ${"hello" && "world"}`); // all truthy → returns last ("world")

// || returns the FIRST TRUTHY operand, or the LAST operand if all are falsy
// (short-circuits: stops evaluating as soon as it finds a truthy value)
console.log(`null || "default" → ${null || "default"}`);       // null is falsy → returns "default"
console.log(`"value" || "default" → ${"value" || "default"}`); // "value" is truthy → returns "value"

// ?? only short-circuits on null or undefined — 0, "", false are NOT treated as "missing"
// Use ?? for default values when 0 or "" are valid inputs
console.log(`null ?? "fallback" → ${null ?? "fallback"}`);  // null → use fallback
console.log(`0 ?? "fallback" → ${0 ?? "fallback"}`);        // 0 is NOT nullish → keep 0

// ?. (optional chaining) — short-circuits to undefined if the left side is null/undefined
const user = { profile: { name: "Alice" } };
console.log(`user?.profile?.name → ${user?.profile?.name}`);  // "Alice"
console.log(`user?.address?.city → ${user?.address?.city}`);  // undefined (no TypeError)

// ─────────────────────────────────────────────
// PART 3: if / else if / else — Grade Classifier
// ─────────────────────────────────────────────

console.log("\n--- grades ---");

function getGrade(score) {
  if      (score >= 90) return "A";
  else if (score >= 80) return "B";
  else if (score >= 70) return "C";
  else if (score >= 60) return "D";
  else                  return "F";
}

[95, 82, 71, 65, 45].forEach(s => console.log(`${s} → ${getGrade(s)}`));

// ─────────────────────────────────────────────
// PART 4: switch — Day Type
// ─────────────────────────────────────────────

console.log("\n--- day type ---");

function getDayType(day) {
  switch (day) {
    case "Saturday":
    case "Sunday":
      return "Weekend"; // fall-through: both cases share this return
    default:
      return "Weekday";
  }
}

["Monday", "Saturday", "Sunday", "Wednesday"].forEach(
  d => console.log(`${d} → ${getDayType(d)}`)
);

// ─────────────────────────────────────────────
// PART 5: for loop — 7× Multiplication Table
// ─────────────────────────────────────────────

console.log("\n--- 7× table ---");

for (let i = 1; i <= 10; i++) {
  console.log(`7 × ${i} = ${7 * i}`);
}

// ─────────────────────────────────────────────
// PART 6: while loop — Countdown
// ─────────────────────────────────────────────

console.log("\n--- countdown ---");

let count = 5;
const countdownParts = [];
while (count >= 1) {
  countdownParts.push(count); // collect numbers
  count--;
}
// Print all on one line, then Liftoff!
process.stdout.write(countdownParts.join(" ") + " ");
console.log("Liftoff!");

// ─────────────────────────────────────────────
// PART 7: for...of — Iterate Array
// ─────────────────────────────────────────────

console.log("\n--- for...of ---");

const colours = ["red", "green", "blue"];
const upperColours = [];
for (const colour of colours) {
  upperColours.push(colour.toUpperCase());
}
console.log(upperColours.join(" ")); // RED GREEN BLUE

// ─────────────────────────────────────────────
// PART 8: for...in — Iterate Object Keys
// ─────────────────────────────────────────────

console.log("\n--- for...in ---");

const config = { host: "localhost", port: 3000, debug: true };
for (const key in config) {
  // for...in gives you the KEY; use bracket notation to get the VALUE
  console.log(`${key}: ${config[key]}`);
}

// ─────────────────────────────────────────────
// PART 9: break and continue
// ─────────────────────────────────────────────

console.log("\n--- break and continue ---");

const included = [];
for (let i = 1; i <= 10; i++) {
  if (i % 2 !== 0) continue; // skip odd numbers — go to next iteration
  if (i > 8)       break;    // stop the loop entirely once i exceeds 8
  included.push(i);
}
console.log(included.join(" ")); // 2 4 6 8
