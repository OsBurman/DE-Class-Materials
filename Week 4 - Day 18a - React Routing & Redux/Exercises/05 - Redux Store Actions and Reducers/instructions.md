# Exercise 05: Redux Store Actions and Reducers

## Objective
Set up a Redux Toolkit store with `createSlice` and `configureStore`, then manually dispatch actions and read state — without React yet.

## Background
Before wiring Redux to React, it's essential to understand the core loop: the **store** holds state, **actions** describe what happened, and **reducers** define how state changes in response. Redux Toolkit's `createSlice` simplifies this by generating action creators and a reducer together. In this exercise you will build and test a shopping cart slice entirely in plain JavaScript (no React).

## Requirements
1. Create a file `store/cartSlice.js` using `createSlice` from `@reduxjs/toolkit` with:
   - `name: 'cart'`
   - `initialState: { items: [], totalQuantity: 0 }`
   - Three reducers:
     - `addItem(state, action)` — `action.payload` is `{ id, name, price }`. If the item already exists (match by `id`), increment its `quantity` by 1. Otherwise push `{ ...payload, quantity: 1 }` to `items`. Always increment `totalQuantity` by 1.
     - `removeItem(state, action)` — `action.payload` is an item `id`. Remove that item from `items` and decrement `totalQuantity` by that item's quantity.
     - `clearCart(state)` — reset `items` to `[]` and `totalQuantity` to `0`.
2. Create a file `store/store.js` using `configureStore` that includes the `cartSlice.reducer` under the key `cart`.
3. Export the action creators (`addItem`, `removeItem`, `clearCart`) from `cartSlice.js`.
4. In `index.js` (entry file), demonstrate the store works by:
   - Dispatching `addItem` twice for a "Laptop" (id: 1, price: 999)
   - Dispatching `addItem` once for a "Mouse" (id: 2, price: 29)
   - Logging `store.getState().cart` to the console
   - Dispatching `removeItem` for the Laptop
   - Logging the state again
   - Dispatching `clearCart`
   - Logging the state a final time

## Hints
- `createSlice` returns an object with `.reducer` and `.actions`. Destructure them: `export const { addItem, removeItem, clearCart } = cartSlice.actions;`
- Redux Toolkit uses Immer under the hood — you can safely mutate `state.items` directly inside reducers (e.g. `state.items.push(...)`, `state.totalQuantity++`).
- `store.dispatch(addItem({ id: 1, name: 'Laptop', price: 999 }))` — the action creator wraps the payload automatically.
- `Array.prototype.findIndex` is useful for checking if an item with a given `id` already exists.

## Expected Output
```
After adding Laptop x2 and Mouse x1:
{ items: [ { id: 1, name: 'Laptop', price: 999, quantity: 2 }, { id: 2, name: 'Mouse', price: 29, quantity: 1 } ], totalQuantity: 3 }

After removing Laptop:
{ items: [ { id: 2, name: 'Mouse', price: 29, quantity: 1 } ], totalQuantity: 1 }

After clearing cart:
{ items: [], totalQuantity: 0 }
```
