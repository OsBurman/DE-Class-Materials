// Exercise 04: Arrays and Core Array Methods — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Creating Arrays
// ─────────────────────────────────────────────

let fruits = ["apple", "banana", "cherry"]; // using let so we can mutate it
console.log(`fruits: [${fruits.map(f => `'${f}'`).join(", ")}]  length: ${fruits.length}`);

const mixed = [42, "hello", true, [1, 2]]; // mixed types including nested array
console.log(`mixed: [${mixed.map(m => JSON.stringify(m)).join(", ")}]  length: ${mixed.length}`);

// ─────────────────────────────────────────────
// PART 2: Accessing and Updating Elements
// ─────────────────────────────────────────────

console.log(`\nfruits[0]: ${fruits[0]}`);
console.log(`fruits[last]: ${fruits[fruits.length - 1]}`);

fruits[1] = "blueberry"; // direct index assignment replaces the element
console.log(`After update fruits[1]:`, fruits);

// ─────────────────────────────────────────────
// PART 3: Adding and Removing (push/pop/unshift/shift)
// ─────────────────────────────────────────────

// push returns the new length of the array
const afterPush = fruits.push("date");
console.log(`\npush "date" → new length: ${afterPush}  fruits:`, fruits);

// pop returns the removed element
const popped = fruits.pop();
console.log(`pop → removed: ${popped}           fruits:`, fruits);

// unshift returns the new length
const afterUnshift = fruits.unshift("avocado");
console.log(`unshift "avocado" → length: ${afterUnshift} fruits:`, fruits);

// shift returns the removed element
const shifted = fruits.shift();
console.log(`shift → removed: ${shifted}      fruits:`, fruits);

// ─────────────────────────────────────────────
// PART 4: splice — Insert, Remove, Replace
// ─────────────────────────────────────────────

// splice(startIndex, deleteCount) → returns array of removed elements; mutates original
const removed = fruits.splice(1, 1); // remove 1 element at index 1 ("blueberry")
console.log(`\nsplice remove at index 1 → removed:`, removed, " fruits:", fruits);

// splice(startIndex, 0, ...items) → insert without removing
fruits.splice(1, 0, "elderberry", "fig");
console.log(`splice insert at index 1 → fruits:`, fruits);

// splice(startIndex, 1, newValue) → replace 1 element at that index
fruits.splice(2, 1, "grape");
console.log(`splice replace index 2   → fruits:`, fruits);

// ─────────────────────────────────────────────
// PART 5: Non-Mutating Methods
// ─────────────────────────────────────────────

// slice returns a shallow copy of a portion — does NOT mutate original
const sub = fruits.slice(1, 3); // indices 1 and 2 (end is exclusive)
console.log(`\nslice(1,3):`, sub);
console.log(`fruits still:`, fruits);

// concat returns a NEW array — does NOT mutate original
const combined = fruits.concat(["honeydew", "kiwi"]);
console.log(`concat:`, combined);

// indexOf returns the first index of the value, or -1 if absent
console.log(`indexOf "cherry": ${fruits.indexOf("cherry")}`);

// includes returns a boolean
console.log(`includes "fig": ${fruits.includes("fig")}`); // was spliced in then replaced

// ─────────────────────────────────────────────
// PART 6: reverse and sort
// ─────────────────────────────────────────────

// reverse() mutates the array in place
fruits.reverse();
console.log(`\nreverse:`, fruits);
fruits.reverse(); // reverse back to restore original order
console.log(`reversed back:`, fruits);

const nums = [10, 1, 21, 2];

// Default sort converts elements to strings and compares UTF-16 code units
// "10" < "2" alphabetically because "1" < "2", so 10 comes before 2
const numsCopy1 = [...nums];
numsCopy1.sort();
console.log(`\nnums default sort:`, numsCopy1, " (lexicographic — \"10\" < \"2\" as strings)");

// Numeric comparator: (a, b) => a - b sorts in ascending numeric order
const numsCopy2 = [...nums];
numsCopy2.sort((a, b) => a - b);
console.log(`nums numeric sort:`, numsCopy2);

// ─────────────────────────────────────────────
// PART 7: Spread and Destructuring
// ─────────────────────────────────────────────

// Spread creates a shallow copy — fruitsCopy and fruits are separate arrays
const fruitsCopy = [...fruits];
fruitsCopy.push("ADDED");
console.log(`\nfruitsCopy after mutation:`, fruitsCopy);
console.log(`fruits unchanged:`, fruits);

// Array destructuring: bind first, second, then collect the rest with rest syntax
const [first, second, ...rest] = fruits;
console.log(`first: ${first}`);
console.log(`second: ${second}`);
console.log(`rest:`, rest);
