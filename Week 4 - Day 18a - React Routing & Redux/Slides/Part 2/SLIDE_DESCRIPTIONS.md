# Week 4 - Day 18a: React Routing & Redux
## Part 2 Slide Descriptions

---

### Slide 1: Part 2 Title Slide
**Title:** React Routing & Redux
**Subtitle:** Part 2 — Redux Fundamentals, Redux Toolkit, useSelector, useDispatch & State Patterns
**Visual:** Data flow diagram: Component → dispatch(action) → reducer(state, action) → new state → Component re-renders
**Notes:** Opening slide for Part 2. Transition from routing (navigating between pages) to Redux (managing state that lives across the whole app).

---

### Slide 2: The State Management Spectrum
**Title:** When Do You Need Redux?
**Visual:** Escalating complexity diagram with three tiers

**Tier 1 — Local State (`useState`)**
```
✅ Use when: only one component cares about it
Examples: form input value, dropdown open/closed, hover state
```

**Tier 2 — Shared State (`useContext`)**
```
✅ Use when: a few related components need the same data
✅ Stable data that doesn't change frequently
Examples: current theme, logged-in user, language preference
⚠️ Not for: data that updates many times per second
```

**Tier 3 — Global State (Redux)**
```
✅ Use when:
  - Many unrelated components need the same data
  - State changes are complex (multiple fields update together)
  - You need to trace every state change for debugging
  - Data changes frequently (cart, notifications, real-time feeds)
Examples: shopping cart, user notifications, app-wide loading state
```

**Caption:** "Context is a broadcast channel. Redux is a database with an audit trail."
**Notes:** The key message: Redux is not a replacement for `useState` or `useContext`. They solve different problems. The decision guide is in Slide 14. For now, plant the idea that Redux earns its complexity when state changes are frequent, complex, and need traceability.

---

### Slide 3: Redux Core Concepts — Store, Action, Reducer
**Title:** The Three Pieces of Redux
**Visual:** Three annotated definition boxes

**Store:**
```
The store is the single object that holds ALL of your app's global state.
One store. One source of truth.

store.getState() → { cart: [...], user: null, notifications: [] }
```

**Action:**
```
An action is a plain JavaScript object that describes what happened.
It always has a 'type' string. It may carry a 'payload'.

{ type: 'cart/addItem', payload: { id: 42, name: 'Laptop', price: 999 } }
{ type: 'cart/removeItem', payload: 42 }
{ type: 'user/logout' }
```

**Reducer:**
```
A reducer is a PURE FUNCTION that takes the current state and an action,
and returns the NEW state.

(currentState, action) → newState

Rules:
  - Never mutate state directly (return a new object)
  - No side effects (no API calls, no random numbers)
  - Same inputs ALWAYS produce same output
```

**Notes:** Emphasize the reducer rules. "Never mutate state directly" is the most violated rule. Redux Toolkit (next slide) makes this easier by using Immer under the hood, which lets you write mutating-looking code that actually creates a new state object.

---

### Slide 4: The Redux Data Flow
**Title:** Unidirectional Data Flow — The Redux Loop
**Visual:** Circular flow diagram with code snippets at each step

```
┌─────────────────────────────────────────────────┐
│                                                   │
│  1. User clicks "Add to Cart"                     │
│          ↓                                        │
│  2. Component dispatches an action:               │
│     dispatch({ type: 'cart/addItem',             │
│                payload: product })                │
│          ↓                                        │
│  3. Redux passes (currentState, action)           │
│     to the cart REDUCER                          │
│          ↓                                        │
│  4. Reducer returns NEW state object              │
│          ↓                                        │
│  5. Store saves the new state                     │
│          ↓                                        │
│  6. All subscribed components re-render           │
│     (NavbarComponent sees cart.length updated)   │
│          ↓ (loop back to 1)                       │
└─────────────────────────────────────────────────┘
```

**The key insight box:**
> State only ever changes through this one path. Every change is an explicit action with a type. This makes debugging predictable: you can replay every action to recreate any state.

**Notes:** This diagram is the heart of Redux. Draw it out slowly. The unidirectional flow is what makes Redux debuggable. In contrast, with direct state mutation (or even `useContext` with complex setState), it's easy to lose track of what caused a state change.

---

### Slide 5: Why Redux Toolkit?
**Title:** Vanilla Redux vs Redux Toolkit (RTK)
**Visual:** Side-by-side code comparison

**Vanilla Redux (lots of boilerplate):**
```javascript
// action type constant
const ADD_ITEM = 'cart/addItem';

// action creator
const addItem = (product) => ({ type: ADD_ITEM, payload: product });

// reducer — must manually spread to avoid mutation
function cartReducer(state = [], action) {
  switch (action.type) {
    case ADD_ITEM:
      return [...state, action.payload];   // new array
    default:
      return state;
  }
}

// store setup
import { createStore, combineReducers } from 'redux';
const rootReducer = combineReducers({ cart: cartReducer });
const store = createStore(rootReducer);
```

**Redux Toolkit (same result, much less code):**
```javascript
import { createSlice, configureStore } from '@reduxjs/toolkit';

const cartSlice = createSlice({
  name: 'cart',
  initialState: [],
  reducers: {
    addItem: (state, action) => {
      state.push(action.payload);   // looks like mutation — RTK handles immutability!
    }
  }
});
```

**Note box:** "Redux Toolkit is the **official recommended approach**. It's not a different library — it's Redux with all best practices baked in. Always use RTK for new projects."
**Notes:** Install: `npm install @reduxjs/toolkit react-redux`. The "looks like mutation" part with `state.push()` is made safe by RTK using the Immer library internally — it intercepts the mutations and produces a new immutable state.

---

### Slide 6: createSlice — Actions and Reducer in One
**Title:** `createSlice` — The Heart of Redux Toolkit
**Visual:** Annotated `createSlice` call

```javascript
import { createSlice } from '@reduxjs/toolkit';

const cartSlice = createSlice({
  name: 'cart',                  // ← used as prefix for action types: 'cart/addItem'

  initialState: {                // ← the state shape this slice manages
    items: [],
    total: 0
  },

  reducers: {                    // ← keys become action names; values are reducers
    addItem(state, action) {
      state.items.push(action.payload);       // Immer makes this safe
      state.total += action.payload.price;
    },
    removeItem(state, action) {
      state.items = state.items.filter(i => i.id !== action.payload);
      const removed = state.items.find(i => i.id === action.payload);
      if (removed) state.total -= removed.price;
    },
    clearCart(state) {
      state.items = [];
      state.total = 0;
    }
  }
});

// createSlice auto-generates:
export const { addItem, removeItem, clearCart } = cartSlice.actions;
// action creators: addItem(product) → { type: 'cart/addItem', payload: product }

export default cartSlice.reducer;
// the reducer function to pass to the store
```

**Notes:** The split between `.actions` (exported action creators) and `.reducer` (the combined reducer function) is the key API surface of `createSlice`. Students should understand that `cartSlice.actions.addItem` is an **action creator** — a function that builds the action object — not the action itself.

---

### Slide 7: configureStore — Assembling the Store
**Title:** `configureStore` — Combining All Slices
**Visual:** Store setup file

```javascript
// store/store.js
import { configureStore } from '@reduxjs/toolkit';
import cartReducer from './cartSlice';
import userReducer from './userSlice';
import notificationsReducer from './notificationsSlice';

export const store = configureStore({
  reducer: {
    cart: cartReducer,              // store.getState().cart
    user: userReducer,              // store.getState().user
    notifications: notificationsReducer  // store.getState().notifications
  }
  // configureStore also auto-enables:
  // ✅ Redux DevTools Extension
  // ✅ redux-thunk middleware (for async actions)
  // ✅ Serializable state check in development
});

// TypeScript helpers (bonus slide — good to see):
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

**State shape:**
```
store.getState() → {
  cart: { items: [], total: 0 },
  user: { id: null, name: null },
  notifications: []
}
```

**Notes:** `configureStore` automatically calls `combineReducers` — no need to do it manually. The keys in the `reducer` object become the top-level keys in your state tree. Students should understand that each slice "owns" one branch of the state tree.

---

### Slide 8: Provider — Connecting the Store to React
**Title:** `<Provider>` — Making the Store Available Everywhere
**Visual:** Annotated main.jsx / index.jsx

```jsx
// main.jsx (or index.jsx)
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { store } from './store/store';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Provider store={store}>      {/* ← makes Redux store available via hooks */}
      <BrowserRouter>             {/* ← makes routing available via hooks */}
        <App />
      </BrowserRouter>
    </Provider>
  </React.StrictMode>
);
```

**Provider wrapping order note box:**
> Both `<Provider>` and `<BrowserRouter>` use React Context under the hood. Their order doesn't matter (neither is a child of the other's required context). Convention: `Provider` wraps `BrowserRouter`.

**Analogy:** "The Redux `Provider` works exactly like React's `Context.Provider` — it injects the store into the component tree. Every component inside can now access the Redux store via hooks, without prop drilling."
**Notes:** The connection to Day 17a's Context Provider pattern is important here. Students already understand how a Provider makes data available to an entire subtree. Redux `Provider` is the same pattern, just for the Redux store.

---

### Slide 9: useSelector — Reading State from the Store
**Title:** `useSelector` — Subscribe to Store State
**Visual:** Annotated hook usage

```jsx
import { useSelector } from 'react-redux';

// Basic: select a slice
function CartBadge() {
  const cartItems = useSelector(state => state.cart.items);
  //                             ↑ full Redux state  ↑ select what you need

  return <span className="badge">{cartItems.length}</span>;
}

// Select a derived value:
function CartTotal() {
  const total = useSelector(state => state.cart.total);
  return <span>Total: ${total.toFixed(2)}</span>;
}

// Select a specific item:
function ProductCard({ id }) {
  const isInCart = useSelector(
    state => state.cart.items.some(item => item.id === id)
  );
  return (
    <button disabled={isInCart}>
      {isInCart ? 'In Cart' : 'Add to Cart'}
    </button>
  );
}
```

**How `useSelector` works:**
1. Runs the selector function against the current state
2. Compares the result to the previous result (reference equality)
3. If different → re-renders the component
4. If same → skips re-render ✅ (performance optimization)

**Notes:** The reference equality check is why you should select the minimum amount of state you need. Don't do `state => state` — that selects the entire state object and will cause re-renders on any store change anywhere in the app.

---

### Slide 10: useDispatch — Triggering State Changes
**Title:** `useDispatch` — Send Actions to the Store
**Visual:** Full add-to-cart component

```jsx
import { useDispatch, useSelector } from 'react-redux';
import { addItem, removeItem } from '../store/cartSlice';

function ProductCard({ product }) {
  const dispatch = useDispatch();
  //              ↑ get the dispatch function — call it to send actions

  const isInCart = useSelector(
    state => state.cart.items.some(item => item.id === product.id)
  );

  const handleAdd = () => {
    dispatch(addItem(product));
    //       ↑ addItem(product) creates: { type: 'cart/addItem', payload: product }
    //         dispatch() sends it to the Redux store
  };

  const handleRemove = () => {
    dispatch(removeItem(product.id));
  };

  return (
    <div className="card">
      <h3>{product.name}</h3>
      <p>${product.price}</p>
      {isInCart
        ? <button onClick={handleRemove}>Remove from Cart</button>
        : <button onClick={handleAdd}>Add to Cart</button>
      }
    </div>
  );
}
```

**Notes:** The `dispatch(addItem(product))` pattern has two steps: `addItem(product)` is the action creator call (returns the action object), then `dispatch()` sends that object to the store. Some students try to call `dispatch(addItem)` without the argument — remind them that action creators are functions that must be called.

---

### Slide 11: Multiple Slices — Modeling Real App State
**Title:** Working with Multiple Slices
**Visual:** Two slices + cross-slice selectors

**userSlice.js:**
```javascript
const userSlice = createSlice({
  name: 'user',
  initialState: { id: null, name: null, email: null, isLoggedIn: false },
  reducers: {
    login(state, action) {
      const { id, name, email } = action.payload;
      state.id = id;
      state.name = name;
      state.email = email;
      state.isLoggedIn = true;
    },
    logout(state) {
      state.id = null;
      state.name = null;
      state.email = null;
      state.isLoggedIn = false;
    }
  }
});

export const { login, logout } = userSlice.actions;
export default userSlice.reducer;
```

**Component using two slices:**
```jsx
function Navbar() {
  const { name, isLoggedIn } = useSelector(state => state.user);
  const cartCount = useSelector(state => state.cart.items.length);
  const dispatch = useDispatch();

  return (
    <nav>
      <span>🛒 {cartCount}</span>
      {isLoggedIn
        ? <><span>Hi, {name}</span><button onClick={() => dispatch(logout())}>Logout</button></>
        : <Link to="/login">Login</Link>
      }
    </nav>
  );
}
```

**Notes:** A component can call `useSelector` multiple times and `useDispatch` once. Each `useSelector` call sets up an independent subscription — only the components using the changed slice will re-render.

---

### Slide 12: Redux DevTools
**Title:** Redux DevTools — Time-Travel Debugging
**Visual:** Screenshot mockup of Redux DevTools interface with labels

**Installation:**
> Install the **Redux DevTools Extension** in your browser (Chrome/Firefox). `configureStore` from RTK automatically connects to it — no extra setup needed.

**DevTools panels (labeled mockup):**

```
┌──────────────────────────────────────────────────────┐
│ Redux DevTools                                        │
├────────────────┬─────────────────────────────────────┤
│ Action Log     │  Diff / State / Action               │
│                │                                      │
│ @@INIT         │  state.cart.items: []                │
│ cart/addItem   │  + added: { id: 42, name: 'Laptop' } │
│ cart/addItem   │                                      │
│ cart/removeItem│                                      │
│                │                                      │
│ [▶ Jump] [↩]  │  ← time-travel to any past state     │
└────────────────┴─────────────────────────────────────┘
```

**What you can do:**
- See every action dispatched, in order
- Click any action to jump back to that state (time travel!)
- See exactly what changed in state after each action (diff view)
- Import/export state snapshots for bug reproduction
- "Commit" to reset the baseline

**Notes:** Open the browser devtools, click the Redux tab. Demo this live if possible. Time-travel debugging is one of the most impressive demos in Redux — being able to replay a bug by stepping through actions is extremely powerful.

---

### Slide 13: Async Actions with createAsyncThunk (Preview)
**Title:** `createAsyncThunk` — Async Actions in RTK
**Visual:** Pattern overview with loading state

```javascript
// In cartSlice.js or a separate productsSlice.js
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

// createAsyncThunk wraps an async function and dispatches
// pending / fulfilled / rejected actions automatically
export const fetchProducts = createAsyncThunk(
  'products/fetchAll',
  async () => {
    const response = await fetch('/api/products');
    return response.json();   // this becomes action.payload in 'fulfilled'
  }
);

const productsSlice = createSlice({
  name: 'products',
  initialState: { items: [], loading: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchProducts.pending,   (state) => { state.loading = true; })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchProducts.rejected,  (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  }
});
```

**In a component:**
```jsx
const dispatch = useDispatch();
const { items, loading } = useSelector(state => state.products);

useEffect(() => {
  dispatch(fetchProducts());
}, [dispatch]);
```

**Note box:** "We're previewing this today — Day 19a is where we integrate real APIs. For now, understand the pattern: one async thunk creates three auto-dispatched actions: `pending`, `fulfilled`, `rejected`."
**Notes:** Don't deep-dive here — the pattern will make more sense in Day 19a with real Fetch/Axios calls. Today: plant the concept, show that RTK has first-class async support.

---

### Slide 14: State Management Decision Guide
**Title:** Choosing the Right State Tool
**Visual:** Decision flowchart

```
Start: Where does this state belong?
           ↓
Does only ONE component need it?
     Yes → useState ✅
     No  ↓
Does a SMALL number of related components need it?
Does it change INFREQUENTLY (theme, auth, language)?
     Yes → useContext ✅
     No  ↓
Do MANY unrelated components need it?
Does it change FREQUENTLY?
Do you need to trace every change for debugging?
Is the update logic COMPLEX?
     Yes → Redux ✅
```

**Quick rule-of-thumb table:**

| Data | Tool |
|---|---|
| Form input values | `useState` |
| Toggle, modal open/closed | `useState` |
| Fetched data used in one component | `useState` + `useEffect` |
| Current logged-in user | `useContext` |
| Theme (light/dark) | `useContext` |
| Shopping cart | Redux |
| Notifications feed | Redux |
| Complex filter/search state across pages | Redux |
| Real-time data (chat, live scores) | Redux or external library |

**Note box:** "Context re-renders ALL consumers when the value changes. Redux's `useSelector` only re-renders components that use the specific changed data. For high-frequency updates, Redux wins on performance."
**Notes:** This slide answers the question "when do I reach for Redux vs Context?" Students always ask this. The key: Context for stable data with wide reach; Redux for frequently-changing data or complex state transitions.

---

### Slide 15: Complete Example — Cart with Redux
**Title:** Full Shopping Cart with Redux Toolkit
**Visual:** Complete 4-file example

**store/cartSlice.js:**
```javascript
const cartSlice = createSlice({
  name: 'cart',
  initialState: { items: [], total: 0 },
  reducers: {
    addItem(state, action) {
      const existing = state.items.find(i => i.id === action.payload.id);
      if (existing) {
        existing.qty += 1;
      } else {
        state.items.push({ ...action.payload, qty: 1 });
      }
      state.total += action.payload.price;
    },
    removeItem(state, action) {
      const item = state.items.find(i => i.id === action.payload);
      if (item) state.total -= item.price * item.qty;
      state.items = state.items.filter(i => i.id !== action.payload);
    },
    clearCart(state) { state.items = []; state.total = 0; }
  }
});
export const { addItem, removeItem, clearCart } = cartSlice.actions;
export default cartSlice.reducer;
```

**CartPage.jsx:**
```jsx
import { useSelector, useDispatch } from 'react-redux';
import { removeItem, clearCart } from '../store/cartSlice';

export default function CartPage() {
  const { items, total } = useSelector(state => state.cart);
  const dispatch = useDispatch();

  if (items.length === 0) return <p>Your cart is empty.</p>;

  return (
    <div>
      {items.map(item => (
        <div key={item.id}>
          <span>{item.name} × {item.qty}</span>
          <span>${(item.price * item.qty).toFixed(2)}</span>
          <button onClick={() => dispatch(removeItem(item.id))}>Remove</button>
        </div>
      ))}
      <strong>Total: ${total.toFixed(2)}</strong>
      <button onClick={() => dispatch(clearCart())}>Clear Cart</button>
    </div>
  );
}
```
**Notes:** The `existing.qty += 1` inside `addItem` looks like direct mutation — and it is, syntactically. But RTK's Immer wrapper intercepts this and produces a new state object. This is the biggest "trust me" moment in Redux Toolkit. Show the Redux DevTools diff to prove a new state object is created.

---

### Slide 16: Common Redux Patterns and Pitfalls
**Title:** Patterns & Pitfalls
**Visual:** Two-column table

**✅ Best Practices:**

| Pattern | Why |
|---|---|
| One slice per feature domain | Keeps reducers focused and manageable |
| Select minimum state in `useSelector` | Prevents unnecessary re-renders |
| Put business logic in reducers, not components | Keeps components thin and testable |
| Export action creators from slice file | One import to dispatch from any component |
| Use Redux DevTools during development | Understand every state change |
| Keep reducers pure — no API calls | Side effects belong in thunks or components |

**❌ Common Mistakes:**

| Mistake | Fix |
|---|---|
| `dispatch(addItem)` (missing parentheses) | `dispatch(addItem(product))` — call the action creator |
| Mutating state in a vanilla reducer | Use RTK (Immer) or return new objects: `[...state, item]` |
| Selecting entire state: `state => state` | Select only what you need: `state => state.cart.items` |
| Putting ALL state in Redux | Local UI state (`isOpen`, `inputValue`) belongs in `useState` |
| Forgetting `<Provider>` in main.jsx | Hooks throw: "could not find react-redux context" |
| Not importing action creators | Must export from slice: `export const { addItem } = slice.actions` |

**Notes:** The `dispatch(addItem)` mistake (forgetting to call the action creator) is the single most common Redux bug. Make students say it out loud: "action creators are functions, call them with parentheses."

---

### Slide 17: Day 18a Summary
**Title:** Day 18a Complete — What You Can Now Build
**Visual:** Two-section summary

**🗺️ React Router (Part 1)**
- `<BrowserRouter>` → wraps app in `main.jsx`
- `<Routes>` + `<Route path element>` → page-to-component mapping
- `<Link>`, `<NavLink>` → navigation without reload
- `useNavigate` → programmatic navigation
- `useParams` → read `:id` from URL (always a string)
- `useSearchParams` → read/write `?key=value` query strings
- `useLocation` → full URL object + navigation state
- Nested routes + `<Outlet>` → shared layouts
- `<ProtectedRoute>` → guard authenticated pages
- HOCs → `(Component) => EnhancedComponent` — wrapping for cross-cutting concerns

**📦 Redux Toolkit (Part 2)**
- `createSlice` → `name`, `initialState`, `reducers` → exports `.actions` and `.reducer`
- `configureStore` → combines slices, enables DevTools + thunk middleware
- `<Provider store={store}>` → wraps app alongside `<BrowserRouter>`
- `useSelector(state => state.sliceName.field)` → subscribe to store state
- `useDispatch()` + `dispatch(actionCreator(payload))` → trigger state changes
- `createAsyncThunk` → async actions with `pending`/`fulfilled`/`rejected` states (preview)
- State decision: `useState` → `useContext` → Redux (based on scope, frequency, complexity)

**🔭 Coming up — Day 19a:** React API integration (Fetch/Axios), error boundaries, and React Testing Library.
