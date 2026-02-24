// ============================================================
// Day 14 — Part 1  |  02-es6-features.js
// ES6+ Features: Default Params, Spread/Rest, Destructuring,
// Enhanced Object Literals
// ============================================================

"use strict";

// ============================================================
// 1. DEFAULT PARAMETERS
// ============================================================

// Only `undefined` triggers the default — null, 0, "" do NOT
function greet(name = "World", punctuation = "!") {
  return `Hello, ${name}${punctuation}`;
}

console.log(greet());               // Hello, World!
console.log(greet("Alice"));        // Hello, Alice!
console.log(greet("Bob", "."));     // Hello, Bob.
console.log(greet(undefined, "?")); // Hello, World?  ← undefined triggers default
console.log(greet(null, "?"));      // Hello, null?   ← null does NOT trigger default

// Default can reference earlier parameters
function createPoint(x = 0, y = x) {
  return { x, y };
}
console.log(createPoint());     // { x: 0, y: 0 }
console.log(createPoint(5));    // { x: 5, y: 5 }
console.log(createPoint(3, 7)); // { x: 3, y: 7 }

// Default can be any expression — even a function call
function generateId() {
  return Math.floor(Math.random() * 1000);
}

function createUser(name, id = generateId()) {
  return { name, id };
}
console.log(createUser("Alice")); // id is randomly generated each call


// ============================================================
// 2. REST PARAMETERS  (...)
// ============================================================

// Rest collects remaining arguments into a real Array
function sum(...numbers) {
  return numbers.reduce((total, n) => total + n, 0);
}
console.log(sum(1, 2, 3));           // 6
console.log(sum(10, 20, 30, 40));    // 100

// Rest must be the LAST parameter
function buildMessage(prefix, separator, ...words) {
  return prefix + words.join(separator);
}
console.log(buildMessage(">>", "-", "Hello", "World", "!"));
// >> Hello-World-!

// Compare with the old `arguments` object (NOT an array):
function oldWay() {
  // arguments is array-like but has no .map, .filter, etc.
  console.log(Array.from(arguments).join(", "));
}
oldWay(1, 2, 3); // 1, 2, 3


// ============================================================
// 3. SPREAD OPERATOR  (...)
// ============================================================
// Same syntax as rest, but used at the CALL site (expands iterables)

// --- 3a. Spread with Arrays ---
const nums1 = [1, 2, 3];
const nums2 = [4, 5, 6];

const combined = [...nums1, ...nums2];
console.log(combined); // [1, 2, 3, 4, 5, 6]

// Insert in the middle
const withMiddle = [...nums1, 99, ...nums2];
console.log(withMiddle); // [1, 2, 3, 99, 4, 5, 6]

// Shallow copy of an array
const original = [1, 2, 3];
const copy = [...original];
copy.push(4);
console.log(original); // [1, 2, 3] — not affected
console.log(copy);     // [1, 2, 3, 4]

// Pass array elements as individual arguments
function add(a, b, c) {
  return a + b + c;
}
const values = [1, 2, 3];
console.log(add(...values)); // 6

// --- 3b. Spread with Objects (ES2018) ---
const defaults = { theme: "light", lang: "en", fontSize: 14 };
const userPrefs = { lang: "fr", fontSize: 18 };

// Merge objects — later keys override earlier ones
const config = { ...defaults, ...userPrefs };
console.log(config);
// { theme: "light", lang: "fr", fontSize: 18 }

// Shallow clone an object
const original2 = { a: 1, b: { c: 2 } };
const clone = { ...original2 };
clone.a = 99;
clone.b.c = 99; // ⚠️ still shares nested reference!
console.log(original2.a); // 1 (primitive — OK)
console.log(original2.b.c); // 99 (object — shared!)

// Add or override a single property immutably
const user = { name: "Alice", role: "viewer" };
const admin = { ...user, role: "admin" };
console.log(admin); // { name: "Alice", role: "admin" }


// ============================================================
// 4. ARRAY DESTRUCTURING
// ============================================================

// Basic
const [first, second, third] = [10, 20, 30];
console.log(first, second, third); // 10 20 30

// Skip elements with commas
const [a, , b] = [1, 2, 3];
console.log(a, b); // 1 3

// Default values in destructuring
const [x = 0, y = 0, z = 0] = [7, 8];
console.log(x, y, z); // 7 8 0

// Rest in destructuring
const [head, ...tail] = [1, 2, 3, 4, 5];
console.log(head); // 1
console.log(tail); // [2, 3, 4, 5]

// Swap variables — elegant!
let p = 1, q = 2;
[p, q] = [q, p];
console.log(p, q); // 2 1

// Destructuring from function return value
function getCoords() {
  return [40.7128, -74.0060]; // NYC lat/lng
}
const [lat, lng] = getCoords();
console.log(`Lat: ${lat}, Lng: ${lng}`);

// Nested array destructuring
const matrix = [[1, 2], [3, 4]];
const [[r1c1, r1c2], [r2c1, r2c2]] = matrix;
console.log(r1c1, r1c2, r2c1, r2c2); // 1 2 3 4


// ============================================================
// 5. OBJECT DESTRUCTURING
// ============================================================

const person = { name: "Alice", age: 30, city: "New York" };

// Basic
const { name, age } = person;
console.log(name, age); // Alice 30

// Rename with alias
const { name: personName, city: personCity } = person;
console.log(personName, personCity); // Alice New York

// Default values
const { country = "Unknown", name: pName } = person;
console.log(country, pName); // Unknown Alice

// Rename + default
const { role: userRole = "viewer" } = person;
console.log(userRole); // viewer

// Nested object destructuring
const employee = {
  id: 1,
  profile: {
    firstName: "Bob",
    lastName: "Smith",
    address: {
      city: "Chicago",
      zip: "60601"
    }
  }
};

const { profile: { firstName, address: { city } } } = employee;
console.log(firstName, city); // Bob Chicago

// Destructuring in function parameters (very common in React/Node!)
function displayUser({ name, age, role = "guest" }) {
  console.log(`${name} (${age}) — Role: ${role}`);
}
displayUser({ name: "Alice", age: 30 });         // Alice (30) — Role: guest
displayUser({ name: "Bob", age: 25, role: "admin" }); // Bob (25) — Role: admin

// Mixed destructuring: object with arrays
const data = {
  status: "ok",
  coords: [51.5, -0.1],
  tags: ["london", "weather"]
};

const { status, coords: [latitude, longitude], tags: [primaryTag] } = data;
console.log(status, latitude, longitude, primaryTag);
// ok 51.5 -0.1 london

// Rest in object destructuring
const { name: n, ...rest } = person;
console.log(n);    // Alice
console.log(rest); // { age: 30, city: "New York" }


// ============================================================
// 6. ENHANCED OBJECT LITERALS
// ============================================================

// --- 6a. Shorthand Properties ---
// When variable name matches key name, you can omit the value
const username = "alice";
const score = 95;

// Old way:
const playerOld = { username: username, score: score };

// New way:
const player = { username, score };
console.log(player); // { username: "alice", score: 95 }

// --- 6b. Shorthand Methods ---
// Old way:
const calcOld = {
  add: function(a, b) { return a + b; },
};

// New way:
const calc = {
  add(a, b) { return a + b; },
  subtract(a, b) { return a - b; },
  multiply(a, b) { return a * b; },
};
console.log(calc.add(2, 3));      // 5
console.log(calc.subtract(9, 4)); // 5

// --- 6c. Computed Property Names ---
// Dynamically compute a key using []
const prefix = "user";
const dynamicObj = {
  [`${prefix}Name`]: "Alice",
  [`${prefix}Age`]: 30,
  [`${prefix}Role`]: "admin",
};
console.log(dynamicObj);
// { userName: "Alice", userAge: 30, userRole: "admin" }

// Practical: build lookup table dynamically
const fields = ["firstName", "lastName", "email"];
const emptyForm = fields.reduce((acc, field) => {
  return { ...acc, [field]: "" };
}, {});
console.log(emptyForm);
// { firstName: "", lastName: "", email: "" }

// --- 6d. Getters and Setters in Literals ---
const temperature = {
  _celsius: 0,
  get fahrenheit() {
    return this._celsius * 9 / 5 + 32;
  },
  set fahrenheit(value) {
    this._celsius = (value - 32) * 5 / 9;
  },
  get celsius() {
    return this._celsius;
  },
  set celsius(value) {
    this._celsius = value;
  }
};

temperature.celsius = 100;
console.log(temperature.fahrenheit); // 212

temperature.fahrenheit = 32;
console.log(temperature.celsius); // 0


// ============================================================
// 7. PUTTING IT ALL TOGETHER — Realistic Example
// ============================================================

// Config builder using all the above features
function createApiConfig(
  baseURL,
  {
    method = "GET",
    headers = {},
    timeout = 5000,
    ...options
  } = {}
) {
  const defaultHeaders = {
    "Content-Type": "application/json",
    Accept: "application/json",
  };

  return {
    baseURL,
    method,
    headers: { ...defaultHeaders, ...headers },
    timeout,
    ...options,
    // Computed key to store a timestamp
    [`_created_${method.toLowerCase()}`]: new Date().toISOString(),
  };
}

const config = createApiConfig("https://api.example.com", {
  method: "POST",
  headers: { Authorization: "Bearer abc123" },
  retries: 3,
});

console.log(config);
/*
{
  baseURL: "https://api.example.com",
  method: "POST",
  headers: { "Content-Type": "application/json", Accept: "...", Authorization: "Bearer abc123" },
  timeout: 5000,
  retries: 3,
  _created_post: "2024-..."
}
*/
