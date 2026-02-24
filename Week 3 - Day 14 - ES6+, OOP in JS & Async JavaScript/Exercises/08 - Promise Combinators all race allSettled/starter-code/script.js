// Exercise 08: Promise Combinators — all, race, allSettled

// ── SETUP ─────────────────────────────────────────────────────────────────────

// Helper — creates a promise that resolves or rejects after `ms` milliseconds
function delay(label, ms, shouldFail = false) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      shouldFail ? reject(`FAILED: ${label}`) : resolve(`OK: ${label}`);
    }, ms);
  });
}

// ── REQUIREMENT 1: Promise.all — all resolve ──────────────────────────────────

// TODO: Pass delay("A", 100), delay("B", 200), delay("C", 300) to Promise.all
//       Log the resolved array.
//       Expected: Promise.all resolved: [ 'OK: A', 'OK: B', 'OK: C' ]


// ── REQUIREMENT 2: Promise.all — one rejects ─────────────────────────────────

// TODO: Pass delay("A", 100), delay("B", 200, true), delay("C", 300) to Promise.all
//       Handle the rejection with .catch and log the reason.
//       Expected: Promise.all rejected: FAILED: B


// ── REQUIREMENT 3: Promise.race — fastest resolves ───────────────────────────

// TODO: Pass delay("slow", 300), delay("fast", 100), delay("medium", 200) to Promise.race
//       Log the winner.
//       Expected: Promise.race winner: OK: fast


// ── REQUIREMENT 4: Promise.race — fastest rejects ────────────────────────────

// TODO: Pass delay("normal", 300), delay("quickFail", 50, true), delay("other", 200)
//       Handle rejection with .catch and log the reason.
//       Expected: Promise.race first rejected: FAILED: quickFail


// ── REQUIREMENT 5: Promise.allSettled ────────────────────────────────────────

// TODO: Pass delay("X", 100), delay("Y", 150, true), delay("Z", 200) to Promise.allSettled
//       Iterate results and log each status + value/reason.
//       Expected:
//         allSettled results:
//           fulfilled → OK: X
//           rejected  → FAILED: Y
//           fulfilled → OK: Z
