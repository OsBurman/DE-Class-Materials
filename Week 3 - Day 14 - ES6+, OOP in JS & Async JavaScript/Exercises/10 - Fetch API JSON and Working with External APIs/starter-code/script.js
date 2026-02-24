// Exercise 10: Fetch API, JSON, and Working with External APIs
// Open index.html in a browser — all output appears in the browser console.

const BASE_URL = "https://jsonplaceholder.typicode.com";
const output = document.getElementById("output");

// ── REQUIREMENT 1: Fetch a list of users ─────────────────────────────────────

// TODO: fetch(`${BASE_URL}/users`)
//   Parse the JSON response.
//   Log "Total users: <count>" then each user's name prefixed by their index (1-based).
//   Also build an <ul> of names and set output.innerHTML to it.


// ── REQUIREMENT 2: Fetch a single resource ───────────────────────────────────

// TODO: fetch(`${BASE_URL}/posts/1`)
//   Parse JSON.
//   Log "Post title: <title>" and "Post body: <body>".


// ── REQUIREMENT 3: POST new data ─────────────────────────────────────────────

// TODO: fetch(`${BASE_URL}/posts`, {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({ title: "My Post", body: "Hello world", userId: 1 })
//       })
//   Parse the JSON response and log "Created post: <object>".


// ── REQUIREMENT 4: Error handling for a bad URL ───────────────────────────────

// TODO: fetch(`${BASE_URL}/nonexistent`)
//   Check response.ok. If false, throw new Error("HTTP error: " + response.status).
//   Catch and log "Fetch error: <message>".


// ── REQUIREMENT 5: JSON.stringify and JSON.parse ──────────────────────────────

// TODO: const product = { id: 1, name: "Widget", price: 9.99 }
//   Serialize with JSON.stringify(product, null, 2) and log "JSON string:\n<str>"
//   Parse back with JSON.parse and log "Parsed name: <name>"
