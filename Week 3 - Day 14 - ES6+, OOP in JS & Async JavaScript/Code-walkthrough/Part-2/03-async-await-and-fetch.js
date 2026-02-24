// ============================================================
// Day 14 — Part 2  |  03-async-await-and-fetch.js
// async/await, Error Handling, Fetch API, Axios,
// JSON, Working with Real APIs
// ============================================================

"use strict";

// ============================================================
// 1. async / await — SYNTACTIC SUGAR OVER PROMISES
// ============================================================
// async/await does NOT replace Promises — it's a cleaner syntax
// to work WITH Promises. Under the hood it's still Promises.
//
// Rules:
//   • `async` before a function → function ALWAYS returns a Promise
//   • `await` inside an async function → pauses until Promise settles
//   • `await` can ONLY be used inside an `async` function
//     (or at the top level of an ES module)

// An async function always returns a Promise:
async function getValue() {
  return 42; // automatically wrapped: Promise.resolve(42)
}
getValue().then(v => console.log("async return:", v)); // 42

// Awaiting a resolved value:
async function demo() {
  const value = await Promise.resolve("hello");
  console.log("awaited:", value); // hello
}
demo();


// ============================================================
// 2. REWRITING PROMISE CHAINS WITH async/await
// ============================================================

// Simulated async helpers (same as in 02-promises.js)
function delay(ms, value) {
  return new Promise(resolve => setTimeout(() => resolve(value), ms));
}

function fetchUser(userId) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const db = {
        1: { id: 1, name: "Alice", departmentId: 10 },
        2: { id: 2, name: "Bob",   departmentId: 20 },
      };
      const user = db[userId];
      user ? resolve(user) : reject(new Error(`User ${userId} not found`));
    }, 150);
  });
}

function fetchDepartment(deptId) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const depts = {
        10: { id: 10, name: "Engineering" },
        20: { id: 20, name: "Marketing" },
      };
      const dept = depts[deptId];
      dept ? resolve(dept) : reject(new Error(`Dept ${deptId} not found`));
    }, 100);
  });
}

// --- Promise chain version ---
function loadUserDataChain(userId) {
  return fetchUser(userId)
    .then(user => fetchDepartment(user.departmentId)
      .then(dept => ({ user, dept }))
    )
    .then(({ user, dept }) => {
      console.log(`[chain] ${user.name} → ${dept.name}`);
    });
}

// --- async/await version (same logic, much more readable) ---
async function loadUserData(userId) {
  const user = await fetchUser(userId);
  const dept = await fetchDepartment(user.departmentId);
  console.log(`[async] ${user.name} → ${dept.name}`);
  return { user, dept };
}

loadUserDataChain(1);
loadUserData(2);


// ============================================================
// 3. ERROR HANDLING WITH async/await
// ============================================================

// try/catch works exactly like synchronous code
async function loadUserSafe(userId) {
  try {
    const user = await fetchUser(userId);
    const dept = await fetchDepartment(user.departmentId);
    return { user, dept };
  } catch (err) {
    console.error("loadUserSafe error:", err.message);
    return null; // return a fallback value
  } finally {
    console.log("loadUserSafe done (always runs)");
  }
}

loadUserSafe(1).then(data => {
  if (data) console.log("Loaded:", data.user.name);
});

loadUserSafe(999).then(data => {
  console.log("Result for 999:", data); // null
});

// ⚠️ Unhandled async errors
// This is a common mistake — NOT catching async errors:
async function forgotCatch() {
  const user = await fetchUser(999); // will throw
  console.log(user.name); // never reached
}
// forgotCatch(); // Would cause UnhandledPromiseRejection in Node!

// Always handle the rejection:
forgotCatch().catch(err => console.error("Caught outside:", err.message));


// ============================================================
// 4. SEQUENTIAL vs PARALLEL AWAIT
// ============================================================

// Sequential — each awaits before starting the next
// Total time: 300ms (150 + 100 + 50)
async function sequential() {
  const start = Date.now();
  const user  = await delay(150, { name: "Alice" });
  const dept  = await delay(100, { name: "Engineering" });
  const perms = await delay(50,  ["read", "write"]);
  console.log(`Sequential: ${Date.now() - start}ms`);
  return { user, dept, perms };
}

// Parallel — all start immediately, wait for all to finish
// Total time: ~150ms (longest one)
async function parallel() {
  const start = Date.now();
  const [user, dept, perms] = await Promise.all([
    delay(150, { name: "Alice" }),
    delay(100, { name: "Engineering" }),
    delay(50,  ["read", "write"]),
  ]);
  console.log(`Parallel: ${Date.now() - start}ms`);
  return { user, dept, perms };
}

sequential();
parallel();

// Rule: if the async operations are INDEPENDENT, use Promise.all.
// If each one depends on the result of the previous, use sequential await.


// ============================================================
// 5. ASYNC IIFE (Immediately Invoked Function Expression)
// ============================================================
// Top-level await requires an ES module (type="module" or .mjs)
// In CommonJS Node.js, wrap in an async IIFE to use await at top level

(async () => {
  const result = await delay(100, "IIFE result");
  console.log("Async IIFE:", result);
})();

// With error handling:
(async () => {
  try {
    const user = await fetchUser(1);
    console.log("IIFE user:", user.name);
  } catch (err) {
    console.error("IIFE error:", err.message);
  }
})();


// ============================================================
// 6. FETCH API
// ============================================================
// fetch() is the modern browser API (and Node 18+) for HTTP requests
// Returns a Promise<Response>

// --- 6a. Basic GET request ---
// NOTE: This runs in Node 18+ (native fetch) or browser
// Replace with https://jsonplaceholder.typicode.com for live demos

async function getPost(id) {
  const response = await fetch(`https://jsonplaceholder.typicode.com/posts/${id}`);

  // ⚠️ CRITICAL: fetch() only rejects for network errors (DNS failure, no internet)
  // A 404 or 500 response does NOT reject the Promise — you must check response.ok
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  // response.json() is ALSO a Promise — must be awaited
  const post = await response.json();
  return post;
}

getPost(1).then(post => {
  console.log("Post title:", post.title);
}).catch(err => console.error("Fetch error:", err.message));

getPost(99999).then(post => {
  console.log("Should not get here:", post);
}).catch(err => console.error("Expected error:", err.message));
// Expected error: HTTP error! status: 404


// --- 6b. The Response Object ---
async function inspectResponse() {
  const response = await fetch("https://jsonplaceholder.typicode.com/posts/1");

  console.log("ok:",          response.ok);          // true (2xx)
  console.log("status:",      response.status);       // 200
  console.log("statusText:",  response.statusText);   // "OK"
  console.log("url:",         response.url);
  console.log("content-type:", response.headers.get("content-type"));

  // Body reading methods (only one can be called — body is a stream):
  // response.json()   → parse body as JSON
  // response.text()   → body as string
  // response.blob()   → body as Blob (images, files)
  // response.arrayBuffer() → raw binary

  const data = await response.json();
  return data;
}


// --- 6c. POST request ---
async function createPost(title, body, userId) {
  const response = await fetch("https://jsonplaceholder.typicode.com/posts", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ title, body, userId }),
  });

  if (!response.ok) {
    throw new Error(`POST failed: ${response.status}`);
  }

  return await response.json();
}

createPost("Hello World", "My first post", 1).then(newPost => {
  console.log("Created post ID:", newPost.id); // JSONPlaceholder returns id: 101
}).catch(err => console.error(err.message));


// --- 6d. PUT / PATCH / DELETE ---
async function updatePost(id, updates) {
  const response = await fetch(`https://jsonplaceholder.typicode.com/posts/${id}`, {
    method: "PATCH", // PATCH for partial update, PUT for full replace
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(updates),
  });
  if (!response.ok) throw new Error(`Update failed: ${response.status}`);
  return response.json();
}

async function deletePost(id) {
  const response = await fetch(`https://jsonplaceholder.typicode.com/posts/${id}`, {
    method: "DELETE",
  });
  if (!response.ok) throw new Error(`Delete failed: ${response.status}`);
  console.log(`Post ${id} deleted (status ${response.status})`);
  // DELETE often returns 200 {} or 204 No Content
}

updatePost(1, { title: "Updated Title" }).then(p => console.log("Updated:", p.title));
deletePost(1).catch(err => console.error(err.message));


// --- 6e. Full CRUD wrapper ---
class ApiClient {
  constructor(baseURL) {
    this.baseURL = baseURL;
  }

  async request(path, options = {}) {
    const url = `${this.baseURL}${path}`;
    const response = await fetch(url, {
      headers: { "Content-Type": "application/json", ...options.headers },
      ...options,
      body: options.body ? JSON.stringify(options.body) : undefined,
    });
    if (!response.ok) {
      const text = await response.text();
      throw new Error(`${options.method || "GET"} ${url} → ${response.status}: ${text}`);
    }
    // 204 No Content has no body
    if (response.status === 204) return null;
    return response.json();
  }

  get(path)           { return this.request(path); }
  post(path, body)    { return this.request(path, { method: "POST",   body }); }
  put(path, body)     { return this.request(path, { method: "PUT",    body }); }
  patch(path, body)   { return this.request(path, { method: "PATCH",  body }); }
  delete(path)        { return this.request(path, { method: "DELETE" }); }
}

const api = new ApiClient("https://jsonplaceholder.typicode.com");

(async () => {
  try {
    const posts = await api.get("/posts?_limit=3");
    console.log("First 3 posts:", posts.map(p => p.id));

    const newPost = await api.post("/posts", { title: "Test", body: "Hello", userId: 1 });
    console.log("New post:", newPost.id);
  } catch (err) {
    console.error("API error:", err.message);
  }
})();


// ============================================================
// 7. AXIOS — Overview and Comparison
// ============================================================
// Axios is a popular HTTP library with these advantages over fetch:
//   ✅ Automatically parses JSON (no response.json() needed)
//   ✅ Throws on non-2xx responses (no manual if (!response.ok) check)
//   ✅ Works in both browser and Node.js (older Node too)
//   ✅ Request/response interceptors
//   ✅ Request cancellation (AbortController-compatible)
//   ✅ Upload progress tracking
//   ✅ Automatic request timeout

// Install: npm install axios
// const axios = require("axios");  // CommonJS
// import axios from "axios";        // ES Module

/*
// --- Basic GET ---
const { data } = await axios.get("https://jsonplaceholder.typicode.com/posts/1");
// `data` is already the parsed JSON object — no .json() needed
console.log(data.title);

// --- POST ---
const response = await axios.post("/posts", {
  title: "Hello",
  body: "World",
  userId: 1,
});
console.log(response.data); // the created post

// --- Error handling (automatically throws on 4xx/5xx) ---
try {
  await axios.get("/posts/99999");
} catch (err) {
  console.error(err.response.status);  // 404
  console.error(err.response.data);    // error body
  console.error(err.message);          // Request failed with status code 404
}

// --- axios.create — instance with defaults ---
const api = axios.create({
  baseURL: "https://jsonplaceholder.typicode.com",
  timeout: 5000,
  headers: {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
  },
});

// Now relative paths work:
const user = await api.get("/users/1");
const posts = await api.get("/posts?userId=1");
const newPost = await api.post("/posts", { title: "Test", body: "Hi", userId: 1 });

// --- Interceptors ---
// Request interceptor — runs before every request
api.interceptors.request.use(config => {
  console.log(`→ ${config.method.toUpperCase()} ${config.url}`);
  return config;
});

// Response interceptor — runs on every response
api.interceptors.response.use(
  response => response,          // 2xx
  error => {
    if (error.response?.status === 401) {
      // redirect to login, refresh token, etc.
    }
    return Promise.reject(error); // pass it on
  }
);
*/

// Fetch vs Axios comparison table (in comments):
// ┌─────────────────────────┬──────────────┬──────────────┐
// │ Feature                 │ fetch()      │ axios        │
// ├─────────────────────────┼──────────────┼──────────────┤
// │ Built-in (no install)   │ ✅           │ ❌           │
// │ Auto JSON parse         │ ❌ manual    │ ✅           │
// │ Throws on 4xx/5xx       │ ❌ manual    │ ✅           │
// │ Request timeout         │ ❌ AbortCtrl │ ✅ built-in  │
// │ Interceptors            │ ❌           │ ✅           │
// │ Upload progress         │ ❌           │ ✅           │
// │ Old Node.js support     │ ❌ Node 18+  │ ✅           │
// └─────────────────────────┴──────────────┴──────────────┘


// ============================================================
// 8. JSON — Parsing and Stringification
// ============================================================

// --- JSON.parse() — string → JavaScript value ---
const jsonString = '{"name":"Alice","age":30,"hobbies":["reading","coding"]}';
const parsed = JSON.parse(jsonString);
console.log(parsed.name);         // Alice
console.log(parsed.hobbies[1]);   // coding
console.log(typeof parsed.age);   // number

// Parse with reviver function (transform values during parsing)
const withDate = '{"name":"Event","date":"2024-06-15T10:00:00.000Z"}';
const event = JSON.parse(withDate, (key, value) => {
  if (key === "date") return new Date(value); // convert string to Date object
  return value;
});
console.log(event.date instanceof Date); // true
console.log(event.date.getFullYear());   // 2024

// ⚠️ JSON.parse throws SyntaxError on invalid JSON:
try {
  JSON.parse("{bad json}");
} catch (err) {
  console.error("Parse error:", err.message);
}


// --- JSON.stringify() — JavaScript value → string ---
const user = {
  name: "Alice",
  age: 30,
  hobbies: ["reading", "coding"],
  address: { city: "New York" },
};

// Basic
const basic = JSON.stringify(user);
console.log(basic);
// {"name":"Alice","age":30,"hobbies":["reading","coding"],"address":{"city":"New York"}}

// Pretty-print with 2-space indent
const pretty = JSON.stringify(user, null, 2);
console.log(pretty);
/*
{
  "name": "Alice",
  "age": 30,
  ...
}
*/

// Replacer — array of keys to include (whitelist)
const minimal = JSON.stringify(user, ["name", "age"]);
console.log(minimal); // {"name":"Alice","age":30}

// Replacer — function (customize every key)
const censored = JSON.stringify(user, (key, value) => {
  if (key === "age") return undefined; // omit this key
  if (typeof value === "string") return value.toUpperCase();
  return value;
});
console.log(censored); // {"name":"ALICE","hobbies":["READING","CODING"],"address":{"city":"NEW YORK"}}

// ⚠️ What JSON.stringify DROPS:
const tricky = {
  name: "Alice",
  fn: () => "hello",          // undefined — functions are omitted
  sym: Symbol("s"),            // undefined — Symbols are omitted
  undef: undefined,            // undefined — undefined values are omitted
  nan: NaN,                    // becomes null
  inf: Infinity,               // becomes null
  date: new Date("2024-01-01"), // becomes ISO string (then parse gives string, not Date!)
};
console.log(JSON.stringify(tricky));
// {"name":"Alice","nan":null,"inf":null,"date":"2024-01-01T00:00:00.000Z"}

// ⚠️ Circular reference throws:
const circular = { a: 1 };
circular.self = circular;
try {
  JSON.stringify(circular);
} catch (err) {
  console.error("Circular:", err.message);
  // Converting circular structure to JSON
}

// JSON round-trip (serialize then deserialize):
const obj = { x: 1, nested: { y: 2 } };
const roundTripped = JSON.parse(JSON.stringify(obj));
roundTripped.nested.y = 999;
console.log(obj.nested.y); // 2 — NOT affected (deep clone via JSON)
// ⚠️ This only works for plain data (no functions, Dates, Symbols, undefined)


// ============================================================
// 9. WORKING WITH REAL API RESPONSES
// ============================================================

// Paginated list endpoint
async function getAllPosts(limit = 10, page = 1) {
  const response = await fetch(
    `https://jsonplaceholder.typicode.com/posts?_limit=${limit}&_page=${page}`
  );
  if (!response.ok) throw new Error(`HTTP ${response.status}`);

  const posts = await response.json();
  const total = parseInt(response.headers.get("x-total-count") ?? "100");
  const totalPages = Math.ceil(total / limit);

  return { posts, total, page, totalPages };
}

(async () => {
  try {
    const { posts, total, totalPages } = await getAllPosts(5, 1);
    console.log(`Showing ${posts.length} of ${total} posts (${totalPages} pages)`);
    posts.forEach(p => console.log(`  [${p.id}] ${p.title.slice(0, 40)}...`));
  } catch (err) {
    console.error("getAllPosts error:", err.message);
  }
})();


// Nested/related resources
async function getUserWithPosts(userId) {
  const [user, posts] = await Promise.all([
    fetch(`https://jsonplaceholder.typicode.com/users/${userId}`)
      .then(r => { if (!r.ok) throw new Error(`User ${userId} not found`); return r.json(); }),
    fetch(`https://jsonplaceholder.typicode.com/posts?userId=${userId}`)
      .then(r => r.json()),
  ]);
  return { user, posts };
}

(async () => {
  try {
    const { user, posts } = await getUserWithPosts(1);
    console.log(`${user.name} has ${posts.length} posts`);
    console.log(`First post: "${posts[0]?.title}"`);
  } catch (err) {
    console.error("getUserWithPosts error:", err.message);
  }
})();


// ============================================================
// 10. ABORT CONTROLLER — Cancel a fetch request
// ============================================================

async function fetchWithTimeout(url, timeoutMs = 3000) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const response = await fetch(url, { signal: controller.signal });
    clearTimeout(timeoutId);
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return await response.json();
  } catch (err) {
    if (err.name === "AbortError") {
      throw new Error(`Request timed out after ${timeoutMs}ms`);
    }
    throw err;
  }
}

fetchWithTimeout("https://jsonplaceholder.typicode.com/posts/1")
  .then(post => console.log("Fetched:", post.title))
  .catch(err => console.error("Timeout error:", err.message));
