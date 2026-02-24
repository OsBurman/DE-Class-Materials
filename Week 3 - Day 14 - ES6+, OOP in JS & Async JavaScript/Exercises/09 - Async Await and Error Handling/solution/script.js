// Exercise 09: Async/Await and Error Handling — SOLUTION

// ── SETUP ─────────────────────────────────────────────────────────────────────

function delay(label, ms, shouldFail = false) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      shouldFail ? reject(`FAILED: ${label}`) : resolve(`OK: ${label}`);
    }, ms);
  });
}

// ── REQUIREMENT 1: Basic async/await ─────────────────────────────────────────

async function runSequential() {
  const r1 = await delay("step 1", 100);
  console.log(r1);
  const r2 = await delay("step 2", 100);
  console.log(r2);
  return "done";
}

runSequential().then(console.log);

// ── REQUIREMENT 2: try/catch with async/await ────────────────────────────────

async function runWithError() {
  try {
    const r1 = await delay("good step", 100);
    console.log(r1);
    const r2 = await delay("bad step", 100, true);
    console.log(r2); // never reached
  } catch (err) {
    console.log(`Caught async error: ${err}`);
  } finally {
    console.log("Finally block ran");
  }
}

runWithError();

// ── REQUIREMENT 3: await Promise.all ─────────────────────────────────────────

async function runParallel() {
  const results = await Promise.all([
    delay("p1", 100),
    delay("p2", 150),
    delay("p3", 200),
  ]);
  console.log("Parallel results:", results);
}

runParallel();

// ── REQUIREMENT 4: Sequential vs Parallel timing ──────────────────────────────

async function sequential() {
  const start = Date.now();
  await delay("s1", 200);
  await delay("s2", 200);
  await delay("s3", 200);
  console.log(`Sequential done in ~${Date.now() - start}ms`);
}

async function parallel() {
  const start = Date.now();
  await Promise.all([delay("p1", 200), delay("p2", 200), delay("p3", 200)]);
  console.log(`Parallel done in ~${Date.now() - start}ms`);
}

sequential();
parallel();

// ── REQUIREMENT 5: Async function returning a value ──────────────────────────

async function getUser(id) {
  await delay("lookup", 150);
  return { id, name: "User " + id };
}

(async () => {
  const user = await getUser(42);
  console.log("User:", user);
})();
