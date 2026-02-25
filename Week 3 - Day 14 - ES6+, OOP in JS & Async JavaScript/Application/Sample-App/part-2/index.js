// Day 14 Part 2 — Async JavaScript: Event Loop, Promises, async/await, Fetch
// Run: node index.js

"use strict";

console.log("╔══════════════════════════════════════════════════════════════╗");
console.log("║  Day 14 Part 2 — Async JavaScript                          ║");
console.log("╚══════════════════════════════════════════════════════════════╝\n");

async function main() {
  demoEventLoop();
  await demoCallbacks();
  await demoPromises();
  await demoAsyncAwait();
  await demoPromiseCombinators();
  demoJSON();
}
main();

// ─────────────────────────────────────────────────────────────
// 1. Event Loop & Call Stack
// ─────────────────────────────────────────────────────────────
function demoEventLoop() {
  console.log("=== 1. Event Loop & Execution Order ===");
  // Demonstrate synchronous vs async execution order
  console.log("  [1] Synchronous — runs first");
  setTimeout(() => console.log("  [3] setTimeout(0) — macro-task queue"), 0);
  Promise.resolve().then(() => console.log("  [2] Promise.resolve().then — micro-task queue"));
  console.log("  [1] Synchronous — still sync block");
  // Output order: [1], [1], [2], [3]
  // Micro-tasks (Promise) run before macro-tasks (setTimeout)
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 2. Callbacks
// ─────────────────────────────────────────────────────────────
function demoCallbacks() {
  return new Promise(resolve => {
    console.log("=== 2. Callbacks ===");
    // Simulate async with setTimeout
    function fetchUser(id, callback) {
      setTimeout(() => {
        if (id > 0) callback(null, { id, name: `User-${id}` });
        else        callback(new Error("Invalid ID"), null);
      }, 50);
    }

    fetchUser(1, (err, user) => {
      if (err) { console.log("  Error:", err.message); return; }
      console.log("  Callback user:", user.name);

      // Callback hell example (then refactored with Promises)
      fetchUser(2, (err2, user2) => {
        if (err2) return;
        console.log("  Nested callback user:", user2.name);
        console.log("  Callback hell = deeply nested → hard to read/maintain");
        console.log();
        resolve();
      });
    });
  });
}

// ─────────────────────────────────────────────────────────────
// 3. Promises
// ─────────────────────────────────────────────────────────────
function demoPromises() {
  console.log("=== 3. Promises ===");

  // Creating a Promise
  function fetchProduct(id) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const products = {
          1: { id: 1, name: "Laptop", price: 999 },
          2: { id: 2, name: "Phone",  price: 599 },
        };
        if (products[id]) resolve(products[id]);
        else reject(new Error(`Product ${id} not found`));
      }, 60);
    });
  }

  // Promise chaining
  return fetchProduct(1)
    .then(p => {
      console.log("  Resolved:", p.name, "$" + p.price);
      return p.price * 1.1; // transform
    })
    .then(total => console.log("  With tax:", "$" + total.toFixed(2)))
    .then(() => fetchProduct(99)) // rejected
    .catch(err => console.log("  Caught:", err.message))
    .finally(() => {
      console.log("  finally() always runs");
      console.log();
    });
}

// ─────────────────────────────────────────────────────────────
// 4. async / await
// ─────────────────────────────────────────────────────────────
async function demoAsyncAwait() {
  console.log("=== 4. async / await ===");

  // Simulated API calls
  async function getUser(id)   {
    await delay(40);
    return { id, name: `Alice-${id}`, dept: "Engineering" };
  }
  async function getScore(userId) {
    await delay(30);
    return { userId, score: 88 + userId };
  }

  // Sequential await
  const user  = await getUser(1);
  const score = await getScore(user.id);
  console.log("  Sequential: user =", user.name, "| score =", score.score);

  // Parallel await (faster — both run concurrently)
  const [user2, score2] = await Promise.all([getUser(2), getScore(2)]);
  console.log("  Parallel:   user =", user2.name, "| score =", score2.score);

  // Error handling with async/await
  async function riskyFetch(shouldFail) {
    await delay(20);
    if (shouldFail) throw new Error("API connection timeout");
    return "data received";
  }

  try {
    const result = await riskyFetch(false);
    console.log("  Success:", result);
    await riskyFetch(true);
  } catch (e) {
    console.log("  Caught async error:", e.message);
  } finally {
    console.log("  async/await finally block");
    console.log();
  }
}

// ─────────────────────────────────────────────────────────────
// 5. Promise Combinators
// ─────────────────────────────────────────────────────────────
async function demoPromiseCombinators() {
  console.log("=== 5. Promise Combinators ===");

  const p1 = delay(30).then(() => "A-done");
  const p2 = delay(50).then(() => "B-done");
  const p3 = delay(10).then(() => "C-done");
  const pFail = delay(20).then(() => { throw new Error("D-failed"); });

  // Promise.all — all must resolve; one rejection rejects all
  const all = await Promise.all([p1, p2, p3]);
  console.log("  Promise.all:", all);

  // Promise.allSettled — waits for all, gives status of each
  const settled = await Promise.allSettled([
    delay(10).then(() => "ok-1"),
    delay(10).then(() => { throw new Error("fail-2"); }),
    delay(10).then(() => "ok-3"),
  ]);
  settled.forEach(r => console.log(`  allSettled: ${r.status} → ${r.status === "fulfilled" ? r.value : r.reason.message}`));

  // Promise.race — first to settle wins
  const raceP1 = delay(60).then(() => "slow");
  const raceP2 = delay(20).then(() => "fast");
  const winner = await Promise.race([raceP1, raceP2]);
  console.log("  Promise.race winner:", winner);

  // Promise.any — first fulfilled wins (ignores rejections)
  const any = await Promise.any([
    delay(30).then(() => { throw new Error("reject-1"); }),
    delay(20).then(() => "first-fulfilled"),
    delay(40).then(() => "second-fulfilled"),
  ]);
  console.log("  Promise.any:", any);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 6. JSON
// ─────────────────────────────────────────────────────────────
function demoJSON() {
  console.log("=== 6. JSON Parsing & Stringification ===");

  const obj = { name: "Alice", scores: [88, 92, 95], active: true, secret: undefined };

  const json   = JSON.stringify(obj, null, 2);
  console.log("  JSON.stringify:\n" + json.split("\n").map(l => "    " + l).join("\n"));
  console.log("  Note: 'undefined' values are omitted in JSON");

  const parsed = JSON.parse(json);
  console.log("  JSON.parse → name:", parsed.name, "| scores:", parsed.scores);

  // Deep clone using JSON (works for plain objects)
  const clone = JSON.parse(JSON.stringify({ a: 1, b: { c: 2 } }));
  clone.b.c = 999;
  console.log("  Deep clone via JSON — original b.c still:", JSON.parse(JSON.stringify({ a: 1, b: { c: 2 } })).b.c);

  console.log("\n✓ Async JavaScript demo complete.");
}

// ── Utility ──────────────────────────────────────────────────
function delay(ms) { return new Promise(res => setTimeout(res, ms)); }
