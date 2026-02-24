// Exercise 06: Event Loop, Call Stack, and Async Execution Model — SOLUTION

// ── PART A: Call Stack & Synchronous Execution ────────────────────────────────

function third() {
  console.log("third");
}

function second() {
  console.log("second");
  third();
}

function first() {
  console.log("first");
  second();
}

first();
// Output: first → second → third

// ── PART B: Synchronous vs Asynchronous Ordering ──────────────────────────────

// Prediction: Start, End, Promise microtask, setTimeout 0ms
// Reason: sync code runs first, then microtask queue, then macrotask queue.

console.log("Start");

setTimeout(() => console.log("setTimeout 0ms"), 0);

Promise.resolve().then(() => console.log("Promise microtask"));

console.log("End");
// Output: Start → End → Promise microtask → setTimeout 0ms

// ── PART C: Multiple Timers & Microtasks Interleaved ─────────────────────────

console.log("sync");

Promise.resolve().then(() => console.log("microtask 1"));
Promise.resolve().then(() => console.log("microtask 2"));

setTimeout(() => console.log("macrotask A"), 0);
setTimeout(() => console.log("macrotask B"), 0);

// Output: sync → microtask 1 → microtask 2 → macrotask A → macrotask B

// ── PART D: Nested Microtasks ─────────────────────────────────────────────────

setTimeout(() => console.log("timer"), 0);

Promise.resolve()
  .then(() => {
    console.log("then 1");
    return Promise.resolve();
  })
  .then(() => {
    console.log("then 2");
  });

// Output: then 1 → then 2 → timer
// Note: returning Promise.resolve() from a .then adds another microtask tick,
// but both "then 1" and "then 2" still flush before the macrotask queue runs.
