# Day 19a — Part 1: React API Integration & Error Boundaries
## Slide Descriptions

---

### Slide 1: Title Slide
**"React API Integration & Error Boundaries"**
Subtitle: Fetch, Axios, Loading States, Error Handling, Error Boundaries
Week 4 – Day 19a, Part 1

---

### Slide 2: Day 18a Recap + Today's Agenda

**Recap — Day 18a:**
- React Router v6: `BrowserRouter`, `Routes`, `Route`, `useParams`, `useSearchParams`, `useNavigate`
- Redux Toolkit: `createSlice`, `configureStore`, `Provider`, `useSelector`, `useDispatch`
- `createAsyncThunk` — **preview only**: pending/fulfilled/rejected lifecycle
- The state management decision guide: `useState` → `useContext` → Redux

**Today — Part 1:**
- `fetch` API: the browser's built-in HTTP tool
- `axios`: the popular third-party HTTP library — when and why
- The three-state pattern: `loading` / `data` / `error`
- Custom data-fetching hook: `useFetch`
- POST, PUT, DELETE with both approaches
- Full `createAsyncThunk` implementation (the Day 18a preview, now complete)
- Error handling: `response.ok`, `try/catch`, status codes
- Error Boundaries: catching render-phase crashes with a class component

**Today — Part 2:**
- React Testing Library + Jest: writing tests for components
- Testing async components and hooks
- Mocking API calls in tests
- Environment variables in Vite/React (`.env` files)

---

### Slide 3: The Problem — Where Does Data Come From?

**The gap in our React knowledge:**
```
Week 3:
  useState / useEffect / useContext ✅
  Redux store / slices / useSelector ✅
  React Router / navigation ✅

Missing: how does real data get INTO the store or component state?
  → Products don't live in the component
  → Cart totals need to persist to a server
  → User login must hit an authentication service
```

**The data flow:**
```
React Component / Redux Thunk
      ↕  HTTP request (fetch / axios)
REST API Server (Spring Boot, Node, etc.)
      ↕  SQL / NoSQL queries
Database
```

**HTTP methods we'll use:**
| Method | Purpose | Example |
|---|---|---|
| `GET` | Read data | `GET /api/products` |
| `POST` | Create new resource | `POST /api/products` |
| `PUT` | Replace a resource | `PUT /api/products/42` |
| `PATCH` | Partially update | `PATCH /api/products/42` |
| `DELETE` | Remove a resource | `DELETE /api/products/42` |

---

### Slide 4: The Fetch API — Browser-Native HTTP

```javascript
// Basic GET — returns a Promise
fetch('https://api.example.com/products')
  .then(response => response.json())   // Response object → parse body as JSON
  .then(data => console.log(data))
  .catch(error => console.error(error));

// Modern async/await (preferred)
async function getProducts() {
  try {
    const response = await fetch('https://api.example.com/products');

    // ⚠️ fetch does NOT throw on 4xx/5xx — must check manually
    if (!response.ok) {
      throw new Error(`HTTP error: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Fetch failed:', error);
    throw error;   // re-throw so callers can handle it
  }
}

// POST with a request body
async function createProduct(product) {
  const response = await fetch('https://api.example.com/products', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(product)   // must stringify the object
  });

  if (!response.ok) throw new Error(`HTTP error: ${response.status}`);
  return response.json();
}
```

**Key fact:** `fetch` is built into every modern browser and Node 18+. No install required.

---

### Slide 5: Axios — When and Why

```bash
npm install axios
```

```javascript
import axios from 'axios';

// GET — response data is already parsed
const { data } = await axios.get('/api/products');
// ↑ axios automatically calls .json() and puts the body in response.data

// POST
const { data: newProduct } = await axios.post('/api/products', {
  name: 'Widget',
  price: 9.99
});
// ↑ axios automatically JSON.stringifies the body and sets Content-Type header

// PUT / PATCH / DELETE
await axios.put(`/api/products/${id}`, updatedProduct);
await axios.patch(`/api/products/${id}`, { price: 12.99 });
await axios.delete(`/api/products/${id}`);
```

**Axios instance — configure once, use everywhere:**
```javascript
// src/api/client.js
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,  // from .env file
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
});

// Add auth token to every request automatically
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default api;
```

**fetch vs axios:**
| | `fetch` | `axios` |
|---|---|---|
| Install | Built-in | `npm install axios` |
| Auto JSON parse | No (`.json()` needed) | Yes — `response.data` |
| Throws on 4xx/5xx | ❌ No | ✅ Yes |
| Request timeout | Manual (AbortController) | Built-in (`timeout` option) |
| Interceptors | Manual | Built-in `.interceptors` |
| Upload progress | No | Yes |
| Best for | Quick scripts, simple calls | Production apps, teams |

---

### Slide 6: The Three-State Pattern

**Every async operation has three possible states:**
```javascript
// ❌ Common mistake — only handling the success case
function ProductList() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch('/api/products')
      .then(r => r.json())
      .then(setProducts);  // what about loading? what about errors?
  }, []);

  return <ul>{products.map(p => <li key={p.id}>{p.name}</li>)}</ul>;
}

// ✅ Complete three-state pattern
function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [error,    setError]    = useState(null);

  useEffect(() => {
    let cancelled = false;   // prevent setting state on unmounted component

    setLoading(true);
    setError(null);

    fetch('/api/products')
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then(data => {
        if (!cancelled) setProducts(data);
      })
      .catch(err => {
        if (!cancelled) setError(err.message);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => { cancelled = true; };   // cleanup on unmount
  }, []);

  if (loading) return <p>Loading products...</p>;
  if (error)   return <p>Error: {error} <button onClick={retry}>Retry</button></p>;
  return <ul>{products.map(p => <li key={p.id}>{p.name}</li>)}</ul>;
}
```

---

### Slide 7: Custom useFetch Hook

**Extract the pattern into a reusable hook:**
```javascript
// src/hooks/useFetch.js
import { useState, useEffect } from 'react';

export function useFetch(url, options = {}) {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    if (!url) return;
    let cancelled = false;

    setLoading(true);
    setError(null);

    fetch(url, options)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`);
        return res.json();
      })
      .then(data => { if (!cancelled) setData(data); })
      .catch(err  => { if (!cancelled) setError(err.message); })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [url]);   // re-runs whenever url changes

  return { data, loading, error };
}
```

**Usage — any component that needs data:**
```javascript
function ProductDetail({ id }) {
  const { data: product, loading, error } = useFetch(`/api/products/${id}`);

  if (loading) return <Spinner />;
  if (error)   return <ErrorMessage message={error} />;
  if (!product) return null;

  return <div><h1>{product.name}</h1><p>${product.price}</p></div>;
}

function UserProfile({ userId }) {
  const { data: user, loading, error } = useFetch(`/api/users/${userId}`);
  // Same hook, different URL — zero extra code
}
```

**The `cancelled` flag** prevents the "Warning: Can't perform a React state update on an unmounted component" error — happens when a user navigates away before the fetch completes.

---

### Slide 8: Mutations — POST, PUT, DELETE

**Mutations are triggered by user action, not on mount:**
```javascript
// Custom hook for mutations
function useCreateProduct() {
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);

  const createProduct = async (productData) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch('/api/products', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(productData)
      });
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return await response.json();   // return the created resource
    } catch (err) {
      setError(err.message);
      throw err;   // re-throw so the calling component can react
    } finally {
      setLoading(false);
    }
  };

  return { createProduct, loading, error };
}

// Using the mutation hook
function AddProductForm() {
  const { createProduct, loading, error } = useCreateProduct();
  const navigate = useNavigate();

  const handleSubmit = async (formData) => {
    try {
      const newProduct = await createProduct(formData);
      navigate(`/products/${newProduct.id}`);   // redirect on success
    } catch {
      // error already set in the hook — displayed below
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* form fields */}
      {error && <p className="error">{error}</p>}
      <button type="submit" disabled={loading}>
        {loading ? 'Saving...' : 'Save Product'}
      </button>
    </form>
  );
}
```

---

### Slide 9: createAsyncThunk — Full Implementation

**The Day 18a preview — now in full:**
```javascript
// store/productsSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import api from '../api/client';   // axios instance

// 1. Define the thunk (action creator that dispatches 3 actions automatically)
export const fetchProducts = createAsyncThunk(
  'products/fetchAll',             // action type prefix
  async (params, { rejectWithValue }) => {
    try {
      const { data } = await api.get('/products', { params });
      return data;                 // becomes action.payload in 'fulfilled'
    } catch (error) {
      // rejectWithValue lets you control the rejected payload
      return rejectWithValue(error.response?.data?.message ?? error.message);
    }
  }
);

export const createProduct = createAsyncThunk(
  'products/create',
  async (productData, { rejectWithValue }) => {
    try {
      const { data } = await api.post('/products', productData);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message ?? error.message);
    }
  }
);

// 2. Slice handles all three lifecycle actions in extraReducers
const productsSlice = createSlice({
  name: 'products',
  initialState: { items: [], loading: false, error: null },
  reducers: {},   // synchronous reducers (none needed here)
  extraReducers: (builder) => {
    builder
      // fetchProducts lifecycle
      .addCase(fetchProducts.pending,   (state)         => { state.loading = true; state.error = null; })
      .addCase(fetchProducts.fulfilled, (state, action) => { state.loading = false; state.items = action.payload; })
      .addCase(fetchProducts.rejected,  (state, action) => { state.loading = false; state.error = action.payload; })

      // createProduct lifecycle
      .addCase(createProduct.fulfilled, (state, action) => {
        state.items.push(action.payload);  // optimistic update
      });
  }
});

export default productsSlice.reducer;
```

---

### Slide 10: Using createAsyncThunk in a Component

```javascript
// ProductsPage.jsx
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchProducts } from '../store/productsSlice';

function ProductsPage() {
  const dispatch = useDispatch();
  const { items, loading, error } = useSelector(state => state.products);

  useEffect(() => {
    dispatch(fetchProducts());          // dispatch on mount
  }, [dispatch]);

  // Dispatch with parameters
  const handleFilterChange = (category) => {
    dispatch(fetchProducts({ category }));   // passed as first arg to thunk
  };

  if (loading) return <ProductListSkeleton />;
  if (error)   return <ErrorBanner message={error} onRetry={() => dispatch(fetchProducts())} />;

  return (
    <div>
      <FilterBar onChange={handleFilterChange} />
      <ul>
        {items.map(product => <ProductCard key={product.id} product={product} />)}
      </ul>
    </div>
  );
}
```

**The full dispatch flow:**
```
dispatch(fetchProducts())
  ↓
RTK dispatches { type: 'products/fetchAll/pending' }
  ↓
Axios calls GET /api/products
  ↓ (success)
RTK dispatches { type: 'products/fetchAll/fulfilled', payload: [...] }
  ↓
extraReducers.fulfilled runs: state.items = payload, loading = false
  ↓
useSelector components re-render with new items
```

---

### Slide 11: Error Handling Patterns

**Three layers of error handling:**

**Layer 1 — HTTP-level errors (status codes):**
```javascript
// fetch: manual check
if (!response.ok) {
  throw new Error(`Server error: ${response.status}`);
}

// axios: automatic throw on 4xx/5xx
// Catch in the try/catch — error.response has the details:
catch (error) {
  if (error.response) {
    // Server responded with 4xx or 5xx
    const status  = error.response.status;
    const message = error.response.data?.message ?? 'Request failed';

    if (status === 401) navigate('/login');            // unauthorized → redirect
    if (status === 403) setError('Access denied');     // forbidden
    if (status === 404) setError('Not found');
    if (status >= 500)  setError('Server error — please try again');
  } else if (error.request) {
    // Request made but no response received (network down, timeout)
    setError('Network error — check your connection');
  } else {
    // Something else went wrong
    setError(error.message);
  }
}
```

**Layer 2 — Application-level errors:**
```javascript
// Validate before you render
if (!data || data.length === 0) return <EmptyState />;
if (!data.price || data.price < 0) return <ErrorMessage message="Invalid product data" />;
```

**Layer 3 — Render-phase errors → Error Boundaries (next slide)**

---

### Slide 12: Loading State UI Patterns

**Three loading patterns — choose based on context:**

**1. Spinner / loading indicator (simple, always works):**
```jsx
if (loading) return <div className="spinner">Loading...</div>;
```

**2. Skeleton UI (best UX — content placeholder maintains layout):**
```jsx
function ProductCardSkeleton() {
  return (
    <div className="product-card skeleton">
      <div className="skeleton-image" />      {/* gray placeholder boxes */}
      <div className="skeleton-title" />
      <div className="skeleton-price" />
    </div>
  );
}

if (loading) return (
  <ul>{Array.from({ length: 6 }).map((_, i) => <ProductCardSkeleton key={i} />)}</ul>
);
```

**3. Optimistic UI (mutation response — show result before server confirms):**
```jsx
// Immediately add item to cart in UI, then sync with server
const handleAddToCart = async (item) => {
  setCartItems(prev => [...prev, { ...item, optimistic: true }]);  // immediate
  try {
    await api.post('/cart', item);
    setCartItems(prev => prev.map(i => i.id === item.id ? { ...i, optimistic: false } : i));
  } catch {
    setCartItems(prev => prev.filter(i => i.id !== item.id));  // rollback on failure
    setError('Failed to add to cart');
  }
};
```

**Disable submit button during loading:**
```jsx
<button type="submit" disabled={loading}>
  {loading ? 'Saving...' : 'Save'}
</button>
```

---

### Slide 13: Error Boundaries — What They Catch

**The problem:**
```jsx
// What happens if product.price is undefined and we do this:
function ProductCard({ product }) {
  return <p>${product.price.toFixed(2)}</p>;   // throws TypeError!
}

// Without an Error Boundary:
// → The entire React tree crashes
// → White screen of death
// → User sees nothing

// With an Error Boundary:
// → Only the ProductCard section crashes
// → ErrorBoundary renders a fallback UI
// → Rest of the app continues working
```

**What Error Boundaries catch:**
✅ Render method errors (`TypeError: Cannot read property`)
✅ Errors in lifecycle methods (`componentDidMount`, etc.)
✅ Errors in constructors of child components

**What Error Boundaries do NOT catch:**
❌ Errors in event handlers (use `try/catch` in the handler)
❌ Errors in async code — `setTimeout`, `fetch`, Promise rejections
❌ Errors in the Error Boundary component itself
❌ Server-side rendering errors

```
App
 ├── Navbar  (fine — separate subtree)
 └── ErrorBoundary
       └── ProductDetail → throws
             ↓
       ErrorBoundary renders <fallback> instead
       Navbar still works — crash is contained
```

---

### Slide 14: Error Boundary Implementation

**Must be a class component — no hooks equivalent (as of React 18):**
```jsx
import { Component } from 'react';

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  // Called when a descendant throws during render
  // Return new state object — MUST return something
  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  // Called after the error — use for logging
  componentDidCatch(error, errorInfo) {
    console.error('Error boundary caught:', error, errorInfo);
    // logErrorToService(error, errorInfo.componentStack);  // Sentry, DataDog, etc.
  }

  render() {
    if (this.state.hasError) {
      // Custom fallback — or use props.fallback for reusability
      return this.props.fallback ?? (
        <div className="error-fallback">
          <h2>Something went wrong.</h2>
          <p>{this.state.error?.message}</p>
          <button onClick={() => this.setState({ hasError: false, error: null })}>
            Try Again
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
```

---

### Slide 15: Error Boundary Usage

**Wrap specific subtrees — not the whole app:**
```jsx
// App.jsx — strategic placement
function App() {
  return (
    <>
      <Navbar />         {/* Intentionally OUTSIDE any boundary — must always render */}

      <Routes>
        {/* Wrap each route independently — crash in one doesn't break others */}
        <Route path="/"        element={
          <ErrorBoundary fallback={<p>Home page failed to load.</p>}>
            <Home />
          </ErrorBoundary>
        } />

        <Route path="/products/:id" element={
          <ErrorBoundary fallback={<p>Product unavailable. <Link to="/products">Browse all</Link></p>}>
            <ProductDetail />
          </ErrorBoundary>
        } />
      </Routes>
    </>
  );
}

// Or wrap at the feature level (card inside a list)
function ProductList({ products }) {
  return (
    <ul>
      {products.map(product => (
        <ErrorBoundary key={product.id} fallback={<li>Product unavailable</li>}>
          <ProductCard product={product} />
        </ErrorBoundary>
      ))}
    </ul>
  );
}
```

**react-error-boundary (popular library — avoid rewriting class components):**
```bash
npm install react-error-boundary
```
```jsx
import { ErrorBoundary } from 'react-error-boundary';

<ErrorBoundary
  fallbackRender={({ error, resetErrorBoundary }) => (
    <div>
      <p>Error: {error.message}</p>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )}
  onError={(error, info) => logError(error, info)}
>
  <ProductDetail />
</ErrorBoundary>
```

---

### Slide 16: Environment Variables in React

**Why environment variables?**
- API base URL differs between local, staging, production
- API keys must never be committed to Git
- Feature flags can be toggled per environment

**Vite (`.env` files):**
```bash
# .env.local — never committed (listed in .gitignore)
VITE_API_URL=http://localhost:8080/api
VITE_STRIPE_PUBLIC_KEY=pk_test_abc123

# .env.development — safe to commit (no secrets)
VITE_API_URL=http://localhost:8080/api
VITE_FEATURE_DARK_MODE=true

# .env.production — safe to commit (no secrets, real URL)
VITE_API_URL=https://api.myapp.com
VITE_FEATURE_DARK_MODE=false
```

**Rules:**
- Vite: must prefix with `VITE_` — anything else is private and inaccessible
- CRA (Create React App): must prefix with `REACT_APP_`
- Never put secrets (private API keys, DB passwords) in frontend env vars — they're visible to anyone who opens DevTools

**Access in code:**
```javascript
// Vite
const apiUrl = import.meta.env.VITE_API_URL;

// CRA
const apiUrl = process.env.REACT_APP_API_URL;
```

**Use in Axios instance:**
```javascript
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});
```

---

### Slide 17: Part 1 Summary

**API integration in React — the essentials:**

| Concept | Code |
|---|---|
| GET with fetch | `const res = await fetch(url); if (!res.ok) throw…; const data = await res.json();` |
| GET with axios | `const { data } = await axios.get(url);` |
| POST with axios | `await axios.post(url, body)` |
| Three-state pattern | `loading`, `data`, `error` state + useEffect |
| Custom useFetch hook | Reusable hook returning `{ data, loading, error }` |
| Axios instance | `axios.create({ baseURL, timeout })` + interceptors |
| createAsyncThunk | Dispatches pending/fulfilled/rejected; `extraReducers` handles each |
| HTTP error handling | fetch: `if (!res.ok) throw`; axios: `error.response.status` |
| Error Boundary | Class component with `getDerivedStateFromError` + `componentDidCatch` |
| What EB catches | Render errors — NOT async errors or event handler errors |
| Env variables (Vite) | `VITE_` prefix in `.env`; access via `import.meta.env.VITE_*` |

**Coming up in Part 2:**
- Writing tests with React Testing Library and Jest
- Testing the async patterns we just built
- Mocking API calls so tests never hit a real server
