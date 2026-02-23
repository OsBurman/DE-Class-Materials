# Week 3 - Day 14 (Thursday): ES6+, OOP in JS & Async JavaScript
## Part 2 — Lecture Script (60 Minutes)

---

**[00:00–02:00] Opening — The Async Problem**

Welcome back. Part 1 was about how to structure code — classes, inheritance, modules, syntax. Part 2 is about how code behaves over TIME.

Here's the situation: JavaScript is a single-threaded language. It runs one operation at a time, on one call stack. But web applications need to do things that take time — fetch data from a server, read a file, wait for a timer to fire. If JavaScript blocked and waited for each of those, every fetch request would freeze the entire page. No scrolling, no clicking, no animation. Nothing, until the data arrived.

JavaScript's solution is the event loop. It's an elegant coordination mechanism that lets one thread handle many things without blocking. And once you understand it, async code stops being magic and starts making complete sense.

We're going to go from the foundational event loop model, through callbacks, through Promises, all the way to async/await and the Fetch API. By the end of this part, you'll be making real HTTP requests to real APIs and handling the responses correctly.

Let's go.

---

**[02:00–08:00] The Event Loop — How JavaScript Does Many Things**

Single-threaded. One call stack. Yet your page responds to clicks, runs animations, fetches data, all at the same time. How?

The answer has three parts: the call stack, the browser's native APIs, and a queue.

Your JavaScript code runs on the **call stack**. Each function call pushes a frame onto the stack. When the function returns, the frame is popped off. The stack handles one thing at a time.

When you do something that takes time — `fetch()`, `setTimeout()`, DOM event listeners — JavaScript hands that work OFF to the browser's native APIs. These APIs run in C++ threads outside your JavaScript thread. They do the actual work: making the network request, waiting for the timer, monitoring for clicks.

When the native API finishes — the data arrived, the timer fired, the button was clicked — it places a **callback** into a queue.

The **event loop** has one job: watch the call stack. When the call stack is empty, pick the next callback from the queue and push it onto the stack.

This is why event-driven and async code in JavaScript never truly "runs at the same time" — the callbacks run sequentially, one at a time, but they're interleaved between other operations so the program stays responsive.

Let me show you the most clarifying example:

```javascript
console.log("1");
setTimeout(() => console.log("3"), 0); // 0ms delay
console.log("2");
```

What order do you expect? If you said 1, 2, 3 — you're right. Even with a 0 millisecond delay, the `setTimeout` callback goes through the event loop. It doesn't jump the queue. The call stack first completes all synchronous code — logs 1, logs 2. Then, when the stack is empty, the event loop picks up the setTimeout callback and logs 3.

This means: no setTimeout callback will EVER run while synchronous code is running, even if the timer already expired.

---

**[08:00–14:00] Microtasks vs Macrotasks — Execution Order**

There are actually two queues, not one, and they have different priorities.

The **task queue** (also called macrotask queue) holds: setTimeout callbacks, setInterval callbacks, I/O callbacks, DOM event callbacks.

The **microtask queue** holds: Promise resolution callbacks, and anything queued with `queueMicrotask()`.

The event loop priority rule: after each macrotask, **drain the entire microtask queue** before processing the next macrotask. Microtasks run before the next macrotask, always.

Here's what this looks like in practice:

```javascript
console.log("sync 1");
setTimeout(() => console.log("timeout"), 0);      // macrotask
Promise.resolve().then(() => console.log("promise")); // microtask
console.log("sync 2");
```

Output: `sync 1`, `sync 2`, `promise`, `timeout`.

Sync code runs first — logs 1 and 2. Stack empties. Event loop checks: is there anything in the microtask queue? Yes — the Promise callback. Runs it: logs "promise". Microtask queue now empty. Now check the macrotask queue: setTimeout callback. Runs it: logs "timeout."

Why does this matter practically? When you `await` something in async code, the continuation of your function is scheduled as a microtask. When a DOM event fires, the handler runs as a macrotask. If you're wondering why your Promise resolution handler runs before a setTimeout you set up at the same time, this is the answer.

---

**[14:00–18:00] Synchronous vs Asynchronous — The Blocking Problem**

Before we go into the async patterns, let me make sure the "why" is clear.

Synchronous code runs from top to bottom, line by line, each line waiting for the previous to complete. That's fine for most logic. But it's catastrophic for I/O.

Imagine JavaScript HAD a synchronous `fetch()`:

```javascript
// This is hypothetical — browsers don't allow this
const user  = syncFetch("/api/user");   // 300ms — page FREEZES
const posts = syncFetch("/api/posts");  // 200ms — still FROZEN
// 500ms of completely unresponsive UI
```

During those 500ms: you can't scroll, can't click, can't see any animations. The browser's rendering engine runs on the same thread as JavaScript in most cases. A blocking synchronous operation locks everything.

The async model: initiate operations, hand them off, keep going:

```javascript
// Real fetch — non-blocking
fetch("/api/user").then(r => r.json()).then(user => {
  // This runs when data arrives — execution returns to here eventually
  renderUser(user);
});
// This line runs immediately — before the fetch is even close to done
showLoadingSpinner();
```

The golden rule: **never block the call stack**. Long computations (sorting millions of items, parsing huge JSON), blocking I/O, infinite loops — these are all ways to kill your UI. If you need to do expensive computation, Web Workers run in a separate thread. For I/O, always use the async APIs.

---

**[18:00–24:00] Callbacks — The Original Async Pattern**

The original mechanism for handling async results: pass a function TO the async operation, to be called when it completes.

You've been using callbacks since Day 13:

```javascript
btn.addEventListener("click", function(event) {
  // This IS a callback — called by the browser when the click fires
  handleClick(event);
});

setTimeout(function() {
  // This IS a callback — called after 2000ms
  doSomething();
}, 2000);
```

Node.js built its entire ecosystem on callbacks with a convention: callback is always the last argument, and it receives `(error, result)`.

```javascript
fs.readFile("./data.txt", "utf8", function(err, data) {
  if (err) {
    console.error("Failed:", err.message);
    return; // stop here — data is undefined
  }
  console.log(data);
});
```

Always check the error first. If you skip the error check and data is undefined because an error occurred, you'll get confusing downstream errors rather than the root cause.

Callbacks work. They're universal. DOM events still use callbacks and that's the right tool there. The problem shows up when you need to do multiple async operations where each depends on the result of the previous one. Let me show you.

---

**[24:00–28:00] Callback Hell**

Suppose you need to: get a user → get their posts → get comments on the first post → save the result to a file. All async, each depending on the previous result. With callbacks:

```javascript
getUser(userId, function(userErr, user) {
  if (userErr) { handleError(userErr); return; }

  getPosts(user.id, function(postsErr, posts) {
    if (postsErr) { handleError(postsErr); return; }

    getComments(posts[0].id, function(commentsErr, comments) {
      if (commentsErr) { handleError(commentsErr); return; }

      saveToFile(comments, function(saveErr) {
        if (saveErr) { handleError(saveErr); return; }

        console.log("Done!");
      });
    });
  });
});
```

Look at that indentation. The code marches to the right. Each new async step adds another level. This is called callback hell or the pyramid of doom, and it was the lived reality of JavaScript developers for years.

The problems: the structure is hard to read. Error handling has to happen at every level. You can't share a single error handler. Adding another async step means another level. Debugging a failure deep in the pyramid is painful.

This frustration drove the community to adopt Promises in 2015 as the standard solution. Let's see what they solve.

---

**[28:00–36:00] Promises — States and Chaining**

A Promise is an object representing an eventual value. It has three possible states: pending (operation in progress), fulfilled (completed successfully with a value), or rejected (failed with a reason). Once settled — once it moves to fulfilled or rejected — it never changes back.

Creating a Promise:

```javascript
function delay(ms) {
  return new Promise((resolve, reject) => {
    if (typeof ms !== "number") {
      reject(new TypeError("ms must be a number"));
      return;
    }
    setTimeout(() => resolve(`Done after ${ms}ms`), ms);
  });
}
```

The function you pass to `new Promise(...)` is called the executor. It runs synchronously. Inside it, you call either `resolve(value)` to fulfill the Promise or `reject(error)` to reject it.

Now here's what makes Promises powerful — chaining. `.then()` returns a NEW Promise. Whatever you return from `.then()`'s callback becomes the resolved value of that new Promise. This lets you chain:

```javascript
fetchUser(userId)
  .then(user => fetchPosts(user.id)) // return a Promise → chains automatically
  .then(posts => renderPosts(posts))
  .then(() => console.log("All done"))
  .catch(error => showError(error.message)) // catches ANY rejection in the chain
  .finally(() => hideSpinner());            // always runs
```

Notice: one `.catch()` at the end handles errors from ANY step in the chain. That's the key improvement over callbacks — centralized error handling.

The return value rule: if you return a non-Promise value from `.then()`, the next `.then()` gets that value. If you return a Promise, the chain waits for that Promise to resolve and passes its value forward. If you throw, it jumps to `.catch()`.

The most common mistake with Promise chaining: forgetting to `return` the inner Promise.

```javascript
// WRONG — nested, not chained. .catch() won't catch errors from the inner fetch
fetchUser(id).then(user => {
  fetchPosts(user.id).then(posts => renderPosts(posts)); // not returned!
});

// CORRECT — return the Promise to continue the chain
fetchUser(id)
  .then(user => fetchPosts(user.id)) // returned → chain continues
  .then(posts => renderPosts(posts))
  .catch(err => handleError(err));
```

---

**[36:00–42:00] Promise Combinators**

Sometimes you have multiple independent async operations and you want to run them together, not one after another.

`Promise.all` — run all concurrently, wait for all:

```javascript
const [user, posts, comments] = await Promise.all([
  fetchUser(id),
  fetchPosts(id),
  fetchComments(id)
]);
```

All three requests start at the same time. If fetching user takes 300ms, posts 200ms, comments 150ms — sequential would take 650ms. `Promise.all` takes 300ms — the maximum, not the sum. If ANY of them rejects, `Promise.all` rejects immediately.

`Promise.allSettled` — get all results regardless of success or failure:

```javascript
const results = await Promise.allSettled([fetchA(), fetchB(), fetchC()]);
results.forEach(result => {
  if (result.status === "fulfilled") process(result.value);
  else                               log(result.reason);
});
```

`allSettled` never rejects. It resolves with an array of objects, each having `status: "fulfilled"` or `status: "rejected"`. Use this when partial success is acceptable — like sending batch notifications where some might fail.

`Promise.race` — first to settle wins:

```javascript
const fastest = await Promise.race([fetchFromUS(), fetchFromEU()]);
```

The first Promise to either fulfill or reject determines the outcome. Classic use case: timeout pattern.

```javascript
function withTimeout(promise, ms) {
  const timeout = new Promise((_, reject) =>
    setTimeout(() => reject(new Error(`Timed out after ${ms}ms`)), ms)
  );
  return Promise.race([promise, timeout]);
}
const data = await withTimeout(fetch("/api/slow"), 5000);
```

`Promise.any` — first to SUCCEED wins (ES2021): rejects only if ALL reject.

```javascript
const response = await Promise.any([fetchFromCDN1(url), fetchFromCDN2(url)]);
// First CDN that responds successfully wins
```

---

**[42:00–48:00] async/await**

`async`/`await` is ES2017 syntactic sugar over Promises. It doesn't replace them — it makes them easier to write. An `async` function always returns a Promise. `await` pauses execution of the `async` function until the awaited Promise settles.

```javascript
async function fetchUserData(userId) {
  const response = await fetch(`/api/users/${userId}`);
  const user     = await response.json();
  return user; // the async function's returned Promise resolves with user
}
```

Compare to the equivalent Promise chain:
```javascript
function fetchUserData(userId) {
  return fetch(`/api/users/${userId}`)
    .then(response => response.json());
}
```

Both do the same thing. The async/await version reads like synchronous code — top to bottom, each line before the next. That's the entire point.

`await` only works inside `async` functions. At the top level of an ES module, you can use top-level `await` — that's a newer feature. In a regular script, you'd need to wrap in an async function.

The sequential vs concurrent distinction is critical. When you `await` things one by one, they run sequentially:

```javascript
async function sequential() {
  const user  = await fetchUser(id);  // 300ms
  const posts = await fetchPosts(id); // 200ms (starts after user done)
  return { user, posts }; // 500ms total
}
```

For operations that don't depend on each other, use `Promise.all`:

```javascript
async function concurrent() {
  const [user, posts] = await Promise.all([fetchUser(id), fetchPosts(id)]);
  return { user, posts }; // 300ms total (max of both)
}
```

This is a real performance difference — and it's an incredibly common mistake. If your page takes 2 seconds to load and you have 5 independent API calls each taking 400ms, they're probably sequential when they should be concurrent.

---

**[48:00–54:00] Error Handling and the Fetch API**

Error handling with async/await uses `try`/`catch` — standard JavaScript control flow, no new syntax needed.

```javascript
async function loadUser(id) {
  try {
    const response = await fetch(`/api/users/${id}`);
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    const user = await response.json();
    return user;
  } catch (error) {
    console.error("Failed to load user:", error.message);
    throw error; // re-throw so caller knows
  } finally {
    hideSpinner(); // always runs
  }
}
```

Here's the critical `fetch` gotcha: `fetch()` only REJECTS on network errors — no internet connection, the server is completely unreachable. It does NOT reject on 404 Not Found or 500 Internal Server Error. Those are valid HTTP responses. `fetch()` happily resolves with a Response object even when the status is 404.

That's why you must check `response.ok`. It's `true` if the status is 200–299. If it's false — 404, 500, whatever — throw an error yourself.

`response.status` gives you the numeric status code. `response.statusText` gives you "OK", "Not Found", etc. `response.ok` is the boolean shortcut.

To read the body: `await response.json()` parses JSON, `await response.text()` gives you raw text, `await response.blob()` for binary. Each of these also returns a Promise. And you can only call one of them — once you read the body, it's consumed. `response.bodyUsed` becomes `true`.

For POST requests, you include a `method`, `headers` object with `"Content-Type": "application/json"`, and a `body` of `JSON.stringify(yourData)`. The `Content-Type` header tells the server how to interpret the body bytes.

---

**[54:00–58:00] Axios and JSON**

Axios is a popular library that wraps HTTP requests with a cleaner API. Install with `npm install axios`.

```javascript
import axios from "axios";
const { data } = await axios.get("/api/users/1");
// data is already parsed JSON — no .json() needed
```

Axios key differences from fetch: it throws automatically on non-2xx status codes — you don't have to check `.ok`. It automatically stringifies request bodies and parses response bodies. It works in Node.js without any configuration.

You can create an Axios instance with a base URL and default headers — incredibly useful for API clients:

```javascript
const api = axios.create({
  baseURL: "https://api.example.com",
  timeout: 10000,
  headers: { "Authorization": `Bearer ${getToken()}` }
});
const user  = await api.get("/users/1");
const posts = await api.get("/posts?userId=1");
```

For Axios errors: `error.response` exists when the server responded with a non-2xx code. `error.request` exists when the request was made but no response received. `error.message` is always present.

JSON — quick summary since you'll use it constantly. `JSON.stringify(obj)` converts a JavaScript value to a JSON string. `JSON.parse(str)` converts a JSON string back to a JavaScript value. Things that get stripped by stringify: functions, Symbols, `undefined` values. Dates get converted to ISO strings, but when you parse back, they're still strings — not Date objects.

Always wrap `JSON.parse()` in a try/catch. If the string isn't valid JSON, it throws a SyntaxError. This happens with API responses that send HTML error pages instead of JSON, which is more common than you'd think.

---

**[58:00–60:00] Complete Example and Summary**

To close, let me describe the complete API integration pattern that pulls everything together. You have a `fetchJSON` helper that does the fetch, checks `response.ok`, and returns parsed data or throws. Your main `loadUser()` function is `async`, uses `Promise.all` to run concurrent fetches, uses `try/catch/finally` for error handling and spinner management, and uses `textContent` (not `innerHTML`) for displaying user data to the DOM because we need to avoid XSS. It combines everything from Part 1 and Part 2: DOM selectors, event listeners, async/await, Promise.all, JSON, and DocumentFragment for efficient rendering.

Let me give you the complete mental model for async JavaScript: operations that take time are handed to native APIs. When they complete, callbacks are queued. The event loop feeds those callbacks to the call stack when it's empty. Microtasks (Promises) have priority over macrotasks (timers). Callbacks are the original pattern — still right for DOM events. Promises gave us chaining and centralized error handling. async/await gave us synchronous-looking syntax over Promises. Fetch makes HTTP requests. Axios wraps that in a more convenient API. JSON is the text format APIs speak.

Next week, React and Angular build on exactly this foundation. React's `useEffect` hook is where you'll write your Fetch calls. Angular's `HttpClient` returns Observables, which are like Promises but more powerful — we'll cover those in Angular week.

Tomorrow is TypeScript. Today's classes become type-safe. Interfaces let you describe the shape of objects. Generics let you write functions that work on any type while maintaining type safety. TypeScript is how Angular is written and how serious React projects are structured. See you tomorrow.

---

*[END OF PART 2 — 60 MINUTES]*
