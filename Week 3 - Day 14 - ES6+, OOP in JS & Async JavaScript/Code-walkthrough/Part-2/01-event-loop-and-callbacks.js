// ============================================================
// Day 14 — Part 2  |  01-event-loop-and-callbacks.js
// Event Loop, Synchronous vs Asynchronous, Callbacks,
// Callback Hell, Error-First Callbacks
// ============================================================

"use strict";

// ============================================================
// 1. THE JAVASCRIPT RUNTIME — Mental Model
// ============================================================
//
// JavaScript is single-threaded — one thing at a time.
// But the browser/Node.js gives it async superpowers via:
//
//   ┌──────────────────────────────────────────────────────┐
//   │                  JS ENGINE (V8)                      │
//   │                                                      │
//   │   Call Stack          Memory Heap                    │
//   │   ──────────          ───────────                    │
//   │   [ main() ]          { objects, closures… }        │
//   │   [ greet() ]                                        │
//   │   [ console.log ]                                    │
//   │                                                      │
//   └──────────────────────────────────────────────────────┘
//           │
//           │  offloads async work to:
//           ▼
//   ┌──────────────────────────────────────────────────────┐
//   │              WEB APIs / Node APIs                    │
//   │  setTimeout  fetch  fs.readFile  addEventListener    │
//   │                                                      │
//   │   When the async work finishes, callbacks go into:  │
//   │                                                      │
//   │   Macrotask Queue (Task Queue)                       │
//   │     setTimeout, setInterval, I/O callbacks           │
//   │                                                      │
//   │   Microtask Queue (higher priority!)                 │
//   │     Promise .then/.catch callbacks, queueMicrotask   │
//   └──────────────────────────────────────────────────────┘
//           │
//           │  Event Loop
//           ▼
//   "While call stack is empty, first drain all microtasks,
//    then take one macrotask, repeat."
//
// KEY RULE:
//   Microtasks run before the next macrotask.
//   Promise callbacks are microtasks.
//   setTimeout callbacks are macrotasks.


// ============================================================
// 2. SYNCHRONOUS CODE — Blocking
// ============================================================

function syncHeavy() {
  console.log("Start heavy work...");
  // Simulate blocking CPU work
  const start = Date.now();
  while (Date.now() - start < 100) {} // busy-wait 100ms (bad practice!)
  console.log("Heavy work done.");
}

console.log("Before sync call");
syncHeavy();
console.log("After sync call");
// Output (in order, nothing else can run during the while loop):
// Before sync call
// Start heavy work...
// Heavy work done.
// After sync call


// ============================================================
// 3. ASYNCHRONOUS CODE — Non-Blocking
// ============================================================

console.log("--- Async demo start ---");

setTimeout(() => {
  console.log("Timeout A (1000ms)");
}, 1000);

setTimeout(() => {
  console.log("Timeout B (0ms)");  // ← STILL async, even with 0ms!
}, 0);

Promise.resolve("Microtask C").then(val => {
  console.log(val); // runs BEFORE Timeout B because it's a microtask!
});

console.log("--- Async demo end ---");

// Output:
// --- Async demo start ---
// --- Async demo end ---
// Microtask C           ← microtask, runs before any macrotask
// Timeout B (0ms)       ← macrotask with 0ms delay
// Timeout A (1000ms)    ← macrotask with 1s delay

// ⚠️ setTimeout(fn, 0) does NOT mean "run immediately".
//    It means "put this callback in the macrotask queue after 0ms".
//    Synchronous code and microtasks run first.


// ============================================================
// 4. setInterval
// ============================================================

let count = 0;
const intervalId = setInterval(() => {
  count++;
  console.log(`Tick: ${count}`);
  if (count >= 3) {
    clearInterval(intervalId); // ALWAYS clear intervals when done!
    console.log("Interval stopped.");
  }
}, 200);

// ⚠️ Forgetting to clearInterval causes memory leaks — the callback
//    runs forever until the process/tab is killed.


// ============================================================
// 5. THE CALLBACK PATTERN
// ============================================================
// A callback is a function passed as an argument to be called later.
// It's the original async pattern in JavaScript.

// Simple callback example
function fetchUser(userId, callback) {
  // Simulating a network request with setTimeout
  console.log(`Fetching user ${userId}...`);
  setTimeout(() => {
    const users = {
      1: { id: 1, name: "Alice", role: "admin" },
      2: { id: 2, name: "Bob",   role: "viewer" },
    };
    const user = users[userId];
    if (user) {
      callback(null, user); // error-first: no error, pass data
    } else {
      callback(new Error(`User ${userId} not found`)); // pass error
    }
  }, 300);
}

// Calling it:
fetchUser(1, (err, user) => {
  if (err) {
    console.error("Error:", err.message);
    return;
  }
  console.log("Got user:", user.name); // Got user: Alice
});

fetchUser(99, (err, user) => {
  if (err) {
    console.error("Error:", err.message); // Error: User 99 not found
    return;
  }
  console.log("Got user:", user.name);
});


// ============================================================
// 6. ERROR-FIRST CALLBACKS (Node.js Convention)
// ============================================================
// Convention: callback(error, data)
//   • First arg is always the error (or null if no error)
//   • Second arg is the result data

function readConfig(filename, callback) {
  setTimeout(() => {
    if (filename === "config.json") {
      callback(null, { host: "localhost", port: 3000 }); // success
    } else {
      callback(new Error(`File not found: ${filename}`)); // error
    }
  }, 100);
}

readConfig("config.json", (err, config) => {
  if (err) {
    console.error("Failed to read config:", err.message);
    return; // ← always return after handling the error!
  }
  console.log(`Server: ${config.host}:${config.port}`);
  // Server: localhost:3000
});

readConfig("missing.json", (err, config) => {
  if (err) {
    console.error("Failed to read config:", err.message);
    return;
  }
  console.log("This won't run");
});


// ============================================================
// 7. CALLBACK HELL (Pyramid of Doom)
// ============================================================
// Realistic scenario: load a user, then their orders, then order details
// When each step depends on the previous, callbacks nest deeply.

function fetchUserCb(userId, cb) {
  setTimeout(() => cb(null, { id: userId, name: "Alice" }), 100);
}

function fetchOrdersCb(userId, cb) {
  setTimeout(() => cb(null, [
    { orderId: 101, total: 59.99 },
    { orderId: 102, total: 24.50 }
  ]), 150);
}

function fetchOrderDetailsCb(orderId, cb) {
  setTimeout(() => cb(null, {
    orderId,
    items: ["Widget", "Gadget"],
    shipped: true
  }), 120);
}

// The pyramid grows with each nested callback:
fetchUserCb(1, (err, user) => {
  if (err) { console.error(err); return; }
  console.log("User:", user.name);

  fetchOrdersCb(user.id, (err, orders) => {
    if (err) { console.error(err); return; }
    console.log("Orders:", orders.length);

    fetchOrderDetailsCb(orders[0].orderId, (err, details) => {
      if (err) { console.error(err); return; }
      console.log("Details:", details.items.join(", "));

      // What if you need ANOTHER level? Indent further…
      // This keeps going — hence "pyramid of doom"
    });
  });
});

// Problems with callback hell:
//   1. Hard to read — logic flows diagonally, not top-to-bottom
//   2. Hard to error-handle — must check err at every level
//   3. Hard to reuse — code is tightly nested
//   4. Hard to reason about — control flow is non-obvious


// ============================================================
// 8. NAMED FUNCTIONS — Partial Mitigation
// ============================================================
// Flatten the pyramid by extracting named functions,
// but this only cosmetically fixes the problem.

function handleDetails(err, details) {
  if (err) { console.error(err); return; }
  console.log("Details:", details.items.join(", "));
}

function handleOrders(err, orders) {
  if (err) { console.error(err); return; }
  console.log("Orders:", orders.length);
  fetchOrderDetailsCb(orders[0].orderId, handleDetails);
}

function handleUser(err, user) {
  if (err) { console.error(err); return; }
  console.log("User:", user.name);
  fetchOrdersCb(user.id, handleOrders);
}

fetchUserCb(1, handleUser);

// Better formatting — but the underlying flow is still fragile.
// The real solution is Promises (next file).


// ============================================================
// 9. EVENT LOOP ORDER — Verification
// ============================================================
// Let's concretely verify the microtask vs macrotask ordering

console.log("=== Event Loop Order Demo ===");

// Macrotask (setTimeout)
setTimeout(() => console.log("4. macrotask (setTimeout 0)"), 0);

// Microtask (Promise)
Promise.resolve()
  .then(() => console.log("2. microtask 1"))
  .then(() => console.log("3. microtask 2")); // chained — also microtask

// Synchronous
console.log("1. synchronous");

// Expected output:
// 1. synchronous
// 2. microtask 1
// 3. microtask 2
// 4. macrotask (setTimeout 0)


// ============================================================
// 10. REAL-WORLD ASYNC PATTERNS WITH CALLBACKS
// ============================================================

// Pattern: parallel async operations with callbacks
// (without Promises, tracking completion is awkward)

function loadResource(name, delay, callback) {
  setTimeout(() => callback(null, `[${name} data]`), delay);
}

function loadAllParallel(names, delays, done) {
  const results = new Array(names.length);
  let completed = 0;

  names.forEach((name, i) => {
    loadResource(name, delays[i], (err, data) => {
      if (err) return done(err);
      results[i] = data;
      completed++;
      if (completed === names.length) {
        done(null, results); // all done
      }
    });
  });
}

loadAllParallel(
  ["users", "products", "config"],
  [300, 100, 200],
  (err, results) => {
    if (err) { console.error(err); return; }
    console.log("All loaded:", results);
    // All loaded: ["[users data]", "[products data]", "[config data]"]
    // Note: arrives in index order, not completion order
  }
);

// This manual tracking of `completed` is exactly what Promise.all does for us.
// The next file shows the clean way.
