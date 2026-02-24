// Exercise 11: Error Handling with try, catch, and Custom Errors — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Basic try/catch
// ─────────────────────────────────────────────

console.log("--- basic try/catch ---");

try {
  const value = null;
  value.toUpperCase(); // throws TypeError: Cannot read properties of null
} catch (error) {
  // error.name gives the error type; error.message gives the description
  console.log(`Caught ${error.name}: ${error.message}`);
}
// Code after the try/catch block continues normally
console.log("Execution continued after catch");

// ─────────────────────────────────────────────
// PART 2: finally Block
// ─────────────────────────────────────────────

console.log("\n--- finally ---");

function readData(shouldFail) {
  try {
    if (shouldFail) {
      throw new Error("Data read failed"); // simulate a failure
    }
    console.log("Data read successfully");
  } catch (error) {
    console.log(`Error caught: ${error.message}`);
  } finally {
    // finally ALWAYS executes — even if try returns early, or catch throws again
    // Use it for cleanup: closing files, database connections, releasing locks, etc.
    console.log("Cleaning up resources");
  }
}

readData(false); // success path
console.log("---");
readData(true);  // error path

// ─────────────────────────────────────────────
// PART 3: Rethrowing Errors
// ─────────────────────────────────────────────

console.log("\n--- rethrowing ---");

function parseAge(input) {
  const age = Number(input);
  if (Number.isNaN(age)) {
    throw new TypeError("Age must be a number"); // wrong type
  }
  if (age < 0 || age > 150) {
    throw new RangeError("Age must be between 0 and 150"); // out of range
  }
  return age;
}

function processAge(input) {
  try {
    const result = parseAge(input);
    console.log(`processAge("${input}") → ${result}`);
  } catch (error) {
    if (error instanceof TypeError) {
      console.log(`Type problem: ${error.message}`);
    } else if (error instanceof RangeError) {
      console.log(`Range problem: ${error.message}`);
    } else {
      throw error; // unexpected error — let it propagate to a higher handler
    }
  }
}

processAge("abc"); // TypeError
processAge(-5);    // RangeError
processAge("25");  // valid

// ─────────────────────────────────────────────
// PART 4: Custom Error Classes
// ─────────────────────────────────────────────

console.log("\n--- custom errors ---");

// Custom error classes let you carry extra information and distinguish error types
class ValidationError extends Error {
  constructor(message, field) {
    super(message); // call Error constructor: sets this.message and this.stack
    this.name  = "ValidationError"; // override the default "Error" name
    this.field = field;             // extra property specific to this error type
  }
}

class NotFoundError extends Error {
  constructor(resource) {
    super(`${resource} not found`);
    this.name = "NotFoundError";
  }
}

function validateUser(user) {
  if (!user.name) {
    throw new ValidationError("Name is required", "name");
  }
  if (!user.email.includes("@")) {
    throw new ValidationError("Email must contain @", "email");
  }
  return "User is valid";
}

// Test invalid name
try {
  validateUser({ name: "", email: "alice@example.com" });
} catch (err) {
  console.log(`${err.name} on field "${err.field}": ${err.message}`);
}

// Test invalid email
try {
  validateUser({ name: "Alice", email: "notanemail" });
} catch (err) {
  console.log(`${err.name} on field "${err.field}": ${err.message}`);
}

// Test valid user
try {
  console.log(`validateUser valid: ${validateUser({ name: "Alice", email: "alice@example.com" })}`);
} catch (err) {
  console.log(err.message);
}

// ─────────────────────────────────────────────
// PART 5: Error Propagation
// ─────────────────────────────────────────────

console.log("\n--- error propagation ---");

// c throws; b does not catch; a does not catch; the error bubbles up to the try/catch below
function c() {
  throw new Error("thrown in c");
}
function b() {
  c(); // does not catch — error propagates up
}
function a() {
  b(); // does not catch — error propagates up
}

try {
  a(); // the error from c() bubbles through b() and a() and is caught here
} catch (error) {
  console.log(`Caught from a(): ${error.message}`);
}

// ─────────────────────────────────────────────
// PART 6: instanceof Type Checking
// ─────────────────────────────────────────────

console.log("\n--- instanceof checks ---");

const validationErr = new ValidationError("test", "field");

// instanceof checks the prototype chain:
// ValidationError → Error → Object
console.log(`validationErr instanceof ValidationError → ${validationErr instanceof ValidationError}`); // true
console.log(`validationErr instanceof Error → ${validationErr instanceof Error}`);                     // true (parent class)
console.log(`validationErr instanceof NotFoundError → ${validationErr instanceof NotFoundError}`);     // false (sibling class)
