// Day 14 Part 1 — ES6+: Classes, Prototypes, Destructuring, Spread/Rest, Modules, Map/Set
// Run: node index.js

"use strict";

console.log("╔═══════════════════════════════════════════════════════════════╗");
console.log("║  Day 14 Part 1 — ES6+ & OOP in JavaScript                   ║");
console.log("╚═══════════════════════════════════════════════════════════════╝\n");

demoClassesPrototypes();
demoES6Features();
demoMapAndSet();

// ─────────────────────────────────────────────────────────────
// 1. Classes, Prototypes & Inheritance
// ─────────────────────────────────────────────────────────────
function demoClassesPrototypes() {
  console.log("=== 1. Classes & Prototypal Inheritance ===");

  // Base class
  class Animal {
    #name;                        // private field (ES2022)
    #sound;
    static count = 0;             // static field

    constructor(name, sound) {
      this.#name  = name;
      this.#sound = sound;
      Animal.count++;
    }

    get name()  { return this.#name; }
    get sound() { return this.#sound; }

    speak() { return `${this.#name} says ${this.#sound}!`; }
    toString() { return `Animal(${this.#name})`; }
    static getCount() { return `${Animal.count} animals created`; }
  }

  // Subclass
  class Dog extends Animal {
    #breed;
    constructor(name, breed) {
      super(name, "Woof");
      this.#breed = breed;
    }
    fetch(item = "ball") { return `${this.name} fetches the ${item}!`; }
    toString() { return `Dog(${this.name}, ${this.#breed})`; }
  }

  class Cat extends Animal {
    constructor(name) { super(name, "Meow"); }
    purr() { return `${this.name} purrs contentedly…`; }
  }

  const rex  = new Dog("Rex", "Labrador");
  const luna = new Cat("Luna");
  console.log("  " + rex.speak());
  console.log("  " + rex.fetch("stick"));
  console.log("  " + luna.speak());
  console.log("  " + luna.purr());
  console.log("  " + Animal.getCount());
  console.log("  instanceof: rex instanceof Dog:", rex instanceof Dog, "| rex instanceof Animal:", rex instanceof Animal);

  // Prototype chain
  console.log("\n  Prototype chain:");
  console.log("  Dog.prototype → Animal.prototype → Object.prototype → null");
  console.log("  rex.__proto__ === Dog.prototype:", Object.getPrototypeOf(rex) === Dog.prototype);

  // Constructor function (pre-ES6 way)
  function Vehicle(make, model) { this.make = make; this.model = model; }
  Vehicle.prototype.describe = function() { return `${this.make} ${this.model}`; };
  const car = new Vehicle("Toyota", "Camry");
  console.log("\n  Constructor fn (pre-ES6):", car.describe());
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 2. ES6+ Features
// ─────────────────────────────────────────────────────────────
function demoES6Features() {
  console.log("=== 2. ES6+ Features ===");

  // Destructuring — Arrays
  const [a, b, ...rest] = [1, 2, 3, 4, 5];
  console.log("  Array destructuring:", a, b, "rest:", rest);

  // Destructuring — Objects
  const user = { name: "Alice", age: 28, role: "developer", dept: "Engineering" };
  const { name, age, ...others } = user;
  console.log("  Object destructuring:", name, age, "rest:", others);

  // Alias in destructuring
  const { name: userName, role: userRole = "guest" } = user;
  console.log("  With alias:", userName, userRole);

  // Nested destructuring
  const { address: { city, zip } = { city: "Unknown", zip: "00000" } } = {};
  console.log("  Nested (with default):", city, zip);

  // Spread — arrays & objects
  const arr1 = [1, 2, 3];
  const arr2 = [4, 5, 6];
  console.log("\n  Spread arrays:", [...arr1, ...arr2]);
  const obj1 = { a: 1, b: 2 };
  const obj2 = { c: 3, d: 4 };
  const merged = { ...obj1, ...obj2, extra: true };
  console.log("  Spread objects:", merged);

  // Rest parameters
  function stats(label, ...nums) {
    const avg = nums.reduce((s, n) => s + n, 0) / nums.length;
    return `${label}: avg=${avg.toFixed(1)} min=${Math.min(...nums)} max=${Math.max(...nums)}`;
  }
  console.log("\n  " + stats("Scores", 82, 95, 74, 88, 91));

  // Enhanced object literals
  const x = 10, y = 20;
  const point = { x, y, sum() { return this.x + this.y; }, [`coord_${x}`]: true };
  console.log("\n  Enhanced object literal:", point);

  // Default parameters
  function greet(name = "World", greeting = "Hello") {
    return `${greeting}, ${name}!`;
  }
  console.log("  Default params:", greet(), greet("Alice", "Hi"));

  // Optional chaining & Nullish coalescing
  const data = { user: { profile: { email: "a@b.com" } } };
  console.log("\n  Optional chaining:", data?.user?.profile?.email);
  console.log("  Optional chaining (missing):", data?.user?.address?.city ?? "No city");
  const val = null ?? "default";
  console.log("  Nullish coalescing:", val);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 3. Map & Set
// ─────────────────────────────────────────────────────────────
function demoMapAndSet() {
  console.log("=== 3. Map & Set ===");

  // Map — ordered key-value pairs, any type as key
  const map = new Map();
  map.set("name", "Alice");
  map.set(42, "answer");
  map.set(true, "boolean key");
  const objKey = { id: 1 };
  map.set(objKey, "object key works!");

  console.log("  Map size:", map.size);
  console.log("  map.get('name'):", map.get("name"));
  console.log("  map.get(42):", map.get(42));
  console.log("  map.has(true):", map.has(true));

  console.log("  Iterating Map:");
  map.forEach((v, k) => console.log(`    ${String(k).slice(0,20).padEnd(20)} → ${v}`));

  // Convert to array
  const entries = [...map.entries()];
  console.log("  [...map.entries()].length:", entries.length);

  // Set — unique values only
  const set = new Set([1, 2, 3, 2, 1, 4, 3, 5]);
  console.log("\n  Set from [1,2,3,2,1,4,3,5]:", [...set]);
  set.add(6);
  set.delete(3);
  console.log("  After add(6) delete(3):", [...set]);
  console.log("  set.has(2):", set.has(2));

  // Set for deduplication
  const words = ["apple", "banana", "apple", "cherry", "banana", "date"];
  const unique = [...new Set(words)];
  console.log("\n  Dedup words:", unique);

  // Set union, intersection, difference
  const setA = new Set([1, 2, 3, 4]);
  const setB = new Set([3, 4, 5, 6]);
  const union        = new Set([...setA, ...setB]);
  const intersection = new Set([...setA].filter(x => setB.has(x)));
  const difference   = new Set([...setA].filter(x => !setB.has(x)));
  console.log("  A:", [...setA], "  B:", [...setB]);
  console.log("  Union:", [...union]);
  console.log("  Intersection:", [...intersection]);
  console.log("  Difference (A - B):", [...difference]);

  console.log("\n✓ ES6+ Part 1 demo complete.");
}
