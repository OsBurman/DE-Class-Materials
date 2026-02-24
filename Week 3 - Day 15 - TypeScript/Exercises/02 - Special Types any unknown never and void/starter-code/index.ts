// Exercise 02: Special Types — any, unknown, never, and void

// ── 1. any ────────────────────────────────────────────────────────────────────

// TODO: Declare `flexible` as type `any`.
//       Assign it "hello", log it. Reassign to 42, log it. Reassign to true, log it.
//       Format: "flexible as string: hello" / "flexible as number: 42" / "flexible as boolean: true"


// ── 2. unknown ────────────────────────────────────────────────────────────────

// TODO: Declare `mystery` as type `unknown` and assign the string "42".
//       Comment out mystery.toUpperCase() and note the TypeScript error.
//       Then add a typeof check: if (typeof mystery === "string") { ... }
//       Inside the check, call mystery.toUpperCase() and log it.
//       Format: "mystery uppercased: 42"

// mystery.toUpperCase(); // Error: Object is of type 'unknown'


// ── 3. void ───────────────────────────────────────────────────────────────────

// TODO: Write function logMessage(msg: string): void that console.logs the msg.
//       Call it with "TypeScript is great".
//       Format: "logMessage: TypeScript is great"


// ── 4. never ─────────────────────────────────────────────────────────────────

// TODO: Write function throwError(message: string): never that throws new Error(message).
//       Call it inside a try/catch with "Something went wrong".
//       Log "Caught: " + the error message.


// ── 5. Exhaustive switch with never ──────────────────────────────────────────

// TODO: Define type Direction = "north" | "south" | "east" | "west"
// TODO: Write function assertNever(x: never): never that throws "Unexpected: " + x
// TODO: Write function describeDirection(d: Direction): string using a switch.
//       Each case should return "<direction> → Go <direction>"
//       The default branch should call assertNever(d).
// TODO: Call describeDirection with all four values and log each result.
