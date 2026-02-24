# Day 12 Application — JavaScript Fundamentals: Shopping Cart

## Overview

You'll build a **Shopping Cart** — a browser-based app built with vanilla JavaScript. No frameworks, no libraries. Products are defined in a JS array, and users can add items, update quantities, and see a running total.

---

## Learning Goals

- Declare variables with `let`, `const`, and understand scope
- Work with arrays and array methods (`push`, `find`, `filter`, `map`, `reduce`)
- Use template literals for dynamic HTML generation
- Write functions using declaration, expression, and arrow syntax
- Understand closures, hoisting, and `this`
- Use control flow and error handling

---

## Prerequisites

- A browser — open `index.html` directly, no install needed

---

## Project Structure

```
starter-code/
├── index.html       ← provided, do not modify
├── styles.css       ← provided, do not modify
└── cart.js          ← TODO: complete all functions
```

---

## Part 1 — Data & Setup in `cart.js`

**Task 1 — Product catalog (const)**  
The `products` array is provided at the top. Each product has `id`, `name`, `price`, `emoji`.  
Declare `let cart = []` — an array of `{ productId, quantity }` objects.

**Task 2 — Hoisting awareness**  
Call `initCart()` at the top of the file before the function is declared.  
Add a comment explaining why this works (function declaration hoisting).

---

## Part 2 — Cart Functions

**Task 3 — `addToCart(productId)` (function declaration)**  
- Use `cart.find()` to check if the product is already in the cart.
- If yes: increment `quantity`.
- If no: push `{ productId, quantity: 1 }` to `cart`.
- Call `renderCart()` after updating.

**Task 4 — `removeFromCart(productId)` (arrow function)**  
Use `cart.filter()` to return a new array without the specified product.  
Assign result back to `cart`. Call `renderCart()`.

**Task 5 — `updateQuantity(productId, newQty)` (function expression)**  
Assign `const updateQuantity = function(productId, newQty) {...}`.  
- If `newQty <= 0`, remove item from cart.
- Otherwise, find the item and update its quantity.
- Call `renderCart()`.

**Task 6 — `getTotal()` (arrow function)**  
Use `cart.reduce()` to calculate the total price.  
For each cart item, look up the product price using `products.find()`.  
Return the total as a `number`.

---

## Part 3 — Rendering

**Task 7 — `renderCart()` (function declaration)**  
Use `cart.map()` to build an HTML string for each cart item using **template literals**.  
Each item should show: emoji, name, quantity controls (- / +), price, remove button.  
Use `document.getElementById('cart-items').innerHTML = ...` to update the DOM.  
Call `renderTotal()`.

**Task 8 — `renderTotal()` (arrow function)**  
Display the total price formatted with `toFixed(2)`.  
Show an "Empty cart" message if `cart.length === 0`.

---

## Part 4 — Closures & Scope

**Task 9 — `createDiscountCalculator(discountPercent)` (closure)**  
Return a function that accepts a `price` and returns the discounted price.  
```js
const tenPercentOff = createDiscountCalculator(10);
tenPercentOff(50); // returns 45
```
Call this in `renderTotal()` to apply a discount if the cart total exceeds $50.

**Task 10 — Strict mode**  
Add `'use strict';` at the top of `cart.js`. Attempt to use an undeclared variable in a comment block and explain what would happen without strict mode.

---

## Stretch Goals

1. Add `localStorage` persistence — save and load `cart` from `localStorage`.
2. Add a `clearCart()` function with a confirmation prompt.
3. Show a "Best value" badge on the highest-priced item in the cart.

---

## Submission Checklist

- [ ] `const` used for products catalog, `let` for cart (not `var`)
- [ ] `find`, `filter`, `map`, `reduce` all used
- [ ] Template literals used for HTML generation
- [ ] Function declaration, function expression, and arrow function all used
- [ ] Closure `createDiscountCalculator` implemented correctly
- [ ] Hoisting demonstrated and commented
- [ ] `'use strict'` added
- [ ] Cart renders correctly in the browser
