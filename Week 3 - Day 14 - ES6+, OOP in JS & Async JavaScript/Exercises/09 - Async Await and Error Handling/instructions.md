# Exercise 09: Async/Await and Error Handling

## Objective
Write async functions using the **`async`/`await`** syntax. Use **`try/catch`** to handle errors, and combine `async/await` with `Promise.all` for concurrent operations.

---

## Background
`async/await` is syntactic sugar over Promises. An `async` function always returns a Promise. Inside it, `await` pauses execution until the awaited Promise settles — keeping code looking synchronous while staying non-blocking.

```js
async function doWork() {
  const result = await somePromise;
  return result;
}
```

---

## Setup
Use this helper throughout the exercise:
```js
function delay(label, ms, shouldFail = false) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      shouldFail ? reject(`FAILED: ${label}`) : resolve(`OK: ${label}`);
    }, ms);
  });
}
```

---

## Requirements

### Requirement 1 — Basic async/await
Write an `async function runSequential()` that:
1. Awaits `delay("step 1", 100)`
2. Logs the result
3. Awaits `delay("step 2", 100)`
4. Logs the result
5. Returns `"done"`

Call the function and `.then(console.log)` on the returned Promise.

Expected output:
```
OK: step 1
OK: step 2
done
```

---

### Requirement 2 — try/catch with async/await
Write an `async function runWithError()` that:
- Awaits a step that succeeds → logs result
- Awaits a step that fails (`shouldFail: true`) inside a `try/catch`
- In the `catch` block, logs `"Caught async error: <message>"`
- In a `finally` block, logs `"Finally block ran"`

Expected output:
```
OK: good step
Caught async error: FAILED: bad step
Finally block ran
```

---

### Requirement 3 — await Promise.all
Write an `async function runParallel()` that:
- Starts three `delay` promises simultaneously using `Promise.all`
- Awaits the combined result
- Logs the results array

Expected output:
```
Parallel results: [ 'OK: p1', 'OK: p2', 'OK: p3' ]
```

---

### Requirement 4 — Sequential vs Parallel timing comparison
Write two async functions:
- `sequential()` — awaits three 200 ms delays one after another; log "Sequential done" + total time
- `parallel()` — awaits `Promise.all` of three 200 ms delays; log "Parallel done" + total time

Use `Date.now()` to measure time.

Expected output (approximate):
```
Sequential done in ~600ms
Parallel done in ~200ms
```

---

### Requirement 5 — Async function returning a value
Write an `async function getUser(id)` that:
- Simulates a 150 ms delay
- Returns `{ id, name: "User " + id }`

Await it from another async function and log the user object.

Expected output:
```
User: { id: 42, name: 'User 42' }
```

---

## Running Your Code
```
node script.js
```
