// Exercise 09: Async/Await and Error Handling

// ── SETUP ─────────────────────────────────────────────────────────────────────

function delay(label, ms, shouldFail = false) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      shouldFail ? reject(`FAILED: ${label}`) : resolve(`OK: ${label}`);
    }, ms);
  });
}

// ── REQUIREMENT 1: Basic async/await ─────────────────────────────────────────

// TODO: Write async function runSequential() that awaits two steps in sequence.
//       Log each result. Return "done" and log it via .then(console.log).
//       Expected: OK: step 1 / OK: step 2 / done


// ── REQUIREMENT 2: try/catch with async/await ────────────────────────────────

// TODO: Write async function runWithError().
//       First await succeeds, second await fails (shouldFail: true).
//       Wrap in try/catch/finally.
//       Expected: OK: good step / Caught async error: FAILED: bad step / Finally block ran


// ── REQUIREMENT 3: await Promise.all ─────────────────────────────────────────

// TODO: Write async function runParallel().
//       Start three delays simultaneously with Promise.all, await the combined result.
//       Expected: Parallel results: [ 'OK: p1', 'OK: p2', 'OK: p3' ]


// ── REQUIREMENT 4: Sequential vs Parallel timing ──────────────────────────────

// TODO: Write async function sequential() — await three 200ms delays in sequence.
//       Write async function parallel() — await Promise.all of three 200ms delays.
//       Use Date.now() to measure and log elapsed time for each.
//       Expected: Sequential done in ~600ms / Parallel done in ~200ms


// ── REQUIREMENT 5: Async function returning a value ──────────────────────────

// TODO: Write async function getUser(id) that resolves after 150ms and returns
//       { id, name: "User " + id }.
//       Await it from another async IIFE and log the user.
//       Expected: User: { id: 42, name: 'User 42' }
