# Week 4 - Day 18a: React Routing & Redux
## Part 1 Slide Descriptions

---

### Slide 1: Title Slide
**Title:** React Routing & Redux
**Subtitle:** Day 18a — Part 1: React Router v6, Navigation, Nested Routes & Higher Order Components
**Visual:** Diagram showing a multi-page React app: Home → Products → Product Detail, connected by arrows labeled "React Router". Below it, a smaller box labeled "HOC" wrapping a locked component.
**Notes:** Opening slide. Two big topics today: client-side routing (Part 1) and global state management with Redux (Part 2).

---

### Slide 2: Day 17a Recap — Where We Are
**Title:** What We Can Do After Day 17a
**Visual:** App component tree diagram with annotations

```
App
├── Navbar (useContext → theme)
├── ProductList (useState, useEffect)
│   └── ProductCard × N (useState, event handlers)
└── Footer
```

**Checklist:**
- ✅ `useState` — component memory
- ✅ `useEffect` — side effects and data fetching
- ✅ `useContext` — shared data across the tree (theme, auth user)
- ✅ Custom hooks — reusable stateful logic
- ❓ How does a user navigate to `/products/42`?
- ❓ How does the URL reflect app state?
- ❓ How does a "Add to Cart" affect a persistent global cart?

**Caption:** "We know how to build components and share data. Today we add two missing pieces: navigation and serious global state management."
**Notes:** Bridge from Day 17a. Specifically reference useContext — we'll compare it to Redux in Part 2. Students should recognize the component tree structure.

---

### Slide 3: Client-Side Routing — The Concept
**Title:** Client-Side Routing vs Server-Side Routing
**Visual:** Two-column comparison diagram

**Traditional (Server-Side) Routing:**
```
User clicks /products
    ↓
Browser makes HTTP GET /products
    ↓
Server returns a new HTML page
    ↓
Full page reload (flash, slow)
```

**Client-Side Routing (SPA):**
```
User clicks /products
    ↓
JavaScript intercepts the click
    ↓
URL bar updates to /products
    ↓
React renders the Products component
    ↓
Zero HTTP request — instant
```

**Key insight box:**
> React Router intercepts navigation events. The URL changes, the browser history updates, but the page **never reloads**. React renders the correct components for the current URL.

**Notes:** This is the "why" of React Router. Students often don't understand why we need a library for something that seems like it should just work. The key concept: the browser normally handles URL changes, React Router takes over that job.

---

### Slide 4: Installing React Router v6
**Title:** Getting Started with React Router v6
**Visual:** Setup steps with code

```bash
npm install react-router-dom
```

**Three things to know about v6:**
1. **`Routes` replaced `Switch`** — more predictable matching
2. **Relative paths everywhere** — nested routes are simpler
3. **`element` prop replaces `component` prop** — you pass JSX, not a reference

**Version note box:**

| Version | Status | Key difference |
|---|---|---|
| v5 | Legacy (still common in codebases) | `<Switch>`, `component={...}`, `useHistory` |
| **v6** (current) | **Use this** | `<Routes>`, `element={<Comp />}`, `useNavigate` |

**Minimal app structure:**
```jsx
// index.jsx or main.jsx
import { BrowserRouter } from 'react-router-dom';

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
```

**Notes:** `BrowserRouter` wraps the entire application — it sets up the routing context that every other router component and hook depends on. This is similar to how `Provider` in Context API wraps everything. Mention this once and move on; students will see the pattern repeat.

---

### Slide 5: Routes and Route — Defining Your Pages
**Title:** `<Routes>` and `<Route>` — Matching URLs to Components
**Visual:** Annotated code block

```jsx
// App.jsx
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Products from './pages/Products';
import ProductDetail from './pages/ProductDetail';
import NotFound from './pages/NotFound';

function App() {
  return (
    <div>
      <Navbar />
      <main>
        <Routes>                                      {/* renders the FIRST match */}
          <Route path="/"         element={<Home />} />
          <Route path="/products" element={<Products />} />
          <Route path="/products/:id" element={<ProductDetail />} />
          <Route path="*"         element={<NotFound />} />  {/* catch-all */}
        </Routes>
      </main>
    </div>
  );
}
```

**Key rules:**
- `<Routes>` renders only the **first** `<Route>` that matches the current URL
- `path="*"` is the wildcard — matches anything not already matched (use for 404)
- `element` receives JSX — `<ProductDetail />` not `ProductDetail`
- React Router v6 uses **exact matching by default** — no more `exact` prop needed

**Notes:** The switch from `component={ProductDetail}` (v5) to `element={<ProductDetail />}` (v6) is the most common source of confusion from tutorials. Make sure students write JSX in the element prop.

---

### Slide 6: Link and NavLink — Declarative Navigation
**Title:** `<Link>` and `<NavLink>` — Navigate Without Reloading
**Visual:** Code + rendered output side by side

**`<Link>` — basic navigation:**
```jsx
import { Link } from 'react-router-dom';

// Renders as <a href="/products"> but intercepts the click
<Link to="/products">View Products</Link>
<Link to={`/products/${product.id}`}>See Details</Link>
```

**`<NavLink>` — active state awareness:**
```jsx
import { NavLink } from 'react-router-dom';

// Automatically adds 'active' class when URL matches
<NavLink to="/products">Products</NavLink>

// Custom active styling:
<NavLink
  to="/products"
  style={({ isActive }) => ({ fontWeight: isActive ? 'bold' : 'normal' })}
  className={({ isActive }) => isActive ? 'nav-active' : ''}
>
  Products
</NavLink>
```

**CSS (auto-applied by NavLink):**
```css
.active { color: blue; border-bottom: 2px solid blue; }
```

**Comparison:**

| | `<Link>` | `<NavLink>` |
|---|---|---|
| Prevents page reload | ✅ | ✅ |
| Active class/style | ❌ | ✅ |
| Use for | Body links, buttons | Navigation bars |

**Notes:** The `isActive` callback in `NavLink` is a v6 feature. Students building navbars should use `NavLink` for every nav item so the active page is highlighted.

---

### Slide 7: useNavigate — Programmatic Navigation
**Title:** `useNavigate` — Navigate in Code (Not in JSX)
**Visual:** Three use cases with code

**Use case 1 — Redirect after form submit:**
```jsx
import { useNavigate } from 'react-router-dom';

function LoginForm() {
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    await login(credentials);
    navigate('/dashboard');         // redirect after successful login
  };
}
```

**Use case 2 — Go back:**
```jsx
<button onClick={() => navigate(-1)}>← Back</button>  // -1 = previous page
<button onClick={() => navigate(1)}>Forward →</button>
```

**Use case 3 — Replace history (no back button):**
```jsx
navigate('/login', { replace: true });
// User can't press Back to return to the page they came from
// Common after logout
```

**Notes:** `useNavigate` replaces `useHistory` from v5. The `replace` option is important for login/logout flows — after logout you don't want users pressing Back to return to an authenticated page.

---

### Slide 8: Route Parameters with useParams
**Title:** `useParams` — Dynamic URL Segments
**Visual:** URL anatomy + code

**URL structure:**
```
/products/42
         ↑
         Route parameter — declared as :id in the route path
```

**Route definition (App.jsx):**
```jsx
<Route path="/products/:id" element={<ProductDetail />} />
```

**Component reads the parameter:**
```jsx
import { useParams } from 'react-router-dom';

function ProductDetail() {
  const { id } = useParams();   // { id: '42' } — always a string!

  const productId = Number(id); // convert to number if needed

  const product = products.find(p => p.id === productId);

  if (!product) return <p>Product not found.</p>;

  return (
    <div>
      <h1>{product.name}</h1>
      <p>{product.price}</p>
    </div>
  );
}
```

**Multiple params:**
```jsx
<Route path="/users/:userId/posts/:postId" element={<Post />} />
// useParams() → { userId: '5', postId: '12' }
```

**Notes:** The string-to-number conversion is a common gotcha. URL params are always strings. If your data uses numeric IDs, you must convert. A find with strict equality (`===`) comparing string `'42'` to number `42` will always be false.

---

### Slide 9: Query Strings with useSearchParams
**Title:** `useSearchParams` — URL Query Parameters
**Visual:** URL anatomy + code

**URL structure:**
```
/products?category=electronics&sort=price&order=asc
         ↑          ↑                ↑
         ?          key=value pairs  & separates them
         starts query string
```

**Reading query params:**
```jsx
import { useSearchParams } from 'react-router-dom';

function Products() {
  const [searchParams, setSearchParams] = useSearchParams();

  const category = searchParams.get('category') ?? 'all';  // null if not present
  const sort     = searchParams.get('sort')     ?? 'name';

  const filtered = products
    .filter(p => category === 'all' || p.category === category)
    .sort((a, b) => a[sort].toString().localeCompare(b[sort].toString()));

  return (
    <div>
      <select
        value={category}
        onChange={e => setSearchParams({ category: e.target.value, sort })}
      >
        <option value="all">All</option>
        <option value="electronics">Electronics</option>
      </select>
      {/* product list */}
    </div>
  );
}
```

**Notes:** `useSearchParams` works like `useState` for the query string — reading and writing are symmetrical. `setSearchParams` updates the URL (adds browser history entry) without a page reload. This is perfect for filter/sort state that you want bookmarkable and shareable via URL.

---

### Slide 10: Nested Routes and Outlet
**Title:** Nested Routes — Shared Layouts with `<Outlet>`
**Visual:** App shell diagram

```
URL: /dashboard/profile

App
└── Layout (Navbar + Sidebar always visible)
    └── <Outlet /> → renders the matched child route
        └── Profile component appears here
```

**Route definition:**
```jsx
<Routes>
  <Route path="/dashboard" element={<DashboardLayout />}>
    <Route index         element={<DashboardHome />} />     {/* /dashboard */}
    <Route path="profile" element={<Profile />} />          {/* /dashboard/profile */}
    <Route path="settings" element={<Settings />} />        {/* /dashboard/settings */}
  </Route>
</Routes>
```

**DashboardLayout component:**
```jsx
import { Outlet } from 'react-router-dom';

function DashboardLayout() {
  return (
    <div className="dashboard">
      <Sidebar />                {/* always rendered */}
      <main>
        <Outlet />               {/* child route renders here */}
      </main>
    </div>
  );
}
```

**Notes:** `<Outlet />` is a placeholder that says "render the currently matched child route here." The parent layout stays mounted; only the child content swaps. This is how you build shared navbars, sidebars, and headers without duplicating them in every component.

---

### Slide 11: Index Routes and 404 Handling
**Title:** Index Routes and Not-Found Pages
**Visual:** Code block + behavior table

**Index routes — the default child:**
```jsx
<Route path="/dashboard" element={<DashboardLayout />}>
  <Route index element={<DashboardHome />} />     {/* renders at /dashboard exactly */}
  <Route path="profile" element={<Profile />} />
</Route>
```

**Without `index`, navigating to `/dashboard` alone would render `<DashboardLayout>` with an empty `<Outlet>` — nothing inside the main area. The `index` route fills that gap.**

**Nested 404:**
```jsx
<Route path="/dashboard" element={<DashboardLayout />}>
  <Route index element={<DashboardHome />} />
  <Route path="profile" element={<Profile />} />
  <Route path="*" element={<NotFound />} />   {/* catches /dashboard/anything-else */}
</Route>

{/* Top-level 404 */}
<Route path="*" element={<NotFound />} />    {/* catches /anything-not-listed */}
```

**Behavior summary:**

| URL | What renders |
|---|---|
| `/dashboard` | `DashboardLayout` → `DashboardHome` (index) |
| `/dashboard/profile` | `DashboardLayout` → `Profile` |
| `/dashboard/xyz` | `DashboardLayout` → `NotFound` (nested catch-all) |
| `/anything-else` | Top-level `NotFound` |

**Notes:** `index` replaces the old v5 pattern of `exact path="/"`. In v6, `index` is the idiomatic way to define the default child route.

---

### Slide 12: Higher Order Components — The Concept
**Title:** Higher Order Components (HOC) — Wrapping to Enhance
**Visual:** Function diagram

**Definition:**
> A **Higher Order Component** is a function that takes a component and returns a new, enhanced component.

```
HOC = (WrappedComponent) => EnhancedComponent
```

**Visual:**
```
withAuth(ProductDetail)
    ↓
Returns a NEW component that:
  - Checks if user is logged in
  - If yes → renders <ProductDetail />
  - If no  → redirects to /login
```

**Pattern structure:**
```jsx
function withAuth(WrappedComponent) {
  return function AuthProtected(props) {
    const { user } = useAuth();
    if (!user) return <Navigate to="/login" replace />;
    return <WrappedComponent {...props} />;   // spread all props through
  };
}

// Usage:
const ProtectedDashboard = withAuth(Dashboard);
// <ProtectedDashboard /> — if not logged in, redirects. If logged in, shows Dashboard.
```

**Notes:** HOCs were the primary pattern for cross-cutting concerns before hooks. They're still used widely in existing codebases and in libraries (React Redux's `connect` is an HOC). Understanding them is important for reading real-world React code.

---

### Slide 13: HOC Examples — withLogger and withLoading
**Title:** HOC Patterns You'll See in the Wild
**Visual:** Two practical HOC examples

**withLogger — track renders in development:**
```jsx
function withLogger(WrappedComponent) {
  return function LoggedComponent(props) {
    useEffect(() => {
      console.log(`[${WrappedComponent.displayName || WrappedComponent.name}] rendered`, props);
    });

    return <WrappedComponent {...props} />;
  };
}

const LoggedProductList = withLogger(ProductList);
```

**withLoading — show spinner while data loads:**
```jsx
function withLoading(WrappedComponent) {
  return function WithLoadingComponent({ isLoading, ...props }) {
    if (isLoading) return <div className="spinner">Loading...</div>;
    return <WrappedComponent {...props} />;
  };
}

const ProductListWithLoading = withLoading(ProductList);

// Usage:
<ProductListWithLoading isLoading={loading} products={products} />
```

**HOC vs Hooks comparison box:**

| | HOC | Custom Hook |
|---|---|---|
| Syntax | Function wrapping a component | Function starting with `use` |
| Use for | Component-level behavior | Stateful logic in a component |
| Common for | Auth protection, logging, loading | Data fetching, subscriptions |
| Composability | Stack HOCs: `withAuth(withLogger(Comp))` | Call multiple hooks inline |

**Notes:** Hooks have largely replaced HOCs for new code. But HOCs are still valuable for wrapping entire routes (protected routes) and are everywhere in legacy codebases. The `withAuth` pattern on the previous slide is the most important one to know.

---

### Slide 14: Protected Routes with React Router v6
**Title:** Protected Routes — The Modern Pattern
**Visual:** Full implementation

**The ProtectedRoute component (replaces withAuth HOC for routes):**
```jsx
import { Navigate, Outlet } from 'react-router-dom';

function ProtectedRoute({ isAuthenticated, redirectTo = '/login' }) {
  if (!isAuthenticated) {
    return <Navigate to={redirectTo} replace />;
  }
  return <Outlet />;   // render child routes if authenticated
}
```

**Using it in your route tree:**
```jsx
function App() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login"  element={<Login />} />
      <Route path="/signup" element={<Signup />} />

      {/* All routes inside here require authentication */}
      <Route element={<ProtectedRoute isAuthenticated={!!user} />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/profile"   element={<Profile />} />
        <Route path="/settings"  element={<Settings />} />
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
```

**Note box:** "This uses a layout route without a `path` — just an `element`. It acts as a guard: if the condition fails, redirect; otherwise render `<Outlet>` for all protected children."
**Notes:** This pattern (a route with only `element` and no `path`) is the v6 idiomatic replacement for the HOC-based `withAuth`. It's cleaner and uses the framework's own primitives. Show both patterns so students understand the evolution.

---

### Slide 15: useLocation — Reading the Current URL
**Title:** `useLocation` — The Full URL Object
**Visual:** Anatomy diagram + code

**What `useLocation` returns:**
```jsx
import { useLocation } from 'react-router-dom';

function SomeComponent() {
  const location = useLocation();
  /*
  location = {
    pathname: '/products/42',    ← the URL path
    search:   '?sort=price',    ← query string (raw)
    hash:     '#reviews',       ← URL fragment
    state:    { from: '/cart' } ← programmatic state (not in URL)
    key:      'abc123'          ← unique key for this history entry
  }
  */
}
```

**Common use: remember where the user came from:**
```jsx
// In a protected route — remember the attempted URL
function ProtectedRoute({ isAuthenticated }) {
  const location = useLocation();
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return <Outlet />;
}

// In the Login component — redirect back after login
function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname ?? '/dashboard';

  const handleLogin = async () => {
    await login();
    navigate(from, { replace: true });   // go back to where they tried to go
  };
}
```

**Notes:** The `state` property is invisible in the URL but travels with the navigation. This "redirect after login" pattern — saving where the user tried to go and sending them there after authentication — is used in virtually every real application.

---

### Slide 16: Complete Routing Example
**Title:** Full Routing Structure — Mini App
**Visual:** File structure + complete App.jsx

**File structure:**
```
src/
  pages/
    Home.jsx
    Products.jsx
    ProductDetail.jsx
    Dashboard.jsx
    Login.jsx
    NotFound.jsx
  components/
    Navbar.jsx
    ProtectedRoute.jsx
  App.jsx
  main.jsx
```

**Complete App.jsx:**
```jsx
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Home from './pages/Home';
import Products from './pages/Products';
import ProductDetail from './pages/ProductDetail';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import NotFound from './pages/NotFound';
import { useAuth } from './hooks/useAuth';

export default function App() {
  const { user } = useAuth();

  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/"             element={<Home />} />
        <Route path="/products"     element={<Products />} />
        <Route path="/products/:id" element={<ProductDetail />} />
        <Route path="/login"        element={<Login />} />

        <Route element={<ProtectedRoute isAuthenticated={!!user} />}>
          <Route path="/dashboard" element={<Dashboard />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Routes>
    </>
  );
}
```

**Notes:** This is the pattern students will use in projects. Walk through the structure slowly — pointing out that `<Navbar>` is outside `<Routes>` so it renders on every page, the protected route wrapper, and the catch-all.

---

### Slide 17: Part 1 Summary
**Title:** Part 1 Summary — React Routing
**Visual:** Four-section card

**🗺️ React Router v6 Setup**
- `BrowserRouter` wraps the whole app in `main.jsx`
- `Routes` → `Route path="..." element={<Component />}`
- `path="*"` catches unmatched routes (404)

**🔗 Navigation**
- `<Link to="...">` — declarative navigation (no reload)
- `<NavLink>` — same, plus automatic `active` class
- `useNavigate()` — programmatic: `navigate('/path')`, `navigate(-1)`, `navigate(path, { replace: true })`
- `useLocation()` — read current URL + carry state across navigations

**📍 Route Data**
- `useParams()` — read `:id` style path segments (always strings)
- `useSearchParams()` — read/write `?key=value` query strings

**🏗️ Structure**
- Nested routes + `<Outlet>` — shared layouts (sidebars, navbars)
- `index` routes — default child at parent path
- `<ProtectedRoute>` — guard entire route subtrees

**🧩 Higher Order Components**
- `(WrappedComponent) => EnhancedComponent`
- HOC for auth protection, logging, loading states
- Modern alternative: custom hooks + `<ProtectedRoute>` wrapper

**Coming up — Part 2:** Redux — global state management at scale.
