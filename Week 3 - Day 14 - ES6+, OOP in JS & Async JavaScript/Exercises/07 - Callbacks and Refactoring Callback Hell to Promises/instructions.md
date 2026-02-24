# Exercise 07: Callbacks and Refactoring Callback Hell to Promises

## Objective
Experience the **callback pyramid of doom**, understand why it is problematic, then refactor the same async flow using **Promises** with `.then()` chaining and `.catch()` for error handling.

---

## Background
Before Promises, async operations were composed by passing callbacks. Deep nesting creates hard-to-read, hard-to-maintain "callback hell." Promises flatten this into a readable chain.

---

## Requirements

### Part A — Callback Hell (Pyramid of Doom)

**Requirement 1:**  
Simulate three sequential async steps using `setTimeout`-based helpers.

Create a helper:
```js
function simulateAsync(label, delay, callback) {
  setTimeout(() => {
    console.log(`Step done: ${label}`);
    callback();
  }, delay);
}
```

Nest three calls:
- Step 1 "fetch user" (300 ms) → inside callback, Step 2 "load profile" (200 ms) → inside callback, Step 3 "get posts" (100 ms)

Expected output (in order):
```
Step done: fetch user
Step done: load profile
Step done: get posts
Callback chain complete
```
Log `"Callback chain complete"` at the innermost level.

---

**Requirement 2:**  
Add error simulation. Create:
```js
function simulateAsyncMayFail(label, delay, shouldFail, callback, errorCallback) { ... }
```
If `shouldFail` is true, call `errorCallback("Error in " + label)` instead. Call the function with `shouldFail: true` on the second step and log the error message.

Expected output:
```
Step done: start
Error in middle step
```

---

### Part B — Refactoring with Promises

**Requirement 3:**  
Create a `promiseDelay(label, delay)` function that returns a Promise which resolves with `label` after `delay` ms.

Chain three `.then()` calls for "fetch user" (300 ms) → "load profile" (200 ms) → "get posts" (100 ms).

Expected output:
```
Resolved: fetch user
Resolved: load profile
Resolved: get posts
Promise chain complete
```

---

**Requirement 4:**  
Create `promiseDelayMayFail(label, delay, shouldFail)` — returns a Promise that rejects with `"Error: " + label` if `shouldFail` is true.

Build a chain where the second step fails. Use `.catch()` to handle the rejection.

Expected output:
```
Resolved: step one
Caught error: Error: step two
```

---

**Requirement 5:**  
Demonstrate `.finally()`. Attach a `.finally()` to a Promise chain (one that rejects) and log `"Cleanup always runs"`.

Expected output:
```
Caught: something failed
Cleanup always runs
```

---

## Running Your Code
```
node script.js
```

Outputs will appear with slight delays due to setTimeout.
