# Day 18a — React Routing & Redux: Complete Reference

**Topics:** React Router v6, Navigation, Route Parameters, Nested Routes, HOCs, Redux Toolkit, useSelector, useDispatch, Redux DevTools, State Management Patterns

---

## Table of Contents
1. [React Router Setup](#1-react-router-setup)
2. [Route Definitions](#2-route-definitions)
3. [Navigation](#3-navigation)
4. [Route Data — Params and Query Strings](#4-route-data)
5. [Nested Routes and Outlet](#5-nested-routes-and-outlet)
6. [Protected Routes](#6-protected-routes)
7. [Higher Order Components (HOC)](#7-higher-order-components)
8. [Redux — Core Concepts](#8-redux--core-concepts)
9. [Redux Toolkit — createSlice](#9-redux-toolkit--createslice)
10. [configureStore and Provider](#10-configurestore-and-provider)
11. [useSelector and useDispatch](#11-useselector-and-usedispatch)
12. [Multiple Slices](#12-multiple-slices)
13. [createAsyncThunk (Preview)](#13-createasyncthunk-preview)
14. [State Management Decision Guide](#14-state-management-decision-guide)
15. [Complete Full-Stack Example](#15-complete-full-stack-example)
16. [Common Mistakes & Fixes](#16-common-mistakes--fixes)
17. [Quick Reference Syntax](#17-quick-reference-syntax)
18. [Looking Ahead — Day 19a](#18-looking-ahead--day-19a)

---

## 1. React Router Setup

### Installation
```bash
npm install react-router-dom
```

### Wrap the App (main.jsx)
```jsx
import { BrowserRouter } from 'react-router-dom';

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
```

**Why `BrowserRouter`?** Sets up the routing context (HTML5 History API). Every router hook (`useNavigate`, `useParams`, etc.) requires this context above them in the tree.

### v5 → v6 Key Differences

| v5 | v6 |
|---|---|
| `<Switch>` | `<Routes>` |
| `component={MyComp}` | `element={<MyComp />}` |
| `useHistory()` | `useNavigate()` |
| `exact` prop needed | Exact matching by default |
| `<Redirect>` | `<Navigate>` |

---

## 2. Route Definitions

### Basic Route Map
```jsx
import { Routes, Route } from 'react-router-dom';

function App() {
  return (
    <>
      <Navbar />               {/* outside Routes — renders on every page */}
      <Routes>
        <Route path="/"             element={<Home />} />
        <Route path="/products"     element={<Products />} />
        <Route path="/products/:id" element={<ProductDetail />} />
        <Route path="/login"        element={<Login />} />
        <Route path="*"             element={<NotFound />} />  {/* 404 */}
      </Routes>
    </>
  );
}
```

### Rules
- `<Routes>` renders the **first** matching `<Route>` only
- `path="*"` = wildcard catch-all (404 page)
- `element` takes JSX: `element={<MyComponent />}` not `element={MyComponent}`
- Exact matching is default in v6 — no `exact` prop needed

---

## 3. Navigation

### Declarative — Link and NavLink
```jsx
import { Link, NavLink } from 'react-router-dom';

// Basic link (no reload)
<Link to="/products">View Products</Link>
<Link to={`/products/${id}`}>See Details</Link>

// NavLink — adds 'active' class automatically
<NavLink to="/products">Products</NavLink>

// NavLink with custom active styling
<NavLink
  to="/products"
  style={({ isActive }) => ({ fontWeight: isActive ? 'bold' : 'normal' })}
  className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
>
  Products
</NavLink>
```

### Programmatic — useNavigate
```jsx
import { useNavigate } from 'react-router-dom';

function LoginForm() {
  const navigate = useNavigate();

  const onSuccess = () => navigate('/dashboard');             // push to history
  const onLogout  = () => navigate('/login', { replace: true }); // replace history entry
  const onBack    = () => navigate(-1);                       // go back one page
  const onForward = () => navigate(1);                        // go forward one page
}
```

**`replace: true`:** Does not add a new history entry. User can't press Back to return. Use after login/logout.

### Rule
> Never use `<a href="/path">` for internal React Router navigation — it causes a full page reload.

---

## 4. Route Data

### Path Parameters — useParams
```jsx
// Route definition
<Route path="/products/:id" element={<ProductDetail />} />

// Component
import { useParams } from 'react-router-dom';

function ProductDetail() {
  const { id } = useParams();          // always a STRING
  const productId = Number(id);        // convert for numeric comparison
  const product = products.find(p => p.id === productId);
}

// Multiple params
// Route: /users/:userId/posts/:postId
const { userId, postId } = useParams();
```

⚠️ **Always convert `useParams` values from string** when comparing with numeric IDs.

### Query Strings — useSearchParams
```jsx
import { useSearchParams } from 'react-router-dom';

function Products() {
  const [searchParams, setSearchParams] = useSearchParams();

  const category = searchParams.get('category') ?? 'all';  // null if absent
  const sort     = searchParams.get('sort')     ?? 'name';

  // Update URL: /products?category=electronics&sort=price
  const handleCategoryChange = (newCategory) => {
    setSearchParams({ category: newCategory, sort });
  };
}
```

**Advantage:** Filter/sort state lives in the URL → bookmarkable, shareable, browser Back button undoes filter changes.

### useLocation — Full URL Object
```jsx
import { useLocation } from 'react-router-dom';

const location = useLocation();
// {
//   pathname: '/products/42',
//   search:   '?sort=price',
//   hash:     '#reviews',
//   state:    { from: '/cart' },  ← programmatic state (not in URL)
//   key:      'abc123'
// }
```

---

## 5. Nested Routes and Outlet

### Layout Route Pattern
```jsx
// App.jsx — route definition
<Routes>
  <Route path="/dashboard" element={<DashboardLayout />}>
    <Route index           element={<DashboardHome />} />  {/* /dashboard */}
    <Route path="profile"  element={<Profile />} />        {/* /dashboard/profile */}
    <Route path="settings" element={<Settings />} />       {/* /dashboard/settings */}
    <Route path="*"        element={<NotFound />} />       {/* /dashboard/anything */}
  </Route>
</Routes>

// DashboardLayout.jsx
import { Outlet } from 'react-router-dom';

function DashboardLayout() {
  return (
    <div className="dashboard-shell">
      <Sidebar />       {/* always rendered */}
      <main>
        <Outlet />      {/* matched child renders here */}
      </main>
    </div>
  );
}
```

### Index Routes
```jsx
// Without index: /dashboard renders DashboardLayout with empty Outlet
// With index:    /dashboard renders DashboardLayout + DashboardHome in Outlet
<Route index element={<DashboardHome />} />
```

### Behavior Table

| URL | Renders |
|---|---|
| `/dashboard` | `DashboardLayout` → `DashboardHome` (index) |
| `/dashboard/profile` | `DashboardLayout` → `Profile` |
| `/dashboard/xyz` | `DashboardLayout` → `NotFound` (nested catch-all) |
| `/anything-else` | Top-level `NotFound` |

---

## 6. Protected Routes

### ProtectedRoute Component
```jsx
import { Navigate, Outlet, useLocation } from 'react-router-dom';

function ProtectedRoute({ isAuthenticated, redirectTo = '/login' }) {
  const location = useLocation();

  if (!isAuthenticated) {
    // Save where the user was trying to go
    return <Navigate to={redirectTo} state={{ from: location }} replace />;
  }
  return <Outlet />;
}
```

### Usage in App
```jsx
<Routes>
  <Route path="/login"  element={<Login />} />
  <Route path="/signup" element={<Signup />} />

  {/* All children require authentication */}
  <Route element={<ProtectedRoute isAuthenticated={!!user} />}>
    <Route path="/dashboard" element={<Dashboard />} />
    <Route path="/profile"   element={<Profile />} />
    <Route path="/settings"  element={<Settings />} />
  </Route>

  <Route path="*" element={<NotFound />} />
</Routes>
```

### Redirect Back After Login
```jsx
function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname ?? '/dashboard';

  const handleLogin = async () => {
    await login(credentials);
    navigate(from, { replace: true });  // send user back where they tried to go
  };
}
```

---

## 7. Higher Order Components

### Pattern
```jsx
// HOC = function that takes a component, returns an enhanced component
function withAuth(WrappedComponent) {
  return function AuthProtected(props) {
    const { user } = useAuth();
    if (!user) return <Navigate to="/login" replace />;
    return <WrappedComponent {...props} />;  // spread all props through
  };
}

const ProtectedDashboard = withAuth(Dashboard);
```

### Practical HOC Examples
```jsx
// withLoading — show spinner while data loads
function withLoading(WrappedComponent) {
  return function WithLoadingComponent({ isLoading, ...rest }) {
    if (isLoading) return <div>Loading...</div>;
    return <WrappedComponent {...rest} />;
  };
}
const ProductListWithLoading = withLoading(ProductList);
// <ProductListWithLoading isLoading={loading} products={products} />

// withLogger — log renders in development
function withLogger(WrappedComponent) {
  return function LoggedComponent(props) {
    console.log(`[${WrappedComponent.name}] rendered`, props);
    return <WrappedComponent {...props} />;
  };
}
```

### HOC vs Custom Hooks

| | HOC | Custom Hook |
|---|---|---|
| Syntax | `withX(Component)` → new component | `useX()` → stateful value |
| Use for | Route guards, component-level behavior | Reusable logic within a component |
| Modern preference | Less common in new code | Preferred for new code |

---

## 8. Redux — Core Concepts

### The Three Pieces

| Concept | What It Is | Example |
|---|---|---|
| **Store** | Single object holding all global state | `{ cart: {items:[], total:0}, user: null }` |
| **Action** | Plain object describing what happened | `{ type: 'cart/addItem', payload: product }` |
| **Reducer** | Pure function: `(state, action) => newState` | Returns new state with item added |

### Reducer Rules
1. Never mutate state directly — return new objects (RTK handles this with Immer)
2. No side effects — no API calls, no `Math.random()`, no `Date.now()`
3. Deterministic — same inputs always produce the same output

### The Redux Data Flow (Unidirectional)
```
Component dispatches action
  ↓
Redux sends (state, action) to reducer
  ↓
Reducer returns new state
  ↓
Store saves new state
  ↓
Components subscribed via useSelector re-render
```

---

## 9. Redux Toolkit — createSlice

### Installation
```bash
npm install @reduxjs/toolkit react-redux
```

### createSlice Anatomy
```javascript
import { createSlice } from '@reduxjs/toolkit';

const cartSlice = createSlice({
  name: 'cart',                        // prefix for action types: 'cart/addItem'
  initialState: { items: [], total: 0 },

  reducers: {
    addItem(state, action) {
      // Immer makes this mutation-style code safe
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

    clearCart(state) {
      state.items = [];
      state.total = 0;
    }
  }
});

// Exports:
export const { addItem, removeItem, clearCart } = cartSlice.actions;
// addItem, removeItem, clearCart are ACTION CREATORS — call them to build action objects
// addItem(product) → { type: 'cart/addItem', payload: product }

export default cartSlice.reducer;
// Pass to configureStore
```

---

## 10. configureStore and Provider

### Store Setup (store/store.js)
```javascript
import { configureStore } from '@reduxjs/toolkit';
import cartReducer from './cartSlice';
import userReducer from './userSlice';

export const store = configureStore({
  reducer: {
    cart: cartReducer,    // state.cart
    user: userReducer,    // state.user
  }
  // Automatically enables:
  // ✅ Redux DevTools Extension
  // ✅ redux-thunk middleware
  // ✅ Serializable state check (dev only)
});
```

### Provider (main.jsx)
```jsx
import { Provider } from 'react-redux';
import { store } from './store/store';

ReactDOM.createRoot(document.getElementById('root')).render(
  <Provider store={store}>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </Provider>
);
```

### combineReducers — Explicit Root Reducer

When you pass `reducer: { cart, user }` to `configureStore`, RTK calls `combineReducers` internally. Use it explicitly when you need a **nested state shape** or want to **wrap the root reducer** (e.g., reset all state on logout):

```javascript
import { combineReducers, configureStore } from '@reduxjs/toolkit';

// Nested structure: state.admin.users and state.admin.roles
const adminReducer = combineReducers({
  users: usersReducer,
  roles: rolesReducer,
});

export const store = configureStore({
  reducer: {
    admin: adminReducer,  // state.admin.users, state.admin.roles
    cart:  cartReducer,   // state.cart
    user:  userReducer,   // state.user
  }
});
```

```javascript
// Reset entire state on logout — wrap the root reducer
const rootReducer = combineReducers({ cart: cartReducer, user: userReducer });

function appReducer(state, action) {
  if (action.type === 'user/logout') {
    state = undefined;  // each slice falls back to its own initialState
  }
  return rootReducer(state, action);
}

export const store = configureStore({ reducer: appReducer });
```

---

## 11. useSelector and useDispatch

### useSelector — Reading State
```jsx
import { useSelector } from 'react-redux';

// Select a primitive value
const total = useSelector(state => state.cart.total);

// Select an array
const items = useSelector(state => state.cart.items);

// Select a derived boolean (computed from state)
const isInCart = useSelector(
  state => state.cart.items.some(item => item.id === productId)
);

// Multiple selectors in one component
function Navbar() {
  const cartCount   = useSelector(state => state.cart.items.length);
  const userName    = useSelector(state => state.user.name);
  const isLoggedIn  = useSelector(state => state.user.isLoggedIn);
}
```

**Performance note:** Select the minimum needed. `useSelector` re-renders the component only when the selected value changes (reference equality check).

### useDispatch — Sending Actions
```jsx
import { useDispatch } from 'react-redux';
import { addItem, removeItem, clearCart } from '../store/cartSlice';

function ProductCard({ product }) {
  const dispatch = useDispatch();

  return (
    <button onClick={() => dispatch(addItem(product))}>
    {/*                         ↑ call action creator with payload
                             ↑ dispatch sends the resulting action object */}
      Add to Cart
    </button>
  );
}
```

⚠️ **Common mistake:** `dispatch(addItem)` — missing `(product)`. Must call the action creator!

---

## 12. Multiple Slices

### userSlice.js
```javascript
const userSlice = createSlice({
  name: 'user',
  initialState: { id: null, name: null, email: null, isLoggedIn: false },
  reducers: {
    login(state, action) {
      const { id, name, email } = action.payload;
      state.id = id; state.name = name; state.email = email;
      state.isLoggedIn = true;
    },
    logout(state) {
      state.id = null; state.name = null; state.email = null;
      state.isLoggedIn = false;
    }
  }
});
export const { login, logout } = userSlice.actions;
export default userSlice.reducer;
```

### Component Using Two Slices
```jsx
function Navbar() {
  const { name, isLoggedIn } = useSelector(state => state.user);
  const cartCount = useSelector(state => state.cart.items.length);
  const dispatch = useDispatch();
  // Each useSelector is an independent subscription
  // Only re-renders when ITS selected data changes
}
```

---

## 13. createAsyncThunk (Preview)

Covered fully in Day 19a. Pattern overview:

```javascript
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

// Thunk: auto-dispatches pending/fulfilled/rejected
export const fetchProducts = createAsyncThunk(
  'products/fetchAll',
  async () => {
    const res = await fetch('/api/products');
    return res.json();  // becomes action.payload in 'fulfilled'
  }
);

const productsSlice = createSlice({
  name: 'products',
  initialState: { items: [], loading: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchProducts.pending,   (s)    => { s.loading = true; })
      .addCase(fetchProducts.fulfilled, (s, a) => { s.loading = false; s.items = a.payload; })
      .addCase(fetchProducts.rejected,  (s, a) => { s.loading = false; s.error = a.error.message; });
  }
});
```

```jsx
// In component
const dispatch = useDispatch();
const { items, loading, error } = useSelector(state => state.products);

useEffect(() => {
  dispatch(fetchProducts());
}, [dispatch]);
```

---

## 14. State Management Decision Guide

### The Decision Tree
```
Does only ONE component need this state?
  → Yes: useState

Do a FEW related components need it, and it changes INFREQUENTLY?
(theme, current user, language)
  → Yes: useContext

Does it need to be:
  - Shared across MANY unrelated components?
  - Updated FREQUENTLY?
  - Complex update logic (multiple fields atomically)?
  - Fully traceable for debugging?
  → Yes: Redux
```

### Quick Reference Table

| State | Tool |
|---|---|
| Form input values | `useState` |
| Modal open/closed, hover | `useState` |
| API data used in one component | `useState` + `useEffect` |
| Current user / auth | `useContext` |
| Theme (light/dark) | `useContext` |
| Language preference | `useContext` |
| Shopping cart | Redux |
| Notifications feed | Redux |
| App-wide loading / error | Redux |
| Filter/sort state across pages | Redux (or `useSearchParams`) |
| Real-time data | Redux or external lib |

### Context vs Redux Performance
- **Context:** Re-renders ALL consumers when value changes
- **Redux `useSelector`:** Re-renders ONLY components that subscribed to the changed data

---

## 15. Complete Full-Stack Example

### File Structure
```
src/
  store/
    store.js
    cartSlice.js
    userSlice.js
  pages/
    Home.jsx
    Products.jsx
    ProductDetail.jsx
    CartPage.jsx
    Dashboard.jsx
    Login.jsx
    NotFound.jsx
  components/
    Navbar.jsx
    ProductCard.jsx
    ProtectedRoute.jsx
  hooks/
    useAuth.js
  App.jsx
  main.jsx
```

### main.jsx
```jsx
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { store } from './store/store';

ReactDOM.createRoot(document.getElementById('root')).render(
  <Provider store={store}>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </Provider>
);
```

### App.jsx
```jsx
import { Routes, Route } from 'react-router-dom';
import { useSelector } from 'react-redux';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
// ...page imports

export default function App() {
  const isLoggedIn = useSelector(state => state.user.isLoggedIn);

  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/"             element={<Home />} />
        <Route path="/products"     element={<Products />} />
        <Route path="/products/:id" element={<ProductDetail />} />
        <Route path="/cart"         element={<CartPage />} />
        <Route path="/login"        element={<Login />} />

        <Route element={<ProtectedRoute isAuthenticated={isLoggedIn} />}>
          <Route path="/dashboard" element={<Dashboard />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Routes>
    </>
  );
}
```

### ProductDetail.jsx — Routing + Redux Together
```jsx
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { addItem } from '../store/cartSlice';

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const product = useSelector(state =>
    state.products?.items.find(p => p.id === Number(id))
  );
  const isInCart = useSelector(state =>
    state.cart.items.some(i => i.id === Number(id))
  );

  if (!product) return <p>Product not found. <button onClick={() => navigate(-1)}>Back</button></p>;

  return (
    <div>
      <button onClick={() => navigate(-1)}>← Back</button>
      <h1>{product.name}</h1>
      <p>${product.price}</p>
      <button
        onClick={() => dispatch(addItem(product))}
        disabled={isInCart}
      >
        {isInCart ? '✓ In Cart' : 'Add to Cart'}
      </button>
    </div>
  );
}
```

---

## 16. Common Mistakes & Fixes

### React Router

| Mistake | Fix |
|---|---|
| `<a href="/products">` for internal links | Use `<Link to="/products">` |
| Forgetting `<BrowserRouter>` in main.jsx | Wrap app: `<BrowserRouter><App /></BrowserRouter>` |
| `element={MyComponent}` (no JSX) | `element={<MyComponent />}` |
| `useParams()` value used as number without conversion | `const id = Number(useParams().id)` |
| `navigate('/login')` after logout (user presses Back) | `navigate('/login', { replace: true })` |
| No `index` route → empty layout | Add `<Route index element={<DefaultPage />} />` |

### Redux

| Mistake | Fix |
|---|---|
| `dispatch(addItem)` — missing `(payload)` | `dispatch(addItem(product))` — call action creator |
| Mutating state in vanilla reducer | Use RTK (Immer) or return new objects |
| `useSelector(state => state)` — selects everything | Select minimum: `state => state.cart.items` |
| `new ProductService()` mindset — calling `store.getState()` in components | Use `useSelector` hook, never access store directly |
| Forgetting `export` on action creators | `export const { addItem } = cartSlice.actions` |
| Putting `isDropdownOpen` in Redux | UI-only state belongs in `useState` |
| Forgetting `<Provider>` | Hooks throw "could not find react-redux context value" |

---

## 17. Quick Reference Syntax

```jsx
// --- REACT ROUTER ---
import { BrowserRouter, Routes, Route, Link, NavLink,
         useNavigate, useParams, useSearchParams, useLocation,
         Navigate, Outlet } from 'react-router-dom';

// Setup
<BrowserRouter><App /></BrowserRouter>

// Routes
<Routes>
  <Route path="/path"   element={<Page />} />
  <Route path="/path/:id" element={<Detail />} />
  <Route index          element={<Default />} />    // default child
  <Route path="*"       element={<NotFound />} />   // catch-all
</Routes>

// Navigation
<Link to="/path">Go</Link>
<NavLink to="/path" className={({isActive}) => isActive ? 'active' : ''}>Nav</NavLink>
navigate('/path')
navigate(-1)                              // go back
navigate('/path', { replace: true })      // no history entry

// Route data
const { id } = useParams();              // path params (strings)
const [p, setP] = useSearchParams();     // query strings
const location = useLocation();          // full URL object

// Nested
<Route element={<Layout />}>
  <Route path="child" element={<Child />} />
</Route>
// In Layout: <Outlet />
```

```javascript
// --- REDUX TOOLKIT ---
import { createSlice, configureStore, createAsyncThunk } from '@reduxjs/toolkit';
import { Provider, useSelector, useDispatch } from 'react-redux';

// Slice
const mySlice = createSlice({
  name: 'feature',
  initialState: {},
  reducers: {
    doSomething(state, action) { /* Immer-safe mutation */ }
  }
});
export const { doSomething } = mySlice.actions;
export default mySlice.reducer;

// Store
const store = configureStore({ reducer: { feature: myReducer } });

// Provider
<Provider store={store}><App /></Provider>

// Hooks
const data = useSelector(state => state.feature.data);
const dispatch = useDispatch();
dispatch(doSomething(payload));
```

---

## 18. Looking Ahead — Day 19a

**Day 19a: React API & Testing** builds directly on today's Redux patterns:

| Today (Day 18a) | Day 19a |
|---|---|
| `createAsyncThunk` (preview) | Full `createAsyncThunk` with real `fetch`/`axios` |
| Redux loading/error state shape | Loading states with skeleton UIs and error messages |
| Redux store holds fetched data | API responses stored in Redux slices |
| `ProtectedRoute` guards routes | Error Boundaries guard component subtrees from crashes |
| Component testing concepts | React Testing Library — render, query, fire events |

**Day 19a topics preview:**
- `fetch` and `axios` for HTTP requests
- `createAsyncThunk` with real API calls
- Loading and error state patterns
- Error Boundaries — `componentDidCatch` / `ErrorBoundary` component
- React Testing Library: `render`, `screen`, `fireEvent`, `userEvent`
- Jest: `describe`, `it`/`test`, `expect`, matchers
- `msw` (Mock Service Worker) for mocking API calls in tests
- Environment variables with `.env` files (`import.meta.env.VITE_API_URL`)
