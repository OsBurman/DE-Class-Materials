# Week 3 - Day 14 (Thursday): ES6+, OOP in JS & Async JavaScript
## Part 2: Async JavaScript — Slide Descriptions

**Section Goal:** Explain how JavaScript handles asynchronous operations, move from callbacks through Promises to async/await, and build complete HTTP integrations with the Fetch API and Axios.

**Part 2 Learning Objectives:**
- Explain how the event loop enables non-blocking code in a single-threaded language
- Describe the difference between synchronous and asynchronous execution
- Use callbacks and recognize the callback hell anti-pattern
- Create and chain Promises with `.then`, `.catch`, `.finally`
- Combine multiple Promises with `Promise.all`, `Promise.race`, `Promise.allSettled`, `Promise.any`
- Write clean asynchronous code with `async`/`await`
- Handle errors in async functions with `try`/`catch`
- Make HTTP requests with the Fetch API and Axios
- Parse and serialize JSON
- Build a complete external API integration

---

## Slide 1: Title Slide

**Title:** ES6+, OOP in JavaScript & Async Programming
**Subtitle:** Part 2: The Event Loop, Promises, and the Fetch API
**Day:** Week 3 - Day 14 (Thursday)
**Notes for instructor:** Connect to Day 13 events — those event listeners were already asynchronous. Today we formalize the model and add HTTP.

---

## Slide 2: The Event Loop — JavaScript's Concurrency Model

**Title:** The Event Loop — How JavaScript Does Many Things at Once

**Content:**
JavaScript is **single-threaded** — it can only execute one operation at a time. Yet it handles timers, HTTP requests, user events, and file I/O without freezing the page. How?

The answer is the **event loop** — a coordination mechanism between JavaScript's single thread and the browser's (or Node.js's) native APIs.

**The core model:**
```
Your JavaScript code runs on the CALL STACK — one operation at a time.

Long-running work (HTTP requests, timers, file I/O) is handed to BROWSER/NODE APIS
to run in the background (NOT on your JS thread).

When that background work completes, a CALLBACK is placed in a queue.

The EVENT LOOP monitors the call stack. When the stack is EMPTY, it takes
the next callback from the queue and pushes it onto the stack.
```

**A timer example:**
```javascript
console.log("1 — synchronous");

setTimeout(() => {
  console.log("3 — timer callback (after call stack is empty)");
}, 0); // 0ms delay — but STILL goes through the event loop

console.log("2 — synchronous");

// Output:
// 1 — synchronous
// 2 — synchronous
// 3 — timer callback (after call stack is empty)
```

Even with a 0ms delay, `setTimeout` does not run immediately. The callback is placed in the task queue, and only runs after the current call stack fully empties.

**Why this matters for application design:**
- Long synchronous work (like sorting 1 million items) blocks the entire UI — no clicks, no renders, no anything
- Async operations (HTTP, timers) keep the event loop free
- The golden rule: **never block the call stack**

---

## Slide 3: The Full Picture — Call Stack, Web APIs, and Queues

**Title:** Call Stack, Web APIs, Task Queue, and Microtask Queue

**Content:**
The complete picture has more pieces than just "stack" and "queue."

**Components:**
```
┌─────────────────────────────────────────────────────────────┐
│  CALL STACK                                                 │
│  (JavaScript thread — one frame at a time)                  │
│  [ main() → fetchUser() → ... ]                             │
└─────────────────────┬───────────────────────────────────────┘
                      │ browser/Node hands off
                      ▼
┌─────────────────────────────────────────────────────────────┐
│  WEB APIs / NODE APIs                                       │
│  (run natively, outside JS thread)                          │
│  [ setTimeout | fetch | DOM events | fs.readFile | ... ]    │
└──────────────┬──────────────────────┬───────────────────────┘
               │ on complete          │
               ▼                      ▼
┌──────────────────────┐  ┌───────────────────────────────────┐
│  MICROTASK QUEUE     │  │  TASK QUEUE (Macrotask Queue)     │
│  (higher priority)   │  │  (lower priority)                 │
│  [Promise callbacks] │  │  [setTimeout, setInterval,       │
│  [queueMicrotask]    │  │   I/O callbacks, DOM events]      │
└──────────┬───────────┘  └─────────────┬─────────────────────┘
           │                            │
           └─────── EVENT LOOP ─────────┘
                  (drain microtasks first, then one macrotask)
```

**The critical priority rule:**
The event loop drains the **entire microtask queue** before processing the next macrotask. This is why Promise callbacks run before `setTimeout` callbacks, even if both are queued at the "same time."

**Demonstration:**
```javascript
console.log("sync 1");

setTimeout(() => console.log("timeout — macrotask"), 0);

Promise.resolve().then(() => console.log("promise — microtask"));

console.log("sync 2");

// Output:
// sync 1
// sync 2
// promise — microtask    ← microtask queue runs before macrotask
// timeout — macrotask
```

**Practical implication for debugging:** When your async code runs in an unexpected order, knowing the microtask/macrotask priority is the key to understanding why.

---

## Slide 4: Synchronous vs Asynchronous Code

**Title:** Synchronous vs Asynchronous — Two Execution Models

**Content:**
**Synchronous code** executes line by line. Each line waits for the previous one to finish before running. The call stack grows and shrinks predictably.

```javascript
// Synchronous — each line blocks until complete
function loadDataSync() {
  const data = readFileSync("data.json"); // BLOCKS — no other code can run
  const parsed = JSON.parse(data);
  return parsed;
}
```

**Asynchronous code** initiates an operation and moves on. The result is handled via a callback, Promise, or `await` when it's ready.

```javascript
// Asynchronous — initiates, then continues; result handled later
function loadDataAsync() {
  fetch("/api/data")          // initiates HTTP request, returns immediately
    .then(res => res.json())  // scheduled when response arrives
    .then(data => render(data));
  
  console.log("This runs BEFORE the fetch completes");
}
```

**The blocking problem — why sync I/O is catastrophic in browsers:**
```javascript
// Imagine this is synchronous network (it's NOT — just for illustration)
function badSync() {
  const user = syncFetch("/api/user");   // browser freezes here for 300ms
  const posts = syncFetch("/api/posts"); // browser freezes here for 200ms
  // Total: 500ms of completely frozen UI — no scrolling, no clicking, no animation
}

// Async version — browser stays responsive throughout
async function good() {
  const user  = await fetch("/api/user");   // browser stays live
  const posts = await fetch("/api/posts");  // browser stays live
}
```

**Three async patterns (in chronological order of adoption):**
1. **Callbacks** — original pattern (Node.js, DOM events, jQuery Ajax)
2. **Promises** — standardized in ES6 (2015), structured chaining
3. **async/await** — ES2017, synchronous-looking syntax over Promises

Each is more ergonomic than the previous, but they all use the same event loop under the hood.

---

## Slide 5: Callbacks — The Original Async Pattern

**Title:** Callbacks — Passing Functions as Continuations

**Content:**
A **callback** is a function passed as an argument to another function, to be called when an asynchronous operation completes. This was JavaScript's original async mechanism.

**The Node.js error-first callback convention:**
```javascript
// Convention: callback(error, result)
// If error occurred: callback(err, null)
// If success:        callback(null, result)

const fs = require("fs");

fs.readFile("./data.txt", "utf8", function(err, data) {
  if (err) {
    console.error("File read failed:", err.message);
    return; // stop here — don't try to use data
  }
  console.log("File contents:", data);
});

console.log("This runs BEFORE the file is read");
```

**DOM event callbacks (you already know these from Day 13):**
```javascript
// addEventListener IS a callback pattern
document.querySelector("#btn").addEventListener("click", function(event) {
  // This function is the callback — called when the click event fires
  console.log("Clicked:", event.target);
});
```

**Timer callbacks:**
```javascript
setTimeout(function() {
  console.log("Runs after 2 seconds");
}, 2000);

setInterval(function() {
  console.log("Runs every second");
}, 1000);
```

**What callbacks do well:**
- Simple, universal — works everywhere
- Direct — nothing to import or configure
- Familiar model for DOM events (still the right tool there)

**The problem:** Multiple dependent async operations create deeply nested callbacks. We'll see that next.

---

## Slide 6: Callback Hell — Why Promises Were Invented

**Title:** Callback Hell — The Pyramid of Doom

**Content:**
When one async operation depends on the result of another, callbacks nest — and the code structure spirals right.

**The pyramid of doom:**
```javascript
// Task: get a user → get their posts → get comments on first post → save to file
getUser(userId, function(userErr, user) {
  if (userErr) { handleError(userErr); return; }

  getPosts(user.id, function(postsErr, posts) {
    if (postsErr) { handleError(postsErr); return; }

    getComments(posts[0].id, function(commentsErr, comments) {
      if (commentsErr) { handleError(commentsErr); return; }

      saveToFile(comments, function(saveErr) {
        if (saveErr) { handleError(saveErr); return; }

        console.log("Done! Saved", comments.length, "comments");
        // What if we needed to do one MORE async thing here?
        // That's another level of nesting...
      });
    });
  });
});
```

**Problems with deeply nested callbacks:**
1. **Readability** — the pyramid shape obscures the logical flow
2. **Error handling** — must check errors at every level; easy to miss one
3. **No shared error handler** — can't catch all errors in one place
4. **Inversion of control** — you're trusting the function you pass your callback to, to call it correctly (once, with the right arguments, handling errors)
5. **Stack traces** — async callbacks don't show the original call site in errors

**"Callback hell" led directly to Promises:** The JavaScript community felt this pain for years before ES6. Promises were adopted as the standard solution in 2015. The `async`/`await` syntax (2017) is built on top of Promises and makes async code look synchronous — solving the readability problem completely.

---

## Slide 7: Promises — States and Creation

**Title:** Promises — A Value That Will Exist in the Future

**Content:**
A `Promise` is an object representing the eventual completion (or failure) of an asynchronous operation. It's a contract: "I promise to give you a value later."

**The three states:**
```
Promise States:
  ┌─────────┐
  │ PENDING │  ── operation in progress ──►  ┌──────────────┐
  └─────────┘                                │  FULFILLED   │ (resolved with a value)
       │                                     └──────────────┘
       └───────────────────────────────────► ┌──────────────┐
                                             │   REJECTED   │ (rejected with a reason/error)
                                             └──────────────┘
Once settled (fulfilled or rejected), a Promise CANNOT change state.
```

**Creating a Promise:**
```javascript
function delay(ms) {
  return new Promise((resolve, reject) => {
    // The executor function runs synchronously — immediately on construction
    if (typeof ms !== "number") {
      reject(new TypeError("ms must be a number")); // reject with an error
      return;
    }
    setTimeout(() => resolve(`Done after ${ms}ms`), ms); // resolve with a value
  });
}

const p = delay(1000);
console.log(p); // Promise { <pending> } — not yet settled
```

**Wrapping callback-based APIs with Promises:**
```javascript
function readFilePromise(path) {
  return new Promise((resolve, reject) => {
    fs.readFile(path, "utf8", (err, data) => {
      if (err) reject(err);   // error → rejected promise
      else     resolve(data); // success → fulfilled promise
    });
  });
}
```

**Pre-resolved / pre-rejected Promises:**
```javascript
Promise.resolve(42);          // already fulfilled with value 42
Promise.reject(new Error("Failed")); // already rejected
// Useful for: returning consistent Promise types from functions that might not be async
```

---

## Slide 8: Promise Chaining — .then, .catch, .finally

**Title:** Promise Chaining — Linear Async Flow

**Content:**
The power of Promises is chaining: `.then()` runs when a Promise fulfills, `.catch()` runs when any Promise in the chain rejects, and `.finally()` always runs.

**Basic chaining:**
```javascript
fetchUser(userId)
  .then(user => {
    console.log("Got user:", user.name);
    return fetchPosts(user.id);   // return a new Promise — chain continues
  })
  .then(posts => {
    console.log("Got posts:", posts.length);
    return renderPosts(posts);    // can return a non-Promise value too
  })
  .then(renderedCount => {
    console.log(`Rendered ${renderedCount} posts`);
  })
  .catch(error => {
    // Catches ANY rejection from any .then() above
    console.error("Something went wrong:", error.message);
    showErrorMessage(error.message);
  })
  .finally(() => {
    // Always runs — success OR failure
    hideLoadingSpinner();
  });
```

**The return value rule:**
What you `return` from a `.then()` handler determines what the next `.then()` receives:
- Return a non-Promise value → next `.then()` gets that value
- Return a Promise → next `.then()` waits for that Promise and gets its resolved value
- Throw an error → jumps to the nearest `.catch()`

**Flattening vs nesting — Promises flatten automatically:**
```javascript
// Nested (bad — callback hell but with Promises)
fetchUser(id).then(user => {
  fetchPosts(user.id).then(posts => { /* ... */ }); // nested Promise — .catch won't catch this!
});

// Chained (correct — errors propagate to .catch)
fetchUser(id)
  .then(user => fetchPosts(user.id)) // return the Promise!
  .then(posts => { /* ... */ })
  .catch(err => handleError(err));
```

**`.finally()` nuances:**
```javascript
.finally(() => {
  hideSpinner(); // runs on both success and failure
  // if you return a value from finally, it's IGNORED
  // if you throw from finally, the chain is rejected with that error
});
```

---

## Slide 9: Promise Combinators — all, race, allSettled, any

**Title:** Promise Combinators — Running Multiple Promises

**Content:**
Often you need to run multiple async operations together. Promise combinators handle the coordination.

**`Promise.all` — all must succeed:**
```javascript
// Runs all three requests concurrently (not sequentially!)
// Resolves when ALL three resolve; rejects immediately if ANY rejects
const [user, posts, comments] = await Promise.all([
  fetch("/api/user/1").then(r => r.json()),
  fetch("/api/posts?userId=1").then(r => r.json()),
  fetch("/api/comments?userId=1").then(r => r.json())
]);
// vs sequential: 300ms + 200ms + 150ms = 650ms
// Promise.all: max(300ms, 200ms, 150ms) = 300ms  ← 2x faster
```

**`Promise.allSettled` — get all results regardless:**
```javascript
// Never rejects — resolves with an array of result objects
const results = await Promise.allSettled([fetchA(), fetchB(), fetchC()]);

results.forEach(result => {
  if (result.status === "fulfilled") {
    console.log("Success:", result.value);
  } else {
    console.error("Failed:", result.reason.message);
  }
});
// Use when: you want all results even if some fail (e.g., batch API calls)
```

**`Promise.race` — first to settle wins:**
```javascript
// Resolves or rejects as soon as ANY Promise settles
const fastest = await Promise.race([
  fetch("https://api-us.example.com/data"),
  fetch("https://api-eu.example.com/data")
]);
// Use for: timeout pattern, geographic routing, performance comparisons
```

**Timeout pattern with `Promise.race`:**
```javascript
function withTimeout(promise, ms) {
  const timeout = new Promise((_, reject) =>
    setTimeout(() => reject(new Error(`Timed out after ${ms}ms`)), ms)
  );
  return Promise.race([promise, timeout]);
}

const data = await withTimeout(fetch("/api/slow-endpoint"), 5000);
```

**`Promise.any` (ES2021) — first to SUCCEED wins:**
```javascript
// Resolves with first fulfilled value; rejects only if ALL reject (AggregateError)
const firstResponse = await Promise.any([
  fetchFromCDN1(url),
  fetchFromCDN2(url),
  fetchFromCDN3(url)
]);
// Use for: redundant requests, fallback strategies
// Difference from race: ignores rejections unless ALL reject
```

---

## Slide 10: async/await — Synchronous-Looking Async Code

**Title:** async/await — Promises with Clean Syntax

**Content:**
`async`/`await` is ES2017 syntactic sugar over Promises. It doesn't replace Promises — it makes them easier to write and read. An `async` function always returns a Promise.

**Core syntax:**
```javascript
// Mark the function as async
async function fetchUserData(userId) {
  // await pauses execution of THIS function until the Promise resolves
  // It does NOT block the event loop or other code
  const response = await fetch(`/api/users/${userId}`);
  const user     = await response.json();
  return user;  // async functions always return a Promise — this resolves with user
}

// Call it like a regular function — it returns a Promise
fetchUserData(1).then(user => console.log(user));

// Or await it from another async function
async function main() {
  const user = await fetchUserData(1);
  console.log(user);
}
```

**Equivalent Promise chain comparison:**
```javascript
// Promise chain
function fetchUserData(userId) {
  return fetch(`/api/users/${userId}`)
    .then(response => response.json())
    .then(user => user);
}

// async/await — same behavior, linear reading order
async function fetchUserData(userId) {
  const response = await fetch(`/api/users/${userId}`);
  const user     = await response.json();
  return user;
}
```

**Sequential vs concurrent — important distinction:**
```javascript
// Sequential — user first, THEN posts — 500ms total
async function sequential(userId) {
  const user  = await fetchUser(userId);  // wait 300ms
  const posts = await fetchPosts(userId); // wait 200ms
  return { user, posts };
}

// Concurrent — both start at same time — 300ms total (max of the two)
async function concurrent(userId) {
  const [user, posts] = await Promise.all([
    fetchUser(userId),   // starts immediately
    fetchPosts(userId)   // starts immediately
  ]);
  return { user, posts };
}
```

**`await` only works inside `async` functions** (or at top level in ES modules). Calling `await` outside an `async` function is a SyntaxError.

---

## Slide 11: Error Handling with async/await

**Title:** Error Handling — try/catch with async/await

**Content:**
With `async`/`await`, you use standard `try`/`catch` for error handling — no more `.catch()` chains. Unhandled Promise rejections are a serious issue: in Node.js they terminate the process; in browsers they appear as uncaught errors.

**Basic try/catch:**
```javascript
async function fetchData(url) {
  try {
    const response = await fetch(url);

    // CRITICAL: fetch only rejects on network errors — not on 404/500!
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const data = await response.json();
    return data;

  } catch (error) {
    // Catches: network errors, JSON parse errors, and our thrown HTTP error
    if (error instanceof TypeError) {
      console.error("Network error (no internet?):", error.message);
    } else {
      console.error("API error:", error.message);
    }
    throw error; // re-throw so the caller knows it failed
  }
}
```

**Granular error handling — try/catch per operation:**
```javascript
async function loadDashboard(userId) {
  let user, posts;

  try {
    user = await fetchUser(userId);
  } catch (err) {
    showError("Could not load user profile");
    return; // bail out entirely if user fails
  }

  try {
    posts = await fetchPosts(userId);
  } catch (err) {
    posts = []; // posts are optional — show empty state instead of failing
    console.warn("Posts failed to load:", err.message);
  }

  renderDashboard(user, posts);
}
```

**`finally` in async/await:**
```javascript
async function submitForm(formData) {
  showSpinner();
  try {
    const result = await postToAPI("/submit", formData);
    showSuccess(result);
  } catch (error) {
    showError(error.message);
  } finally {
    hideSpinner(); // always runs — success or failure
  }
}
```

**async/await with Promise combinators:**
```javascript
async function loadAll(ids) {
  try {
    const results = await Promise.all(ids.map(id => fetchItem(id)));
    return results;
  } catch (error) {
    // If ANY Promise.all item rejects, caught here
    throw new Error(`Failed loading items: ${error.message}`);
  }
}
```

---

## Slide 12: The Fetch API — HTTP Requests from JavaScript

**Title:** The Fetch API — Making HTTP Requests

**Content:**
`fetch()` is the modern browser API for making HTTP requests. It returns a Promise. It replaced the older (and verbose) `XMLHttpRequest`.

**GET request:**
```javascript
// fetch returns a Promise that resolves to a Response object
const response = await fetch("https://jsonplaceholder.typicode.com/users");

// Response represents the HTTP response — status, headers, body
console.log(response.status);  // 200
console.log(response.ok);      // true (200-299)

// Body must be parsed explicitly — response.json() returns ANOTHER Promise
const users = await response.json();
console.log(users); // the parsed array
```

**POST request with body and headers:**
```javascript
const response = await fetch("https://api.example.com/users", {
  method:  "POST",
  headers: {
    "Content-Type":  "application/json",       // required for JSON body
    "Authorization": `Bearer ${accessToken}`,  // JWT auth header
    "Accept":        "application/json"
  },
  body: JSON.stringify({
    name:  "Alice",
    email: "alice@example.com"
  })
});

if (!response.ok) {
  throw new Error(`Failed to create user: ${response.status}`);
}

const newUser = await response.json();
console.log("Created:", newUser.id);
```

**PUT, PATCH, DELETE:**
```javascript
// PUT — replace entire resource
await fetch(`/api/users/${id}`, { method: "PUT",   body: JSON.stringify(updatedUser), headers });

// PATCH — update partial resource
await fetch(`/api/users/${id}`, { method: "PATCH", body: JSON.stringify({ email }), headers });

// DELETE
await fetch(`/api/users/${id}`, { method: "DELETE" });
```

**The most common mistake with fetch — forgetting to check `response.ok`:**
```javascript
// WRONG — fetch only rejects on network failure, NOT on 404 or 500
const data = await fetch("/api/resource").then(r => r.json()); // silently processes error response body

// CORRECT — always check response.ok
const response = await fetch("/api/resource");
if (!response.ok) throw new Error(`HTTP ${response.status}`);
const data = await response.json();
```

---

## Slide 13: Response Object — Reading the Server's Answer

**Title:** Working with the Response Object

**Content:**
The `Response` object returned by `fetch()` represents the entire HTTP response — status, headers, and body.

**Response properties:**
```javascript
const response = await fetch("/api/data");

// Status
response.status;      // 200, 404, 500, etc.
response.statusText;  // "OK", "Not Found", "Internal Server Error"
response.ok;          // true if status is 200–299

// Headers
response.headers.get("Content-Type");   // "application/json; charset=utf-8"
response.headers.get("X-Request-Id");   // custom headers
response.headers.has("Authorization");  // boolean

// URL
response.url;         // final URL after redirects
response.redirected;  // true if a redirect occurred
```

**Body reading methods (each returns a Promise, each can only be called ONCE):**
```javascript
await response.json();   // parse body as JSON → JavaScript object
await response.text();   // parse body as string
await response.blob();   // parse body as binary Blob (images, files)
await response.arrayBuffer(); // raw bytes
await response.formData();    // form data

// After calling any of these, response.bodyUsed === true — can't read again
```

**Handling different content types:**
```javascript
async function smartFetch(url) {
  const response = await fetch(url);

  if (!response.ok) throw new Error(`HTTP ${response.status}`);

  const contentType = response.headers.get("Content-Type") || "";

  if (contentType.includes("application/json")) {
    return await response.json();
  } else if (contentType.includes("text/")) {
    return await response.text();
  } else {
    return await response.blob(); // binary — image, PDF, etc.
  }
}
```

**Sending and receiving with JSON — full pattern:**
```javascript
const BASE_URL = "https://jsonplaceholder.typicode.com";

const jsonHeaders = {
  "Content-Type": "application/json",
  "Accept":       "application/json"
};

async function apiPost(endpoint, body) {
  const response = await fetch(`${BASE_URL}${endpoint}`, {
    method:  "POST",
    headers: jsonHeaders,
    body:    JSON.stringify(body)
  });
  if (!response.ok) {
    const errBody = await response.text();
    throw new Error(`POST ${endpoint} failed (${response.status}): ${errBody}`);
  }
  return response.json();
}
```

---

## Slide 14: Axios — A Popular Fetch Alternative

**Title:** Axios — Simplified HTTP with Extra Features

**Content:**
Axios is a popular third-party library for HTTP requests. It wraps `XMLHttpRequest` (browser) or Node's `http` (Node.js) and provides a cleaner API with more built-in features than native `fetch`.

**Install:** `npm install axios`

**Basic usage:**
```javascript
import axios from "axios";

// GET — Axios automatically parses JSON
const response = await axios.get("https://jsonplaceholder.typicode.com/users");
console.log(response.data);   // already parsed — no .json() needed
console.log(response.status); // 200
console.log(response.headers);

// POST
const newUser = await axios.post("/api/users", {
  name:  "Alice",
  email: "alice@example.com"
  // Axios automatically sets Content-Type: application/json
});
console.log(newUser.data); // response body (already parsed)
```

**Axios vs Fetch — key differences:**
| Feature | Fetch | Axios |
|---|---|---|
| JSON body | `JSON.stringify(body)` manually | Automatic |
| Response JSON | `await r.json()` | `response.data` directly |
| Error on non-2xx | No — must check `response.ok` | **Yes — throws automatically** |
| Request cancel | `AbortController` | Built-in cancel token / signal |
| Browser support | Modern browsers | IE11+ via polyfill |
| Node.js | Node 18+ native | Yes, always |
| Interceptors | No (manual middleware) | Built-in |

**Axios instance — reusable configuration:**
```javascript
const api = axios.create({
  baseURL: "https://api.example.com",
  timeout: 10000, // 10 second timeout
  headers: {
    "Authorization": `Bearer ${getToken()}`,
    "Accept":        "application/json"
  }
});

// All requests use the base URL and headers automatically
const user  = await api.get("/users/1");
const posts = await api.get("/posts?userId=1");
await api.post("/users", newUserData);
await api.delete(`/users/${id}`);
```

**Error handling with Axios:**
```javascript
try {
  const response = await api.get("/users/999");
} catch (error) {
  if (error.response) {
    // Server responded with a non-2xx status
    console.error("Status:", error.response.status);
    console.error("Body:",   error.response.data);
  } else if (error.request) {
    // Request was made but no response (network error, timeout)
    console.error("No response received:", error.request);
  } else {
    // Error setting up the request
    console.error("Request error:", error.message);
  }
}
```

---

## Slide 15: JSON — Serialization and Deserialization

**Title:** JSON — The Language of APIs

**Content:**
JSON (JavaScript Object Notation) is the data exchange format used by virtually all REST APIs. It's text — a string — that represents structured data.

**`JSON.stringify` — JavaScript → JSON string:**
```javascript
const user = {
  name:      "Alice",
  age:       30,
  active:    true,
  scores:    [95, 87, 92],
  address:   { city: "NYC", zip: "10001" },
  createdAt: new Date()
};

JSON.stringify(user);
// '{"name":"Alice","age":30,"active":true,"scores":[95,87,92],"address":{"city":"NYC","zip":"10001"},"createdAt":"2024-01-15T..."}'

// Pretty print (for logging, debugging)
JSON.stringify(user, null, 2); // null = no replacer, 2 = indent spaces
```

**`JSON.parse` — JSON string → JavaScript:**
```javascript
const json   = '{"name":"Alice","age":30,"scores":[95,87,92]}';
const parsed = JSON.parse(json);

console.log(parsed.name);    // "Alice"
console.log(parsed.scores);  // [95, 87, 92]
console.log(typeof parsed);  // "object"

// Always wrap in try/catch — invalid JSON throws SyntaxError
try {
  const data = JSON.parse(responseText);
} catch (err) {
  console.error("Invalid JSON:", err.message);
}
```

**What JSON CANNOT represent (stripped by `JSON.stringify`):**
```javascript
const obj = {
  fn:        () => "hello",  // functions → stripped (undefined in output)
  symbol:    Symbol("id"),   // Symbols → stripped
  undef:     undefined,      // undefined values → stripped
  date:      new Date(),     // Date → converted to ISO string (but loses Date type on parse!)
  circular:  obj             // circular references → throws TypeError
};

JSON.stringify(obj);
// '{"date":"2024-01-15T..."}' — only date survived, as a string
```

**Deep cloning with JSON (works for simple data, not functions/Dates):**
```javascript
// Simple deep clone — loses Date type, functions, undefined, Symbols
const deepCopy = JSON.parse(JSON.stringify(original));

// Modern alternative — preserves more types
const deepCopy = structuredClone(original); // newer API, browser and Node 17+
```

**Working with API dates:**
```javascript
// Dates come as ISO strings from APIs
const response = await fetch("/api/user/1");
const user     = await response.json();
console.log(user.createdAt); // "2024-01-15T10:30:00.000Z" — string, not Date

// Convert to Date when needed
const createdAt = new Date(user.createdAt);
console.log(createdAt.toLocaleDateString()); // "1/15/2024"
```

---

## Slide 16: Building a Complete API Integration

**Title:** Practical Example — Complete External API Integration

**Content:**
Bringing everything together: async/await, error handling, Fetch, JSON, and DOM manipulation from Day 13.

**Scenario:** Load and display users from JSONPlaceholder (a free test API).

**HTML:**
```html
<input id="user-id" type="number" min="1" max="10" value="1" />
<button id="load-btn">Load User</button>
<div id="user-card" class="hidden"></div>
<div id="post-list"></div>
<p id="error-msg" class="hidden error"></p>
<div id="spinner" class="hidden">Loading...</div>
```

**JavaScript:**
```javascript
const BASE = "https://jsonplaceholder.typicode.com";

const loadBtn  = document.querySelector("#load-btn");
const userCard = document.querySelector("#user-card");
const postList = document.querySelector("#post-list");
const errorMsg = document.querySelector("#error-msg");
const spinner  = document.querySelector("#spinner");

async function fetchJSON(url) {
  const response = await fetch(url);
  if (!response.ok) throw new Error(`HTTP ${response.status} — ${url}`);
  return response.json();
}

async function loadUser(userId) {
  spinner.classList.remove("hidden");
  userCard.classList.add("hidden");
  postList.textContent = "";
  errorMsg.classList.add("hidden");

  try {
    // Concurrent fetch — user and posts at the same time
    const [user, posts] = await Promise.all([
      fetchJSON(`${BASE}/users/${userId}`),
      fetchJSON(`${BASE}/posts?userId=${userId}`)
    ]);

    // Render user card (textContent for user data — safer)
    userCard.textContent = ""; // clear
    const h2   = document.createElement("h2");
    h2.textContent = user.name;
    const email = document.createElement("p");
    email.textContent = `Email: ${user.email}`;
    const city  = document.createElement("p");
    city.textContent = `City: ${user.address.city}`;
    userCard.append(h2, email, city);
    userCard.classList.remove("hidden");

    // Render posts
    const fragment = document.createDocumentFragment();
    posts.forEach(post => {
      const article = document.createElement("article");
      const title   = document.createElement("h3");
      const body    = document.createElement("p");
      title.textContent = post.title;
      body.textContent  = post.body;
      article.append(title, body);
      fragment.appendChild(article);
    });
    postList.appendChild(fragment);

  } catch (error) {
    errorMsg.textContent = `Failed to load data: ${error.message}`;
    errorMsg.classList.remove("hidden");
  } finally {
    spinner.classList.add("hidden");
  }
}

loadBtn.addEventListener("click", () => {
  const userId = parseInt(document.querySelector("#user-id").value);
  if (userId >= 1 && userId <= 10) loadUser(userId);
});

loadUser(1); // load on page init
```

This example combines: DOM selectors (Day 13 Part 1), event listeners (Day 13 Part 2), async/await, Promise.all, error handling, Fetch, JSON, and DocumentFragment.

---

## Slide 17: Async Patterns and Best Practices

**Title:** Async Patterns and Common Mistakes

**Content:**

**✅ Always check `response.ok` with Fetch:**
```javascript
const res = await fetch(url);
if (!res.ok) throw new Error(`HTTP ${res.status}`); // Don't skip this!
const data = await res.json();
```

**✅ Concurrent > Sequential when operations are independent:**
```javascript
// Sequential — slow (operations wait for each other unnecessarily)
const user  = await fetchUser(id);
const posts = await fetchPosts(id); // doesn't use user — why wait?

// Concurrent — fast
const [user, posts] = await Promise.all([fetchUser(id), fetchPosts(id)]);
```

**✅ Always handle errors — unhandled rejections are a serious bug:**
```javascript
// WRONG — if the Promise rejects, it's silently swallowed or crashes Node
fetchData().then(processData); // no .catch!

// CORRECT
fetchData().then(processData).catch(handleError);
// or
try { const d = await fetchData(); processData(d); } catch (e) { handleError(e); }
```

**⚠️ Don't mix await and .then unnecessarily — pick one style:**
```javascript
// Confusing — mixed styles
const data = await fetch(url).then(r => r.json()).then(d => d.users);

// Clear — consistent async/await
const response = await fetch(url);
const body     = await response.json();
const data     = body.users;
```

**⚠️ await in loops — sequential, not concurrent:**
```javascript
// WRONG — awaits each one before starting the next (sequential)
for (const id of ids) {
  const item = await fetchItem(id); // blocks loop
}

// CORRECT — start all, then collect
const items = await Promise.all(ids.map(id => fetchItem(id)));
```

**✅ Use loading/error/empty states in UI:**
```javascript
async function load() {
  setState({ loading: true, error: null, data: null });
  try {
    const data = await fetchData();
    setState({ loading: false, data, error: null });
  } catch (err) {
    setState({ loading: false, data: null, error: err.message });
  }
}
```
This pattern is exactly what React's `useState` + `useEffect` formalizes — you'll see it again next week.

---

## Slide 18: Part 2 Summary — Day 14 Complete

**Title:** Day 14 Complete — ES6+, OOP, and Async

**Content:**

**Part 2 Summary:**
- **Event loop** — JavaScript is single-threaded; long work is handed to browser APIs; callbacks return via task/microtask queues when the stack is empty
- **Microtasks** (Promises) run before **macrotasks** (setTimeout) — explains execution order
- **Callbacks** — original async pattern; works but leads to pyramid of doom for chained operations
- **Promises** — three states (pending → fulfilled/rejected); `.then()` chains; `.catch()` for error handling; `.finally()` always runs
- **Promise combinators**: `all` (concurrent, all or nothing), `allSettled` (all results), `race` (first to settle), `any` (first to succeed)
- **async/await** — synchronous-looking syntax over Promises; `await` only in `async` functions; `try/catch` for error handling
- **Fetch API** — returns a Promise resolving to a `Response` object; must check `response.ok`; body parsed with `.json()`, `.text()`, `.blob()`
- **Axios** — auto JSON, auto error on non-2xx, axios instances with base URL; structured error types
- **JSON** — `stringify` (JS → string), `parse` (string → JS); functions/Symbols/undefined are stripped

**Day 14 Learning Objectives — Achieved:**
1. ✅ Implement OOP patterns with constructor functions and ES6 classes
2. ✅ Use classes and understand prototypal inheritance
3. ✅ Apply ES6+ features: spread/rest, destructuring, modules
4. ✅ Work with Map and Set data structures
5. ✅ Understand the event loop and asynchronous execution model
6. ✅ Implement async operations using Promises
7. ✅ Use async/await for cleaner async code
8. ✅ Make HTTP requests using Fetch and Axios
9. ✅ Work with JSON data from APIs
10. ✅ Handle errors in asynchronous code

**Coming tomorrow — Day 15: TypeScript:**
Today's JavaScript classes become type-safe. Type annotations, interfaces, generics, utility types, decorators, and `tsconfig.json`. TypeScript is how Angular is written and how React teams build large apps.
