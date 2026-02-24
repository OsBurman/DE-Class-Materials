// Exercise 10: Fetch API, JSON, and Working with External APIs — SOLUTION

const BASE_URL = "https://jsonplaceholder.typicode.com";
const output = document.getElementById("output");

// ── REQUIREMENT 1: Fetch a list of users ─────────────────────────────────────

fetch(`${BASE_URL}/users`)
  .then((response) => response.json())
  .then((users) => {
    console.log(`Total users: ${users.length}`);
    users.forEach((user, i) => console.log(`${i + 1}. ${user.name}`));

    const ul = document.createElement("ul");
    users.forEach((user) => {
      const li = document.createElement("li");
      li.textContent = user.name;
      ul.appendChild(li);
    });
    output.innerHTML = "";
    output.appendChild(ul);
  });

// ── REQUIREMENT 2: Fetch a single resource ───────────────────────────────────

fetch(`${BASE_URL}/posts/1`)
  .then((response) => response.json())
  .then((post) => {
    console.log("Post title:", post.title);
    console.log("Post body:", post.body);
  });

// ── REQUIREMENT 3: POST new data ─────────────────────────────────────────────

fetch(`${BASE_URL}/posts`, {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ title: "My Post", body: "Hello world", userId: 1 }),
})
  .then((response) => response.json())
  .then((created) => {
    console.log("Created post:", created);
  });

// ── REQUIREMENT 4: Error handling for a bad URL ───────────────────────────────

fetch(`${BASE_URL}/nonexistent`)
  .then((response) => {
    if (!response.ok) {
      throw new Error("HTTP error: " + response.status);
    }
    return response.json();
  })
  .catch((err) => {
    console.log("Fetch error:", err.message);
  });

// ── REQUIREMENT 5: JSON.stringify and JSON.parse ──────────────────────────────

const product = { id: 1, name: "Widget", price: 9.99 };
const jsonString = JSON.stringify(product, null, 2);
console.log("JSON string:\n" + jsonString);

const parsed = JSON.parse(jsonString);
console.log("Parsed name:", parsed.name);
