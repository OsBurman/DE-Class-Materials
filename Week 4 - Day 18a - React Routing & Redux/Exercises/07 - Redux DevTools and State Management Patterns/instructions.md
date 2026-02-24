# Exercise 07: Redux DevTools and State Management Patterns

## Objective
Build a multi-slice Redux store, use the Redux DevTools Extension to inspect and time-travel through state, and apply the selector pattern for derived state.

## Background
Real applications manage multiple independent slices of state (e.g., user auth, cart, UI flags) rather than one big object. The Redux DevTools browser extension lets you inspect every dispatched action, see the resulting state diff, and replay ("time-travel") to any point in history. In this exercise you will add a second slice for UI notifications, compose both slices into one store, and practice using selectors to derive computed values.

## Requirements
1. Reuse the `cartSlice.js` from Exercise 06 (copy it unchanged).
2. Create a new `store/notificationsSlice.js` using `createSlice`:
   - `name: 'notifications'`
   - `initialState: { messages: [], unreadCount: 0 }`
   - Reducers:
     - `addNotification(state, action)` — `action.payload` is a string message. Push `{ id: Date.now(), text: action.payload, read: false }` to `messages`. Increment `unreadCount`.
     - `markAllRead(state)` — set every message's `read` to `true`. Reset `unreadCount` to `0`.
     - `clearNotifications(state)` — reset `messages` to `[]` and `unreadCount` to `0`.
3. Register both slices in `store/store.js` (keys `cart` and `notifications`).
4. Create a `selectCartTotal` **selector function** in `store/cartSlice.js`:
   - `export const selectCartTotal = state => state.cart.items.reduce((sum, item) => sum + item.price * item.quantity, 0);`
5. Build a `<NotificationBell>` component that:
   - Shows "🔔 N unread" using `useSelector` to read `unreadCount`.
   - Has an "Add Notification" button that dispatches a sample notification message.
   - Has a "Mark All Read" button that dispatches `markAllRead`.
6. Display `<CartSummary>` (from Ex 06) alongside `<NotificationBell>` in `App.jsx`.
7. In `<CartSummary>`, add a line that shows the **cart total** (sum of price × quantity for all items) using the `selectCartTotal` selector with `useSelector`.
8. Open the app in the browser and use Redux DevTools to verify every action is logged.

## Hints
- Install Redux DevTools as a browser extension (Chrome/Firefox). Redux Toolkit's `configureStore` enables DevTools automatically in development — no extra config needed.
- Derived / computed state (like cart total) belongs in **selector functions**, not in reducers. Keep reducer state normalized.
- `selectCartTotal` is used like: `const total = useSelector(selectCartTotal);`
- Multiple `useSelector` calls in one component are fine — each subscribes independently.

## Expected Output
```
🔔 2 unread   [Add Notification] [Mark All Read]

Cart: 2 items | Total: $2,027.00
- Laptop x2    [Remove]
- Mouse x1     [Remove]
[Clear Cart]
```

After "Mark All Read":
```
🔔 0 unread   [Add Notification] [Mark All Read]
```

(Redux DevTools panel shows action history like:)
```
@@INIT
cart/addItem
cart/addItem
notifications/addNotification
notifications/markAllRead
```
