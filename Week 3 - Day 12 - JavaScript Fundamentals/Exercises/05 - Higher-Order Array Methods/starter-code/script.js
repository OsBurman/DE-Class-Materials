// Exercise 05: Higher-Order Array Methods
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
// 1. forEach — log "Name: $price" for each product
// ─────────────────────────────────────────────

console.log("--- forEach ---");
// TODO: Use products.forEach() to log each product as "Laptop: $999"


// ─────────────────────────────────────────────
// 2. map — extract product names into an array
// ─────────────────────────────────────────────

console.log("\n--- map: names ---");
// TODO: const productNames = products.map(...)
//       Log productNames


// ─────────────────────────────────────────────
// 3. map — apply 10% discount to all prices
// ─────────────────────────────────────────────

console.log("\n--- map: 10% discount ---");
// TODO: const discounted = products.map(p => ({ name: p.name, price: ... }))
//       Apply price * 0.9 and round to 2 decimal places with Math.round or toFixed
//       Log each discounted item


// ─────────────────────────────────────────────
// 4. filter — only products that are in stock
// ─────────────────────────────────────────────

console.log("\n--- filter: in stock ---");
// TODO: const inStockProducts = products.filter(...)
//       Log the names of inStockProducts


// ─────────────────────────────────────────────
// 5. filter + map — Electronics that are in stock
// ─────────────────────────────────────────────

console.log("\n--- filter+map: electronics in stock ---");
// TODO: const electronicNames = products.filter(...).map(...)
//       Filter for Electronics AND inStock, then map to names
//       Log electronicNames


// ─────────────────────────────────────────────
// 6. reduce — total price of all products
// ─────────────────────────────────────────────

console.log("\n--- reduce: total price ---");
// TODO: const total = products.reduce((acc, p) => acc + p.price, 0)
//       Log: `Total value: $${total}`


// ─────────────────────────────────────────────
// 7. reduce — group names by category
// ─────────────────────────────────────────────

console.log("\n--- reduce: group by category ---");
// TODO: const grouped = products.reduce((acc, p) => {
//         // If acc[p.category] doesn't exist yet, initialise it as an empty array
//         // Then push p.name into it
//         // Return acc
//       }, {})
//       Log grouped


// ─────────────────────────────────────────────
// 8. find — first product with price > $400
// ─────────────────────────────────────────────

console.log("\n--- find: first product > $400 ---");
// TODO: const expensive = products.find(p => p.price > 400)
//       Log expensive.name


// ─────────────────────────────────────────────
// 9. findIndex — index of "Jeans"
// ─────────────────────────────────────────────

console.log("\n--- findIndex: \"Jeans\" ---");
// TODO: const jeansIndex = products.findIndex(p => p.name === "Jeans")
//       Log jeansIndex


// ─────────────────────────────────────────────
// 10. some — any product out of stock?
// ─────────────────────────────────────────────

console.log("\n--- some: any out of stock ---");
// TODO: Log products.some(p => !p.inStock)


// ─────────────────────────────────────────────
// 11. every — all products under $1000?
// ─────────────────────────────────────────────

console.log("\n--- every: all under $1000 ---");
// TODO: Log products.every(p => p.price < 1000)


// ─────────────────────────────────────────────
// 12. Chaining — in stock, > $50, names, sorted alphabetically
// ─────────────────────────────────────────────

console.log("\n--- chain: in stock, > $50, names, sorted ---");
// TODO: Chain filter → filter (or combine conditions) → map → sort
//       Result should be: [ 'Jeans', 'Laptop', 'Tablet' ]
