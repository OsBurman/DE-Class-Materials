SLIDE 1 — Title Slide
Slide content: Title: "Async JavaScript & Working with APIs" | Subtitle: "Event Loop, Promises, Async/Await, Fetch & Axios" | Your name, date, course name

Script:
"Good morning/afternoon everyone. Today's lesson is one of the most important sessions in this entire course. Everything we cover today underpins how modern JavaScript actually works — and once this clicks, you'll understand why JavaScript behaves the way it does in virtually every real-world application you'll ever build.
By the end of today you will understand how JavaScript handles time, how it manages tasks that take a while to complete — like fetching data from a server — and how to write clean, readable async code. Let's get into it."

SLIDE 2 — Lesson Objectives
Slide content: Bulleted list of today's learning goals — Understand the event loop & async execution model | Implement async operations with Promises | Use async/await for clean async code | Make HTTP requests with Fetch and Axios | Work with JSON data | Handle errors in async code

Script:
"Here are the six things I want you to walk out of here knowing cold. Keep these in mind as we go — everything we do today maps back to one of these six outcomes. Don't worry if some of these words don't mean anything to you yet. They will."

SLIDE 3 — The JavaScript Runtime: The Big Picture
Slide content: Diagram showing: Call Stack | Web APIs (browser environment) | Callback Queue | Event Loop arrow cycling between queue and stack | Heap (memory)

Script:
"Before we write a single line of async code, we need to understand what's actually happening under the hood when JavaScript runs.
JavaScript is single-threaded. That is the most important sentence I will say today. Write it down. Single-threaded means JavaScript can only do one thing at a time. There is one call stack. Only one function can be executing at any given moment.
So look at this diagram. You have five components working together.
The heap is just where objects are stored in memory — don't worry about that too much today.
The call stack is where JavaScript keeps track of what function it's currently executing. When you call a function, it gets pushed onto the stack. When it returns, it gets popped off. It's literally a stack data structure.
The Web APIs box — this is the browser's environment. Things like setTimeout, fetch, DOM event listeners — these are not actually part of JavaScript itself. They're provided by the browser. When JavaScript hands off a task to a Web API, it doesn't wait for it. It moves on.
The callback queue — sometimes called the task queue — is where callbacks sit and wait once a Web API has finished its work.
And the event loop — the event loop has one very simple job: it watches the call stack and the callback queue. If the call stack is empty, it takes the first thing from the callback queue and pushes it onto the stack.
That's the whole machine. Everything async in JavaScript runs through this machine."

SLIDE 4 — The Call Stack in Action
Slide content: Step-by-step visual of a simple call stack — show main() → greet() → console.log() being pushed and popped. Simple annotated code block on one side, stack visualization on the other.

Script:
"Let me make the call stack concrete before we complicate things.
javascriptfunction greet(name) {
  console.log('Hello, ' + name);
}

function main() {
  greet('Alice');
}

main();
When this runs: main gets pushed onto the stack. Inside main, greet is called — greet gets pushed on top. Inside greet, console.log is called — that gets pushed on top. console.log executes and pops off. greet finishes and pops off. main finishes and pops off. Stack is empty.
Simple. Synchronous. One thing at a time. No surprises.
Now what happens when something takes time? What if one of those functions needs to go fetch data from a server that might take two seconds? If JavaScript just sat there and waited, your entire browser would freeze. Nothing could respond to clicks, nothing could animate, nothing could update. That's the problem async code solves."

SLIDE 5 — Synchronous vs Asynchronous Code
Slide content: Two side-by-side code blocks. Left: synchronous code that blocks. Right: async code with setTimeout showing non-blocking behavior. Output order shown below each.

Script:
"Let's look at the difference directly.
javascript// Synchronous
console.log('one');
console.log('two');
console.log('three');
// Output: one, two, three — always, in order
No surprises. Each line runs, completes, and the next line runs.
Now look at this:
javascript// Asynchronous
console.log('one');
setTimeout(() => {
  console.log('two');
}, 1000);
console.log('three');
// Output: one, three, two
This trips up nearly every new developer the first time they see it. 'Two' logs last even though it's written second in the code. Why?
Because setTimeout hands its callback off to the browser's Web API. JavaScript doesn't wait. It keeps executing — logs 'three' — and only once the call stack is empty and the timer has elapsed does the event loop pull that callback out of the queue and run it.
This is the fundamental shift in thinking you need to make. In async JavaScript, the order you write code is not always the order it executes."

SLIDE 6 — Callbacks
Slide content: Definition of a callback function. Simple callback example. Then a real-world-ish example simulating an API call with setTimeout and a callback.

Script:
"So how did JavaScript developers handle async code before modern tools? With callbacks.
A callback is simply a function you pass to another function to be called later — when the async work is done.
javascriptfunction fetchUserData(userId, callback) {
  setTimeout(() => {
    const user = { id: userId, name: 'Alice' };
    callback(user);
  }, 1000);
}

fetchUserData(1, function(user) {
  console.log('Got user:', user.name);
});
This works. When the simulated fetch completes after a second, it calls our callback with the user data. This was the standard pattern for years.
The problem? What happens when you need to do multiple async things in sequence? One after another?"

SLIDE 7 — Callback Hell
Slide content: The infamous "pyramid of doom" — deeply nested callback code. Visual of the triangle shape formed by indentation. Quote: "Also known as the Pyramid of Doom."

Script:
"This is what happens when you need to chain async operations using callbacks:
javascriptfetchUser(1, function(user) {
  fetchOrders(user.id, function(orders) {
    fetchOrderDetails(orders[0].id, function(details) {
      fetchProductInfo(details.productId, function(product) {
        console.log(product.name);
        // What if something goes wrong here?
      });
    });
  });
});
Look at that shape. Every time you need to do something async after a previous async thing completes, you nest another callback inside. This is called callback hell, or the pyramid of doom.
It's not just ugly — it's genuinely hard to reason about. Error handling in this pattern is a nightmare. You have to pass error arguments into every single callback and check them manually. It's fragile, it's messy, and it doesn't scale.
This pain is exactly why Promises were introduced."

SLIDE 8 — Promises: The Concept
Slide content: Definition: "A Promise is an object representing the eventual completion or failure of an asynchronous operation." | Three states diagram: Pending → Fulfilled OR Rejected. Visual state machine.

Script:
"A Promise is an object that represents a value that isn't available yet — but will be at some point in the future, or won't be because something went wrong.
Think of it like ordering food at a restaurant. You place your order — that's the Promise being created. You get a buzzer. The Promise is now pending. You don't stand at the counter and stare at the kitchen. You go sit down. You go do other things. When the food is ready, the buzzer goes off — the Promise is fulfilled. If the kitchen runs out of what you ordered, they come tell you — the Promise is rejected.
There are exactly three states: pending, fulfilled, and rejected. A Promise can only ever transition from pending to one of the other two, and once it's settled it never changes again. That immutability is part of what makes Promises reliable.
Let's look at how you create one."

SLIDE 9 — Creating a Promise
Slide content: Code block showing Promise constructor with resolve and reject. Annotated with labels pointing to executor function, resolve call, reject call.

Script:
"Here's the anatomy of a Promise:
javascriptconst myPromise = new Promise((resolve, reject) => {
  // This function is called the executor
  // Do your async work here
  
  const success = true;
  
  if (success) {
    resolve('It worked!'); // Fulfills the promise
  } else {
    reject(new Error('Something went wrong')); // Rejects the promise
  }
});
The new Promise() constructor takes one argument — a function called the executor. The executor receives two arguments: resolve and reject. These are functions provided by JavaScript.
When your async work succeeds, you call resolve with the result value. When it fails, you call reject with an error.
Now let's look at how you consume a Promise — how you actually use the value once it's ready."

SLIDE 10 — Consuming Promises: .then(), .catch(), .finally()
Slide content: Code block showing .then(), .catch(), .finally() chained on a Promise. Annotations explaining each method's role.

Script:
"Once you have a Promise, you chain methods onto it to handle its outcome.
javascriptmyPromise
  .then((value) => {
    console.log('Success:', value);
  })
  .catch((error) => {
    console.error('Error:', error.message);
  })
  .finally(() => {
    console.log('This always runs, success or failure');
  });
.then() is called when the Promise fulfills. The value passed to resolve becomes the argument here.
.catch() is called when the Promise rejects. The error passed to reject ends up here.
.finally() runs regardless of outcome — useful for cleanup like hiding a loading spinner.
One critical thing to understand: these methods themselves return new Promises. That's what enables chaining."

SLIDE 11 — Promise Chaining
Slide content: Code block showing a chain of .then() calls where each returns a new value. Visual flow diagram showing data transforming through the chain.

Script:
"Because .then() always returns a new Promise, you can chain them. And if you return a value inside a .then(), that value becomes the resolved value of the next Promise in the chain.
javascriptfetch('https://api.example.com/user/1')
  .then(response => response.json())       // Parse JSON, returns a Promise
  .then(user => {
    console.log(user.name);
    return fetch(`/api/orders/${user.id}`); // Return another Promise
  })
  .then(response => response.json())
  .then(orders => {
    console.log(orders);
  })
  .catch(error => {
    console.error('Something failed:', error);
  });
Notice how flat this is compared to callback hell. Same sequential async operations, but readable from top to bottom. And notice there's only one .catch() at the end — a rejection at any point in the chain falls all the way down to it. You don't have to handle errors at every single step."

SLIDE 12 — Promise.all, Promise.race, Promise.allSettled
Slide content: Three sections — one per method. Short description and code snippet for each. Table comparing behavior when one Promise fails.

Script:
"JavaScript gives us several static methods on the Promise object for coordinating multiple Promises at once.
Promise.all — runs multiple Promises in parallel and waits for all of them to fulfill. If any one rejects, the whole thing rejects immediately.
javascriptPromise.all([
  fetch('/api/users'),
  fetch('/api/products'),
  fetch('/api/orders')
])
.then(([users, products, orders]) => {
  // All three completed successfully
})
.catch(error => {
  // At least one failed
});
Use this when you need all results and it's okay to fail fast.
Promise.race — resolves or rejects as soon as the first Promise settles, whichever that is. Useful for timeouts — race your fetch against a timeout Promise and handle whichever finishes first.
javascriptPromise.race([
  fetch('/api/data'),
  new Promise((_, reject) => setTimeout(() => reject(new Error('Timeout')), 5000))
])
.then(response => console.log('Got data'))
.catch(error => console.log('Timed out or failed'));
Promise.allSettled — waits for all Promises to settle regardless of whether they fulfill or reject. Returns an array of result objects, each with a status of either 'fulfilled' or 'rejected' and the corresponding value or reason. Use this when you need results from everything and you want to handle failures individually rather than failing fast.
javascriptPromise.allSettled([
  fetch('/api/users'),
  fetch('/api/broken-endpoint'),
  fetch('/api/products')
])
.then(results => {
  results.forEach(result => {
    if (result.status === 'fulfilled') {
      console.log('Success:', result.value);
    } else {
      console.log('Failed:', result.reason);
    }
  });
});
The choice between these three depends entirely on your use case."

SLIDE 13 — Async/Await: Introduction
Slide content: Side-by-side comparison — Promise chain on the left, equivalent async/await on the right. Same operation, dramatically different readability.

Script:
"Async/await was introduced in ES2017 and it is, quite simply, syntactic sugar over Promises. It doesn't replace Promises — it's built on top of them. What it does is let you write async code that looks and reads like synchronous code.
Look at these two blocks doing the exact same thing:
javascript// With Promises
function getUser() {
  return fetch('/api/user/1')
    .then(res => res.json())
    .then(user => {
      console.log(user);
      return user;
    });
}

// With async/await
async function getUser() {
  const res = await fetch('/api/user/1');
  const user = await res.json();
  console.log(user);
  return user;
}
The async/await version reads like a recipe. Step one, fetch. Step two, parse. Step three, log. Any developer — even one who doesn't know JavaScript — can follow the logic.
Two keywords to learn: async and await. Let's break them down."

SLIDE 14 — The async and await Keywords
Slide content: Definitions. async: "Marks a function as asynchronous — it always returns a Promise." await: "Pauses execution of the async function until the Promise settles. Can only be used inside an async function."

Script:
"async goes in front of a function declaration or expression. It does two things: it marks the function as asynchronous, and it makes the function automatically return a Promise. If you return a plain value from an async function, JavaScript wraps it in Promise.resolve() automatically.
javascriptasync function greet() {
  return 'Hello'; // Same as: return Promise.resolve('Hello')
}

greet().then(msg => console.log(msg)); // 'Hello'
await can only be used inside an async function. When JavaScript hits an await, it pauses that function's execution — but critically, it does not block the call stack. Other code can still run. It's like JavaScript is saying 'I'll come back to this function when this Promise resolves.' Under the hood the event loop is still doing its job.
javascriptasync function loadData() {
  console.log('Starting...');
  const data = await someAsyncOperation(); // Pauses here
  console.log('Got data:', data);          // Resumes when Promise resolves
}
This is the most important nuance: await pauses the function, not the entire program."

SLIDE 15 — Error Handling with Async/Await
Slide content: try/catch/finally block wrapping await calls. Comparison with .catch() on Promises. Note about unhandled promise rejections.

Script:
"With Promises we used .catch(). With async/await, we use standard try/catch blocks — the same error handling mechanism you'd use for any synchronous code.
javascriptasync function getUser(id) {
  try {
    const response = await fetch(`/api/users/${id}`);
    
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    
    const user = await response.json();
    return user;
  } catch (error) {
    console.error('Failed to fetch user:', error.message);
    // You could re-throw here, return a default, or handle it
    throw error;
  } finally {
    console.log('Fetch attempt complete');
  }
}
Important note about that response.ok check — I want you to pay attention to this because it trips people up constantly. The fetch API does not reject on HTTP error status codes like 404 or 500. It only rejects on network failures. So if your server returns a 404, the await fetch() will still succeed from JavaScript's perspective. You have to check response.ok yourself and throw if necessary. We'll come back to this in a moment.
Always handle your Promise rejections. An unhandled rejection will throw a warning in Node and can crash your application in some environments."

SLIDE 16 — The Fetch API
Slide content: Fetch API overview. Basic GET request. Anatomy of a Response object — status, ok, headers, body methods (.json(), .text(), .blob()). Note about response.ok.

Script:
"The Fetch API is the modern, built-in browser API for making HTTP requests. It replaced XMLHttpRequest — you may see XHR in older codebases, but fetch is what you'll use going forward.
A basic GET request:
javascriptconst response = await fetch('https://jsonplaceholder.typicode.com/users/1');
const user = await response.json();
console.log(user);
Two awaits. One for the network request itself — the response headers arriving. One for reading the body — parsing the JSON. These are two separate steps because the body might be large and stream in separately from the headers.
The Response object has several important properties:
response.ok — a boolean, true if the status is 200-299. Always check this.
response.status — the numeric HTTP status code, like 200, 404, 500.
response.headers — a Headers object.
And several body-reading methods — all of which return Promises:
response.json() — parse body as JSON
response.text() — get body as a plain string
response.blob() — for binary data like images
For a POST request with a JSON body:
javascriptconst response = await fetch('/api/users', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ name: 'Alice', email: 'alice@example.com' })
});

const newUser = await response.json();
The second argument to fetch is the options object. method, headers, and body are the most common properties you'll set."

SLIDE 17 — JSON: Parsing and Stringification
Slide content: JSON.parse() vs JSON.stringify(). Code examples. Common gotchas: undefined, functions, and circular references not supported in JSON.

Script:
"Since we're working with APIs, let's talk briefly about JSON — JavaScript Object Notation. It is the universal language of web APIs. When data travels over HTTP, it travels as a string. JSON is the format of that string.
Two methods you need to know cold:
javascript// JavaScript object → JSON string (for sending data)
const user = { name: 'Alice', age: 30 };
const jsonString = JSON.stringify(user);
// Result: '{"name":"Alice","age":30}'

// JSON string → JavaScript object (for receiving data)
const parsed = JSON.parse(jsonString);
// Result: { name: 'Alice', age: 30 }
JSON.stringify when you're sending. JSON.parse when you're receiving. response.json() in the Fetch API is essentially JSON.parse wrapped in a Promise for you.
Things JSON cannot represent: undefined, functions, Symbol, and circular references. If you JSON.stringify an object with an undefined value, that key just disappears. Something to watch for.
You can pretty-print JSON for debugging:
javascriptconsole.log(JSON.stringify(user, null, 2)); // 2-space indentation
````"

---

## SLIDE 18 — Axios
**Slide content:** Axios overview — what it is, why use it vs Fetch. Key differences table: automatic JSON parsing, throws on HTTP errors, request/response interceptors, better browser support, works in Node.js natively.

---

**Script:**

"Fetch is great, but in production applications you'll frequently encounter Axios — a popular third-party HTTP library. It's worth knowing both.
```javascript
// Install: npm install axios
import axios from 'axios';

// GET request
const response = await axios.get('/api/users/1');
console.log(response.data); // Already parsed, no .json() needed

// POST request
const newUser = await axios.post('/api/users', {
  name: 'Alice',
  email: 'alice@example.com'
});
console.log(newUser.data);
```

Notice a few differences from Fetch.

First: Axios automatically parses JSON for you. The data is on `response.data`, ready to use. No `.json()` call needed.

Second — and this is significant — **Axios throws an error for HTTP error status codes like 404 and 500**. Remember how I said Fetch doesn't? Axios does. This means your try/catch will actually catch network errors and HTTP errors alike, which is more intuitive.

Third: Axios works identically in both the browser and Node.js. Fetch is a browser API — in Node you'd need to install a polyfill or use the newer built-in fetch. Axios handles both environments out of the box.

Fourth: Axios has interceptors — middleware-style functions that can run before every request or after every response. Incredibly useful for things like attaching auth tokens to every request automatically.
```javascript
// Attach auth token to every request
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

// Or with interceptors
axios.interceptors.request.use(config => {
  config.headers.Authorization = `Bearer ${getToken()}`;
  return config;
});
```

For small projects or quick scripts, Fetch is fine. For larger applications, Axios's convenience features often make it the better choice."

---

## SLIDE 19 — Working with External APIs: A Complete Example
**Slide content:** Full end-to-end code example — fetching from a real public API (JSONPlaceholder or similar), handling loading/error states, processing and displaying data. Annotated heavily.

---

**Script:**

"Let's put everything together in one realistic example. We're going to fetch a list of posts from JSONPlaceholder — a free public fake API great for practice.
```javascript
async function loadPosts() {
  const loadingEl = document.getElementById('loading');
  const errorEl = document.getElementById('error');
  const listEl = document.getElementById('posts');

  loadingEl.style.display = 'block';

  try {
    const response = await fetch('https://jsonplaceholder.typicode.com/posts?_limit=5');

    if (!response.ok) {
      throw new Error(`Server error: ${response.status}`);
    }

    const posts = await response.json();

    posts.forEach(post => {
      const li = document.createElement('li');
      li.textContent = post.title;
      listEl.appendChild(li);
    });

  } catch (error) {
    errorEl.textContent = `Failed to load posts: ${error.message}`;
    errorEl.style.display = 'block';
  } finally {
    loadingEl.style.display = 'none';
  }
}

loadPosts();
```

Walk through this with me. We're managing three UI states: loading, error, and success — this is the standard pattern for any async data fetching in a real application.

We show a loading indicator before the request starts. Inside try, we fetch, check `response.ok`, parse JSON, and render. In catch, we display the error. In finally — regardless of outcome — we hide the loading indicator.

This is production-grade thinking. Every async operation that touches a UI should handle these three states."

---

## SLIDE 20 — The Same Example with Axios
**Slide content:** The previous example rewritten with Axios. Side-by-side diff highlighting the differences.

---

**Script:**

"Here's the same thing with Axios — notice how it cleans up:
```javascript
import axios from 'axios';

async function loadPosts() {
  try {
    const response = await axios.get(
      'https://jsonplaceholder.typicode.com/posts',
      { params: { _limit: 5 } }
    );

    // No response.ok check needed — Axios throws on HTTP errors
    // No .json() call needed — data is already parsed
    const posts = response.data;

    posts.forEach(post => {
      const li = document.createElement('li');
      li.textContent = post.title;
      document.getElementById('posts').appendChild(li);
    });

  } catch (error) {
    // error.response exists for HTTP errors
    // error.message exists for network errors
    if (error.response) {
      console.error('HTTP Error:', error.response.status);
    } else {
      console.error('Network Error:', error.message);
    }
  }
}
```

Two fewer steps. The Axios error object also gives you richer information — `error.response` exists when the server responded with an error status, `error.request` exists when the request was sent but no response received, and `error.message` covers everything else."

---

## SLIDE 21 — Common Async Pitfalls
**Slide content:** List of common mistakes — forgetting await, not checking response.ok with Fetch, using await inside forEach (won't work as expected), swallowing errors in empty catch blocks.

---

**Script:**

"Before we wrap up, let me flag the mistakes I see new developers make most often with async code.

**Forgetting await.** If you forget await before a Promise, you get the Promise object itself, not the value. `const user = fetch(url)` — user is a Promise, not user data. The code won't error, it'll just behave wrong and be confusing to debug.

**Not checking response.ok with Fetch.** I've mentioned this twice today because it matters that much. Fetch does not throw on 404 or 500. Check response.ok.

**Using await inside forEach.** This one is subtle:
```javascript
// This does NOT work as expected
posts.forEach(async (post) => {
  await savePost(post); // Each save runs, but forEach doesn't wait for any of them
});

// Do this instead
for (const post of posts) {
  await savePost(post); // Waits for each save before moving to next
}

// Or for parallel execution:
await Promise.all(posts.map(post => savePost(post)));
```

**Empty catch blocks.** Never do this:
```javascript
try {
  await riskyOperation();
} catch (e) {
  // silently do nothing
}
```

Silent failures are the hardest bugs to track down. At minimum, log the error. Handle it meaningfully if you can."

---

## SLIDE 22 — Bringing It All Together: Mental Model
**Slide content:** Flow diagram — "async task starts" → "handed to Web API" → "JS continues" → "task completes, callback/resolve queued" → "event loop picks it up when stack is empty" → "handler runs." Clean visual summary of the whole system.

---

**Script:**

"Let me give you the unified mental model to take away from today.

When you write `await fetch(url)` — here's what actually happens:

One: `fetch` is called. The browser's networking Web API starts the HTTP request. JavaScript does NOT wait.

Two: The async function is suspended at the `await` line. Control returns to the call stack. Other code can run.

Three: The network request completes. The browser queues the resolved Promise callback.

Four: The event loop sees the call stack is empty. It picks up the queued callback and pushes it onto the stack.

Five: Your async function resumes from where it was suspended. The response value is now available.

This is the whole system. The event loop is the engine. Promises and async/await are the clean interface we use to interact with it. The Web APIs are the workers operating outside the main thread.

Once this model is in your head, asynchronous JavaScript stops feeling like magic and starts feeling like a well-engineered machine."

---

## SLIDE 23 — Summary
**Slide content:** Concise recap of every major topic covered today. One sentence per topic. No fluff.

---

**Script:**

"Quick summary of what we covered.

The JavaScript runtime is single-threaded and uses a call stack, Web APIs, a callback queue, and an event loop to handle asynchronous operations.

Synchronous code executes line by line. Asynchronous code offloads work and resumes later via the event loop.

Callbacks were the original async pattern, but they lead to deeply nested, hard-to-maintain code — callback hell.

Promises represent eventual values with three states — pending, fulfilled, rejected — and are consumed with `.then()`, `.catch()`, and `.finally()`.

`Promise.all` runs Promises in parallel and fails fast. `Promise.race` resolves with whichever settles first. `Promise.allSettled` waits for everything and reports all results.

Async/await is syntactic sugar over Promises that makes async code read like synchronous code. Errors are handled with try/catch.

The Fetch API is the built-in browser tool for HTTP requests — always check response.ok.

Axios is a third-party library with automatic JSON parsing, throws on HTTP errors, and includes powerful features like interceptors.

JSON.stringify converts objects to strings for transport. JSON.parse converts strings back to objects."

---

## SLIDE 24 — Practice Exercises
**Slide content:** Three exercises listed — Beginner, Intermediate, Advanced. No answers shown.

---

**Script:**

"Here are three exercises to do before next class.

**Beginner:** Use the Fetch API to GET data from `https://jsonplaceholder.typicode.com/users` and display each user's name and email in a list. Handle loading and error states.

**Intermediate:** Use `Promise.all` to fetch both `/users` and `/posts` from JSONPlaceholder simultaneously. Only render posts written by users who are in your users list — match by userId.

**Advanced:** Create a reusable async function that wraps Axios with retry logic. If a request fails, it should automatically retry up to three times with a one-second delay between attempts, then throw if all retries fail.

These three exercises will force you to use everything from today's lesson. Do the beginner one at minimum."

---

## SLIDE 25 — What's Coming Next
**Slide content:** Preview of upcoming topics — whatever fits your course plan (e.g., advanced error handling patterns, working with authentication, WebSockets, etc.)

---

**Script:**

"Next lesson we'll be building on everything from today when we get into [your next topic]. Make sure you're comfortable with Promises and async/await before then — they're the foundation for everything coming up.

Any questions? Let's take them now."

