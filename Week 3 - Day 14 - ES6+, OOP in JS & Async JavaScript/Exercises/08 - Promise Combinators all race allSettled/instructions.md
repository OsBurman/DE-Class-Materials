# Exercise 08: Promise Combinators — all, race, allSettled

## Objective
Use the three main **Promise combinator** methods to manage multiple concurrent Promises: `Promise.all`, `Promise.race`, and `Promise.allSettled`.

---

## Background

| Method | Resolves when | Rejects when |
|---|---|---|
| `Promise.all(arr)` | **All** resolve | **Any one** rejects |
| `Promise.race(arr)` | **First** settles (resolve or reject) | First settles with rejection |
| `Promise.allSettled(arr)` | **All** settle (never rejects) | — |

---

## Setup
Create this reusable helper at the top of your file:
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

### Requirement 1 — Promise.all (all resolve)
Pass three promises that all resolve to `Promise.all`.  
Log the resulting array.

Expected output:
```
Promise.all resolved: [ 'OK: A', 'OK: B', 'OK: C' ]
```

---

### Requirement 2 — Promise.all (one rejects)
Pass three promises where the second one rejects.  
Handle the rejection with `.catch`.

Expected output:
```
Promise.all rejected: FAILED: B
```

---

### Requirement 3 — Promise.race
Pass three promises with different delays (e.g., 300 ms, 100 ms, 200 ms).  
The fastest one wins.

Expected output:
```
Promise.race winner: OK: fast
```

---

### Requirement 4 — Promise.race (fastest rejects)
Pass three promises where the fastest one rejects.  
Handle with `.catch`.

Expected output:
```
Promise.race first rejected: FAILED: quickFail
```

---

### Requirement 5 — Promise.allSettled
Pass three promises where some resolve and some reject.  
Log each result's `status` and either `value` or `reason`.

Expected output:
```
allSettled results:
  fulfilled → OK: X
  rejected  → FAILED: Y
  fulfilled → OK: Z
```

---

## Running Your Code
```
node script.js
```
