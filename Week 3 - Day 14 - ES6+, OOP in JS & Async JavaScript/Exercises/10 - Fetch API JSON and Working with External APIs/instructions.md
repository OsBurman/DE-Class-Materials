# Exercise 10: Fetch API, JSON, and Working with External APIs

## Objective
Use the browser **Fetch API** to make HTTP requests to a real external API, parse JSON responses, and handle errors. Practice `JSON.stringify` and `JSON.parse`.

---

## Background
The `fetch()` function returns a Promise. The `Response` object has a `.json()` method that parses the body as JSON (also async).

```js
fetch(url)
  .then(response => response.json())
  .then(data => console.log(data));
```

We will use **JSONPlaceholder** (`https://jsonplaceholder.typicode.com`) — a free, public, no-auth REST API.

---

## Setup
Open `index.html` in a browser (double-click or use Live Server). All output will appear in the browser **console** (F12 → Console tab) and in the `<div id="output">` on the page.

---

## Requirements

### Requirement 1 — Fetch a list of users
Fetch `https://jsonplaceholder.typicode.com/users`.  
Log the count of users and the `name` of each one.

Expected console output:
```
Total users: 10
1. Leanne Graham
2. Ervin Howell
...
```

Also display the names in the `#output` div as an unordered list.

---

### Requirement 2 — Fetch a single resource
Fetch `https://jsonplaceholder.typicode.com/posts/1`.  
Log the post's `title` and `body`.

Expected:
```
Post title: sunt aut facere ...
Post body: quia et suscipit ...
```

---

### Requirement 3 — POST new data
Fetch `https://jsonplaceholder.typicode.com/posts` with method `POST`.  
Send a JSON body: `{ title: "My Post", body: "Hello world", userId: 1 }`.  
Set header `Content-Type: application/json`.  
Log the response object (JSONPlaceholder echoes back the created object with an `id`).

Expected:
```
Created post: { title: 'My Post', body: 'Hello world', userId: 1, id: 101 }
```

---

### Requirement 4 — Error handling for a bad URL
Fetch `https://jsonplaceholder.typicode.com/nonexistent`.  
Check `response.ok`. If not ok, throw a new `Error("HTTP error: " + response.status)`.  
Catch and log the error.

Expected:
```
Fetch error: HTTP error: 404
```

---

### Requirement 5 — JSON.stringify and JSON.parse
Without any network calls:
1. Create a JavaScript object `const product = { id: 1, name: "Widget", price: 9.99 }`
2. Serialize it to a JSON string using `JSON.stringify` with indentation (2 spaces)
3. Log the string
4. Parse it back with `JSON.parse`
5. Log the parsed object's `name` property

Expected:
```
JSON string:
{
  "id": 1,
  "name": "Widget",
  "price": 9.99
}
Parsed name: Widget
```

---

## Files
- `index.html` — page that loads `script.js` as a module
- `script.js` — all Fetch and JSON logic

## Running Your Code
Open `index.html` in a browser. Check the console for all output.
