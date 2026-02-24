# Day 20a — React Advanced & Deployment: Complete Reference

**Topics:** Component Composition, Render Props, Compound Components, React.memo, useCallback, useMemo, React.lazy, Suspense, Error Boundaries, useTransition, useDeferredValue, React DevTools, Production Builds, Environment Variables, Deployment, Project Structure

---

## Table of Contents
1. [Component Composition — children and Slots](#1-component-composition--children-and-slots)
2. [Render Props Pattern](#2-render-props-pattern)
3. [Compound Components Pattern](#3-compound-components-pattern)
4. [React.memo](#4-reactmemo)
5. [useCallback](#5-usecallback)
6. [useMemo](#6-usememo)
7. [When to Optimize vs Not](#7-when-to-optimize-vs-not)
8. [Code Splitting — React.lazy and dynamic import](#8-code-splitting--reactlazy-and-dynamic-import)
9. [Suspense and Error Boundaries](#9-suspense-and-error-boundaries)
10. [Route-Based Code Splitting](#10-route-based-code-splitting)
11. [useTransition](#11-usetransition)
12. [useDeferredValue](#12-usedeferredvalue)
13. [React DevTools](#13-react-devtools)
14. [Building for Production](#14-building-for-production)
15. [Environment Variables](#15-environment-variables)
16. [Deployment](#16-deployment)
17. [Project Structure Best Practices](#17-project-structure-best-practices)
18. [Common Mistakes & Fixes](#18-common-mistakes--fixes)
19. [Quick Reference Syntax](#19-quick-reference-syntax)
20. [Looking Ahead](#20-looking-ahead)

---

## Learning Objectives Checklist

- [ ] Implement `props.children` and component injection for composable layouts
- [ ] Write a render props component and explain when to use it vs a custom hook
- [ ] Build a compound component using Context
- [ ] Apply `React.memo`, `useCallback`, `useMemo` to prevent unnecessary re-renders
- [ ] Explain when NOT to optimize (and why measuring comes first)
- [ ] Implement route-based code splitting with `React.lazy` and `Suspense`
- [ ] Add an error boundary around lazy-loaded components
- [ ] Use `useTransition` to keep the UI responsive during expensive state updates
- [ ] Profile a React app with the React DevTools Profiler
- [ ] Run `npm run build` and test the production output locally
- [ ] Set up environment variables for development and production
- [ ] Deploy a React app to Netlify or Vercel
- [ ] Organize a project with feature-based folder structure

---

## 1. Component Composition — children and Slots

### props.children — Single Content Slot

```jsx
function Card({ title, children }) {
  return (
    <div className="card">
      {title && <div className="card-header">{title}</div>}
      <div className="card-body">{children}</div>
    </div>
  );
}

// Usage — parent controls content; Card never needs to change
<Card title="User Profile">
  <Avatar src={user.avatar} />
  <p>{user.bio}</p>
</Card>
```

### Component Injection — Multiple Named Slots

```jsx
function PageLayout({ header, sidebar, children }) {
  return (
    <div className="layout">
      <header>{header}</header>
      <div className="content">
        <aside>{sidebar}</aside>
        <main>{children}</main>
      </div>
    </div>
  );
}

// Usage — PageLayout knows nothing about NavBar, CategoryList, or ProductGrid
<PageLayout
  header={<NavBar user={currentUser} />}
  sidebar={<CategoryList />}
>
  <ProductGrid />
</PageLayout>
```

**Rule:**
- `children` → single content slot
- Named props (`header`, `sidebar`) → multiple distinct layout regions

---

## 2. Render Props Pattern

```jsx
// Provider component — manages behavior (state + events), delegates UI
function MouseTracker({ render }) {
  const [position, setPosition] = useState({ x: 0, y: 0 });
  return (
    <div
      onMouseMove={e => setPosition({ x: e.clientX, y: e.clientY })}
      style={{ height: '100%' }}
    >
      {render(position)}
    </div>
  );
}

// Consumer — provides the UI as a function
<MouseTracker render={({ x, y }) => <p>({x}, {y})</p>} />

// children-as-function variant (same concept, different syntax)
<MouseTracker>
  {({ x, y }) => <p>({x}, {y})</p>}
</MouseTracker>
```

### Render Props vs Custom Hooks

| | Render Props | Custom Hooks |
|---|---|---|
| Extra tree nodes | ✅ Yes — wrapper components | ❌ None |
| Multiple logic sources | Requires nesting | Call multiple hooks in sequence |
| Syntax | JSX + function prop | `const data = useMyHook()` |
| Best for | Logic requiring owned DOM event listener | Everything else |

```jsx
// Same logic as a custom hook (preferred for new code)
function useMousePosition() {
  const [position, setPosition] = useState({ x: 0, y: 0 });
  useEffect(() => {
    const handler = e => setPosition({ x: e.clientX, y: e.clientY });
    window.addEventListener('mousemove', handler);
    return () => window.removeEventListener('mousemove', handler);
  }, []);
  return position;
}

function Component() {
  const { x, y } = useMousePosition(); // zero wrapper components
  return <p>({x}, {y})</p>;
}
```

---

## 3. Compound Components Pattern

```jsx
// 1. Context for shared state
const TabsContext = createContext(null);

// 2. Parent — manages state, provides Context
function Tabs({ children, defaultTab = 0 }) {
  const [activeTab, setActiveTab] = useState(defaultTab);
  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <div className="tabs">{children}</div>
    </TabsContext.Provider>
  );
}

// 3. Sub-components — consume Context; cooperate without explicit prop passing
function Tab({ children, index }) {
  const { activeTab, setActiveTab } = useContext(TabsContext);
  return (
    <button
      className={activeTab === index ? 'tab active' : 'tab'}
      onClick={() => setActiveTab(index)}
    >
      {children}
    </button>
  );
}

function TabPanel({ children, index }) {
  const { activeTab } = useContext(TabsContext);
  return activeTab === index ? <div className="tab-panel">{children}</div> : null;
}

// 4. Attach as static properties — ergonomic single import
Tabs.Tab      = Tab;
Tabs.TabPanel = TabPanel;
```

### Usage

```jsx
<Tabs defaultTab={0}>
  <div className="tab-list">
    <Tabs.Tab index={0}>Details</Tabs.Tab>
    <Tabs.Tab index={1}>Reviews</Tabs.Tab>
  </div>
  <Tabs.TabPanel index={0}><ProductDetails /></Tabs.TabPanel>
  <Tabs.TabPanel index={1}><ReviewList /></Tabs.TabPanel>
</Tabs>
```

**When to use:** Tabs, accordions, dropdowns, modals with header/body/footer, any UI with tightly coupled, cooperating pieces. Used extensively in Headless UI, Radix UI, Reach UI.

---

## 4. React.memo

**Wraps a component and skips re-render if all props are the same (shallow `===` comparison):**

```jsx
// Arrow function component
const ExpensiveList = React.memo(({ items, onDelete }) => {
  return <ul>{items.map(i => <li key={i.id}>{i.name}</li>)}</ul>;
});

// Named function declaration
const ExpensiveList = React.memo(function ExpensiveList({ items }) {
  return <ul>{items.map(i => <li key={i.id}>{i.name}</li>)}</ul>;
});

// On an existing component
export default React.memo(ExpensiveList);
```

**⚠️ Object and function props bypass memo without useCallback/useMemo:**

```jsx
// ❌ New function reference on every render — memo sees "new props" and re-renders anyway
<Memo onClick={() => doSomething()} config={{ theme: 'dark' }} />

// ✅ Stable references — memo works correctly
const handleClick = useCallback(() => doSomething(), []);
const config      = useMemo(() => ({ theme: 'dark' }), []);
<Memo onClick={handleClick} config={config} />
```

**Custom comparison (rarely needed):**
```jsx
const MyComponent = React.memo(
  ({ user }) => <div>{user.name}</div>,
  (prevProps, nextProps) => prevProps.user.id === nextProps.user.id
  // Return true to SKIP re-render; false to allow it
);
```

---

## 5. useCallback

**Returns a stable function reference — same function object between renders unless dependencies change:**

```jsx
import { useCallback } from 'react';

// No deps needed — useState setters are guaranteed stable by React
const handleDelete = useCallback((id) => {
  setItems(prev => prev.filter(item => item.id !== id));
}, []);

// With deps — include any state/prop used inside the callback
const handleSearch = useCallback((query) => {
  return items.filter(item =>
    item.name.toLowerCase().includes(query.toLowerCase())
  );
}, [items]); // items is used inside → include in deps

// Pass to memoized child — now memo actually works
<MemoizedList items={items} onDelete={handleDelete} />
```

**Dependency rules — same as useEffect:**
- Include any value from component scope used inside the callback
- `useState` setters are stable — never need to be in the dep array
- `useRef.current` is mutable — don't include (read it inside the callback at call time)

**`useCallback(fn, deps)` === `useMemo(() => fn, deps)` — same mechanism, different intent.**

---

## 6. useMemo

**Memoizes a computed value — only recomputes when dependencies change:**

```jsx
import { useMemo } from 'react';

// Expensive computation — only recalculates when products, searchTerm, or filters change
const filteredProducts = useMemo(() => {
  return products
    .filter(p => p.name.toLowerCase().includes(searchTerm.toLowerCase()))
    .filter(p => p.price >= filters.minPrice)
    .sort((a, b) => a.price - b.price);
}, [products, searchTerm, filters]);

// Stable object reference for a memoized child
const chartConfig = useMemo(() => ({
  data:   filteredProducts.map(p => ({ label: p.name, value: p.price })),
  colors: ['#3498db', '#e74c3c'],
  type:   'bar'
}), [filteredProducts]);
```

### useMemo vs useCallback

| | `useMemo` | `useCallback` |
|---|---|---|
| Memoizes | A computed **value** | A **function** |
| Returns | Result of calling `fn()` | The function `fn` itself |
| Use for | Expensive calculations, stable object/array refs | Event handlers, callbacks for memoized children |

---

## 7. When to Optimize vs Not

### Optimize when:
- Profiler shows render time > ~16ms (60fps threshold) for a component
- Computation processes large datasets (1,000+ items in a filter/sort)
- A memoized child receives a function/object prop that recreates every render
- A function is a `useEffect` dependency and changing every render causes an infinite loop

### Don't optimize when:
- The operation is trivial (a simple calculation, a short list)
- Props change on every render anyway — memoization overhead with zero benefit
- The value or function isn't passed to children or used in effect dependencies
- You haven't measured an actual problem in the Profiler

### State placement rule:
> Move state as close as possible to where it's used. Frequently-changing state at the top of the tree causes broad re-renders throughout the application. Fix the design before reaching for memo.

```jsx
// ❌ Timer state in App — causes the entire component tree to re-render every second
function App() {
  const [time, setTime] = useState(new Date());
  useEffect(() => { setInterval(() => setTime(new Date()), 1000); }, []);
  return <><Clock time={time} /><HeavySection /></>;  // HeavySection re-renders every second
}

// ✅ Timer state isolated — only Clock re-renders
function Clock() {
  const [time, setTime] = useState(new Date());
  useEffect(() => { setInterval(() => setTime(new Date()), 1000); }, []);
  return <span>{time.toLocaleTimeString()}</span>;
}
function App() {
  return <><Clock /><HeavySection /></>;  // HeavySection never re-renders due to time
}
```

---

## 8. Code Splitting — React.lazy and dynamic import

```jsx
import { lazy } from 'react';

// Static import — always in the bundle
import Home from './pages/Home';

// Lazy import — bundler creates a separate chunk; downloads on demand
const Dashboard     = lazy(() => import('./pages/Dashboard'));
const AdminPanel    = lazy(() => import('./pages/AdminPanel'));
const HeavyEditor   = lazy(() => import('./components/HeavyEditor'));
```

**What's worth splitting:**
- ✅ Route-level pages — most impactful
- ✅ Large feature components not needed on initial render (modals, heavy editors, chart dashboards)
- ✅ Large third-party integrations (chart libraries, PDF viewers, video players)
- ❌ Small, frequently-used shared components
- ❌ Components needed immediately on first render

---

## 9. Suspense and Error Boundaries

### Suspense — handles the pending state

```jsx
import { Suspense } from 'react';

// Single boundary
<Suspense fallback={<Spinner />}>
  <Dashboard />
</Suspense>

// Multiple boundaries — independent loading states
<Layout
  header={
    <Suspense fallback={<NavSkeleton />}><Nav /></Suspense>
  }
>
  <Suspense fallback={<PageSkeleton />}>
    <Routes>...</Routes>
  </Suspense>
</Layout>
```

### Error Boundary — handles the failure state

```jsx
// Option A: react-error-boundary package (recommended — no class component required)
import { ErrorBoundary } from 'react-error-boundary';

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div>
      <p>Failed to load: {error.message}</p>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  );
}

// Always wrap lazy components with BOTH — error boundary outside, Suspense inside
<ErrorBoundary FallbackComponent={ErrorFallback}>
  <Suspense fallback={<Spinner />}>
    <LazyComponent />
  </Suspense>
</ErrorBoundary>
```

**Without an error boundary:** A failed chunk load (network error, 404) throws an unhandled error that crashes the entire React tree → blank screen for the user.

---

## 10. Route-Based Code Splitting

```jsx
import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

const Home          = lazy(() => import('./pages/Home'));
const Dashboard     = lazy(() => import('./pages/Dashboard'));
const ProductDetail = lazy(() => import('./pages/ProductDetail'));
const AdminPanel    = lazy(() => import('./pages/AdminPanel'));

function App() {
  return (
    <BrowserRouter>
      <ErrorBoundary FallbackComponent={ErrorPage}>
        <Suspense fallback={<FullPageLoader />}>
          <Routes>
            <Route path="/"             element={<Home />} />
            <Route path="/dashboard"    element={<Dashboard />} />
            <Route path="/products/:id" element={<ProductDetail />} />
            <Route path="/admin"        element={<AdminPanel />} />
          </Routes>
        </Suspense>
      </ErrorBoundary>
    </BrowserRouter>
  );
}
```

**Preloading on hover (optional):**
```jsx
<Link to="/dashboard" onMouseEnter={() => import('./pages/Dashboard')}>
  Dashboard
</Link>
```

**This is the single most impactful code splitting pattern.** If you have multiple routes and you're not doing this, add it first.

---

## 11. useTransition

**Marks a state update as low-priority ("transition") — high-priority updates (typing) are never blocked:**

```jsx
import { useState, useTransition } from 'react';

function SearchPage() {
  const [query,   setQuery]   = useState('');
  const [results, setResults] = useState([]);
  const [isPending, startTransition] = useTransition();

  const handleSearch = (e) => {
    setQuery(e.target.value);              // urgent — immediate
    startTransition(() => {               // non-urgent — interruptible
      setResults(filterProducts(e.target.value));
    });
  };

  return (
    <>
      <input value={query} onChange={handleSearch} />
      {isPending && <span>Updating...</span>}
      <ResultsList results={results} />
    </>
  );
}
```

**When to use:**
- Filtering / sorting large in-memory lists on each keystroke
- Tab switching where rendering the new content is expensive
- Any update where you want "UI stays interactive while expensive work runs"

---

## 12. useDeferredValue

**Defers a value — React renders with the stale value first, then the new one:**

```jsx
import { useState, useDeferredValue, memo } from 'react';

function App() {
  const [searchTerm, setSearchTerm] = useState('');
  const deferredTerm = useDeferredValue(searchTerm);

  return (
    <>
      <input value={searchTerm} onChange={e => setSearchTerm(e.target.value)} />
      {/* Renders without blocking the input update */}
      <MemoizedList searchTerm={deferredTerm} />
    </>
  );
}

// memo is REQUIRED — without it, deferring has no effect
const MemoizedList = memo(({ searchTerm }) => {
  const results = expensiveFilter(allItems, searchTerm);
  return <ul>{results.map(i => <li key={i.id}>{i.name}</li>)}</ul>;
});
```

### useTransition vs useDeferredValue

| | `useTransition` | `useDeferredValue` |
|---|---|---|
| You control | The **state update** | The **value** |
| `isPending` | ✅ Yes | ❌ No — compare `value !== deferredValue` |
| Use when | You own the setter | Value comes from props or external source |

Both require React 18.

---

## 13. React DevTools

**Install:** React Developer Tools extension — Chrome, Firefox, Edge (free, by the React team)

### Components Tab

- Displays the React component tree (not the DOM tree)
- Select any component: see its current props, state, context, and every hook's value
- Edit state / props live — component re-renders immediately
- `$r` in console — reference to the selected component's fiber object
- Memo badge — visible marker on `React.memo`-wrapped components

### Profiler Tab — Workflow

```
1. Click ⚫ (record)
2. Perform the slow interaction
3. Click ⏹ (stop)
4. Read the flame graph:
   - Width   = relative render time
   - ⬜ Gray  = didn't render (memo worked)
   - 🔵 Blue  = rendered, fast
   - 🟡 Yellow = rendered, medium
   - 🔴 Red   = rendered, slow — investigate
5. "Why did this render?" — shows which prop/state/context triggered the re-render
```

**Profile the production build for accurate numbers:**
```bash
npm run build && npx serve dist
# Open DevTools Profiler on the localhost URL
```

---

## 14. Building for Production

```bash
npm run build    # Vite → dist/   |   CRA → build/
npx serve dist   # test locally before deploying
```

**Build pipeline steps:**
1. TypeScript compilation — type errors fail the build ✅
2. Bundling — all imports resolved
3. Tree-shaking — unused exports removed
4. Minification — names shortened, whitespace stripped (~30–50% size reduction)
5. Chunk splitting — `React.lazy` imports become separate files
6. Content hashing — filenames change when content changes (cache busting)

**Visualize bundle size (Vite):**
```bash
npm install -D rollup-plugin-visualizer
# vite.config.ts: import { visualizer } from 'rollup-plugin-visualizer'
#   plugins: [react(), visualizer({ open: true })]
npm run build   # opens stats.html — visual treemap of your bundle
```

---

## 15. Environment Variables

```bash
# .env              — all environments
VITE_API_URL=http://localhost:3001/api
VITE_APP_NAME=MyApp

# .env.development  — npm run dev only
VITE_API_URL=http://localhost:3001/api

# .env.production   — npm run build only
VITE_API_URL=https://api.myapp.com

# .env.local        — NEVER commit to Git
VITE_STRIPE_PK=pk_test_personal_key_here
```

```javascript
// Access in code (Vite)
const apiUrl = import.meta.env.VITE_API_URL;

// Access in code (Create React App)
const apiUrl = process.env.REACT_APP_API_URL;
```

**.gitignore:**
```
.env.local
.env.*.local
```

**Security rules:**
- Only `VITE_` / `REACT_APP_` prefixed vars are exposed to the browser
- **Never put secrets in frontend env vars** — they're visible in the JavaScript bundle
- Frontend env vars = configuration (URLs, feature flags, publishable keys)
- Secrets (JWT signing keys, DB passwords, private API keys) belong on the server only

---

## 16. Deployment

### Netlify

```
1. Push to GitHub
2. netlify.com → New site → Import from Git
3. Build command:     npm run build
4. Publish directory: dist   (Vite) or build   (CRA)
5. Deploy
```

**SPA routing fix — required:**
```
# public/_redirects
/*    /index.html    200
```

### Vercel

```
1. Push to GitHub
2. vercel.com → New Project → Import
3. Auto-detects Vite/CRA — no config needed
4. Deploy
```

Optional `vercel.json`:
```json
{
  "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }]
}
```

### AWS S3 + CloudFront

```bash
npm run build
aws s3 sync dist/ s3://my-bucket --delete
# Configure CloudFront: error pages 403/404 → /index.html (200) — SPA routing fix
aws cloudfront create-invalidation --distribution-id XXXXX --paths "/*"
```

### Deployment Comparison

| | Netlify | Vercel | AWS S3+CF |
|---|---|---|---|
| Setup | ~5 min | ~5 min | ~30–60 min |
| Auto deploys | ✅ | ✅ | ⚠️ Manual / CI |
| Preview URLs | ✅ Per PR | ✅ Per PR | ❌ |
| Free tier | ✅ Generous | ✅ Generous | ✅ Pay-per-use |
| Best for | Most SPAs | React / Next.js | AWS-ecosystem, enterprise |

**SPA routing — every host needs this configured.** React Router handles navigation client-side. Direct URL access or page refresh sends a server request for the path — which doesn't match any file. Configure the host to return `index.html` for all paths.

---

## 17. Project Structure Best Practices

```
src/
├── assets/           # Images, fonts, icons
├── components/       # Shared, reusable UI (not feature-specific)
│   └── Button/
│       ├── Button.jsx
│       ├── Button.module.css
│       └── Button.test.jsx     ← tests co-located with component
├── features/         # Feature-specific code — co-located by feature
│   ├── auth/
│   │   ├── authSlice.js        ← Redux slice
│   │   ├── LoginForm.jsx       ← component
│   │   ├── LoginForm.test.jsx  ← test next to component
│   │   └── useAuth.js          ← custom hook
│   └── products/
│       ├── productSlice.js
│       ├── ProductList.jsx
│       ├── ProductDetail.jsx
│       └── useProducts.js
├── hooks/            # Shared custom hooks (not feature-specific)
├── pages/            # Thin route wrappers — compose features, no business logic
│   ├── HomePage.jsx
│   └── DashboardPage.jsx
├── services/         # API call functions — keep fetch out of components
│   └── api.js
├── store/            # Redux store configuration
│   └── store.js
└── utils/            # Pure helper functions (formatDate, validate, etc.)
```

**Key principles:**
| Principle | What it means |
|---|---|
| Feature-based | Everything for a feature lives together — not scattered by file type |
| Co-location | Tests, styles, and hooks live next to the component they belong to |
| Thin pages | Pages import from `features/` — they don't contain logic |
| Services layer | `fetch` calls live in `services/` — easy to mock and maintain |
| Barrel exports | `features/auth/index.js` re-exports the public surface for cleaner imports |

---

## 18. Common Mistakes & Fixes

### Performance

| Mistake | Fix |
|---|---|
| Adding `React.memo` without measuring | Profile first — verify a problem exists |
| `React.memo` with inline function props | Add `useCallback` for every callback prop |
| `React.memo` with inline object props | Add `useMemo` for every object/array prop |
| `useMemo` on trivial calculations | Remove — overhead exceeds the savings |
| `useCallback` with missing dependencies | Check the exhaustive-deps ESLint rule |
| Frequently-changing state living high in the tree | Move state down to the component that uses it |

### Code Splitting

| Mistake | Fix |
|---|---|
| Lazy component without `Suspense` | Wrap with `<Suspense fallback={...}>` |
| Lazy component without error boundary | Wrap with `<ErrorBoundary>` |
| Dynamic import with a variable path | Use string literals: `import('./pages/Foo')` |
| Lazy-loading tiny components | Only split routes and large features |

### Deployment

| Mistake | Fix |
|---|---|
| Direct URL (e.g., `/dashboard`) returns 404 | Add `_redirects` file or configure host for SPA routing |
| `VITE_API_URL` points to `localhost` in production | Set correct URL in `.env.production` |
| Secret keys in `VITE_` env vars | Move secrets to the backend |
| Not testing production build before deploying | Always run `npm run build && npx serve dist` first |
| Committing `.env.local` | Add `.env.local` and `.env.*.local` to `.gitignore` |

---

## 19. Quick Reference Syntax

```jsx
// ── Composition ───────────────────────────────────────────────────────────
<Container>{children}</Container>
<Layout header={<Nav />} sidebar={<Aside />}>{content}</Layout>

// ── Render Props ──────────────────────────────────────────────────────────
<DataFetcher url="/api/items" render={({ data, loading }) => ...} />
<DataFetcher url="/api/items">{({ data, loading }) => ...}</DataFetcher>

// ── Compound Components ───────────────────────────────────────────────────
<Tabs defaultTab={0}>
  <Tabs.Tab index={0}>Label</Tabs.Tab>
  <Tabs.TabPanel index={0}>Content</Tabs.TabPanel>
</Tabs>

// ── Memoization ───────────────────────────────────────────────────────────
const MemoComp  = React.memo(Component);
const stableFn  = useCallback(() => doSomething(arg), [arg]);
const computed  = useMemo(() => expensiveCalc(a, b), [a, b]);

// ── Code Splitting ────────────────────────────────────────────────────────
const Page = lazy(() => import('./pages/Page'));
<Suspense fallback={<Spinner />}><Page /></Suspense>
<ErrorBoundary FallbackComponent={ErrorPage}>
  <Suspense fallback={<Spinner />}><Page /></Suspense>
</ErrorBoundary>

// ── Concurrent Features ───────────────────────────────────────────────────
const [isPending, startTransition] = useTransition();
startTransition(() => setExpensiveState(newValue));
const deferred = useDeferredValue(value);

// ── Build & Deploy ────────────────────────────────────────────────────────
npm run build               // → dist/
npx serve dist              // test locally
const url = import.meta.env.VITE_API_URL;  // environment variable

// public/_redirects (Netlify SPA routing)
/*    /index.html    200
```

---

## 20. Looking Ahead

| Day 20a (Today) | Coming Up |
|---|---|
| Render props, compound components | Angular patterns — Day 20b |
| React.memo, useCallback, useMemo | Spring Boot performance — Weeks 5–6 |
| Code splitting + lazy loading | Week 9 full-stack performance review |
| Production builds + env vars | Spring Boot profiles + application.yml — Week 5 Day 25 |
| Netlify / Vercel deployment | CI/CD pipelines — Week 8 Day 37 |
| AWS S3 + CloudFront | AWS deep-dive (EC2, S3, ECS, EKS) — Week 8 Day 40 |
| Feature-based project structure | Microservices structure — Week 8 Day 38 |

The patterns from this week — component composition, state management, performance thinking, shipping code — are foundation skills that apply across every framework and language you'll use.

---

*Day 20a Complete — React Advanced & Deployment*
