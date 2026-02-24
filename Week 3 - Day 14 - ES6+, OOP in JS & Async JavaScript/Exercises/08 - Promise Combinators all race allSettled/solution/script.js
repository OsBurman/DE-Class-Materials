// Exercise 08: Promise Combinators — all, race, allSettled — SOLUTION

// ── SETUP ─────────────────────────────────────────────────────────────────────

function delay(label, ms, shouldFail = false) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      shouldFail ? reject(`FAILED: ${label}`) : resolve(`OK: ${label}`);
    }, ms);
  });
}

// ── REQUIREMENT 1: Promise.all — all resolve ──────────────────────────────────

Promise.all([delay("A", 100), delay("B", 200), delay("C", 300)])
  .then((results) => {
    console.log("Promise.all resolved:", results);
  });

// ── REQUIREMENT 2: Promise.all — one rejects ─────────────────────────────────

Promise.all([delay("A", 100), delay("B", 200, true), delay("C", 300)])
  .catch((err) => {
    console.log("Promise.all rejected:", err);
  });

// ── REQUIREMENT 3: Promise.race — fastest resolves ───────────────────────────

Promise.race([delay("slow", 300), delay("fast", 100), delay("medium", 200)])
  .then((winner) => {
    console.log("Promise.race winner:", winner);
  });

// ── REQUIREMENT 4: Promise.race — fastest rejects ────────────────────────────

Promise.race([delay("normal", 300), delay("quickFail", 50, true), delay("other", 200)])
  .catch((err) => {
    console.log("Promise.race first rejected:", err);
  });

// ── REQUIREMENT 5: Promise.allSettled ────────────────────────────────────────

Promise.allSettled([delay("X", 100), delay("Y", 150, true), delay("Z", 200)])
  .then((results) => {
    console.log("allSettled results:");
    results.forEach((result) => {
      if (result.status === "fulfilled") {
        console.log(`  fulfilled → ${result.value}`);
      } else {
        console.log(`  rejected  → ${result.reason}`);
      }
    });
  });
