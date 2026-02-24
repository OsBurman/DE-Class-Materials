// Exercise 07: Callbacks and Refactoring Callback Hell to Promises

// ── PART A: Callback Hell ─────────────────────────────────────────────────────

// TODO: Requirement 1
//   Create simulateAsync(label, delay, callback).
//   Nest 3 calls: "fetch user" (300ms) → "load profile" (200ms) → "get posts" (100ms)
//   Log "Callback chain complete" at the innermost level.
//
// function simulateAsync(label, delay, callback) { ... }


// TODO: Requirement 2
//   Create simulateAsyncMayFail(label, delay, shouldFail, callback, errorCallback).
//   If shouldFail is true, call errorCallback("Error in " + label) instead.
//   Demonstrate with: step 1 "start" succeeds, step 2 "middle step" fails.
//   Expected: Step done: start / Error in middle step


// ── PART B: Refactoring with Promises ────────────────────────────────────────

// TODO: Requirement 3
//   Create promiseDelay(label, delay) → returns a Promise that resolves with label.
//   Chain: "fetch user" (300ms) → "load profile" (200ms) → "get posts" (100ms)
//   Each .then logs "Resolved: <label>", then returns the next promiseDelay.
//   After all three, log "Promise chain complete".


// TODO: Requirement 4
//   Create promiseDelayMayFail(label, delay, shouldFail).
//   Chain: step one succeeds, step two fails (shouldFail: true).
//   Use .catch() to log "Caught error: <message>".


// TODO: Requirement 5
//   Demonstrate .finally().
//   Create a chain where the promise rejects immediately.
//   .catch logs "Caught: something failed"
//   .finally logs "Cleanup always runs"
