// Exercise 07: Callbacks and Refactoring Callback Hell to Promises — SOLUTION

// ── PART A: Callback Hell ─────────────────────────────────────────────────────

// Requirement 1 — Pyramid of doom
function simulateAsync(label, delay, callback) {
  setTimeout(() => {
    console.log(`Step done: ${label}`);
    callback();
  }, delay);
}

simulateAsync("fetch user", 300, () => {
  simulateAsync("load profile", 200, () => {
    simulateAsync("get posts", 100, () => {
      console.log("Callback chain complete");
    });
  });
});

// Requirement 2 — Error callbacks
function simulateAsyncMayFail(label, delay, shouldFail, callback, errorCallback) {
  setTimeout(() => {
    if (shouldFail) {
      errorCallback("Error in " + label);
    } else {
      console.log(`Step done: ${label}`);
      callback();
    }
  }, delay);
}

simulateAsyncMayFail(
  "start", 100, false,
  () => {
    simulateAsyncMayFail(
      "middle step", 100, true,
      () => { console.log("Should not reach here"); },
      (err) => { console.log(err); }
    );
  },
  (err) => { console.log(err); }
);

// ── PART B: Refactoring with Promises ────────────────────────────────────────

// Requirement 3 — Promise chain
function promiseDelay(label, delay) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(label), delay);
  });
}

promiseDelay("fetch user", 300)
  .then((label) => {
    console.log(`Resolved: ${label}`);
    return promiseDelay("load profile", 200);
  })
  .then((label) => {
    console.log(`Resolved: ${label}`);
    return promiseDelay("get posts", 100);
  })
  .then((label) => {
    console.log(`Resolved: ${label}`);
    console.log("Promise chain complete");
  });

// Requirement 4 — Rejection with .catch
function promiseDelayMayFail(label, delay, shouldFail) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (shouldFail) {
        reject("Error: " + label);
      } else {
        resolve(label);
      }
    }, delay);
  });
}

promiseDelayMayFail("step one", 100, false)
  .then((label) => {
    console.log(`Resolved: ${label}`);
    return promiseDelayMayFail("step two", 100, true);
  })
  .catch((err) => {
    console.log(`Caught error: ${err}`);
  });

// Requirement 5 — .finally
Promise.reject("something failed")
  .catch((err) => {
    console.log(`Caught: ${err}`);
  })
  .finally(() => {
    console.log("Cleanup always runs");
  });
