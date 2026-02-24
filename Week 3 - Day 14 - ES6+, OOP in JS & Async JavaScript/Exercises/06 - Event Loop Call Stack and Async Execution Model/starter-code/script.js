// Exercise 06: Event Loop, Call Stack, and Async Execution Model

// ── PART A: Call Stack & Synchronous Execution ────────────────────────────────

// TODO: Requirement 1
//   Define three functions: first(), second(), third()
//   first()  → logs "first"  then calls second()
//   second() → logs "second" then calls third()
//   third()  → logs "third"
//   Call first().
//   Expected output (in order): first / second / third


// ── PART B: Synchronous vs Asynchronous Ordering ──────────────────────────────

// TODO: Requirement 2
//   Write these four statements in order:
//     console.log("Start")
//     setTimeout 0ms  → logs "setTimeout 0ms"
//     Promise.resolve().then → logs "Promise microtask"
//     console.log("End")
//
//   Add a comment ABOVE with your prediction of the output order.
//   Expected output: Start / End / Promise microtask / setTimeout 0ms


// ── PART C: Multiple Timers & Microtasks Interleaved ─────────────────────────

// TODO: Requirement 3
//   Log "sync" synchronously.
//   Queue Promise.resolve().then → "microtask 1"
//   Queue Promise.resolve().then → "microtask 2"
//   setTimeout 0ms → "macrotask A"
//   setTimeout 0ms → "macrotask B"
//   Expected output: sync / microtask 1 / microtask 2 / macrotask A / macrotask B


// ── PART D: Nested Microtasks ─────────────────────────────────────────────────

// TODO: Requirement 4
//   Schedule setTimeout("timer", 0) FIRST.
//   Then create a Promise chain:
//     .then → logs "then 1", returns Promise.resolve()
//     .then → logs "then 2"
//   Expected output: then 1 / then 2 / timer
