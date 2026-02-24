# Exercise 06: Connecting React to Redux with useSelector and useDispatch

## Objective
Connect a React component to a Redux store using the `<Provider>`, `useSelector`, and `useDispatch` hooks from React-Redux.

## Background
After building the store in isolation (Ex 05), the next step is wiring it into React. The `<Provider>` component makes the store available to every component in the tree. `useSelector` subscribes a component to a slice of state and re-renders it when that slice changes. `useDispatch` returns the `dispatch` function so components can send actions. In this exercise you will build a fully interactive shopping cart UI backed by the Redux store from Ex 05.

## Requirements
1. Reuse the `store/`, `store/cartSlice.js`, and `store/store.js` files from Exercise 05 (or copy them).
2. Wrap your `<App />` with `<Provider store={store}>` in `index.jsx`.
3. Build a `<ProductList>` component that:
   - Renders a hardcoded list of 3 products (id, name, price).
   - Has an "Add to Cart" button for each product.
   - Clicking the button dispatches the `addItem` action using `useDispatch`.
4. Build a `<CartSummary>` component that:
   - Uses `useSelector` to read `state.cart.items` and `state.cart.totalQuantity`.
   - Displays the total quantity as "Cart: N items".
   - Lists each cart item with its name and quantity.
   - Has a "Remove" button next to each item that dispatches `removeItem`.
   - Has a "Clear Cart" button that dispatches `clearCart`.
5. Both components must be displayed in `App.jsx` at the same time.
6. State must update reactively — clicking "Add to Cart" immediately updates the cart summary without any manual refresh.

## Hints
- Import `Provider` from `'react-redux'` and wrap your root render: `<Provider store={store}><App /></Provider>`.
- `const dispatch = useDispatch();` — call `dispatch(addItem(product))` inside your click handler.
- `const items = useSelector(state => state.cart.items);` — the selector function receives the entire Redux state.
- `useSelector` accepts a selector function — you can derive computed values directly inside it, e.g. `state.cart.totalQuantity`.

## Expected Output
```
Products
[Laptop $999]  [Add to Cart]
[Mouse $29]    [Add to Cart]
[Keyboard $79] [Add to Cart]

────────────────────────
Cart: 2 items
- Laptop x2  [Remove]
- Mouse x1   [Remove]
[Clear Cart]
```
After clicking "Remove" next to Laptop:
```
Cart: 1 items
- Mouse x1  [Remove]
[Clear Cart]
```
