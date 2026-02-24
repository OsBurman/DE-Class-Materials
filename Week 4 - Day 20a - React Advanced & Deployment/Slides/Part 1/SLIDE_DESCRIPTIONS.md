# Day 20a — React Advanced & Deployment: Part 1 Slides
## Component Composition Patterns, Render Props, Compound Components & Performance Optimization

---

### Slide 1: Day 20a — React Advanced Patterns & Performance

**Part 1 of 2 | Week 4 — Friday**

**Topics This Hour:**
- Component composition patterns
- The render props pattern
- Compound components
- Performance optimization: React.memo, useCallback, useMemo

**By the end of Part 1 you will:**
- Recognize and implement advanced React composition patterns
- Know when to use render props vs custom hooks
- Memoize components and expensive calculations to prevent unnecessary work

---

### Slide 2: Why Advanced Patterns? — The Problem with Rigidity and Prop Drilling

**Two problems that grow with every application:**

```jsx
// ❌ Rigid component — hard-codes its children, can't be reused in different layouts
function Dashboard() {
  return (
    <Card>
      <CardHeader />
      <CardBody>
        <UserStats />
        <RecentActivity />
      </CardBody>
    </Card>
  );
}
```

1. **Rigidity** — components that hard-code their children can't be reused in new layouts
2. **Prop explosion** — adding more variation means adding more props until the API is unmanageable

**The design principle:**
> Build components that are **open for extension** (accept flexible children/content) but **closed for modification** (you don't have to rewrite the component itself).

React's advanced composition patterns — `props.children`, render props, compound components — are all solutions to making components reusable without making them harder to understand.

---

### Slide 3: Component Composition — `props.children`

**The most fundamental composition pattern — pass content as children:**

```jsx
// Generic container — knows nothing about what goes inside
function Card({ title, children }) {
  return (
    <div className="card">
      {title && <div className="card-header">{title}</div>}
      <div className="card-body">
        {children}
      </div>
    </div>
  );
}

// The parent decides what goes inside
function ProfilePage() {
  return (
    <Card title="User Profile">
      <Avatar src={user.avatar} />
      <p>{user.bio}</p>
      <FollowButton userId={user.id} />
    </Card>
  );
}

function SettingsPage() {
  return (
    <Card title="Account Settings">
      <SettingsForm />
    </Card>
  );
}
```

**Why this is powerful:**
- `Card` never needs to change when you add new page types
- Each page controls its own content completely
- `Card` can be tested independently of any specific content

**`children` is just a prop** — anything between the opening and closing tags becomes `props.children`.

---

### Slide 4: Component Injection — Passing Components as Props

**For layouts with multiple distinct regions:**

```jsx
// Three-slot layout — header, sidebar, and main content
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

// Parent assembles the pieces — PageLayout knows nothing about NavBar, CategoryList, etc.
function App() {
  return (
    <PageLayout
      header={<NavBar user={currentUser} />}
      sidebar={<CategoryList categories={categories} />}
    >
      <ProductGrid products={products} />
    </PageLayout>
  );
}
```

**JSX is just a JavaScript object** — you can pass any JSX as a prop and render it wherever you need.

**Rule of thumb:**
- Use `children` for a single content area
- Use named component props (`header`, `sidebar`, `footer`) for multiple distinct layout slots

---

### Slide 5: Render Props Pattern — Motivation

**Problem: sharing stateful logic between components without duplicating it**

```jsx
// You need mouse-position tracking in THREE different components:
// Option A: Copy-paste the logic everywhere ❌
// Option B: Higher-Order Component — wrapping, hard to compose
// Option C: Render prop — share the logic, let the caller own the UI ✅

// The pattern: a prop whose VALUE is a FUNCTION that returns JSX
<DataComponent render={(data) => <SomeUI data={data} />} />
//                    ↑ receives internal state      ↑ returns JSX
```

**Core idea:**
- The component manages the **behavior** — state, effects, event listeners
- The consumer provides the **UI** as a function
- The component calls that function, passing it the current state

This inverts the dependency: the consumer decides what to render; the provider manages only the logic.

---

### Slide 6: Render Props — Implementation

```jsx
// MouseTracker manages position state, exposes it via render prop
function MouseTracker({ render }) {
  const [position, setPosition] = useState({ x: 0, y: 0 });

  return (
    <div
      onMouseMove={e => setPosition({ x: e.clientX, y: e.clientY })}
      style={{ height: '100%' }}
    >
      {render(position)}    {/* calls the function, passing current position */}
    </div>
  );
}

// Consumer 1 — renders coordinates as text
<MouseTracker render={({ x, y }) => <p>Mouse is at ({x}, {y})</p>} />

// Consumer 2 — moves an image with the cursor (completely different UI, same logic)
<MouseTracker
  render={({ x, y }) => (
    <img src="cursor.png" style={{ position: 'fixed', left: x, top: y }} />
  )}
/>
```

**The `children` as function variant — equally common:**
```jsx
// Instead of a prop named 'render', use children as the function
<MouseTracker>
  {({ x, y }) => <p>At ({x}, {y})</p>}
</MouseTracker>
// Inside MouseTracker: {children(position)} instead of {render(position)}
```

---

### Slide 7: Render Props — Data Fetching Use Case

**A practical, real-world example — reusable fetch + loading + error logic:**

```jsx
function DataFetcher({ url, render }) {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    setLoading(true);
    fetch(url)
      .then(res => res.json())
      .then(data  => { setData(data);         setLoading(false); })
      .catch(err  => { setError(err.message); setLoading(false); });
  }, [url]);

  return render({ data, loading, error });
}

// Each consumer decides its own loading/error/success UI
<DataFetcher
  url="/api/products"
  render={({ data, loading, error }) => {
    if (loading) return <Spinner />;
    if (error)   return <ErrorMessage text={error} />;
    return <ProductList products={data} />;
  }}
/>

<DataFetcher
  url="/api/users"
  render={({ data, loading }) => {
    if (loading) return <Skeleton />;
    return <UserTable users={data ?? []} />;
  }}
/>
```

Same fetch logic, completely different loading and success UIs.

---

### Slide 8: Render Props vs Custom Hooks

**Since React 16.8, most render prop use cases can be replaced by custom hooks:**

| | Render Props | Custom Hooks |
|---|---|---|
| Extra tree nodes | ✅ Adds wrapper components | ❌ Zero extra nodes |
| Composing multiple sources | Requires nesting | Call multiple hooks — no nesting |
| Consumer syntax | JSX + function prop | `const x = useX()` |
| Best for | Logic requiring owned DOM event listeners | Everything else |

```jsx
// The DataFetcher above as a custom hook (preferred for new code)
function useDataFetcher(url) {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  useEffect(() => { /* same fetch logic */ }, [url]);

  return { data, loading, error };
}

// Usage — zero wrapper components, no function-as-children syntax
function ProductList() {
  const { data, loading, error } = useDataFetcher('/api/products');
  if (loading) return <Spinner />;
  return <ul>{data?.map(p => <li key={p.id}>{p.name}</li>)}</ul>;
}
```

**When render props are still useful:** The logic needs a specific DOM event listener on an element the component owns (drag, resize, mouse). Also when consuming library APIs built around the pattern.

---

### Slide 9: Compound Components — The Problem They Solve

**Problem: prop explosion on complex, multi-piece UI**

```jsx
// ❌ Monolithic — every option is a prop; API grows without end
<Select
  options={countries}
  value={selected}
  onChange={setSelected}
  placeholder="Select country"
  isSearchable={true}
  isClearable={true}
  renderOption={(opt) => <Flag code={opt.code} />}
  renderValue={(val) => <span>{val.name}</span>}
  groupBy="continent"
  maxMenuHeight={300}
  {...eightMoreProps}
/>
```

**Compound components split a logically grouped UI into cooperating pieces that share state through Context:**

```jsx
// ✅ Consumer has full control; parent API stays minimal
<Select value={selected} onChange={setSelected}>
  <Select.Trigger placeholder="Select country" />
  <Select.Menu>
    {countries.map(c => (
      <Select.Option key={c.code} value={c}>
        <Flag code={c.code} /> {c.name}
      </Select.Option>
    ))}
  </Select.Menu>
</Select>
```

**HTML already uses this pattern:** `<select>/<option>`, `<table>/<tr>/<td>`, `<ul>/<li>`. Compound components bring the same idea to React components.

---

### Slide 10: Compound Components — Context-Based Implementation

```jsx
// 1. Context holds the shared state
const TabsContext = createContext(null);

// 2. Parent manages state, provides it via Context
function Tabs({ children, defaultTab = 0 }) {
  const [activeTab, setActiveTab] = useState(defaultTab);

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <div className="tabs">{children}</div>
    </TabsContext.Provider>
  );
}

// 3. Sub-components consume Context — they cooperate without explicit prop passing
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
  if (activeTab !== index) return null;
  return <div className="tab-panel">{children}</div>;
}

// 4. Attach as static properties — ergonomic, single import
Tabs.Tab      = Tab;
Tabs.TabPanel = TabPanel;
```

---

### Slide 11: Compound Components — Usage and When to Use

```jsx
// Consumer controls content, order, and what goes inside each panel
function ProductPage() {
  return (
    <Tabs defaultTab={0}>
      <div className="tab-list">
        <Tabs.Tab index={0}>Details</Tabs.Tab>
        <Tabs.Tab index={1}>Reviews</Tabs.Tab>
        <Tabs.Tab index={2}>Shipping</Tabs.Tab>
      </div>
      <Tabs.TabPanel index={0}>
        <ProductDetails product={product} />
      </Tabs.TabPanel>
      <Tabs.TabPanel index={1}>
        <ReviewList productId={product.id} />
      </Tabs.TabPanel>
      <Tabs.TabPanel index={2}>
        <ShippingInfo />
      </Tabs.TabPanel>
    </Tabs>
  );
}
```

**Benefits of the pattern:**
- No prop explosion — the parent `<Tabs>` API stays minimal
- Consumer has full control over content (open for extension)
- Sub-components that consume Context will throw a clear error if used outside the parent — self-documenting
- Each sub-component is independently testable

**When to use:** Tabs, Accordions, Dropdowns, Modals with named slots (Header/Body/Footer), Carousels, any UI family with tightly coupled, cooperating pieces.

---

### Slide 12: Why React Re-renders (and When It Matters)

**React re-renders a component when:**
```
1. Its own state changes (useState, useReducer)
2. Its parent re-renders
3. A context it subscribes to changes
```

```jsx
function Parent() {
  const [count, setCount] = useState(0);

  return (
    <>
      <button onClick={() => setCount(c => c + 1)}>Increment</button>
      <ExpensiveChild />   {/* re-renders every time count changes */}
      <AnotherChild />     {/* also re-renders — even though it uses neither count nor its setter */}
    </>
  );
}
```

**This is usually fine** — React's virtual DOM diffing is fast for most components.

**When it becomes a problem:**
- `ExpensiveChild` renders a large list of hundreds of items
- A parent near the root re-renders frequently (every keystroke, every animation frame)
- A frequently-updated context has many consumers throughout the tree

**The tools:** `React.memo`, `useCallback`, `useMemo`

**The rule: measure first, optimize second.** Only reach for these tools after the React DevTools Profiler shows an actual problem.

---

### Slide 13: React.memo — Memoizing Components

**Wraps a component and skips re-rendering if props haven't changed (shallow comparison):**

```jsx
// Without memo — re-renders every time parent renders
function ExpensiveList({ items }) {
  return <ul>{items.map(i => <li key={i.id}>{i.name}</li>)}</ul>;
}

// With memo — only re-renders when items reference actually changes
const ExpensiveList = React.memo(function ExpensiveList({ items }) {
  return <ul>{items.map(i => <li key={i.id}>{i.name}</li>)}</ul>;
});
```

**⚠️ The critical gotcha — object and function props bypass memo:**

```jsx
function Parent() {
  const [count, setCount] = useState(0);

  // ❌ New function reference on every render — memo sees "new props" and re-renders anyway
  const handleClick = () => console.log('clicked');

  // ❌ New object reference on every render — same problem
  const config = { theme: 'dark' };

  return <MemoizedChild onClick={handleClick} config={config} />;
}
```

**The fix:**
- `useCallback` for functions
- `useMemo` for objects and arrays

---

### Slide 14: useCallback — Stable Function References

**Returns the SAME function reference between renders unless dependencies change:**

```jsx
function Parent() {
  const [count, setCount] = useState(0);
  const [items, setItems] = useState([]);

  // ✅ Stable reference — setItems is guaranteed stable by React (no need in deps)
  const handleDelete = useCallback((id) => {
    setItems(prev => prev.filter(item => item.id !== id));
  }, []);

  const handleUpdate = useCallback((id, data) => {
    setItems(prev => prev.map(item =>
      item.id === id ? { ...item, ...data } : item
    ));
  }, []);

  return (
    <>
      <button onClick={() => setCount(c => c + 1)}>Increment count</button>
      {/* MemoizedList won't re-render when count changes — handleDelete/handleUpdate are stable */}
      <MemoizedList items={items} onDelete={handleDelete} onUpdate={handleUpdate} />
    </>
  );
}

const MemoizedList = React.memo(({ items, onDelete, onUpdate }) => {
  return (
    <ul>
      {items.map(item => (
        <li key={item.id}>
          {item.name}
          <button onClick={() => onDelete(item.id)}>Delete</button>
        </li>
      ))}
    </ul>
  );
});
```

`useCallback(fn, deps)` is syntax sugar for `useMemo(() => fn, deps)` — same mechanism, different intent.

---

### Slide 15: useMemo — Expensive Computations

**Memoizes the RESULT of a calculation — only recomputes when dependencies change:**

```jsx
function ProductDashboard({ products, searchTerm, filters }) {
  // ❌ Runs on EVERY render, even when unrelated state changes
  const filtered = products
    .filter(p => p.name.includes(searchTerm))
    .sort((a, b) => a.price - b.price);

  // ✅ Only recomputes when products, searchTerm, or filters actually change
  const filteredProducts = useMemo(() => {
    return products
      .filter(p => p.name.toLowerCase().includes(searchTerm.toLowerCase()))
      .filter(p => p.price >= filters.minPrice)
      .sort((a, b) => a.price - b.price);
  }, [products, searchTerm, filters]);

  // ✅ Stable object reference for memoized child component
  const chartConfig = useMemo(() => ({
    data:   filteredProducts.map(p => ({ label: p.name, value: p.price })),
    colors: ['#3498db', '#e74c3c'],
    type:   'bar'
  }), [filteredProducts]);

  return (
    <>
      <ProductList products={filteredProducts} />
      <Chart config={chartConfig} />
    </>
  );
}
```

**useMemo vs useCallback:**
| | `useMemo` | `useCallback` |
|---|---|---|
| Memoizes | A computed **value** | A **function** |
| Returns | Result of calling `fn()` | The function `fn` itself |
| Use for | Expensive calculations, stable object/array refs | Callbacks passed to memoized children |

---

### Slide 16: When NOT to Optimize + Decision Guide

**The rule: measure first, optimize second.**

```jsx
// ❌ Premature optimization — the overhead EXCEEDS the savings
const double = useMemo(() => count * 2, [count]);
// React must store the old value, compare deps, and manage cache — all to save one multiplication

// ✅ Memoize only when there's a measured problem
```

**Memoization helps when:**
- Component renders a large list (50+ expensive items)
- Computation iterates over large data (filtering 1,000+ records)
- Child component is wrapped in `React.memo` and would re-render unnecessarily
- Function is a `useEffect` dependency and changes every render (causes infinite loops)

**Memoization hurts or does nothing when:**
- The computation is trivial (< 1ms)
- Props change on every render anyway — memoization overhead with zero benefit
- The value or function isn't passed to children or used in effect dependencies

**The optimization decision flow:**
```
Is there a visible, measurable performance problem?
  → No:  Don't optimize. Move on.
  → Yes: Open React DevTools Profiler
         → Which component is the bottleneck?
              → Expensive calculation? → useMemo
              → Re-rendering when it shouldn't? → React.memo + useCallback
              → Entire page/feature loads too slowly? → Code splitting (Part 2)
              → State changing too broadly? → Move state closer to where it's used
```

---

### Slide 17: Part 1 Summary

**Composition Patterns:**
| Pattern | Use When |
|---|---|
| `props.children` | Generic containers, single content slot |
| Component injection (named props) | Multiple distinct layout regions |
| Render props | Shared behavior that requires owned DOM event binding |
| Custom hooks | Shared behavior without UI coupling (preferred over render props) |
| Compound components | Tightly coupled multi-piece UI with shared implicit state |

**Performance Tools:**
| Tool | What It Does | Use When |
|---|---|---|
| `React.memo` | Skip re-render if props unchanged (shallow) | Child doesn't need parent's unrelated updates |
| `useCallback` | Stable function reference between renders | Callback prop for a memoized child |
| `useMemo` | Cache computed value or stable object reference | Slow computation or object bypassing memo |

**Golden Rule:** Measure with the React DevTools Profiler before adding any optimization. Premature memoization adds complexity without benefit and can actually slow things down.

**Part 2:** Code splitting → Suspense → Concurrent features → DevTools → Production builds → Deployment
