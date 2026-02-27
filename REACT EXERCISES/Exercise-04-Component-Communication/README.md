# Exercise 04 — Component Communication

## Overview
Build a **Shopping Cart** application that demonstrates how React components communicate through props and callbacks. The product catalog and cart panel are **siblings** — all shared state lives in the parent `App` component.

## Learning Objectives
- Lift state up to a common parent component
- Pass data **down** through props
- Pass **callback functions** as props (child-to-parent communication)
- Handle sibling-to-sibling communication via a shared parent
- Perform **immutable** state updates with arrays

## What You'll Build
A two-panel shopping interface:
- **Left panel**: Product catalog with 6 products, each with an "Add to Cart" button
- **Right panel**: Cart showing items, quantities (editable), a remove button, and a running total

## Getting Started
```bash
cd starter-code
npm install
npm run dev
```

## File Structure
```
src/
├── main.jsx
├── App.jsx                  ← Holds all state; renders ProductCatalog + Cart side-by-side
├── App.css
└── components/
    ├── ProductCatalog.jsx   ← Renders the grid of product cards
    ├── ProductCard.jsx      ← Single product card with "Add to Cart" button
    ├── Cart.jsx             ← Cart panel with item list and total
    └── CartItem.jsx         ← Single cart row with qty input and remove button
```

## TODO Checklist

Work through these in order. All TODOs are marked in the source files.

### `App.jsx`
- [ ] **TODO 1** — Declare `cartItems` state as an empty array `[]`
- [ ] **TODO 2** — Implement `addToCart(product)` — if product is already in cart, increment its quantity; otherwise add it with `quantity: 1`
- [ ] **TODO 3** — Implement `removeFromCart(id)` — filter out the item with the matching id
- [ ] **TODO 4** — Implement `updateQuantity(id, newQty)` — if `newQty < 1`, remove the item; otherwise update its quantity
- [ ] **TODO 5** — Compute `cartTotal` as a **derived value** (no `useState` — use `.reduce()`)
- [ ] **TODO 6** — Compute `itemCount` as a derived value (total of all quantities)
- [ ] **TODO 7** — Pass `onAddToCart={addToCart}` to `<ProductCatalog />`
- [ ] **TODO 8** — Pass `cartItems`, `onRemove`, `onUpdateQuantity`, and `cartTotal` to `<Cart />`

### `components/ProductCard.jsx`
- [ ] **TODO 9** — Call `onAddToCart(product)` when "Add to Cart" is clicked

### `components/Cart.jsx`
- [ ] **TODO 10** — Map over `cartItems` and render a `<CartItem />` for each
- [ ] **TODO 11** — Show an empty-state message when `cartItems.length === 0`

## Key Concepts

### Lifting State Up
When two sibling components need to share data, move that state to their closest common ancestor. The parent manages the data and passes it — and functions to mutate it — down as props.

```
App  (owns cartItems state)
├── ProductCatalog  (reads products; calls onAddToCart ↑)
└── Cart            (reads cartItems; calls onRemove ↑, onUpdateQuantity ↑)
```

### Immutable State Updates
Never mutate state directly. Use non-mutating array methods:

```js
// ✅ Correct — creates a brand-new array
setCartItems(prev => prev.map(item =>
  item.id === id ? { ...item, quantity: newQty } : item
))

// ❌ Wrong — mutates existing state object
cartItems.find(item => item.id === id).quantity = newQty
```

### Derived Values (No Extra useState)
`cartTotal` and `itemCount` are **computed** from `cartItems`. Storing them in separate state would create synchronisation bugs. Calculate them directly:

```js
const cartTotal = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
const itemCount = cartItems.reduce((sum, item) => sum + item.quantity, 0)
```

## Expected Behavior
1. Clicking "Add to Cart" adds a new item, or increments quantity if already present
2. The header badge updates in real time
3. Changing the quantity input immediately updates the cart
4. Setting quantity to 0 (or clicking Remove) removes the item
5. The cart total recalculates on every change
6. An empty-cart message appears when no items are in the cart
