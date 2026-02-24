// Exercise 05: Higher-Order Array Methods — SOLUTION
// Run with: node script.js

const products = [
  { id: 1, name: "Laptop",     price: 999,  category: "Electronics", inStock: true  },
  { id: 2, name: "T-Shirt",    price: 25,   category: "Clothing",    inStock: true  },
  { id: 3, name: "Headphones", price: 149,  category: "Electronics", inStock: false },
  { id: 4, name: "Jeans",      price: 60,   category: "Clothing",    inStock: true  },
  { id: 5, name: "Tablet",     price: 499,  category: "Electronics", inStock: true  },
  { id: 6, name: "Jacket",     price: 120,  category: "Clothing",    inStock: false },
];

// ─────────────────────────────────────────────
// 1. forEach — iterates but returns undefined; use for side effects only
// ─────────────────────────────────────────────

console.log("--- forEach ---");
products.forEach(p => console.log(`${p.name}: $${p.price}`));

// ─────────────────────────────────────────────
// 2. map — same length array, value transformed
// ─────────────────────────────────────────────

console.log("\n--- map: names ---");
const productNames = products.map(p => p.name); // extract one property
console.log(productNames);

// ─────────────────────────────────────────────
// 3. map — transform prices
// ─────────────────────────────────────────────

console.log("\n--- map: 10% discount ---");
const discounted = products.map(p => ({
  name: p.name,
  // multiply by 0.9 and round to avoid floating-point imprecision
  price: Math.round(p.price * 0.9 * 10) / 10,
}));
discounted.forEach(d => console.log(d));

// ─────────────────────────────────────────────
// 4. filter — subset of matching elements
// ─────────────────────────────────────────────

console.log("\n--- filter: in stock ---");
const inStockProducts = products.filter(p => p.inStock);
console.log(inStockProducts.map(p => p.name));

// ─────────────────────────────────────────────
// 5. filter + map chained — two conditions
// ─────────────────────────────────────────────

console.log("\n--- filter+map: electronics in stock ---");
const electronicNames = products
  .filter(p => p.category === "Electronics" && p.inStock)
  .map(p => p.name);
console.log(electronicNames);

// ─────────────────────────────────────────────
// 6. reduce — aggregate to a single value
// ─────────────────────────────────────────────

console.log("\n--- reduce: total price ---");
const total = products.reduce((acc, p) => acc + p.price, 0); // 0 is the initial accumulator
console.log(`Total value: $${total}`);

// ─────────────────────────────────────────────
// 7. reduce — build an object (groupBy pattern)
// ─────────────────────────────────────────────

console.log("\n--- reduce: group by category ---");
const grouped = products.reduce((acc, p) => {
  if (!acc[p.category]) {
    acc[p.category] = []; // initialise the array the first time we see this category
  }
  acc[p.category].push(p.name);
  return acc; // always return the accumulator
}, {}); // start with an empty object
console.log(grouped);

// ─────────────────────────────────────────────
// 8. find — returns first match or undefined
// ─────────────────────────────────────────────

console.log("\n--- find: first product > $400 ---");
const expensive = products.find(p => p.price > 400); // stops at first match
console.log(expensive.name);

// ─────────────────────────────────────────────
// 9. findIndex — returns index of first match or -1
// ─────────────────────────────────────────────

console.log("\n--- findIndex: \"Jeans\" ---");
const jeansIndex = products.findIndex(p => p.name === "Jeans");
console.log(jeansIndex);

// ─────────────────────────────────────────────
// 10. some — true if AT LEAST ONE element passes
// ─────────────────────────────────────────────

console.log("\n--- some: any out of stock ---");
console.log(products.some(p => !p.inStock)); // short-circuits on first truthy

// ─────────────────────────────────────────────
// 11. every — true only if ALL elements pass
// ─────────────────────────────────────────────

console.log("\n--- every: all under $1000 ---");
console.log(products.every(p => p.price < 1000)); // short-circuits on first falsy

// ─────────────────────────────────────────────
// 12. Chaining
// ─────────────────────────────────────────────

console.log("\n--- chain: in stock, > $50, names, sorted ---");
const result = products
  .filter(p => p.inStock && p.price > 50) // combine both conditions in one filter
  .map(p => p.name)
  .sort(); // alphabetical sort is fine for strings
console.log(result);
