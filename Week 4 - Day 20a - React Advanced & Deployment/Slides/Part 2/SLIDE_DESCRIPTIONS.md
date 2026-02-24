# Day 20a — React Advanced & Deployment: Part 2 Slides
## Code Splitting, Suspense, Concurrent Features, React DevTools, Production Builds & Deployment

---

### Slide 1: Day 20a Part 2 — Code Splitting, DevTools & Deployment

**Part 2 of 2 | Week 4 — Friday**

**Topics This Hour:**
- Code splitting and lazy loading
- Suspense and error boundaries
- Concurrent features: useTransition and useDeferredValue
- React DevTools — Components and Profiler tabs
- Building for production
- Environment variables
- Deployment strategies
- Project structure best practices

**By the end of Part 2 you will:**
- Implement route-based code splitting to reduce initial load time
- Profile a React app and identify the actual bottleneck
- Build, configure, and deploy a production React application

---

### Slide 2: The Bundle Problem — Why Code Splitting Matters

**Without code splitting, all your JavaScript ships as one file:**

```
Build output (no splitting):
├── index.html                    21 KB
└── assets/
    └── index-Bk29qlQj.js    1,247 KB  ← your entire app + all dependencies
```

**Every user downloads ALL 1,247 KB before ANY page renders.**

A `/login` user downloads dashboard code, chart library, admin panel, product catalog — none of which they need.

**With code splitting:**
```
Build output (with splitting):
├── index.html                       21 KB
└── assets/
    ├── index-Bk29qlQj.js          184 KB  ← core app: router, layout, auth
    ├── Dashboard-Cd4eKj2a.js       91 KB  ← downloaded when user visits /dashboard
    ├── AdminPanel-Df2mNp1q.js     141 KB  ← downloaded when user visits /admin
    └── Charts-Gk4lRm9z.js          87 KB  ← downloaded when charts component renders
```

Users download what they need, when they need it. This is **lazy loading**.

**Rule of thumb:** Any route or heavy feature component not needed on initial load is a code splitting candidate.

---

### Slide 3: React.lazy and dynamic import()

**`React.lazy` wraps a dynamic `import()` so React loads the component asynchronously:**

```jsx
// Static import — always included in the main bundle
import ProductList from './features/products/ProductList';

// Lazy import — bundler creates a separate chunk; downloaded on demand
const ProductList = React.lazy(() => import('./features/products/ProductList'));
const Dashboard   = React.lazy(() => import('./pages/Dashboard'));
const AdminPanel  = React.lazy(() => import('./pages/AdminPanel'));
const Charts      = React.lazy(() => import('./components/Charts/Charts'));
```

**How it works:**
1. The bundler (Vite/Webpack) sees the dynamic `import()` and creates a separate JS chunk at build time
2. At runtime, when React first tries to render `<Dashboard />`, it triggers the import
3. While the chunk is downloading, React suspends rendering — that's where `Suspense` comes in

**What's worth splitting:**
- ✅ Route-level pages — highest impact, most obvious wins
- ✅ Large feature components not needed on load (modals, heavy editors, chart dashboards)
- ✅ Large third-party integrations (chart libraries, PDF viewers, video players)
- ❌ Small, frequently-used components — overhead isn't worth it
- ❌ Components needed immediately on initial render — must stay as static imports

**Note:** The import path must be a string literal — not a variable — so the bundler can analyze it at build time.

---

### Slide 4: Suspense — Fallback UI While Loading

**`Suspense` catches the pending state from `React.lazy` and renders a fallback:**

```jsx
import { lazy, Suspense } from 'react';

const Dashboard  = lazy(() => import('./pages/Dashboard'));
const AdminPanel = lazy(() => import('./pages/AdminPanel'));

// Single boundary — one fallback for all lazy children
function App() {
  return (
    <Suspense fallback={<div className="page-loading">Loading...</div>}>
      <Routes>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/admin"     element={<AdminPanel />} />
      </Routes>
    </Suspense>
  );
}
```

**Multiple Suspense boundaries for granular loading states:**
```jsx
<Layout
  header={
    <Suspense fallback={<NavSkeleton />}>
      <Nav />
    </Suspense>
  }
>
  <Suspense fallback={<PageSkeleton />}>
    <Routes>
      <Route path="/dashboard" element={<Dashboard />} />
    </Routes>
  </Suspense>
</Layout>
```

**⚠️ Error Boundary — required for production:**

Suspense handles **pending** state. It does NOT handle **failure** state. If the chunk download fails (network error, 404), Suspense has no fallback — your app crashes.

```jsx
// Always wrap lazy components with both
<ErrorBoundary fallback={<ErrorPage message="Failed to load. Please refresh." />}>
  <Suspense fallback={<Spinner />}>
    <LazyComponent />
  </Suspense>
</ErrorBoundary>
```

---

### Slide 5: Route-Based Code Splitting

**The single highest-impact code splitting pattern — one chunk per route:**

```jsx
import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

// Every page is a lazy import — only the current route's code downloads
const Home          = lazy(() => import('./pages/Home'));
const Dashboard     = lazy(() => import('./pages/Dashboard'));
const ProductDetail = lazy(() => import('./pages/ProductDetail'));
const AdminPanel    = lazy(() => import('./pages/AdminPanel'));
const Profile       = lazy(() => import('./pages/Profile'));

function App() {
  return (
    <BrowserRouter>
      <Suspense fallback={<FullPageLoader />}>
        <Routes>
          <Route path="/"             element={<Home />} />
          <Route path="/dashboard"    element={<Dashboard />} />
          <Route path="/products/:id" element={<ProductDetail />} />
          <Route path="/admin"        element={<AdminPanel />} />
          <Route path="/profile"      element={<Profile />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}
```

**Optional: preload on hover (user likely about to navigate):**
```jsx
<Link
  to="/dashboard"
  onMouseEnter={() => import('./pages/Dashboard')}
>
  Dashboard
</Link>
```

This is the first performance optimization to add to any multi-route React app. If you have five routes and no code splitting, add this today.

---

### Slide 6: Concurrent React — useTransition

**`useTransition` marks a state update as low-priority — high-priority updates (typing, clicking) are never blocked:**

```jsx
import { useState, useTransition } from 'react';

function SearchPage() {
  const [query,   setQuery]   = useState('');
  const [results, setResults] = useState([]);
  const [isPending, startTransition] = useTransition();

  const handleSearch = (e) => {
    // Urgent — always immediate (keeps input responsive)
    setQuery(e.target.value);

    // Non-urgent — wrapped in transition; React can interrupt this
    // if a more urgent update (next keypress) comes in
    startTransition(() => {
      const filtered = allProducts.filter(p =>
        p.name.toLowerCase().includes(e.target.value.toLowerCase())
      );
      setResults(filtered);
    });
  };

  return (
    <>
      <input value={query} onChange={handleSearch} placeholder="Search..." />
      {isPending && <span className="hint">Updating results...</span>}
      <ResultsList results={results} />
    </>
  );
}
```

**When to use `useTransition`:**
- Filtering or sorting large in-memory datasets in response to user input
- Tab switching where rendering the new tab is expensive
- Any update where you want "UI stays responsive while expensive work happens"

**`isPending`** — true while the transition is in progress. Use it to show a subtle loading indicator instead of freezing the UI.

---

### Slide 7: useDeferredValue

**Defers a value — React renders with the previous (stale) value first, then the new one:**

```jsx
import { useState, useDeferredValue, memo } from 'react';

function App() {
  const [searchTerm, setSearchTerm] = useState('');

  // deferredSearchTerm "lags behind" searchTerm during rapid updates
  const deferredSearchTerm = useDeferredValue(searchTerm);

  return (
    <>
      <input
        value={searchTerm}
        onChange={e => setSearchTerm(e.target.value)}
        placeholder="Search..."
      />
      {/* Gets the deferred value — renders without blocking the input */}
      <MemoizedProductList searchTerm={deferredSearchTerm} />
    </>
  );
}

// memo is required — without it, deferring the value has no effect
const MemoizedProductList = memo(({ searchTerm }) => {
  const results = expensiveFilter(allProducts, searchTerm);
  return <ul>{results.map(p => <li key={p.id}>{p.name}</li>)}</ul>;
});
```

**useTransition vs useDeferredValue:**
| | `useTransition` | `useDeferredValue` |
|---|---|---|
| You control | The **state update** | The **value** |
| `isPending` available | ✅ Yes | ❌ No (compare `value !== deferredValue`) |
| Use when | You own the setter | Value comes from props or external source |

Both are React 18+ concurrent features. They let React interrupt and restart renders to keep the UI responsive.

---

### Slide 8: React DevTools — Components Tab

**Install:** React Developer Tools (Chrome / Firefox / Edge — free, published by the React team)

**The Components tab shows your actual React component tree:**
```
▼ App
  ▼ BrowserRouter
    ▼ Routes
      ▼ Dashboard
        ▼ Suspense  (fallback: <Spinner>)
          ▼ ProductDashboard      ← click to inspect
              ▶ ProductFilter
              ▶ ProductList (memo) ← memo badge shown
              ▶ Charts
```

**What you can see when you select a component:**
- **Props** — current values of all props
- **State** — current values of all `useState` / `useReducer` hooks
- **Context** — values consumed from Context
- **Hooks** — list of all hooks and their current values

**Useful features:**
| Feature | How to use |
|---|---|
| Search | Filter the component tree by name |
| Live edit | Click a state or prop value and edit it — component re-renders immediately |
| Eye icon | Highlights the selected component on the actual page |
| Source link | Jumps to source code (requires source maps) |
| Memo badge | Visible marker on components wrapped in `React.memo` |
| `$r` in console | After selecting a component, `$r` gives you the component's fiber object |

---

### Slide 9: React DevTools — Profiler Tab

**The Profiler records renders and shows exactly which components were slow and why.**

**Workflow:**
1. Switch to the Profiler tab
2. Click the record button (⚫)
3. Perform the interaction that feels slow
4. Click stop (⏹)
5. Examine the flame graph

**Reading the flame graph:**
```
Render #3 — 48.2ms total
├── App                          0.1ms  (no change)
├── ProductDashboard             1.2ms  (state changed)
│   ├── ProductFilter            0.3ms  (parent re-rendered)
│   ├── ProductList             42.7ms  ← SLOW — investigate
│   │   └── ProductItem (×200)  ~0.2ms each
│   └── Charts                   4.0ms
```

**Color coding:**
- ⬜ Gray — component didn't render (memo worked, or not in this render tree)
- 🔵 Blue — rendered, fast
- 🟡 Yellow — rendered, medium
- 🔴 Red — rendered, slow — investigate first

**"Why did this render?" panel:**
Shows exactly which prop, state value, or context value changed. This is how you verify whether `React.memo` is working — and debug it when it isn't.

**Important tip:** Profile the **production build** for accurate numbers. Dev mode adds overhead (warnings, extra checks) that inflates render times.
```bash
npm run build && npx serve dist
# Then open DevTools → Profiler and record
```

---

### Slide 10: Building for Production

**Vite:**
```bash
npm run build    # outputs to dist/
```

**What happens during build:**
1. **TypeScript compilation** — type errors fail the build (wanted behavior)
2. **Bundling** — all module imports resolved and combined
3. **Tree-shaking** — unused exports removed (if you import lodash but only use `_.debounce`, the rest is stripped)
4. **Minification** — variable names shortened, whitespace and comments removed (~30–50% size reduction)
5. **Chunk splitting** — every `React.lazy` import becomes a separate file
6. **Content hashing** — filenames include a hash (`app-Bk29qlQj.js`) that changes when content changes, forcing browser cache invalidation on deployment

**Output:**
- Vite → `dist/`
- Create React App → `build/`
- These folders are all you ship — static HTML, CSS, and JS

**Testing the production build locally (always do this before deploying):**
```bash
npm run build
npx serve dist
# Visit http://localhost:3000 — this is exactly what users will see
```

**Visualizing bundle size (Vite + rollup-plugin-visualizer):**
```bash
npm install -D rollup-plugin-visualizer
# vite.config.ts: add visualizer({ open: true }) to plugins
npm run build   # opens a visual treemap of your bundle
```

---

### Slide 11: Environment Variables

**Never hardcode API URLs, keys, or secrets directly in source code.**

```bash
# .env              — loaded in all environments
VITE_API_URL=http://localhost:3001/api
VITE_APP_NAME=MyApp

# .env.development  — loaded with: npm run dev
VITE_API_URL=http://localhost:3001/api

# .env.production   — loaded with: npm run build
VITE_API_URL=https://api.myapp.com

# .env.local        — personal secrets; NEVER commit to Git
VITE_STRIPE_TEST_KEY=pk_test_your_personal_key
```

**Accessing in code (Vite):**
```javascript
const apiUrl  = import.meta.env.VITE_API_URL;
const appName = import.meta.env.VITE_APP_NAME;
```

**Rules:**
- Only `VITE_` prefixed variables are exposed to the browser bundle
- Create React App uses `REACT_APP_` prefix instead
- Non-prefixed variables exist only in Node.js build tooling — not accessible in browser code

**.gitignore — required:**
```
.env.local
.env.*.local
```

**⚠️ Critical security rule:** Everything in `VITE_` variables **ends up in the browser bundle** and is visible to anyone who inspects your JavaScript. Never put secret API keys, database passwords, JWT signing secrets, or private tokens in frontend environment variables. Those belong on the backend server only. Frontend env vars are for configuration — "which API URL do I call?" — not credentials.

---

### Slide 12: Deployment — Options and Trade-offs

**Three main models for deploying a React SPA:**

| | Static Hosting | Platform-as-a-Service | Self-Managed Cloud |
|---|---|---|---|
| **Services** | Netlify, Vercel, GitHub Pages | Railway, Render | AWS S3+CloudFront, DigitalOcean |
| **Setup time** | ~5 minutes | ~5 minutes | ~30–60 minutes |
| **Free tier** | ✅ Generous | ✅ Generous | ✅ Pay-per-use |
| **Auto deploys** | ✅ Built-in | ✅ Built-in | ⚠️ Manual / CI needed |
| **Preview URLs** | ✅ Per pull request | ✅ Per pull request | ❌ |
| **SSL** | ✅ Automatic | ✅ Automatic | ⚠️ Configure ACM / Let's Encrypt |
| **Best for** | Most SPAs | Apps with backend | AWS-ecosystem, enterprise |

**The `dist/` folder is everything you ship.** A React SPA is static files — no server required unless you add SSR.

**⚠️ The SPA routing gotcha — every deployment needs this configured:**

React Router handles navigation client-side. But when a user types `myapp.com/dashboard` in the address bar, the server receives a request for `/dashboard` — which doesn't exist as a file. Configure your host to redirect all requests that don't match a file to `index.html`.

---

### Slide 13: Deploying to Netlify and Vercel

**Netlify:**
```
1. Push your React app to GitHub
2. netlify.com → Add new site → Import from Git
3. Build command:     npm run build
4. Publish directory: dist   (Vite) or build (CRA)
5. Deploy Site
```

**SPA routing fix — required for Netlify:**
```
# Create: public/_redirects  (or netlify/_redirects)
/*    /index.html    200
```

```
Every push to main → automatic rebuild + deployment
Every pull request  → preview URL (e.g., https://deploy-preview-42--myapp.netlify.app)
```

---

**Vercel:**
```
1. Push to GitHub
2. vercel.com → Add New → Project → Import
3. Vercel auto-detects Vite / CRA — no config needed
4. Deploy
```

SPA routing is handled automatically. Optional `vercel.json` for custom config:
```json
{
  "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }]
}
```

**Both Netlify and Vercel support:**
- Custom domains (point an A or CNAME record in your DNS)
- Environment variables per environment (development / preview / production)
- Instant rollbacks to any previous deployment
- Build logs and deployment history

---

### Slide 14: Deploying to AWS S3 + CloudFront

**For production-scale CDN hosting inside the AWS ecosystem:**

```bash
# 1. Build
npm run build

# 2. Upload dist/ to S3 bucket (static website hosting enabled)
aws s3 sync dist/ s3://my-react-app-bucket --delete
#   --delete removes files from S3 that no longer exist in dist/

# 3. CloudFront distribution configuration:
#    Origin: S3 bucket
#    Default root object: index.html
#    Error pages: 403 → /index.html (200), 404 → /index.html (200)  ← SPA routing fix
#    HTTPS: ACM certificate (free via AWS Certificate Manager)

# 4. Invalidate CloudFront cache after every deployment
aws cloudfront create-invalidation \
  --distribution-id ABCDEFGHIJKLMN \
  --paths "/*"
```

**Why S3 + CloudFront over Netlify/Vercel:**
- Already on AWS — integrates with VPC, IAM, Route 53, CloudWatch
- Enterprise compliance requirements
- Very high traffic (lower per-request cost at scale)

**Simpler AWS option — AWS Amplify Hosting:**
Connect GitHub → Amplify handles S3, CloudFront, HTTPS, preview deploys automatically. Like Netlify/Vercel but in the AWS Console. Recommended if you're on AWS but want fast setup.

---

### Slide 15: Project Structure Best Practices

**Feature-based structure — scales with the application:**

```
src/
├── assets/               # Static assets: images, fonts, icons
├── components/           # Shared, reusable UI (not feature-specific)
│   ├── Button/
│   │   ├── Button.jsx
│   │   ├── Button.module.css
│   │   └── Button.test.jsx    ← tests co-located with components
│   └── Card/
├── features/             # Feature-specific code — everything for a feature lives here
│   ├── auth/
│   │   ├── authSlice.js       ← Redux slice
│   │   ├── LoginForm.jsx      ← component
│   │   ├── LoginForm.test.jsx ← test next to component
│   │   └── useAuth.js         ← custom hook
│   └── products/
│       ├── productSlice.js
│       ├── ProductList.jsx
│       ├── ProductDetail.jsx
│       └── useProducts.js
├── hooks/                # Shared custom hooks (not tied to one feature)
├── pages/                # Route-level components — thin wrappers that compose features
│   ├── HomePage.jsx
│   └── DashboardPage.jsx
├── services/             # API call functions — keep fetch out of components
│   └── api.js
├── store/                # Redux store config
│   └── store.js
└── utils/                # Pure helper functions (formatDate, validateEmail, etc.)
```

**Key principles:**
- **Feature-based over type-based** — `features/products/` instead of `components/ProductList.jsx` + `reducers/productSlice.js` scattered across the tree
- **Co-location** — tests, styles, and hooks live next to the component they belong to
- **Thin pages** — pages import from `features/`; they don't contain business logic
- **`services/` for API** — keep fetch calls centralized so they're easy to mock and swap

---

### Slide 16: Day 20a Summary + Looking Ahead

**Part 1 Recap:**
| Pattern | Use When |
|---|---|
| `props.children` / component injection | Composable, flexible containers |
| Render props | Shared behavior requiring owned DOM event binding |
| Compound components | Tightly-coupled multi-piece UI with shared state |
| `React.memo` + `useCallback` + `useMemo` | After profiling reveals a measured bottleneck |

**Part 2 Recap:**
| Topic | The One-Liner |
|---|---|
| `React.lazy` + `Suspense` | Load code on demand — biggest single-change performance win |
| `useTransition` / `useDeferredValue` | Keep UI responsive during expensive re-renders (React 18) |
| React DevTools Profiler | Find the actual slow component before optimizing |
| `npm run build` | Minified, tree-shaken, hashed, chunk-split production output |
| Netlify / Vercel | Connect GitHub → set build command → ship it |
| Feature-based structure | Co-locate by feature; thin pages; services for API calls |

**The complete React track:**
16a Fundamentals → 17a Hooks → 18a Router + Redux → 19a API + Testing → 20a Advanced + Deploy

**Looking ahead:**
- Day 20b: Angular Signals & Testing (Angular track conclusion)
- Week 5: SQL databases and Spring Boot begin — the backend half of the stack
- Week 8 Day 37: CI/CD pipelines — automating what you deployed manually today
- Week 8 Day 40: AWS deep-dive — ECS, RDS, S3, CloudWatch
