// =============================================================================
// DAY 12 — PART 2 | File 3: Strict Mode, Truthy/Falsy, Control Flow & Errors
// File: 03-control-flow-and-errors.js
//
// Topics covered:
//   1. Strict mode
//   2. Truthy and falsy values
//   3. Control flow statements (if/else, switch, ternary)
//   4. Loops (for, while, do-while, for...of, for...in, break, continue)
//   5. Error handling basics (throw, try/catch/finally)
// =============================================================================

"use strict"; // ← Strict mode — must be the FIRST statement in a file or function


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Strict Mode
// ─────────────────────────────────────────────────────────────────────────────
//
// "use strict" enables a stricter variant of JavaScript that:
//   ✓ Turns silent errors into thrown exceptions
//   ✓ Fixes confusing 'this' behaviour (undefined instead of global in plain calls)
//   ✓ Prevents certain syntax that is reserved for future JS versions
//   ✓ Disallows duplicate parameter names
//   ✓ Disallows writing to read-only properties silently

// Without strict mode — silently creates a global variable (bad!)
// mistypedVar = "oops"; // In strict mode: ReferenceError: mistypedVar is not defined

// Without strict mode — silently fails
// const frozen = Object.freeze({ x: 1 });
// frozen.x = 2; // In strict mode: TypeError

// Strict mode per function scope — useful when you can't enable it for the whole file
function strictFunction() {
  "use strict";
  // this is undefined in a plain function call (not undefined = window in sloppy mode)
  return typeof this === "undefined";
}

// Note: ES Modules and Class bodies are always in strict mode automatically.
// In modern development (with bundlers like Vite/Webpack) strict mode is the default.
console.log("Strict mode: file-level is active for this whole file.");


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Truthy and Falsy Values
// ─────────────────────────────────────────────────────────────────────────────

// JavaScript converts values to boolean in conditional contexts.
// FALSY values — there are exactly 6:
const falsyValues = [false, 0, "", null, undefined, NaN];

console.log("--- Falsy Values ---");
falsyValues.forEach(val => {
  console.log(`  Boolean(${JSON.stringify(val)}) → ${Boolean(val)}`);
});
// Everything else is TRUTHY — including [], {}, "0", -1, Infinity

console.log("--- Truthy surprises ---");
console.log(Boolean([]));       // true  ← empty array is TRUTHY!
console.log(Boolean({}));       // true  ← empty object is TRUTHY!
console.log(Boolean("0"));      // true  ← non-empty string is TRUTHY!
console.log(Boolean("false"));  // true  ← non-empty string is TRUTHY!
console.log(Boolean(-1));       // true  ← any non-zero number is TRUTHY!

// Practical use — checking if a value exists
function processUsername(username) {
  if (!username) {  // falsy check — catches null, undefined, ""
    return "Username is required";
  }
  return `Processing: ${username.trim()}`;
}
console.log(processUsername(""));         // "Username is required"
console.log(processUsername(null));       // "Username is required"
console.log(processUsername("  sarah ")); // "Processing: sarah"

// Short-circuit evaluation with && and ||
const user = null;
const displayName = user && user.name; // user is falsy → stops → displayName is null
console.log(displayName); // null (safe, no TypeError)

const defaultName = null || undefined || "" || "Guest";
console.log(defaultName); // "Guest" — first truthy value in the chain

// Nullish coalescing operator (??) — only falls through for null or undefined (not 0 or "")
const score = 0;
console.log(score || "No score");  // "No score" — 0 is falsy for ||
console.log(score ?? "No score");  // 0 — 0 is NOT null/undefined for ??

// Optional chaining (?.) — safe property access on potentially null/undefined objects
const order = { customer: { name: "Alice", address: null } };
console.log(order.customer?.name);           // "Alice"
console.log(order.customer?.address?.city);  // undefined — no error!
console.log(order.shipping?.trackingId);     // undefined — no error!


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Control Flow Statements
// ─────────────────────────────────────────────────────────────────────────────

// ── 3a. if / else if / else ────────────────────────────────────────────────────

function getLetterGrade(score) {
  if (typeof score !== "number" || Number.isNaN(score)) {
    return "Invalid score";
  }
  if (score >= 90) return "A";
  else if (score >= 80) return "B";
  else if (score >= 70) return "C";
  else if (score >= 60) return "D";
  else return "F";
}

console.log(getLetterGrade(92));       // "A"
console.log(getLetterGrade(75));       // "C"
console.log(getLetterGrade("hello"));  // "Invalid score"

// ── 3b. Ternary operator — concise single-expression conditional ───────────────

const hour = new Date().getHours();
const timeOfDay = hour < 12 ? "morning" : hour < 18 ? "afternoon" : "evening";
console.log(`Good ${timeOfDay}!`);

// Use ternary for simple if/else assignments; avoid nesting ternaries 3+ deep
const isWeekend = (day) => (day === "Saturday" || day === "Sunday") ? "Weekend" : "Weekday";
console.log(isWeekend("Monday"));   // "Weekday"
console.log(isWeekend("Saturday")); // "Weekend"

// ── 3c. switch statement ───────────────────────────────────────────────────────

function describeHttpStatus(code) {
  switch (code) {
    case 200:
      return "OK — request succeeded";
    case 201:
      return "Created — resource created successfully";
    case 400:
      return "Bad Request — invalid input";
    case 401:
      return "Unauthorized — authentication required";
    case 403:
      return "Forbidden — you don't have permission";
    case 404:
      return "Not Found — resource does not exist";
    case 500:
      return "Internal Server Error — server crashed";
    default:
      return `Unknown status code: ${code}`;
  }
}

console.log(describeHttpStatus(200)); // "OK — request succeeded"
console.log(describeHttpStatus(404)); // "Not Found..."
console.log(describeHttpStatus(418)); // "Unknown status code: 418"  (I'm a teapot 🫖)

// Fall-through: multiple cases sharing the same handler (no break/return between them)
function getSeasonActivity(month) {
  switch (month) {
    case "December":
    case "January":
    case "February":
      return "Winter — skiing or hot cocoa";
    case "March":
    case "April":
    case "May":
      return "Spring — hiking or allergies";
    case "June":
    case "July":
    case "August":
      return "Summer — swimming";
    default:
      return "Autumn — pumpkin spice everything";
  }
}
console.log(getSeasonActivity("January")); // "Winter — skiing or hot cocoa"
console.log(getSeasonActivity("July"));    // "Summer — swimming"


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Loops
// ─────────────────────────────────────────────────────────────────────────────

const bootcampWeeks = ["HTML/CSS", "JavaScript", "React", "Java", "Spring Boot"];

// ── 4a. Classic for loop — when you need the index ─────────────────────────────
console.log("--- for loop ---");
for (let i = 0; i < bootcampWeeks.length; i++) {
  console.log(`Week ${i + 1}: ${bootcampWeeks[i]}`);
}

// ── 4b. for...of — iterate over VALUES of any iterable ────────────────────────
console.log("--- for...of ---");
for (const week of bootcampWeeks) {
  console.log(`  → ${week}`);
}

// Works on strings too
for (const char of "Hello") {
  process.stdout.write(char + " "); // H e l l o
}
console.log();

// ── 4c. for...in — iterate over KEYS of an object ──────────────────────────────
console.log("--- for...in ---");
const student = { name: "Alex", grade: "A", score: 94 };
for (const key in student) {
  console.log(`  ${key}: ${student[key]}`);
}
// Note: for...in on arrays gives you the INDICES as strings — use for...of for arrays

// ── 4d. while loop — repeat while condition is true ───────────────────────────
console.log("--- while loop ---");
let attempts = 0;
const maxAttempts = 3;
let loggedIn = false;

while (attempts < maxAttempts && !loggedIn) {
  attempts++;
  console.log(`Login attempt ${attempts}...`);
  if (attempts === 2) {
    loggedIn = true; // simulate successful login on attempt 2
    console.log("Login successful!");
  }
}
if (!loggedIn) console.log("Account locked.");

// ── 4e. do...while loop — runs AT LEAST ONCE ──────────────────────────────────
console.log("--- do...while ---");
let roll;
let rollCount = 0;
do {
  roll = Math.floor(Math.random() * 6) + 1; // 1-6
  rollCount++;
  console.log(`Rolled: ${roll}`);
} while (roll !== 6);
console.log(`Got a 6 after ${rollCount} roll(s)!`);

// ── 4f. break and continue ────────────────────────────────────────────────────
console.log("--- break and continue ---");
const submissions = [
  { student: "Alice", score: 85, late: false },
  { student: "Bob",   score: 42, late: false },
  { student: "Carol", score: 91, late: true  },  // late — skip
  { student: "Dave",  score: 0,  late: false },  // score 0 — break (data error)
  { student: "Eve",   score: 78, late: false }
];

for (const submission of submissions) {
  if (submission.late) {
    console.log(`  Skipping ${submission.student} (late submission) — continue`);
    continue;  // skip to next iteration
  }
  if (submission.score === 0) {
    console.log(`  Stopping — possible data error at ${submission.student} — break`);
    break;     // exit the loop entirely
  }
  console.log(`  ${submission.student}: ${submission.score} — ${submission.score >= 60 ? "PASS" : "FAIL"}`);
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Error Handling: throw, try / catch / finally
// ─────────────────────────────────────────────────────────────────────────────

// ── 5a. throw — explicitly raise an error ─────────────────────────────────────

function divide(a, b) {
  if (typeof a !== "number" || typeof b !== "number") {
    throw new TypeError("Both arguments must be numbers");
  }
  if (b === 0) {
    throw new RangeError("Cannot divide by zero");
  }
  return a / b;
}

// ── 5b. try / catch / finally ─────────────────────────────────────────────────
//
// try   — wrap the code that might throw
// catch — handle the error if one is thrown (receives the error object)
// finally — ALWAYS runs, whether an error occurred or not (useful for cleanup)

console.log("--- try/catch/finally ---");

function safeDivide(a, b) {
  try {
    const result = divide(a, b);
    console.log(`Result: ${result}`);
    return result;
  } catch (error) {
    // error is an Error object with .name, .message, and .stack
    console.log(`Caught ${error.name}: ${error.message}`);
    return null;
  } finally {
    // runs always — even if there's a return in try or catch
    console.log("safeDivide execution complete.");
  }
}

safeDivide(10, 2);     // Result: 5, then "execution complete"
safeDivide(10, 0);     // Caught RangeError: Cannot divide by zero, then "execution complete"
safeDivide("a", 5);    // Caught TypeError: Both arguments must be numbers, then "execution complete"

// ── 5c. Multiple catch targets ─────────────────────────────────────────────────

function parseUserInput(input) {
  try {
    if (typeof input !== "string") throw new TypeError("Input must be a string");
    const parsed = JSON.parse(input); // throws SyntaxError if invalid JSON
    if (!parsed.username) throw new RangeError("Missing required field: username");
    return { success: true, data: parsed };
  } catch (error) {
    if (error instanceof SyntaxError) {
      return { success: false, error: `Invalid JSON: ${error.message}` };
    }
    if (error instanceof TypeError) {
      return { success: false, error: `Type error: ${error.message}` };
    }
    if (error instanceof RangeError) {
      return { success: false, error: `Validation error: ${error.message}` };
    }
    // Re-throw any unexpected error
    throw error;
  }
}

console.log(parseUserInput('{"username":"alice","role":"student"}'));
// { success: true, data: { username: "alice", role: "student" } }

console.log(parseUserInput("{bad json}"));
// { success: false, error: "Invalid JSON: ..." }

console.log(parseUserInput('{"role":"admin"}'));
// { success: false, error: "Validation error: Missing required field: username" }

// ── 5d. Custom error classes ───────────────────────────────────────────────────

class ValidationError extends Error {
  constructor(field, message) {
    super(message);          // call Error constructor with the message
    this.name = "ValidationError";
    this.field = field;      // custom property
  }
}

class NotFoundError extends Error {
  constructor(resource, id) {
    super(`${resource} with id '${id}' not found`);
    this.name = "NotFoundError";
    this.resource = resource;
    this.id = id;
  }
}

function findStudent(id, database) {
  if (typeof id !== "string" || id.trim() === "") {
    throw new ValidationError("id", "Student ID must be a non-empty string");
  }
  const found = database.find(s => s.id === id);
  if (!found) {
    throw new NotFoundError("Student", id);
  }
  return found;
}

const db = [
  { id: "S001", name: "Alice", grade: "A" },
  { id: "S002", name: "Bob",   grade: "B" }
];

try {
  console.log(findStudent("S001", db));  // { id: "S001", name: "Alice", grade: "A" }
  console.log(findStudent("S999", db));  // throws NotFoundError
} catch (err) {
  if (err instanceof NotFoundError) {
    console.log(`Not found: ${err.resource} #${err.id}`);
  } else if (err instanceof ValidationError) {
    console.log(`Validation failed on field '${err.field}': ${err.message}`);
  } else {
    throw err; // re-throw unexpected errors
  }
}

// ── 5e. Error types cheat sheet ────────────────────────────────────────────────
//
//  Error type         When it occurs
//  ────────────────── ─────────────────────────────────────────────
//  Error              Generic base class — use for custom errors
//  TypeError          Wrong type: calling non-function, accessing null.prop
//  ReferenceError     Variable doesn't exist: accessing undeclared variable
//  SyntaxError        Invalid code: JSON.parse, eval with bad syntax
//  RangeError         Value out of valid range: new Array(-1), toFixed(200)
//  URIError           Malformed URI in decodeURIComponent
//  EvalError          Issues with eval() (rare in modern code)
