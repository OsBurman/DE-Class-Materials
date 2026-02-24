# Day 19a — Part 2: React Testing Library & Jest
## Slide Descriptions

---

### Slide 1: Title Slide
**"React Testing Library & Jest"**
Subtitle: Component Tests, Async Testing, Mocking APIs, Environment Variables
Week 4 – Day 19a, Part 2

---

### Slide 2: Why Test? + Testing Overview

**The case for tests:**
- Catch regressions before users do — "it worked last week" is not a testing strategy
- Refactor with confidence — tests prove behavior is preserved
- Tests are documentation — a well-named test describes exactly what the code does
- Required for CI/CD pipelines — automated tests run on every pull request

**The testing pyramid:**
```
          ┌────────────────┐
          │   E2E Tests    │  ← Cypress, Playwright — slow, few
          │  (full stack)  │     test real user journeys
          ├────────────────┤
          │  Integration   │  ← Component trees, API + DB
          │    Tests       │     test that pieces work together
          ├────────────────┤
          │  Unit Tests    │  ← Fast, many, isolated
          │  (most tests)  │     test one function/component at a time
          └────────────────┘
```

**Today's focus:** Unit tests and integration-level component tests using React Testing Library + Jest.

**Tools involved:**
- **Jest** — test runner, assertion library, mocking system
- **React Testing Library (RTL)** — renders components and queries the DOM the way a user would
- **@testing-library/user-event** — simulates realistic user interactions
- **jsdom** — simulates a browser environment in Node.js (no real browser needed)

---

### Slide 3: Test Setup in a Vite Project

**Install:**
```bash
npm install --save-dev vitest jsdom @testing-library/react @testing-library/jest-dom @testing-library/user-event
```

**`vite.config.js` — add test configuration:**
```javascript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,          // no need to import describe/it/expect manually
    environment: 'jsdom',   // simulate browser DOM in Node
    setupFiles: ['./src/test/setup.js'],
  }
});
```

**`src/test/setup.js` — extend matchers:**
```javascript
import '@testing-library/jest-dom';
// Adds DOM-specific matchers:
// toBeInTheDocument(), toHaveTextContent(), toBeVisible(), toBeDisabled(), etc.
```

**Run tests:**
```bash
npm run test           # watch mode
npm run test -- --run  # single run (CI)
```

**Note on CRA:** `create-react-app` projects already include Jest + RTL with no configuration needed.

---

### Slide 4: Jest Fundamentals

```javascript
// src/utils/formatPrice.test.js
import { formatPrice } from './formatPrice';

// describe: groups related tests
describe('formatPrice', () => {

  // it / test: individual test case
  it('formats a number as USD currency', () => {
    // Arrange-Act-Assert pattern
    const result = formatPrice(9.99);                  // Act
    expect(result).toBe('$9.99');                      // Assert
  });

  it('rounds to 2 decimal places', () => {
    expect(formatPrice(9.999)).toBe('$10.00');
  });

  it('handles zero', () => {
    expect(formatPrice(0)).toBe('$0.00');
  });

  it('throws on negative price', () => {
    expect(() => formatPrice(-1)).toThrow('Price cannot be negative');
  });
});
```

**Common matchers:**
```javascript
expect(value).toBe(42)                     // strict equality (===)
expect(value).toEqual({ id: 1 })           // deep equality (objects/arrays)
expect(value).toBeTruthy()                 // not null/undefined/0/''
expect(value).toBeFalsy()
expect(value).toBeNull()
expect(value).toBeUndefined()
expect(array).toContain('item')
expect(array).toHaveLength(3)
expect(fn).toHaveBeenCalled()
expect(fn).toHaveBeenCalledWith('arg1')
expect(fn).toHaveBeenCalledTimes(2)
expect(promise).resolves.toBe(value)       // async — resolves to value
expect(promise).rejects.toThrow()          // async — rejects
```

---

### Slide 5: React Testing Library — Core Philosophy

**RTL's guiding principle:**
> "The more your tests resemble the way your software is used, the more confidence they can give you."
> — Kent C. Dodds

**Test behavior, not implementation:**
```javascript
// ❌ Implementation test — tests internal state, breaks on refactor
expect(wrapper.state('count')).toBe(1);
expect(component.find('Counter').prop('value')).toBe(1);

// ✅ Behavior test — tests what the user sees, survives refactoring
expect(screen.getByText('Count: 1')).toBeInTheDocument();
```

**Core API:**
```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Counter from './Counter';

test('increments count when button is clicked', async () => {
  // Render the component into jsdom
  render(<Counter />);

  // Query: find the button by its accessible role and name
  const button = screen.getByRole('button', { name: /increment/i });

  // Act: simulate a click
  await userEvent.click(button);

  // Assert: check what changed in the DOM
  expect(screen.getByText('Count: 1')).toBeInTheDocument();
});
```

---

### Slide 6: RTL Query Methods

**Three query families — each with different failure behavior:**

| Query | No match | Multiple matches | Use when... |
|---|---|---|---|
| `getBy...` | Throws error | Throws error | Element should be there right now |
| `queryBy...` | Returns `null` | Throws error | Checking element is ABSENT |
| `findBy...` | Throws (async) | Throws | Element appears asynchronously |

**Query selectors — in priority order (most to least accessible):**
```javascript
// 1. Role — best: mirrors how screen readers see the page
screen.getByRole('button', { name: /submit/i })
screen.getByRole('heading', { level: 1 })
screen.getByRole('textbox', { name: /email/i })
screen.getByRole('checkbox', { name: /agree to terms/i })

// 2. Label — for form inputs with <label>
screen.getByLabelText('Email address')

// 3. Placeholder
screen.getByPlaceholderText('Enter your email')

// 4. Text content
screen.getByText('Submit Order')
screen.getByText(/submit/i)  // regex — case-insensitive

// 5. Display value (current value of input/select)
screen.getByDisplayValue('current@email.com')

// 6. Alt text (images)
screen.getByAltText('Product photo')

// 7. Test ID — last resort (not visible to users)
screen.getByTestId('product-card-42')
// Use <div data-testid="product-card-42"> in component
```

---

### Slide 7: Testing a Component — Full Example

```javascript
// ProductCard.jsx
function ProductCard({ product, onAddToCart }) {
  return (
    <div>
      <h2>{product.name}</h2>
      <p>${product.price.toFixed(2)}</p>
      <button onClick={() => onAddToCart(product)}>Add to Cart</button>
    </div>
  );
}
```

```javascript
// ProductCard.test.jsx
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProductCard from './ProductCard';

const mockProduct = { id: 1, name: 'Test Widget', price: 9.99 };

describe('ProductCard', () => {

  it('renders the product name and price', () => {
    render(<ProductCard product={mockProduct} onAddToCart={() => {}} />);

    expect(screen.getByText('Test Widget')).toBeInTheDocument();
    expect(screen.getByText('$9.99')).toBeInTheDocument();
  });

  it('calls onAddToCart with the product when button is clicked', async () => {
    const mockAddToCart = jest.fn();   // mock function — tracks calls

    render(<ProductCard product={mockProduct} onAddToCart={mockAddToCart} />);

    const button = screen.getByRole('button', { name: /add to cart/i });
    await userEvent.click(button);

    expect(mockAddToCart).toHaveBeenCalledTimes(1);
    expect(mockAddToCart).toHaveBeenCalledWith(mockProduct);
  });

  it('renders the Add to Cart button', () => {
    render(<ProductCard product={mockProduct} onAddToCart={() => {}} />);
    expect(screen.getByRole('button', { name: /add to cart/i })).toBeInTheDocument();
  });
});
```

---

### Slide 8: Testing Async Components — waitFor and findBy

```javascript
// ProductList.jsx — fetches data on mount
function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading]   = useState(true);

  useEffect(() => {
    fetch('/api/products')
      .then(r => r.json())
      .then(data => { setProducts(data); setLoading(false); });
  }, []);

  if (loading) return <p>Loading products...</p>;
  return <ul>{products.map(p => <li key={p.id}>{p.name}</li>)}</ul>;
}
```

```javascript
// ProductList.test.jsx
import { render, screen, waitFor } from '@testing-library/react';

// Mock fetch before tests run
beforeEach(() => {
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () => Promise.resolve([
        { id: 1, name: 'Widget A' },
        { id: 2, name: 'Widget B' }
      ])
    })
  );
});

afterEach(() => {
  jest.resetAllMocks();   // clean up between tests
});

it('shows loading state initially', () => {
  render(<ProductList />);
  expect(screen.getByText('Loading products...')).toBeInTheDocument();
});

it('renders products after fetching', async () => {
  render(<ProductList />);

  // findBy* = queries that wait for the element (async)
  // Polls until the element appears or times out (default 1000ms)
  const widgetA = await screen.findByText('Widget A');
  expect(widgetA).toBeInTheDocument();
  expect(screen.getByText('Widget B')).toBeInTheDocument();

  // Or use waitFor for more complex assertions
  await waitFor(() => {
    expect(screen.queryByText('Loading products...')).not.toBeInTheDocument();
  });
});
```

---

### Slide 9: Testing Hooks — renderHook and act

```javascript
// useFetch.test.js
import { renderHook, act, waitFor } from '@testing-library/react';
import { useFetch } from './useFetch';

describe('useFetch', () => {
  beforeEach(() => {
    global.fetch = jest.fn();
  });

  it('starts with loading=true', () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve([])
    });

    const { result } = renderHook(() => useFetch('/api/products'));

    // Immediately after render, before fetch resolves:
    expect(result.current.loading).toBe(true);
    expect(result.current.data).toBeNull();
    expect(result.current.error).toBeNull();
  });

  it('returns data when fetch succeeds', async () => {
    const mockData = [{ id: 1, name: 'Widget' }];
    fetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(mockData)
    });

    const { result } = renderHook(() => useFetch('/api/products'));

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.data).toEqual(mockData);
    expect(result.current.error).toBeNull();
  });

  it('returns error when fetch fails', async () => {
    fetch.mockResolvedValueOnce({ ok: false, status: 500, statusText: 'Server Error' });

    const { result } = renderHook(() => useFetch('/api/products'));

    await waitFor(() => expect(result.current.loading).toBe(false));

    expect(result.current.error).toBe('HTTP 500: Server Error');
    expect(result.current.data).toBeNull();
  });
});
```

---

### Slide 10: Testing Redux-Connected Components

**Provide a test store — don't use your real store:**
```javascript
// src/test/renderWithRedux.jsx — test utility
import { render } from '@testing-library/react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import cartReducer from '../store/cartSlice';
import userReducer from '../store/userSlice';

export function renderWithRedux(
  ui,
  { preloadedState = {}, store = configureStore({
    reducer: { cart: cartReducer, user: userReducer },
    preloadedState
  }) } = {}
) {
  function Wrapper({ children }) {
    return <Provider store={store}>{children}</Provider>;
  }
  return { ...render(ui, { wrapper: Wrapper }), store };
}
```

```javascript
// CartPage.test.jsx
import { renderWithRedux } from '../test/renderWithRedux';
import CartPage from './CartPage';
import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

it('renders cart items from the store', () => {
  renderWithRedux(<CartPage />, {
    preloadedState: {
      cart: {
        items: [{ id: 1, name: 'Widget', price: 9.99, qty: 2 }],
        total: 19.98
      }
    }
  });

  expect(screen.getByText('Widget')).toBeInTheDocument();
  expect(screen.getByText('$19.98')).toBeInTheDocument();
});

it('dispatches clearCart when Clear button is clicked', async () => {
  const { store } = renderWithRedux(<CartPage />, {
    preloadedState: { cart: { items: [{ id: 1, name: 'Widget', price: 9.99, qty: 1 }], total: 9.99 } }
  });

  await userEvent.click(screen.getByRole('button', { name: /clear cart/i }));

  expect(store.getState().cart.items).toHaveLength(0);
});
```

---

### Slide 11: Mocking API Calls — jest.mock

**Mock entire modules:**
```javascript
// Mock the entire api module
jest.mock('../api/client');
import api from '../api/client';   // now a mocked module

// Set return values per test
api.get.mockResolvedValueOnce({ data: [{ id: 1, name: 'Widget' }] });
api.post.mockResolvedValueOnce({ data: { id: 2, name: 'New Widget' } });
api.delete.mockResolvedValueOnce({ data: null });

// Mock an error
api.get.mockRejectedValueOnce(new Error('Network error'));
```

```javascript
// ProductsPage.test.jsx
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithRedux } from '../test/renderWithRedux';

jest.mock('../api/client');
import api from '../api/client';

describe('ProductsPage', () => {
  it('loads and displays products', async () => {
    api.get.mockResolvedValueOnce({
      data: [{ id: 1, name: 'Widget A', price: 9.99 }]
    });

    renderWithRedux(<ProductsPage />);

    expect(screen.getByText('Loading...')).toBeInTheDocument();

    await screen.findByText('Widget A');
    expect(screen.getByText('$9.99')).toBeInTheDocument();
  });

  it('shows error message on API failure', async () => {
    api.get.mockRejectedValueOnce(new Error('Server unavailable'));

    renderWithRedux(<ProductsPage />);

    await screen.findByText(/server unavailable/i);
  });
});
```

---

### Slide 12: Mocking fetch Globally

**When not using axios — mock `global.fetch`:**
```javascript
// In a test file or setup.js
const mockFetch = (data, ok = true, status = 200) => {
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok,
      status,
      statusText: ok ? 'OK' : 'Error',
      json: () => Promise.resolve(data),
      text: () => Promise.resolve(JSON.stringify(data))
    })
  );
};

// Usage in tests
describe('LoginForm', () => {
  afterEach(() => jest.resetAllMocks());

  it('redirects to dashboard after successful login', async () => {
    mockFetch({ token: 'abc123', user: { name: 'Alice' } });

    render(<LoginForm />);

    await userEvent.type(screen.getByLabelText(/email/i), 'alice@example.com');
    await userEvent.type(screen.getByLabelText(/password/i), 'password123');
    await userEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(window.location.pathname).toBe('/dashboard');
    });
  });

  it('shows error message on invalid credentials', async () => {
    mockFetch({ message: 'Invalid credentials' }, false, 401);

    render(<LoginForm />);
    await userEvent.click(screen.getByRole('button', { name: /login/i }));

    await screen.findByText('Invalid credentials');
  });
});
```

---

### Slide 13: Environment Variables in Tests

```javascript
// Vite: import.meta.env.VITE_API_URL
// In tests running in Node (jsdom), import.meta.env is mocked by Vite/Vitest

// vitest.config.js — inject test env values
export default defineConfig({
  test: {
    env: {
      VITE_API_URL: 'http://localhost:8080/api',
      VITE_FEATURE_FLAG: 'true'
    }
  }
});

// Or use a .env.test file
// .env.test
// VITE_API_URL=http://localhost:8080/api
```

**Testing feature flags:**
```javascript
it('shows the beta feature when flag is enabled', () => {
  // Temporarily override env value in this test
  const original = import.meta.env.VITE_FEATURE_BETA;
  import.meta.env.VITE_FEATURE_BETA = 'true';

  render(<App />);
  expect(screen.getByTestId('beta-feature')).toBeInTheDocument();

  import.meta.env.VITE_FEATURE_BETA = original;  // restore
});
```

**In Jest / CRA:**
```javascript
// Set env variable before the test
process.env.REACT_APP_API_URL = 'http://localhost:8080/api';
```

---

### Slide 14: Testing Best Practices

**What TO test:**
```javascript
// ✅ User-visible behavior
expect(screen.getByText('Welcome, Alice')).toBeInTheDocument();

// ✅ Accessibility (roles, labels)
expect(screen.getByRole('button', { name: /submit/i })).toBeEnabled();

// ✅ Conditional rendering
expect(screen.queryByText('Loading...')).not.toBeInTheDocument();

// ✅ User interactions and their results
await userEvent.click(button);
expect(mockOnSubmit).toHaveBeenCalledWith({ email: 'a@b.com' });

// ✅ Error states
expect(screen.getByText(/email is required/i)).toBeInTheDocument();
```

**What NOT to test:**
```javascript
// ❌ CSS classes (implementation detail)
expect(button).toHaveClass('btn-primary');

// ❌ State variable values directly
expect(component.state.isOpen).toBe(true);

// ❌ Third-party library internals
expect(Router.navigate).toHaveBeenCalledWith('/home');

// ❌ Every prop that gets passed (trust the framework)
expect(ProductCard).toHaveBeenCalledWith({ product: {...} }, {});
```

**The ARRANGE-ACT-ASSERT pattern:**
```javascript
it('shows cart count after adding item', async () => {
  // ARRANGE — set up the initial state
  renderWithRedux(<App />, { preloadedState: { cart: { items: [], total: 0 } } });

  // ACT — simulate user interaction
  await userEvent.click(screen.getAllByRole('button', { name: /add to cart/i })[0]);

  // ASSERT — verify the expected outcome
  expect(screen.getByText('Cart (1)')).toBeInTheDocument();
});
```

---

### Slide 15: Test File Organization

**Conventional structure:**
```
src/
  components/
    ProductCard/
      ProductCard.jsx
      ProductCard.test.jsx    ← co-located with component
      ProductCard.module.css
  hooks/
    useFetch.js
    useFetch.test.js          ← co-located with hook
  store/
    cartSlice.js
    cartSlice.test.js         ← test slice reducers directly (pure functions!)
  utils/
    formatPrice.js
    formatPrice.test.js
  test/
    setup.js                  ← global test setup
    renderWithRedux.jsx       ← shared test utilities
    renderWithRouter.jsx      ← Router wrapper for routing-dependent components
```

**Testing slice reducers directly (easiest tests you'll write):**
```javascript
// cartSlice.test.js
import cartReducer, { addItem, removeItem, clearCart } from './cartSlice';

describe('cartSlice', () => {
  const initialState = { items: [], total: 0 };
  const product = { id: 1, name: 'Widget', price: 9.99 };

  it('adds an item to the cart', () => {
    const newState = cartReducer(initialState, addItem(product));
    expect(newState.items).toHaveLength(1);
    expect(newState.total).toBeCloseTo(9.99);
  });

  it('increments qty when adding an existing item', () => {
    const state1 = cartReducer(initialState, addItem(product));
    const state2 = cartReducer(state1, addItem(product));
    expect(state2.items[0].qty).toBe(2);
  });

  it('clears the cart', () => {
    const filledState = cartReducer(initialState, addItem(product));
    const clearedState = cartReducer(filledState, clearCart());
    expect(clearedState.items).toHaveLength(0);
    expect(clearedState.total).toBe(0);
  });
});
```

---

### Slide 16: renderWithRouter + Full Integration Test

```javascript
// src/test/renderWithRouter.jsx
import { render } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import cartReducer from '../store/cartSlice';

export function renderWithProviders(ui, { route = '/', preloadedState = {} } = {}) {
  const store = configureStore({
    reducer: { cart: cartReducer },
    preloadedState
  });

  return render(
    <Provider store={store}>
      <MemoryRouter initialEntries={[route]}>
        {ui}
      </MemoryRouter>
    </Provider>
  );
}
```

```javascript
// ProductDetail.test.jsx — routing + Redux + async
import { renderWithProviders } from '../test/renderWithRouter';
jest.mock('../api/client');
import api from '../api/client';

it('renders product detail after fetch', async () => {
  api.get.mockResolvedValueOnce({
    data: { id: 42, name: 'Super Widget', price: 29.99 }
  });

  renderWithProviders(
    <Routes>
      <Route path="/products/:id" element={<ProductDetail />} />
    </Routes>,
    { route: '/products/42' }
  );

  await screen.findByText('Super Widget');
  expect(screen.getByText('$29.99')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /add to cart/i })).toBeEnabled();
});
```

---

### Slide 17: Day 19a Summary + Looking Ahead

**Part 1 Recap — API Integration:**
- `fetch` needs manual `response.ok` check + `.json()` call; `axios` auto-parses and auto-throws on errors
- **Three-state pattern**: `loading` / `data` / `error` — never skip loading or error states
- `useFetch` custom hook — reusable, cleanup with `cancelled` flag prevents stale updates
- `createAsyncThunk` — full implementation: `rejectWithValue`, `extraReducers`, pending/fulfilled/rejected
- Error Boundary — class component only; catches render errors, NOT async or event handler errors
- `react-error-boundary` package — functional API wrapper around class component

**Part 2 Recap — Testing:**
- Jest: `describe`, `it`, `expect`, `jest.fn()`, `jest.mock()`, `mockResolvedValueOnce`
- RTL queries: `getBy` (must exist), `queryBy` (may not exist), `findBy` (async)
- Query priority: role > labelText > placeholder > text > testId
- Async testing: `await screen.findBy*` or `await waitFor(() => ...)`
- Mock `global.fetch` or `jest.mock('../api/client')` — tests never hit real servers
- Slice reducers are pure functions — test them directly without rendering anything
- `renderWithRedux` / `renderWithProviders` — always provide context in tests

**Coming up — Day 20a: React Advanced & Deployment:**
- Component composition patterns: render props, compound components
- `React.memo`, `useMemo`, `useCallback` — performance optimization
- Code splitting with `React.lazy` + `Suspense`
- React DevTools — flame graph, profiling renders
- Building for production with Vite + deployment strategies
