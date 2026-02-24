// Exercise 11: Error Handling with try, catch, and Custom Errors
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Basic try/catch
// ─────────────────────────────────────────────

console.log("--- basic try/catch ---");

// TODO: Wrap the following in a try/catch block:
//   const value = null;
//   value.toUpperCase();  // ← this throws a TypeError
//
// In the catch block:
//   - Log: `Caught ${error.name}: ${error.message}`
//
// After the try/catch (outside it):
//   - Log: `Execution continued after catch`


// ─────────────────────────────────────────────
// PART 2: finally Block
// ─────────────────────────────────────────────

console.log("\n--- finally ---");

// TODO: Write function readData(shouldFail):
//   try {
//     if (shouldFail) throw new Error("Data read failed")
//     log "Data read successfully"
//   } catch(error) {
//     log "Error caught: [error.message]"
//   } finally {
//     log "Cleaning up resources"  ← always runs
//   }

// TODO: Call readData(false)
// Log "---"
// TODO: Call readData(true)


// ─────────────────────────────────────────────
// PART 3: Rethrowing Errors
// ─────────────────────────────────────────────

console.log("\n--- rethrowing ---");

// TODO: Write function parseAge(input):
//   - const age = Number(input)
//   - if (isNaN(age)) throw new TypeError("Age must be a number")
//   - if (age < 0 || age > 150) throw new RangeError("Age must be between 0 and 150")
//   - return age

// TODO: Write function processAge(input):
//   try {
//     const result = parseAge(input)
//     log `processAge("${input}") → ${result}`
//   } catch(error) {
//     if error instanceof TypeError → log `Type problem: ${error.message}`
//     else if error instanceof RangeError → log `Range problem: ${error.message}`
//     else throw error   ← rethrow unknown errors
//   }

// TODO: Call processAge("abc")
// TODO: Call processAge(-5)
// TODO: Call processAge("25")


// ─────────────────────────────────────────────
// PART 4: Custom Error Classes
// ─────────────────────────────────────────────

console.log("\n--- custom errors ---");

// TODO: Create class ValidationError extends Error:
//   constructor(message, field) {
//     super(message)        ← calls Error constructor to set this.message and this.stack
//     this.name = "ValidationError"
//     this.field = field
//   }

// TODO: Create class NotFoundError extends Error:
//   constructor(resource) {
//     super(`${resource} not found`)
//     this.name = "NotFoundError"
//   }

// TODO: Write function validateUser(user):
//   - if (!user.name)                  throw new ValidationError("Name is required", "name")
//   - if (!user.email.includes("@"))   throw new ValidationError("Email must contain @", "email")
//   - return "User is valid"

// TODO: Call validateUser({ name: "", email: "alice@example.com" })
//         catch and log: `ValidationError on field "${err.field}": ${err.message}`
// TODO: Call validateUser({ name: "Alice", email: "notanemail" })
//         catch and log the same way
// TODO: Call validateUser({ name: "Alice", email: "alice@example.com" })
//         log the return value directly


// ─────────────────────────────────────────────
// PART 5: Error Propagation
// ─────────────────────────────────────────────

console.log("\n--- error propagation ---");

// TODO: Write three functions:
//   function c() { throw new Error("thrown in c") }
//   function b() { c() }
//   function a() { b() }
//
// Call a() inside a try/catch and log: `Caught from a(): ${error.message}`
// Add a comment explaining that the error bubbles up c → b → a → catch


// ─────────────────────────────────────────────
// PART 6: instanceof Type Checking
// ─────────────────────────────────────────────

console.log("\n--- instanceof checks ---");

// TODO: Create const validationErr = new ValidationError("test", "field")
//
// Log:
//   `validationErr instanceof ValidationError → ${...}`  → true
//   `validationErr instanceof Error → ${...}`            → true
//   `validationErr instanceof NotFoundError → ${...}`    → false
