// ============================================================
// Day 14 — Part 2  |  02-promises.js
// Promises: Creation, States, Chaining, Error Handling,
// Promise.all / Promise.race / Promise.allSettled
// ============================================================

"use strict";

// ============================================================
// 1. WHAT IS A PROMISE?
// ============================================================
// A Promise is an object representing the eventual completion
// (or failure) of an asynchronous operation and its resulting value.
//
// Three states — and a Promise can only ever be in ONE state:
//
//   pending   → initial state, neither fulfilled nor rejected
//   fulfilled → operation succeeded, has a result value
//   rejected  → operation failed, has a reason (error)
//
// Once fulfilled or rejected, a Promise is "settled" — immutable.
// A settled Promise never changes state again.


// ============================================================
// 2. CREATING A PROMISE
// ============================================================

const myFirstPromise = new Promise((resolve, reject) => {
  // The executor function runs immediately (synchronously)
  // Call resolve(value) to fulfill the Promise
  // Call reject(reason) to reject it
  // Only the first call matters — subsequent resolve/reject calls are ignored

  const success = true;
  if (success) {
    resolve("Operation succeeded!"); // fulfilled with this value
  } else {
    reject(new Error("Something went wrong")); // rejected with this error
  }
});

myFirstPromise
  .then(value => console.log("Fulfilled:", value))   // Fulfilled: Operation succeeded!
  .catch(err  => console.error("Rejected:", err.message));


// ============================================================
// 3. PROMISE STATES IN ACTION
// ============================================================

// Immediately resolved
const resolved = Promise.resolve(42);
resolved.then(v => console.log("Resolved:", v)); // 42

// Immediately rejected
const rejected = Promise.reject(new Error("Nope"));
rejected.catch(e => console.log("Rejected:", e.message)); // Nope

// Pending — resolves after a delay
const delayed = new Promise(resolve => setTimeout(() => resolve("done!"), 500));
delayed.then(v => console.log("Delayed:", v)); // done! (after 500ms)


// ============================================================
// 4. SIMULATED ASYNC FUNCTIONS (return Promises)
// ============================================================

function fetchUser(userId) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const db = {
        1: { id: 1, name: "Alice", departmentId: 10 },
        2: { id: 2, name: "Bob",   departmentId: 20 },
      };
      const user = db[userId];
      if (user) resolve(user);
      else reject(new Error(`User ${userId} not found`));
    }, 200);
  });
}

function fetchDepartment(departmentId) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const departments = {
        10: { id: 10, name: "Engineering" },
        20: { id: 20, name: "Marketing" },
      };
      const dept = departments[departmentId];
      if (dept) resolve(dept);
      else reject(new Error(`Department ${departmentId} not found`));
    }, 150);
  });
}

function fetchPermissions(userId) {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(userId === 1 ? ["read", "write", "admin"] : ["read"]);
    }, 100);
  });
}


// ============================================================
// 5. THEN CHAINING
// ============================================================
// .then() always returns a NEW Promise
// The value returned from a .then() handler is wrapped in a Promise
// If you return a Promise, it is "adopted" (unwrapped) automatically

fetchUser(1)
  .then(user => {
    console.log("User:", user.name);
    // Return a new Promise — next .then() waits for it
    return fetchDepartment(user.departmentId);
  })
  .then(dept => {
    console.log("Department:", dept.name);
    // Return a plain value — next .then() gets it immediately
    return dept.name.toUpperCase();
  })
  .then(upperName => {
    console.log("Upper:", upperName); // ENGINEERING
  });

// The key insight: each .then() creates a new Promise.
// The chain is flat — no nesting, reads top to bottom.


// ============================================================
// 6. ERROR HANDLING — .catch() and .finally()
// ============================================================

// .catch(fn) is shorthand for .then(undefined, fn)
// It catches rejections from ANY previous step in the chain

fetchUser(999) // will reject
  .then(user => {
    console.log("This won't run");
    return fetchDepartment(user.departmentId); // also won't run
  })
  .then(dept => {
    console.log("This won't run either");
  })
  .catch(err => {
    console.error("Caught:", err.message); // Caught: User 999 not found
  });

// .finally() runs whether the Promise resolved or rejected
// Useful for cleanup (hide spinner, close connection, etc.)
// Does NOT receive the value — just a cleanup hook

fetchUser(1)
  .then(user => {
    console.log("Got user:", user.name);
    return user;
  })
  .catch(err => {
    console.error("Error:", err.message);
  })
  .finally(() => {
    console.log("Done — hide loading spinner"); // always runs
  });

// ⚠️ .catch() followed by .then() can RECOVER a chain:
Promise.reject(new Error("initial error"))
  .catch(err => {
    console.log("Recovered from:", err.message);
    return "default value"; // recovery value
  })
  .then(value => {
    console.log("Continuing with:", value); // Continuing with: default value
  });


// ============================================================
// 7. THROWING IN A .then() HANDLER
// ============================================================
// If a .then() handler throws, the next .catch() catches it

fetchUser(1)
  .then(user => {
    if (user.role === "banned") throw new Error("User is banned");
    return user;
  })
  .then(user => {
    // Explicit validation throw inside .then
    if (!user.name) throw new Error("User has no name");
    return user.name;
  })
  .catch(err => console.error("Chain error:", err.message));


// ============================================================
// 8. CONVERTING CALLBACK APIs TO PROMISES
// ============================================================

// Old callback-based function
function readFileCb(path, callback) {
  setTimeout(() => {
    if (path.endsWith(".txt")) {
      callback(null, `Contents of ${path}`);
    } else {
      callback(new Error(`Unsupported format: ${path}`));
    }
  }, 100);
}

// Promisified wrapper
function readFile(path) {
  return new Promise((resolve, reject) => {
    readFileCb(path, (err, data) => {
      if (err) reject(err);
      else resolve(data);
    });
  });
}

readFile("notes.txt")
  .then(content => console.log("File:", content))
  .catch(err => console.error("File error:", err.message));

readFile("image.png")
  .then(content => console.log("File:", content))
  .catch(err => console.error("File error:", err.message));
  // File error: Unsupported format: image.png

// Node.js has util.promisify() that does this automatically:
// const { promisify } = require("util");
// const readFileAsync = promisify(fs.readFile);


// ============================================================
// 9. Promise.all — Wait for ALL, fail fast
// ============================================================
// Takes an array of Promises
// Resolves when ALL resolve — result is array of values (preserves order!)
// Rejects immediately if ANY one rejects

const usersToLoad = [
  fetchUser(1),
  fetchUser(2),
];

Promise.all(usersToLoad)
  .then(users => {
    console.log("All users:", users.map(u => u.name)); // ["Alice", "Bob"]
  })
  .catch(err => {
    console.error("One failed:", err.message);
  });

// Fail fast demo
Promise.all([
  fetchUser(1),
  fetchUser(999), // will reject
  fetchUser(2),
])
  .then(users => console.log("Won't reach here"))
  .catch(err => console.error("Promise.all failed:", err.message));
  // Promise.all failed: User 999 not found

// ⚠️ Even though users 1 and 2 would succeed, the whole Promise.all
//    rejects as soon as 999 rejects. The other Promises are NOT cancelled —
//    they still run to completion, but their values are discarded.

// Real use: load multiple independent resources in parallel
function loadDashboard(userId) {
  return Promise.all([
    fetchUser(userId),
    fetchPermissions(userId),
  ]).then(([user, permissions]) => ({
    user,
    permissions,
  }));
}

loadDashboard(1).then(dashboard => {
  console.log("Dashboard:", dashboard.user.name, dashboard.permissions);
  // Dashboard: Alice ["read", "write", "admin"]
});


// ============================================================
// 10. Promise.race — First to settle wins
// ============================================================
// Resolves or rejects with the value/reason of the FIRST settled Promise

function withTimeout(promise, ms) {
  const timeout = new Promise((_, reject) =>
    setTimeout(() => reject(new Error(`Timed out after ${ms}ms`)), ms)
  );
  return Promise.race([promise, timeout]);
}

// Fast enough
withTimeout(fetchUser(1), 1000)
  .then(user => console.log("Race won:", user.name))    // Race won: Alice
  .catch(err => console.error("Race error:", err.message));

// Too slow (uncomment to test — use 50ms timeout)
// withTimeout(fetchUser(1), 50)
//   .then(user => console.log("Race won:", user.name))
//   .catch(err => console.error("Race error:", err.message));
//   // Race error: Timed out after 50ms


// ============================================================
// 11. Promise.allSettled — Wait for ALL regardless of outcome
// ============================================================
// Resolves when ALL Promises settle (fulfilled OR rejected)
// Never rejects — always gives back an array of result objects
// Each result: { status: "fulfilled", value } or { status: "rejected", reason }

Promise.allSettled([
  fetchUser(1),
  fetchUser(999),
  fetchUser(2),
]).then(results => {
  results.forEach((result, i) => {
    if (result.status === "fulfilled") {
      console.log(`User ${i}: ${result.value.name}`);
    } else {
      console.log(`User ${i} failed: ${result.reason.message}`);
    }
  });
});
// User 0: Alice
// User 1 failed: User 999 not found
// User 2: Bob

// Use allSettled when you want all results and can handle partial failures


// ============================================================
// 12. Promise.any — First to FULFILL wins (ES2021)
// ============================================================
// Resolves with the first fulfilled Promise
// Rejects only if ALL reject (AggregateError)

Promise.any([
  Promise.reject(new Error("first fails")),
  Promise.resolve("second wins"),
  Promise.resolve("third"),
])
  .then(v => console.log("Promise.any:", v))  // second wins
  .catch(e => console.error("All failed:", e));


// ============================================================
// 13. PROMISE CHAINING — Refactoring Callback Hell
// ============================================================
// The same user → orders → details flow from the previous file,
// now clean and readable

function fetchUser2(id) {
  return new Promise(res => setTimeout(() => res({ id, name: "Alice" }), 100));
}
function fetchOrders(userId) {
  return new Promise(res => setTimeout(() => res([{ orderId: 101 }, { orderId: 102 }]), 150));
}
function fetchOrderDetails(orderId) {
  return new Promise(res => setTimeout(() => res({ orderId, items: ["Widget", "Gadget"], shipped: true }), 120));
}

// Clean, flat chain:
fetchUser2(1)
  .then(user => {
    console.log("User:", user.name);
    return fetchOrders(user.id);
  })
  .then(orders => {
    console.log("Orders:", orders.length);
    return fetchOrderDetails(orders[0].orderId);
  })
  .then(details => {
    console.log("Details:", details.items.join(", "));
  })
  .catch(err => {
    console.error("Something failed:", err.message); // ONE catch handles all levels
  });

// Compare to the callback hell version — same logic, completely different readability.
