# Day 14 — Part 2 Walkthrough Script
## Async JavaScript: Event Loop, Promises, async/await, Fetch API
### Estimated time: 90 minutes

---

## Pre-Class Setup
- Open VS Code with `Part-2/` folder in the explorer
- Node.js terminal ready (test: `node --version` — need 18+ for native `fetch`)
- Chrome DevTools Console open in a tab (for live event loop demos)
- JSONPlaceholder docs open: https://jsonplaceholder.typicode.com
- Optional: Postman or Bruno to quickly demo raw HTTP responses

---

## PART 2A — Event Loop, Sync vs Async, Callbacks (30 min)
### File: `01-event-loop-and-callbacks.js`

---

### [ACTION] Open `01-event-loop-and-callbacks.js`. Start at Section 1 (the diagram).

---

### SECTION 1 — The Runtime Model (8 min)

**Say:**
> "JavaScript is single-threaded — it can only do one thing at a time. But you interact with websites that do multiple things simultaneously: timers tick, data loads, users click things. How? The answer is the event loop."

**[ACTION]** Draw on board:

```
┌─────────────────────────────────────────────────────────┐
│                    JS ENGINE (V8)                        │
│                                                          │
│  Call Stack               Memory Heap                    │
│  ───────────              ───────────                    │
│  [ currently running ]    { all objects live here }      │
│                                                          │
└──────────────────────────┬──────────────────────────────┘
                           │  when async work finishes:
                           ▼
┌─────────────────────────────────────────────────────────┐
│                 Web APIs / Node APIs                     │
│  setTimeout  fetch  addEventListener  fs.readFile        │
│                                                          │
│  Macrotask Queue (Task Queue)                            │
│    ← setTimeout callbacks, setInterval, I/O             │
│                                                          │
│  Microtask Queue  ← HIGHER PRIORITY                      │
│    ← Promise .then/.catch, queueMicrotask                │
└─────────────────────────────────────────────────────────┘

Event Loop rule:
  1. Run current call stack to empty
  2. Drain ALL microtasks (every one)
  3. Take ONE macrotask
  4. Go to step 2
```

**Say:**
> "The key rule: microtasks run before the next macrotask. That means all Promise callbacks run before the next setTimeout fires, even a setTimeout with 0 milliseconds."

**[ASK]** "If you have a setTimeout with 0ms and a Promise.resolve(), which callback runs first?"
> *(The Promise callback — it's a microtask.)*

---

### SECTION 2 — setTimeout with 0ms (3 min)

**[ACTION]** Run the event loop order demo (Section 9 of the file) in Node.js.

**Say:**
> "Look at the output. '1. synchronous' runs first — sync code clears the call stack. Then both microtasks run. Then the setTimeout 0 runs last. The 0 just means 'put this in the macrotask queue as soon as possible', but synchronous code and microtasks still go first."

**⚠️ WATCH OUT:**
> "You will NEVER see `setTimeout(fn, 0)` behave like an immediate function call. It's always deferred. A lot of developers are surprised by this the first time."

---

### SECTION 3 — The Callback Pattern (7 min)

**[ACTION]** Scroll to Section 5 — `fetchUser`.

**Say:**
> "The callback pattern is how JavaScript has handled async operations since the beginning. A callback is just a function you pass in that gets called when the async work is done."

**[ACTION]** Show `fetchUser(1, callback)` and run it.

**Say:**
> "Notice the convention: `callback(null, user)` on success, `callback(new Error(...))` on failure. This is the error-first callback pattern — standardized by Node.js. The first argument is always the error."

**[ASK]** "Why do we write `if (err) { ...; return; }` and then the happy path? What happens if you forget the `return`?"
> *(Without `return`, both the error handling block AND the happy path code would run. You'd try to read `user.name` when `user` is undefined.)*

**⚠️ WATCH OUT:**
> "Always `return` after handling the error in a callback. It's one of the most common bugs in callback-based code."

---

### SECTION 4 — Callback Hell (7 min)

**[ACTION]** Scroll to Section 7 — the nested callback pyramid.

**Say:**
> "Here's the real problem with callbacks. When you need to do three things in sequence — get a user, get their orders, get order details — you end up with this pyramid."

**[ACTION]** Read through the nested callbacks slowly, indenting deeper with each level.

**Say:**
> "Notice how the actual logic — what we WANT to do — is buried inside three layers of nesting. The code flows diagonally instead of top-to-bottom. Error handling is repeated at every level. This is called 'callback hell' or the 'pyramid of doom'."

**[ASK]** "What happens if I need a fourth level? Or a fifth?"
> *(More nesting, more indentation, harder to read and maintain.)*

**[ACTION]** Show the named function version (Section 8) — same logic, flattened.

**Say:**
> "Named functions cosmetically fix the indentation. But the underlying problem — no clean error propagation, hard-to-follow control flow — is still there. The real solution is Promises."

→ **TRANSITION:** "Let's look at how Promises solve this."

---

## PART 2B — Promises (30 min)
### File: `02-promises.js`

---

### [ACTION] Open `02-promises.js`. Start at Section 1.

---

### SECTION 1 — Promise States (5 min)

**[ACTION]** Draw on board:

```
           ┌──────────────┐
           │   pending    │
           └──────┬───────┘
          resolve │    │ reject
                  ▼    ▼
     ┌──────────┐  ┌──────────┐
     │fulfilled │  │ rejected │
     └──────────┘  └──────────┘
         (settled — never changes again)
```

**Say:**
> "A Promise starts in pending. Once it's fulfilled or rejected, it's 'settled' — permanently. You cannot un-reject a Promise or change a resolved value."

**[ACTION]** Show `new Promise((resolve, reject) => {...})`.

**Say:**
> "The function you pass to `new Promise` is called the executor. It runs immediately — synchronously. Inside, you call `resolve` with a value on success or `reject` with an error on failure. Only the first call matters — subsequent calls are silently ignored."

---

### SECTION 2 — .then() Chaining (8 min)

**[ACTION]** Show the `fetchUser` chain in Section 5.

**Say:**
> "Here's the big insight: `.then()` always returns a NEW Promise. What you return from a `.then()` handler becomes the resolution value of that new Promise."

**[ACTION]** Draw on board:

```
fetchUser(1)              → Promise<user>
  .then(user => dept)     → Promise<dept>   (returned a Promise — unwrapped)
  .then(dept => upperName)→ Promise<string> (returned a plain value — wrapped)
  .then(upperName => ...) → Promise<undefined>
```

**Say:**
> "If you return a plain value, it's automatically wrapped in `Promise.resolve(value)`. If you return a Promise, the chain waits for it to settle. This is why the chain stays flat — no nesting."

**[ASK]** "What would happen if I forgot to `return` from a `.then()` handler?"
> *(The next `.then()` would receive `undefined`. You'd lose the data. Missing `return` is a classic Promise bug.)*

**⚠️ WATCH OUT:**
> "Always return from `.then()` handlers. Forgetting `return` is the #1 Promise mistake."

---

### SECTION 3 — Error Handling (7 min)

**[ACTION]** Show `.catch()` and `.finally()` in Section 6.

**Say:**
> "`.catch(fn)` is just shorthand for `.then(undefined, fn)`. It catches rejections from ANY previous step in the chain. You only need ONE `.catch()` at the end — it handles errors from all previous `.then()` calls."

**[ACTION]** Show the recovery pattern: `.catch()` returning a value, followed by `.then()`.

**Say:**
> "If a `.catch()` handler returns a value without throwing, the chain continues as fulfilled with that value. This lets you provide fallbacks — recover from errors and keep going."

**[ACTION]** Show `.finally()`.

**Say:**
> "`.finally()` is for cleanup — hiding a loading spinner, closing a database connection. It doesn't receive the value — it runs regardless of whether the promise fulfilled or rejected."

---

### SECTION 4 — Promise Combinators (10 min)

**[ACTION]** Show `Promise.all` (Section 9).

**[ACTION]** Draw on board:

```
Promise.all([A, B, C])
  → Resolves when ALL fulfill → [valueA, valueB, valueC]
  → Rejects immediately if ANY reject (fail fast)

Promise.race([A, B, C])
  → Resolves/rejects with the FIRST to settle

Promise.allSettled([A, B, C])
  → Always resolves when ALL settle (never rejects)
  → [{ status, value | reason }, ...]

Promise.any([A, B, C])
  → Resolves with FIRST to fulfill
  → Rejects only if ALL reject (AggregateError)
```

**[ASK]** "I'm loading three independent panels for a dashboard. Which combinator do I use?"
> *(`Promise.all` — I need all three, and if one fails, I want to show an error.)*

**[ASK]** "I have three CDN mirrors. I want the fastest one. Which combinator?"
> *(`Promise.race` — first one to respond wins.)*

**[ASK]** "I'm loading 50 user avatars. Some might 404. I still want to show the ones that succeed. Which?"
> *(`Promise.allSettled` — get all results including failures, handle each individually.)*

→ **TRANSITION:** "Promises are great but `.then()` chains can still get messy for complex flows. `async/await` gives us the best of both worlds."

---

## PART 2C — async/await, Fetch, JSON (30 min)
### File: `03-async-await-and-fetch.js`

---

### [ACTION] Open `03-async-await-and-fetch.js`.

---

### SECTION 1 — async/await Basics (8 min)

**Say:**
> "async/await is syntactic sugar — it's not a different system. It's a cleaner way to write Promise-based code. Under the hood, async functions return Promises, and await unwraps them."

**[ACTION]** Show `async function getValue()` — point out it returns `Promise.resolve(42)` automatically.

**[ACTION]** Show the `loadUserData` version vs the Promise chain version side by side.

**Say:**
> "Same operations, same Promises. But the async/await version reads like synchronous code — top to bottom, no callbacks, no `.then()` nesting. This is the main win."

**[ACTION]** Show `try/catch` in `loadUserSafe`.

**Say:**
> "Error handling is now standard `try/catch` — the same pattern you use for synchronous errors. One catch block at the end handles anything that throws anywhere inside."

**⚠️ WATCH OUT:**
> "If you await a Promise that rejects and you don't have a try/catch, you get an UnhandledPromiseRejection. In Node.js this crashes the process in newer versions. Always handle errors."

---

### SECTION 2 — Sequential vs Parallel (5 min)

**[ACTION]** Show `sequential()` and `parallel()` side by side.

**Say:**
> "This is a critical performance trap. If you `await` three things in a row, each one waits for the previous to complete. 150 + 100 + 50 = 300ms total."

**[ACTION]** Show `Promise.all([...])` version — 150ms total.

**Say:**
> "With `Promise.all`, all three start simultaneously. You wait for the longest one — 150ms. This is nearly twice as fast. Always ask: do these operations depend on each other? If not, run them in parallel with `Promise.all`."

**[ACTION]** Run both versions and compare the logged times.

---

### SECTION 3 — Fetch API (12 min)

**[ACTION]** Show `getPost(1)` — the basic GET request.

**⚠️ WATCH OUT — THE BIGGEST FETCH GOTCHA:**
> "This is the most important thing I'll say about fetch: a 404 or 500 response does NOT reject the Promise. Fetch only rejects for network errors — no internet, DNS failure, request blocked. If the server responds — even with an error — fetch considers it a success."

**[ACTION]** Point to `if (!response.ok) throw new Error(...)`.

**Say:**
> "You MUST check `response.ok` (or `response.status`) and throw manually. Every fetch call you write should have this check. Every single one."

**[ASK]** "What does `response.ok` mean exactly?"
> *(It's `true` when status is 200-299. `false` for anything else.)*

**[ACTION]** Show `response.json()` — emphasize it's also a Promise.

**Say:**
> "Two awaits for a basic fetch: first for the response object, then for the body parsing. `response.json()` reads the body stream and parses it — that's async."

**[ACTION]** Show POST request in Section 6c.

**Say:**
> "For POST/PUT/PATCH, you set `method`, add `Content-Type: application/json` header, and pass `JSON.stringify(body)` as the body. The API won't know what to do with your data unless you tell it the format."

**[ACTION]** Show the `ApiClient` class briefly.

**Say:**
> "This is a pattern you'd write once at the start of a project — a centralized place for all HTTP operations. Keeps the `Content-Type` header, error checking, and JSON parsing in one spot instead of scattered everywhere."

---

### SECTION 4 — Axios Comparison (3 min)

**[ACTION]** Point to the commented Axios section and the comparison table.

**Say:**
> "Axios solves the two biggest fetch annoyances: it parses JSON automatically and throws on 4xx/5xx responses. In real projects you'll see both. Fetch is built-in; Axios gives you more features. For simple projects, fetch is fine. For complex apps with interceptors, retries, or upload tracking, Axios is worth the install."

---

### SECTION 5 — JSON (5 min)

**[ACTION]** Show `JSON.parse` and `JSON.stringify` examples.

**Say:**
> "Every API call involves JSON. It's the lingua franca of web APIs. Two operations: `parse` turns a string into a JS object; `stringify` turns a JS object into a string."

**[ACTION]** Show the list of things `JSON.stringify` drops: functions, undefined, Symbols, NaN → null, Infinity → null.

**⚠️ WATCH OUT:**
> "Date objects stringify to ISO strings, but when you parse them back, they come back as strings — not Date objects. If you need to round-trip Dates through JSON, use a reviver function in `JSON.parse` or serialize them intentionally."

**[ACTION]** Show the circular reference error.

**Say:**
> "Circular references throw. If you have an object that references itself, you can't JSON.stringify it without a custom replacer or a library like `flatted`."

---

## Wrap-Up Q&A (5 min)

**[ASK]** "You call `fetch('/api/data')` and the server returns a 500 error. What does your code see?"
> *(The Promise still resolves. `response.ok` is `false`. You must check it and throw manually.)*

**[ASK]** "What's the difference between a microtask and a macrotask?"
> *(Microtasks — Promise callbacks — drain before the next macrotask — setTimeout/setInterval — runs. Microtasks always win.)*

**[ASK]** "I have three API calls that don't depend on each other. How do I run them efficiently?"
> *(`Promise.all([call1, call2, call3])` — all three start in parallel, you await all three.)*

**[ASK]** "What does `await` actually do under the hood?"
> *(It pauses the async function and schedules the rest as a Promise `.then()` callback. The call stack is freed — other code can run. When the awaited Promise settles, execution resumes.)*

**[ASK]** "When would you use `Promise.allSettled` instead of `Promise.all`?"
> *(When partial failure is acceptable — you want to process whatever succeeded and handle whatever failed individually, without the whole operation failing.)*

---

## Board Summary to Leave Up

```
ASYNC PRIMITIVES

setTimeout/setInterval → macrotask queue
Promise .then/.catch   → microtask queue

PROMISE STATES
  pending → fulfilled ✅
  pending → rejected  ❌
  settled = immutable

COMBINATORS
  .all       → all succeed, or fail fast
  .race      → first to settle
  .allSettled→ wait for all, never throws
  .any       → first to succeed

ASYNC/AWAIT
  async fn   → always returns Promise
  await      → pause until Promise settles
  try/catch  → handle rejections like sync errors

FETCH GOTCHA
  fetch() only rejects on network failure
  Always check response.ok!
```

---

## Take-Home Exercise

1. **Event loop prediction:** Without running the code, write down the expected output in order:
   ```js
   console.log("A");
   setTimeout(() => console.log("B"), 0);
   Promise.resolve().then(() => console.log("C")).then(() => console.log("D"));
   console.log("E");
   ```

2. **Promise chain:** Write a function `loadUserProfile(userId)` that:
   - Fetches `https://jsonplaceholder.typicode.com/users/${userId}`
   - Then fetches that user's posts: `/posts?userId=${userId}`
   - Returns `{ user, postCount: posts.length, latestPost: posts[0] }`
   - Uses `.then()` chaining (no async/await)

3. **Rewrite with async/await:** Take your answer from #2 and rewrite it using async/await with try/catch.

4. **Parallel loading:** Modify your function from #3 to load the user's posts AND their todos (`/todos?userId=${userId}`) in parallel using `Promise.all`.

5. **Error handling:** Add a timeout to your fetch calls using `AbortController`. If the request takes more than 3 seconds, it should throw `"Request timed out"`.
