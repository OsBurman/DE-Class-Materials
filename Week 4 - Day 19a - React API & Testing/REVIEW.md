# Day 19a — React API & Testing
## Comprehensive Review Guide

---

## Part 1: API Integration

---

### 1. Fetch API

Browser-native HTTP client. No install required.

```javascript
// GET request
async function getProducts() {
  const response = await fetch('/api/products');

  if (!response.ok) {
    // fetch does NOT throw on 4xx/5xx — you must check manually
    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
  }

  const data = await response.json();  // parse JSON body
  return data;
}

// POST request
async function createProduct(product) {
  const response = await fetch('/api/products', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify(product)    // JS object → JSON string
  });

  if (!response.ok) throw new Error(`HTTP ${response.status}`);
  return response.json();
}
```

**Key behavior:**
- `fetch` only rejects on **network errors** (no connection, DNS failure)
- 4xx and 5xx responses **resolve** — you must check `response.ok`
- Always call `response.json()` as a second `await` — it's also async

---

### 2. Axios

Third-party library with a cleaner API. `npm install axios`.

```javascript
// api/client.js — create a reusable instance
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,   // e.g., 'http://localhost:8080/api'
  timeout: 5000
});

// Request interceptor — add auth token to every request
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Response interceptor — handle 401 globally
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

```javascript
// Usage
const response = await api.get('/products');      // response.data is parsed JSON
const created  = await api.post('/products', { name: 'Widget', price: 9.99 });
await api.put('/products/42', { price: 12.99 });
await api.delete('/products/42');
```

**Key behavior:**
- Auto-parses JSON — data is at `response.data`
- **Throws** on 4xx and 5xx — no need to check `.ok`
- `error.response` — server responded with error status
- `error.request` — request was made but no response (timeout, network error)
- `error.message` — request was never sent (config error)

**fetch vs axios:**
| Feature | fetch | axios |
|---|---|---|
| Installation | Built-in | `npm install axios` |
| JSON parsing | Manual `.json()` | Automatic (`response.data`) |
| Error on 4xx/5xx | ❌ Must check `.ok` | ✅ Throws automatically |
| Timeout | Manual (AbortController) | Built-in (`timeout` option) |
| Interceptors | ❌ Not built-in | ✅ `axios.interceptors` |
| Cancel request | `AbortController` | `axios.CancelToken` |

---

### 3. The Three-State Pattern

Every data fetch needs three pieces of state:

```javascript
function ProductList() {
  const [loading, setLoading] = useState(true);
  const [data,    setData]    = useState(null);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    let cancelled = false;   // cleanup flag: prevent updates after unmount

    async function load() {
      try {
        const response = await fetch('/api/products');
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const json = await response.json();
        if (!cancelled) setData(json);
      } catch (err) {
        if (!cancelled) setError(err.message);
      } finally {
        if (!cancelled) setLoading(false);   // always clear loading
      }
    }

    load();

    return () => { cancelled = true; };   // cleanup: cancel on unmount
  }, []);

  if (loading) return <div className="spinner" aria-label="Loading" />;
  if (error)   return <p role="alert">Error: {error}</p>;

  return (
    <ul>
      {data.map(p => <li key={p.id}>{p.name}</li>)}
    </ul>
  );
}
```

**Rules:**
- Always initialize `loading` to `true`
- Always have a `finally` block to clear loading
- Always handle `error` in JSX — never render just loading/success
- The `cancelled` flag prevents stale state updates after unmount

---

### 4. useFetch Custom Hook

Extracts the three-state pattern into a reusable hook:

```javascript
// hooks/useFetch.js
import { useState, useEffect } from 'react';

export function useFetch(url) {
  const [loading, setLoading] = useState(true);
  const [data,    setData]    = useState(null);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    fetch(url)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`);
        return res.json();
      })
      .then(json => { if (!cancelled) setData(json); })
      .catch(err  => { if (!cancelled) setError(err.message); })
      .finally(()  => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [url]);

  return { loading, data, error };
}
```

```javascript
// Usage
function ProductList() {
  const { loading, data: products, error } = useFetch('/api/products');
  // ...
}

function UserProfile({ userId }) {
  const { loading, data: user, error } = useFetch(`/api/users/${userId}`);
  // ...
}
```

---

### 5. Mutations (POST / PUT / DELETE)

Triggered by **user action** — not on mount:

```javascript
// hooks/useCreateProduct.js
export function useCreateProduct() {
  const [isLoading, setIsLoading] = useState(false);
  const [error,     setError]     = useState(null);
  const navigate = useNavigate();

  async function create(productData) {
    setIsLoading(true);
    setError(null);
    try {
      await api.post('/products', productData);
      navigate('/products');       // redirect on success
    } catch (err) {
      setError(err.response?.data?.message ?? err.message);
    } finally {
      setIsLoading(false);
    }
  }

  return { create, isLoading, error };
}
```

```javascript
// In the component
function NewProductForm() {
  const { create, isLoading, error } = useCreateProduct();

  const handleSubmit = (e) => {
    e.preventDefault();
    create({ name: formData.name, price: formData.price });
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <p role="alert">{error}</p>}
      <button type="submit" disabled={isLoading}>
        {isLoading ? 'Saving...' : 'Create Product'}
      </button>
    </form>
  );
}
```

---

### 6. createAsyncThunk — Full Pattern

For async operations that store data in Redux:

```javascript
// store/productThunks.js
import { createAsyncThunk } from '@reduxjs/toolkit';
import api from '../api/client';

export const fetchProducts = createAsyncThunk(
  'products/fetchAll',
  async (_, { rejectWithValue }) => {
    try {
      const response = await api.get('/products');
      return response.data;                       // becomes action.payload on fulfilled
    } catch (err) {
      return rejectWithValue(err.response?.data?.message ?? err.message);
    }
  }
);

export const createProduct = createAsyncThunk(
  'products/create',
  async (productData, { rejectWithValue }) => {
    try {
      const response = await api.post('/products', productData);
      return response.data;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message ?? err.message);
    }
  }
);
```

```javascript
// store/productSlice.js
import { createSlice } from '@reduxjs/toolkit';
import { fetchProducts, createProduct } from './productThunks';

const productSlice = createSlice({
  name: 'products',
  initialState: { items: [], loading: false, error: null },
  reducers: {
    clearError: state => { state.error = null; }
  },
  extraReducers: builder => {
    builder
      // fetchProducts
      .addCase(fetchProducts.pending,   state => {
        state.loading = true; state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.loading = false; state.items = action.payload;
      })
      .addCase(fetchProducts.rejected,  (state, action) => {
        state.loading = false; state.error = action.payload;
      })
      // createProduct
      .addCase(createProduct.pending,   state => {
        state.loading = true; state.error = null;
      })
      .addCase(createProduct.fulfilled, (state, action) => {
        state.loading = false; state.items.push(action.payload);
      })
      .addCase(createProduct.rejected,  (state, action) => {
        state.loading = false; state.error = action.payload;
      });
  }
});

export const { clearError } = productSlice.actions;
export default productSlice.reducer;
```

```javascript
// Component usage
function ProductsPage() {
  const dispatch  = useDispatch();
  const { items: products, loading, error } = useSelector(state => state.products);

  useEffect(() => {
    dispatch(fetchProducts());
  }, [dispatch]);

  // ...
}
```

---

### 7. Error Handling Layers

Three layers, three strategies:

```
Layer 1 — HTTP errors
  ├── response.ok === false (fetch)
  ├── axios throws on 4xx/5xx
  ├── Status 400 → Bad request (user error)
  ├── Status 401 → Unauthorized → redirect to login
  ├── Status 403 → Forbidden → show "access denied"
  ├── Status 404 → Not found → show "not found" page
  └── Status 500 → Server error → show generic error, log to Sentry

Layer 2 — Application errors
  └── Data validation — check fields exist, types are correct

Layer 3 — Render errors
  └── Error Boundary catches JavaScript errors thrown during rendering
```

---

### 8. Loading UI Patterns

```javascript
// Spinner
if (loading) return <div className="spinner" aria-label="Loading" />;

// Skeleton UI
if (loading) return (
  <div className="product-skeleton">
    <div className="skeleton-box h-6 w-48 mb-2" />
    <div className="skeleton-box h-4 w-24" />
  </div>
);

// Optimistic UI — update immediately, roll back on error
const [items, setItems] = useState(initialItems);

async function handleDelete(id) {
  const previous = items;
  setItems(prev => prev.filter(item => item.id !== id));  // instant update
  try {
    await api.delete(`/items/${id}`);
  } catch {
    setItems(previous);   // roll back on error
  }
}

// Disabled button during submit
<button type="submit" disabled={isLoading}>
  {isLoading ? 'Saving...' : 'Save'}
</button>
```

---

### 9. Error Boundaries

**What Error Boundaries catch:**
- ✅ Errors thrown during rendering (JSX evaluation)
- ✅ Errors in lifecycle methods (`componentDidMount`, `componentDidUpdate`)
- ✅ Errors in constructors of child components

**What Error Boundaries do NOT catch:**
- ❌ Errors in event handlers — use try/catch in the handler
- ❌ Async errors (setTimeout, fetch `.catch`) — catch in your async code
- ❌ Errors thrown in the boundary itself — only children are protected

**Class component implementation (the only way):**
```javascript
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    // Called during rendering phase — must be side-effect-free
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    // Called after rendering — use for logging
    console.error('Error caught by boundary:', error);
    // logToSentry(error, errorInfo.componentStack);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div role="alert">
          <h2>Something went wrong</h2>
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
```

**Usage — wrap each major section separately:**
```javascript
function App() {
  return (
    <ErrorBoundary>
      <Routes>
        <Route path="/products" element={
          <ErrorBoundary>
            <ProductsPage />
          </ErrorBoundary>
        } />
        <Route path="/cart" element={
          <ErrorBoundary>
            <CartPage />
          </ErrorBoundary>
        } />
      </Routes>
    </ErrorBoundary>
  );
}
```

**`react-error-boundary` library (recommended):**
```javascript
import { ErrorBoundary } from 'react-error-boundary';

function FallbackUI({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong: {error.message}</p>
      <button onClick={resetErrorBoundary}>Try Again</button>
    </div>
  );
}

<ErrorBoundary
  FallbackComponent={FallbackUI}
  onError={(error, info) => logToSentry(error, info)}
>
  <ProductsPage />
</ErrorBoundary>
```

---

### 10. Environment Variables

```bash
# .env.local          — local overrides, never commit
# .env.development    — used during `npm run dev`
# .env.production     — used during `npm run build`
# .env.test           — used during `npm run test`

# All client-side Vite vars must start with VITE_
VITE_API_URL=http://localhost:8080/api
VITE_FEATURE_ANALYTICS=false
```

```javascript
// Access in code
const API_URL  = import.meta.env.VITE_API_URL;
const ANALYTICS = import.meta.env.VITE_FEATURE_ANALYTICS === 'true';

// Never put secrets here — everything in VITE_ is compiled into the bundle
// ❌ VITE_SECRET_KEY=my-secret    — visible to anyone in DevTools
// ✅ Secrets live on the server only
```

| | Vite | Create React App |
|---|---|---|
| Prefix | `VITE_` | `REACT_APP_` |
| Access | `import.meta.env.VITE_*` | `process.env.REACT_APP_*` |
| Config file | `vite.config.js` | `package.json scripts` |

---

## Part 2: Testing

---

### 11. Jest / Vitest Fundamentals

```javascript
// Test structure
describe('ComponentOrFunction', () => {

  // Lifecycle hooks
  beforeAll(() => { /* runs once before all tests in this describe */ });
  afterAll(()  => { /* runs once after all tests */ });
  beforeEach(() => { /* runs before EACH test */ });
  afterEach(()  => { jest.resetAllMocks(); });   // always clean up

  it('does something specific', () => {
    // Arrange
    const input = 'hello';
    // Act
    const result = capitalize(input);
    // Assert
    expect(result).toBe('Hello');
  });
});
```

**Common matchers:**
```javascript
expect(val).toBe(42)                          // strict equality ===
expect(val).toEqual({ id: 1, name: 'x' })    // deep equality (objects/arrays)
expect(val).toBeTruthy() / .toBeFalsy()
expect(val).toBeNull() / .toBeUndefined()
expect(arr).toContain('item')
expect(arr).toHaveLength(3)
expect(() => fn()).toThrow('message')
expect(mockFn).toHaveBeenCalled()
expect(mockFn).toHaveBeenCalledTimes(2)
expect(mockFn).toHaveBeenCalledWith('arg1', 'arg2')
expect(promise).resolves.toBe(value)
expect(promise).rejects.toThrow()

// DOM matchers (from @testing-library/jest-dom)
expect(element).toBeInTheDocument()
expect(element).toHaveTextContent('hello')
expect(element).toBeVisible()
expect(element).toBeDisabled() / .toBeEnabled()
expect(element).toHaveValue('input value')
expect(element).toHaveAttribute('href', '/home')
```

---

### 12. RTL Query Methods

| Family | Not found | Multiple found | Use when... |
|---|---|---|---|
| `getBy` | Throws | Throws | Element must be present now |
| `queryBy` | Returns `null` | Throws | Asserting element is absent |
| `findBy` | Rejects (async) | Rejects | Element appears asynchronously |
| `getAllBy` | Throws | Returns array | Multiple elements expected |
| `queryAllBy` | `[]` | Returns array | Multiple elements, may be absent |
| `findAllBy` | Rejects | Returns array | Multiple async elements |

**Query priority (highest → lowest):**
```javascript
// 1. Role — most accessible
screen.getByRole('button', { name: /submit/i })
screen.getByRole('heading', { level: 2 })
screen.getByRole('textbox', { name: /email/i })
screen.getByRole('checkbox', { name: /agree/i })
screen.getByRole('combobox', { name: /country/i })  // <select>
screen.getByRole('link', { name: /home/i })

// 2. Label text
screen.getByLabelText('Email address')

// 3. Placeholder
screen.getByPlaceholderText('Search...')

// 4. Text content
screen.getByText('Submit Order')
screen.getByText(/submit/i)      // regex — case-insensitive

// 5. Display value
screen.getByDisplayValue('current@email.com')

// 6. Alt text
screen.getByAltText('Product image')

// 7. Test ID — last resort
screen.getByTestId('submit-button')
// Requires: <button data-testid="submit-button">
```

---

### 13. User Interactions

```javascript
import userEvent from '@testing-library/user-event';
import { fireEvent } from '@testing-library/react';

// userEvent — recommended (simulates real browser event sequences)
await userEvent.click(element)
await userEvent.type(input, 'hello world')
await userEvent.clear(input)
await userEvent.selectOptions(select, 'option-value')
await userEvent.upload(fileInput, file)
await userEvent.tab()          // tab key navigation
await userEvent.keyboard('{Enter}')

// fireEvent — simpler, synchronous, less realistic
fireEvent.click(element)
fireEvent.change(input, { target: { value: 'new value' } })
fireEvent.submit(form)
```

**userEvent vs fireEvent:**
- `userEvent.type` triggers: focus → keydown → keypress → input → keyup (per character) — exactly like a real user
- `fireEvent.change` triggers: just the change event — shortcut, but may miss handlers that listen to keydown

**Rule of thumb:** Use `userEvent` by default. Use `fireEvent` when you need synchronous simplicity and the component only listens for the high-level event.

---

### 14. Testing Async Components

```javascript
// findBy — polls until element appears (default timeout: 1000ms)
const element = await screen.findByText('Loaded Data');
const elements = await screen.findAllByRole('listitem');

// waitFor — run assertions until they pass or timeout
await waitFor(() => {
  expect(screen.queryByText('Loading...')).not.toBeInTheDocument();
});

// waitFor with multiple assertions
await waitFor(() => {
  expect(screen.getByText('Widget A')).toBeInTheDocument();
  expect(screen.getByText('Widget B')).toBeInTheDocument();
});

// act — wraps state updates that happen outside of RTL's knowledge
// (RTL handles this automatically for most cases — only use manually if needed)
import { act } from '@testing-library/react';

act(() => {
  someExternalEventEmitter.emit('update', newData);
});
```

---

### 15. Mocking — Three Levels

**Level 1: Mock functions with `jest.fn()`**
```javascript
const mockFn = jest.fn();
const mockFn = jest.fn(() => 'default return');   // with implementation
const mockFn = jest.fn().mockReturnValue(42);

// Return different values per call
mockFn
  .mockReturnValueOnce('first call')
  .mockReturnValueOnce('second call')
  .mockReturnValue('all subsequent calls');

// For async functions
mockFn.mockResolvedValueOnce({ id: 1, name: 'Widget' });
mockFn.mockRejectedValueOnce(new Error('Server error'));
```

**Level 2: Mock modules with `jest.mock()`**
```javascript
// At the top of the test file (hoisted automatically)
jest.mock('../api/client');   // replaces entire module with auto-mocked fns

import api from '../api/client';   // now a collection of jest.fn()s

api.get.mockResolvedValueOnce({ data: [{ id: 1 }] });
api.post.mockResolvedValueOnce({ data: { id: 2 } });
api.get.mockRejectedValueOnce(new Error('Server down'));
```

**Level 3: Mock global.fetch**
```javascript
beforeEach(() => {
  global.fetch = jest.fn((url) => {
    if (url.includes('/products')) {
      return Promise.resolve({
        ok: true,
        status: 200,
        json: () => Promise.resolve([{ id: 1, name: 'Widget' }])
      });
    }
    return Promise.reject(new Error('Unknown URL'));
  });
});

afterEach(() => {
  jest.resetAllMocks();
});
```

---

### 16. Testing Redux-Connected Components

```javascript
// src/test/renderWithProviders.jsx
import { render } from '@testing-library/react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import cartReducer from '../store/cartSlice';
import productReducer from '../store/productSlice';

export function renderWithProviders(ui, { route = '/', preloadedState = {} } = {}) {
  const store = configureStore({
    reducer: { cart: cartReducer, products: productReducer },
    preloadedState
  });
  return {
    ...render(
      <Provider store={store}>
        <MemoryRouter initialEntries={[route]}>
          {ui}
        </MemoryRouter>
      </Provider>
    ),
    store
  };
}
```

```javascript
// Usage
it('renders cart items from Redux store', () => {
  renderWithProviders(<CartPage />, {
    preloadedState: {
      cart: { items: [{ id: 1, name: 'Widget', price: 9.99, qty: 2 }], total: 19.98 }
    }
  });
  expect(screen.getByText('Widget')).toBeInTheDocument();
});

it('dispatches action and updates store', async () => {
  const { store } = renderWithProviders(<CartPage />, {
    preloadedState: { cart: { items: [{ id: 1, name: 'Widget', price: 9.99, qty: 1 }], total: 9.99 } }
  });
  await userEvent.click(screen.getByRole('button', { name: /clear cart/i }));
  expect(store.getState().cart.items).toHaveLength(0);
});
```

**Testing Redux slice reducers directly (no rendering needed):**
```javascript
import cartReducer, { addItem, removeItem, clearCart } from './cartSlice';

describe('cartSlice', () => {
  const initial = { items: [], total: 0 };
  const product = { id: 1, name: 'Widget', price: 9.99 };

  it('adds item', () => {
    const state = cartReducer(initial, addItem(product));
    expect(state.items).toHaveLength(1);
    expect(state.total).toBeCloseTo(9.99);
  });

  it('clears cart', () => {
    const filled = cartReducer(initial, addItem(product));
    const cleared = cartReducer(filled, clearCart());
    expect(cleared.items).toHaveLength(0);
    expect(cleared.total).toBe(0);
  });
});
```

---

### 17. Testing Custom Hooks with renderHook

```javascript
import { renderHook, waitFor } from '@testing-library/react';
import { useFetch } from './useFetch';

describe('useFetch', () => {
  afterEach(() => jest.resetAllMocks());

  it('starts in loading state', () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
    );

    const { result } = renderHook(() => useFetch('/api/products'));

    expect(result.current.loading).toBe(true);
    expect(result.current.data).toBeNull();
    expect(result.current.error).toBeNull();
  });

  it('returns data on success', async () => {
    const mockData = [{ id: 1, name: 'Widget' }];
    global.fetch = jest.fn(() =>
      Promise.resolve({ ok: true, json: () => Promise.resolve(mockData) })
    );

    const { result } = renderHook(() => useFetch('/api/products'));

    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.data).toEqual(mockData);
    expect(result.current.error).toBeNull();
  });

  it('returns error on failure', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({ ok: false, status: 500, statusText: 'Server Error' })
    );

    const { result } = renderHook(() => useFetch('/api/products'));

    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.error).toContain('500');
    expect(result.current.data).toBeNull();
  });
});
```

---

### 18. Environment Variables in Tests

```bash
# .env.test
VITE_API_URL=http://localhost:8080/api
VITE_FEATURE_BETA=false
```

```javascript
// vitest.config.js — inject at config level
export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    env: {
      VITE_API_URL: 'http://localhost:8080/api',
    }
  }
});
```

```javascript
// Temporarily override in a single test
it('hides beta features when flag is false', () => {
  const original = import.meta.env.VITE_FEATURE_BETA;
  import.meta.env.VITE_FEATURE_BETA = 'false';

  render(<App />);
  expect(screen.queryByTestId('beta-panel')).not.toBeInTheDocument();

  import.meta.env.VITE_FEATURE_BETA = original;
});
```

---

### 19. Common Mistakes & Fixes

| Mistake | Fix |
|---|---|
| Using `getBy` to assert element is gone | Use `queryBy` then `not.toBeInTheDocument()` |
| Not awaiting `userEvent.click` | Always `await userEvent.*` — it's async |
| Forgetting `await` on `findBy` queries | `findBy` returns a Promise — must await |
| Using `toBe` on objects/arrays | Use `toEqual` for deep equality |
| Mocks leaking between tests | Add `afterEach(() => jest.resetAllMocks())` |
| Not wrapping component in `<Provider>` | Create `renderWithProviders` utility |
| Not wrapping component in `<MemoryRouter>` | Include Router in `renderWithProviders` |
| Querying by `data-testid` when role works | Query by role first — better for accessibility |
| Asserting `loading` is false immediately | Use `waitFor` or `findBy` for async state |
| Calling `render` inside `beforeEach` for all tests | Call `render` inside each `it` — clearer intent |

---

### 20. Test File Organization

```
src/
  components/
    ProductCard/
      ProductCard.jsx
      ProductCard.test.jsx      ← co-located
  hooks/
    useFetch.js
    useFetch.test.js
  store/
    cartSlice.js
    cartSlice.test.js           ← pure function tests — fastest
  utils/
    formatPrice.js
    formatPrice.test.js
  test/
    setup.js                    ← @testing-library/jest-dom import
    renderWithProviders.jsx     ← shared test wrapper
```

**Vitest config:**
```javascript
// vite.config.js
test: {
  globals: true,
  environment: 'jsdom',
  setupFiles: ['./src/test/setup.js'],
  coverage: {
    reporter: ['text', 'html'],
    exclude: ['src/test/**']
  }
}
```

---

### 21. Looking Ahead

**Day 19b — Angular HTTP & RxJS:**
- `HttpClient` — Angular's equivalent of `fetch`/`axios`
- Observables — Angular uses RxJS streams instead of Promises
- `async` pipe — subscribe in templates without `.subscribe()` in component
- `catchError`, `switchMap` — RxJS operators for error handling and request cancellation
- Interceptors — same concept as axios interceptors, Angular-style

**Day 20a — React Advanced & Deployment:**
- `React.memo`, `useMemo`, `useCallback` — prevent unnecessary re-renders
- `React.lazy` + `Suspense` — code splitting: only load bundles when needed
- React DevTools — flame graph, profiler, component inspection
- Production build with Vite: `npm run build` → `dist/` folder
- Deployment strategies: static hosting (Netlify, Vercel), environment-specific builds

**Week 6 — Testing goes deeper:**
- Day 28: Testing Java apps — JUnit, Mockito, `@WebMvcTest`, `@DataJpaTest`
- Mocking service layers with Mockito mirrors `jest.mock()` conceptually
- Same AAA pattern, same test isolation philosophy — different language, same principles
