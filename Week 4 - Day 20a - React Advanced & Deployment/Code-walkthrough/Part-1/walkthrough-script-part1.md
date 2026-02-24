# Day 20a — React Advanced & Deployment
# Part 1 Walkthrough Script
# Estimated time: ~90 minutes

---

## Overview of Part 1

| Segment | Topic | Time |
|---|---|---|
| 0 | Recap + Day intro | 5 min |
| 1 | Children as content (basic composition) | 6 min |
| 2 | Specialisation pattern | 6 min |
| 3 | Higher-Order Components (HOC) | 10 min |
| 4 | Render props pattern | 12 min |
| 5 | Compound components | 15 min |
| 6 | Why performance optimisation (rules first) | 5 min |
| 7 | React.memo | 12 min |
| 8 | useMemo | 10 min |
| 9 | useCallback | 10 min |
| 10 | Optimised Dashboard (putting it all together) | 5 min |
| 11 | Q&A + wrap-up | 4 min |

**Files:** `01-composition-patterns.jsx`, `02-performance-optimization.jsx`

---

## Segment 0 — Recap & Day Introduction (5 min)

"Good morning everyone. Today is Day 20a — the last React day of the program. You've come a long way in four weeks. You've built components, used hooks, connected Redux, wired up a router, called APIs, and written tests. Today we're going to learn the patterns that senior React developers use every day — the advanced composition patterns that make large applications maintainable, and the performance tools that make them fast.

Part 1 is split into two big areas. First: **advanced component patterns** — composition, render props, and compound components. Second: **performance optimisation** — React.memo, useMemo, and useCallback.

Part 2 this afternoon covers deployment: code splitting, lazy loading, Suspense, React DevTools, building for production, and deploying to the real world.

Let's start with the patterns. Open `01-composition-patterns.jsx`."

---

## Segment 1 — Children as Content / Basic Composition (6 min)

*Point to Section 1a — `Card` and `CourseListPage`*

"The first and most fundamental React pattern: passing children into a container component.

Look at `Card`. It has a `title` prop and `children`. It owns the box, border, and padding. It knows nothing about what goes inside it.

```jsx
function Card({ title, children }) {
  return (
    <div style={{ border: '1px solid #ddd', ... }}>
      {title && <h3>{title}</h3>}
      {children}
    </div>
  );
}
```

Now look at how we USE it in `CourseListPage`. Three completely different cards, one reusable component. One card has a button. One has a list. One has a plain paragraph. Card doesn't care.

> **❓ Question:** If I wanted to add an action bar at the bottom of every card — say, a footer with a timestamp — where would I add it?
> *(Answer: Inside the Card component itself — in the JSX, after `{children}`. All consumers get the footer automatically)*

This pattern — 'container owns layout, children own content' — is the foundation of every good component library. Think of Material UI's Card, Ant Design's Modal — they all use this."

---

## Segment 2 — Specialisation Pattern (6 min)

*Point to Section 1b — `Button` and the specialised buttons*

"Next: specialisation. We have a general `Button` that can be primary, danger, or secondary — and handles disabled state, click handlers, the full thing.

```jsx
function Button({ label, onClick, variant = 'primary', disabled = false }) { ... }
```

Then we have specialised wrappers that pre-fill the props:
```jsx
function EnrollButton({ courseId, onEnroll }) {
  return <Button label="Enroll Now" onClick={() => onEnroll(courseId)} variant="primary" />;
}
```

`EnrollButton` doesn't duplicate any styling logic. It just says 'create a Button with these specific settings'. Then in `CourseAdminRow`, the JSX is beautifully readable — `<EnrollButton>` and `<DeleteCourseButton>` — no variant props, no styling boilerplate.

> **⚠️ Watch out:** The `onClick` wrapper `() => onEnroll(courseId)` creates a new function on every render. This is fine here — but when we get to `useCallback` in Section 3, you'll see why this matters when the Button is wrapped in React.memo.

The rule: general component = logic + flexibility. Specialised component = pre-configured convenience wrapper. Both have their place."

---

## Segment 3 — Higher-Order Components (10 min)

*Point to Section 1c — `withLoadingSpinner`, `withAuth`, usage*

"Higher-Order Components — HOCs — were THE React pattern before hooks. They're still widely used in libraries (Redux's `connect()`, React Router's `withRouter`), so you need to know them even though custom hooks have taken over most of their use cases.

A HOC is just a function that takes a component and returns a new, enhanced component.

```jsx
function withLoadingSpinner(WrappedComponent) {
  return function WithSpinner({ isLoading, ...rest }) {
    if (isLoading) return <p>⏳ Loading...</p>;
    return <WrappedComponent {...rest} />;
  };
}
```

Three things to notice:
1. The function name starts with lowercase `with` — that's the convention for HOCs.
2. The returned component starts with uppercase `With` — it IS a component.
3. We spread `...rest` onto `WrappedComponent` — all the original props pass through untouched. **This is critical.** If you forget to spread rest, the wrapped component receives no props.

```jsx
const CourseListWithSpinner = withLoadingSpinner(CourseList);
const ProtectedWithSpinner  = withAuth(withLoadingSpinner(CourseList));
```

The last line shows **HOC composition** — wrapping a component in multiple HOCs. Read it inside-out: first `withLoadingSpinner`, then `withAuth` wraps the result.

> **⚠️ Watch out:** The ORDER of HOC wrapping matters. `withAuth(withLoadingSpinner(X))` means auth is checked first — if not authenticated, the spinner never renders. `withLoadingSpinner(withAuth(X))` means the spinner renders first — even for unauthenticated users. Think about which guard should be outermost.

> **❓ Question:** What's the problem with defining HOC-enhanced components INSIDE a render function or another component's body?
> *(Answer: Every render creates a brand new component type, causing React to unmount and remount the child instead of updating it — a major bug and performance problem. Always define HOC-enhanced components at the MODULE level, outside any component)*"

---

## Segment 4 — Render Props Pattern (12 min)

*Point to Section 2 — `DataFetcher`, `CourseListConsumer`, `CourseCountConsumer`, `MouseTracker`*

"Render props solve the same problem HOCs solve — sharing stateful logic — but through a different mechanism. Instead of wrapping a component, you pass a FUNCTION as a prop, and the component calls that function with its internal data.

Look at `DataFetcher`:
```jsx
function DataFetcher({ url, render }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // ... fetch logic ...

  return render({ data, loading, error });
}
```

It owns the fetch logic completely. It doesn't know or care how the data is displayed — it just calls `render()` and passes everything it knows.

Now look at `CourseListConsumer` and `CourseCountConsumer`. SAME DataFetcher, SAME URL, but completely different UIs:

```jsx
<DataFetcher
  url="/api/courses"
  render={({ data, loading, error }) => {
    if (loading) return <p>Loading...</p>;
    return <ul>{ data?.map(c => <li key={c.id}>{c.title}</li>) }</ul>;
  }}
/>
```

```jsx
<DataFetcher
  url="/api/courses"
  render={({ data, loading }) => (
    <span>{loading ? '...' : `${data?.length} courses`}</span>
  )}
/>
```

The DataFetcher component is completely reusable. The consumer has 100% control over rendering.

Now look at the `children-as-function` variant with `MouseTracker`. Instead of a prop named `render`, we use `children`:
```jsx
<MouseTracker>
  {({ x, y }) => <p>Mouse: {x}, {y}</p>}
</MouseTracker>
```

`{children(position)}` inside MouseTracker — `children` is called as a function. This is the 'children as render prop' pattern and it's arguably cleaner to read.

> **⚠️ Watch out:** Inline render prop functions like `render={({ data }) => <ul>...</ul>}` create a new function on every parent render. If DataFetcher were memo-wrapped, this would break memoisation. For performance-critical cases, define the render function outside the component or use `useCallback`.

> **❓ Question:** React Router v6 uses a prop called `element` instead of `render`. Is that the same pattern?
> *(Answer: Almost — `element` takes JSX (a value), not a function. But the underlying principle — consumer controls what renders — is the same)*"

---

## Segment 5 — Compound Components (15 min)

*Point to Section 3 — `Tabs` and `Accordion`*

"Compound components are my favourite React pattern — and the one that impresses interviewers the most. Think about HTML's `<select>` and `<option>`. You don't pass a `selectedIndex` prop to `<option>`. The components KNOW they're in a select, and they share state implicitly. That's the compound components pattern.

Let's look at our `Tabs` implementation. Here's how a consumer uses it:

```jsx
<Tabs defaultTab={0}>
  <Tabs.List>
    <Tabs.Tab>Overview</Tabs.Tab>
    <Tabs.Tab>Curriculum</Tabs.Tab>
    <Tabs.Tab>Reviews</Tabs.Tab>
  </Tabs.List>
  <Tabs.Panels>
    <Tabs.Panel>Content A</Tabs.Panel>
    <Tabs.Panel>Content B</Tabs.Panel>
    <Tabs.Panel>Content C</Tabs.Panel>
  </Tabs.Panels>
</Tabs>
```

Beautiful, right? No `activeTab` prop passed down manually. No callback prop threaded through. The consumer just describes STRUCTURE.

Now let's look at how it works. The `Tabs` parent:

```jsx
function Tabs({ children, defaultTab = 0 }) {
  const [activeTab, setActiveTab] = useState(defaultTab);

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <div>{children}</div>
    </TabsContext.Provider>
  );
}
```

It owns the state and provides it via Context. Then `Tab` reads from Context:

```jsx
function Tab({ children, index }) {
  const { activeTab, setActiveTab } = useContext(TabsContext);
  const isActive = activeTab === index;
  ...
}
```

> **❓ Question:** Tab needs an `index` to know which tab it is. But the consumer's JSX doesn't pass `index`. How does it get it?
> *(Point to TabList's children mapping)*

Look at `TabList`:
```jsx
function TabList({ children }) {
  return (
    <div>
      {React.Children.map(children, (child, index) =>
        React.cloneElement(child, { index })
      )}
    </div>
  );
}
```

`React.Children.map` iterates over children. `React.cloneElement` creates a copy of each child and INJECTS additional props. So `<Tabs.Tab>Overview</Tabs.Tab>` becomes `<Tab index={0}>Overview</Tab>` automatically. The consumer never needs to manage indices.

At the bottom:
```jsx
Tabs.List   = TabList;
Tabs.Tab    = Tab;
Tabs.Panels = TabPanels;
Tabs.Panel  = TabPanel;
```

We attach sub-components as properties of the parent. This creates a clean namespace — everything lives under `Tabs.*` and import statements stay simple.

> **⚠️ Watch out:** Compound components break if you put non-component children between `TabList`'s items (like a `<span>` or `<div>` for spacing). `React.cloneElement` would try to inject `index` into a DOM element, which React ignores. Defensive implementations check `React.isValidElement(child)` before cloning.

Now look at the `Accordion` example at the bottom. Same pattern, different implementation — `allowMultiple` controls whether one or many panels can be open. The consumer API is just as clean."

---

## Segment 6 — Performance: The Rules First (5 min)

*Switch to `02-performance-optimization.jsx` — point to Section 0*

"Before we touch a single optimisation API, I need to make this absolutely clear: **do not optimise prematurely**.

React re-renders are fast. A component re-rendering doesn't mean the DOM updates — React's reconciler compares the virtual DOM and only applies real DOM changes when something actually changed. Most React apps are perfectly fast without any of the tools we're about to cover.

The three-question checklist before optimising:
1. Have I MEASURED a performance problem? (React DevTools Profiler — covered in Part 2)
2. Is the slow thing actually caused by unnecessary re-renders or expensive calculations?
3. Is the optimisation worth the added complexity?

If you can't answer yes to all three, don't optimise yet.

With that said — let's look at the tools, understand them, and know when to reach for them."

---

## Segment 7 — React.memo (12 min)

*Point to Section 1 — `CourseCardUnmemoised`, `CourseCard`, `CourseCardCustomCompare`, `CourseListParent`*

"By default, when a parent component re-renders, ALL its children re-render too — even if their props haven't changed. React.memo stops this.

Look at `CourseListParent`. It has a counter and two course cards. Every time you click 'Re-render parent', the counter changes and the parent re-renders.

`CourseCardUnmemoised` — no memo. It re-renders every single time the parent does.

`CourseCard` — wrapped in `React.memo`. React compares props shallowly before re-rendering. If props haven't changed, the render is skipped.

```jsx
const CourseCard = React.memo(function CourseCard({ course, onEnroll }) {
  console.log(`✅ CourseCard rendered: ${course.title}`);
  ...
});
```

But now look at the parent's handler:
```jsx
const handleEnrollBroken = (id) => { console.log('Enrolling', id); };
```

Every render of the parent creates a **new function reference** for `handleEnrollBroken`. Even though it does the same thing, it's a new object in memory. React.memo does shallow comparison — and `prevProps.onEnroll !== nextProps.onEnroll` — so the memo is USELESS. The card still re-renders.

> **⚠️ Watch out:** React.memo only works if all props are referentially stable. Objects, arrays, and functions created inline will always fail the shallow comparison. This is exactly why we need `useCallback` — coming in Section 3.

The custom comparator variant:
```jsx
const CourseCardCustomCompare = React.memo(Component, (prevProps, nextProps) => {
  return prevProps.course.id === nextProps.course.id;
});
```

The second argument receives previous and next props. Return `true` to skip re-render, `false` to allow it. Here we only care if the ID changes — we don't care if `enrollmentCount` ticks up.

> **⚠️ Watch out:** A custom comparator that returns `true` too aggressively will cause stale UI — your component won't update when it should. Use with care and test thoroughly."

---

## Segment 8 — useMemo (10 min)

*Point to Section 2 — `CourseFilter`*

"`useMemo` memoises a VALUE — the result of a computation. It only recalculates when its dependencies change.

```jsx
const filteredCourses = useMemo(() => {
  console.log('🔄 Computing filtered courses...');
  return courses
    .filter(c => selectedLevel === 'all' ? true : c.level === selectedLevel)
    .filter(c => c.title.toLowerCase().includes(searchTerm.toLowerCase()))
    .sort((a, b) => a.title.localeCompare(b.title));
}, [courses, searchTerm, selectedLevel]);
```

The function runs once on mount, and then only when `courses`, `searchTerm`, or `selectedLevel` change. If `unrelatedCount` changes and triggers a parent re-render, this computation is SKIPPED — we get the cached value back.

> **❓ Question:** What happens if you forget to include `courses` in the dependency array?
> *(Answer: If courses changes — say, new data loads from an API — `filteredCourses` would show stale results from the previous courses array. Stale closures are a common bug)*

The `courseStats` example shows useMemo for aggregation:
```jsx
const courseStats = useMemo(() => {
  const total = courses.length;
  const byLevel = courses.reduce(...);
  const avgDuration = courses.reduce(...) / total;
  return { total, byLevel, avgDuration };
}, [courses]);
```

This returns an OBJECT. Even if the calculation is fast, using useMemo here provides referential stability — the same object reference is returned when courses hasn't changed. This matters if `courseStats` is passed as a prop to a React.memo child.

> **⚠️ Watch out:** `useMemo(() => a + b, [a, b])` — this is overkill. Simple math doesn't need memoisation. Save it for .filter, .sort, .reduce on large arrays, or for expensive transformations."

---

## Segment 9 — useCallback (10 min)

*Point to Section 3 — `CourseListOptimised`*

"`useCallback` memoises a FUNCTION — it returns the same function reference across renders, as long as its dependencies haven't changed.

Here's the fix for the React.memo problem we saw in Section 1:
```jsx
const handleEnroll = useCallback((id) => {
  setSelectedCourseId(id);
  setNotification(`Enrolled in course #${id}`);
}, []);
```

Empty dependency array — `handleEnroll` is created once and never recreated. Now when this is passed to a React.memo CourseCard, the shallow comparison succeeds and the card doesn't re-render.

With dependencies:
```jsx
const handleUnenroll = useCallback(
  (id) => {
    if (selectedCourseId === id) {
      setSelectedCourseId(null);
    }
  },
  [selectedCourseId]
);
```

This function reads `selectedCourseId` from the outer scope. Without `selectedCourseId` in the dependency array, the function would capture a stale value. With it, the function is recreated whenever `selectedCourseId` changes — but still not on every random re-render.

The `fetchCourseDetails` + `useEffect` example is important:
```jsx
const fetchCourseDetails = useCallback(async (id) => {
  const res = await fetch(`/api/courses/${id}`);
  ...
}, []);

useEffect(() => {
  if (selectedCourseId) fetchCourseDetails(selectedCourseId);
}, [selectedCourseId, fetchCourseDetails]);
```

Because `fetchCourseDetails` is wrapped in `useCallback`, it has a stable reference. It's safe to include in the `useEffect` dependency array without causing an infinite loop.

> **⚠️ Watch out:** If you include an unstable function in a useEffect dependency array WITHOUT useCallback, the effect runs on every render — and if the effect modifies state, you get an infinite loop. Always stabilise functions with useCallback before including them in useEffect deps."

---

## Segment 10 — Optimised Dashboard (5 min)

*Point to Section 4 — `OptimisedCourseDashboard`*

"The `OptimisedCourseDashboard` shows all three tools working in concert. Let's trace it quickly:

- `filteredCourses` uses `useMemo` — only recalculates when courses, searchTerm, or selectedLevel changes
- `handleEnroll` uses `useCallback` — empty deps, never recreated
- `CourseCard` uses `React.memo` — only re-renders when course or onEnroll props change

Click 'Toggle Theme' in your imagination. The parent re-renders. The theme changes. But none of the course cards re-render — because neither `course` nor `handleEnroll` changed. That's the combined effect.

Section 5 at the bottom is a quick reference table. I want you to refer back to this. The question isn't 'should I use useMemo?' it's 'have I measured a problem, and is this the right tool for it?'

Let's take a 10-minute break and then we'll move into Part 2 — code splitting, deployment, and DevTools."

---

## Segment 11 — Q&A Prompts (4 min)

1. "You have a `ProductList` component that is memo-wrapped. Its parent passes a `filters` object: `<ProductList filters={{ category: 'shoes', maxPrice: 100 }} />`. After adding React.memo, you notice the list STILL re-renders on every parent render. Why?"
   *(Answer: The `filters` object literal `{ category: 'shoes', maxPrice: 100 }` is created fresh on every parent render. Even though its contents are identical, it's a new object reference each time — shallow comparison fails. Fix: move the object outside the parent component, or use `useMemo(() => ({ category, maxPrice }), [category, maxPrice])`)*

2. "When would you choose the render props pattern over a custom hook for sharing logic?"
   *(Answer: When the consumer needs to control the rendering output — render props give the consumer 100% rendering control. Custom hooks return values/functions but don't control rendering. When only logic needs sharing, a custom hook is cleaner. When rendering decisions are involved, render props still have a place)*

3. "You create a compound component `Menu` with `Menu.Item` sub-components. A developer nests a `<div>` between two `Menu.Item`s for spacing. The index counting breaks. How would you fix this?"
   *(Answer: Use `React.Children.map` with `React.isValidElement(child)` check before cloneElement, and skip non-component children. Or use a different mechanism for index tracking — like a counter that increments only for valid Tab children)*

4. "What's the difference between useMemo and useCallback?"
   *(Answer: useMemo returns a memoised VALUE — the return value of the function you pass. useCallback returns a memoised FUNCTION — the function itself. `useCallback(fn, deps)` is equivalent to `useMemo(() => fn, deps)`)*

---

*End of Part 1 Script*
