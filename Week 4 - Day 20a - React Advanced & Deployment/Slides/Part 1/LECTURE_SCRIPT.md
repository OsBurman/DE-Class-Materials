# Day 20a — Part 1: React Advanced Patterns & Performance
## Verbatim Lecture Script

**Duration:** 60 minutes
**Timing:** ~165 words/minute
**Style:** Conversational, practical — building on the full week of React work

---

`[00:00–03:00]`

Good morning everyone. Welcome to Day 20a — the last day of our React deep-dive. Week 4 Friday. You've covered an enormous amount of ground this week: hooks on Tuesday, routing and Redux on Wednesday, API calls and testing on Thursday. Today we cap off the React section with two things. Part 1 is advanced patterns and performance optimization. Part 2 — after the break — is getting the application out the door: code splitting, production builds, and actual deployment.

Let me frame what Part 1 is about. We're looking at three patterns — composition, render props, and compound components — and three performance tools: React.memo, useCallback, and useMemo. These are the topics that distinguish junior React developers from senior ones. You'll encounter them in code reviews, in technical interviews, and — most importantly — in real codebases the moment you join a team. Libraries you already use, like React Router and Formik and Headless UI, are built on these patterns.

The goal for this hour: by the end, when you encounter a new component design problem, you should be able to identify which pattern fits and understand why.

---

`[03:00–12:00]`

**Slides 2, 3, 4 — Component Composition.**

Let's start with composition, which is the foundation everything else builds on. I want to make sure this is really solid before we get into the more sophisticated patterns.

Here's the problem composition solves. As applications grow, components tend to become rigid. A `Card` component that only works with one specific structure. A `Layout` that always puts the same content in the same place. Every time you need a variation, you either copy the component or add another prop. Neither is good. Copy-paste means you maintain two components that drift apart. Prop accumulation means your component eventually has fifteen props and nobody knows which ones matter.

React's answer is composition. And the most basic form of composition is `props.children`. Look at slide 3. The `Card` component takes a `title` and `children`. It doesn't know or care what's inside. It's a generic container. On a profile page, you put an avatar and a bio inside. On a settings page, you put a form inside. `Card` itself never changes — the content is always supplied by whoever uses it.

This is actually how HTML works. A `div` doesn't know what you're going to put in it. It's just a box. React's composition model is the exact same idea applied to components.

`children` is just a prop. A special one — it captures anything you write between the opening and closing tags — but it behaves like any other prop. You render it wherever makes sense inside the component. You can even conditionally render it. The `{title && ...}` pattern on slide 3 is an example: if no title is passed, that section simply doesn't render.

Now look at slide 4. Sometimes one slot isn't enough. You need a layout with a header, a sidebar, AND a main content area. Three completely different pieces, each one could be any component. For this, you use named component props — sometimes called "component injection" or "slot composition."

Notice `PageLayout` accepts `header`, `sidebar`, and `children`. All three are JSX expressions passed in as props. Here's the thing about JSX: it's just a JavaScript object. A React element is an object. So you can pass it as a prop and render it wherever you want inside the receiving component. `PageLayout` renders `header` at the top, `sidebar` in the aside, `children` in the main area. It knows nothing about `NavBar` or `CategoryList` or `ProductGrid` specifically.

Quick rule: use `children` when you have one content area. Use named props when you have multiple distinct regions. And as a team, document those props clearly so consumers know what goes where.

---

`[12:00–22:00]`

**Slides 5, 6, 7, 8 — Render Props.**

Render props. This is a pattern that was extremely popular before custom hooks arrived in React 16.8, and you will absolutely see it in existing codebases and library code. Understanding it is also important because it sharpens your intuition for custom hooks — they solve the same problem from a different angle.

The problem they solve is sharing stateful logic across components while letting each component own its own UI. Slide 5 describes the scenario. You have mouse-tracking logic — tracking the current X and Y position of the cursor. Three different components want to use this logic, but each one wants a completely different UI: one shows text coordinates, one moves an image around, one draws on a canvas. If you put the logic inside each component, you duplicate it. If you put it in a parent and pass it down, that's prop drilling.

The render prop pattern gives you a third option. A component manages the behavior — the mouse event listener, the state — and instead of rendering a specific UI, it calls a function that you pass in as a prop. That function receives the current state and returns JSX. The component manages the "what" (position), you manage the "how it looks."

Look at the name: "render prop." It's a prop whose value is a render function.

Slide 6 — implementation. `MouseTracker` has a `render` prop. Inside the component, it sets up the mouse event listener, tracks position in state, and then in its JSX it calls `render(position)`. That's it. The component renders whatever that function returns.

On the consumer side, you write `<MouseTracker render={({ x, y }) => <p>({x}, {y})</p>} />`. You pass a function that takes the position object and returns JSX. Different consumer, same `MouseTracker` — pass a different function, get a different UI.

The `children` as function variant is exactly the same idea with different syntax. Instead of a prop called `render`, you use `children` as the function. You write the function between the opening and closing tags. Inside `MouseTracker`, you call `children(position)` instead of `render(position)`. Many libraries prefer this syntax — React Router's older API used it, Formik uses it. You'll recognize it in the wild.

Slide 7 — a more practical example. `DataFetcher` manages fetch, loading state, and error state. The consumer provides the render function and decides what to show during each state. Different consumers can use the same fetch logic with completely different loading skeletons, error messages, and data presentations.

Now slide 8 — render props versus custom hooks. After React 16.8, custom hooks became the preferred way to share stateful logic. The bottom of slide 8 shows the same `DataFetcher` logic extracted into a `useDataFetcher` hook. From a consumer perspective: you just call the hook at the top of your component function, destructure the result, and write your JSX. No wrapper component, no function-as-prop syntax.

The comparison table tells the story. Render props add extra nodes to the React component tree — wrapper components that show up in DevTools. Multiple render props require nesting. Hooks compose by simply calling them in sequence.

When are render props still the right choice? When the logic needs a specific DOM element and event listener — like `MouseTracker` attaching its `onMouseMove` to a div it owns. That's genuinely harder to extract into a hook without making the hook awkward. Also when working with a library that exposes a render prop API.

The takeaway: for new code you're writing today, use custom hooks for shared logic. Know render props so you can read and work with code that uses them — which is a lot of code.

---

`[22:00–32:00]`

**Slides 9, 10, 11 — Compound Components.**

Compound components. This is my favorite of the three because once you see it, you see it everywhere. In HTML. In every UI component library. In your own code when you stop fighting against complexity and lean into it.

The problem is on slide 9. You've built a `Select` component. At first it just needs options, a value, and onChange. Fine. Then someone needs search capability — add `isSearchable`. Then a clear button — add `isClearable`. Then custom option rendering — add `renderOption`. Then custom selected value rendering — add `renderValue`. Then grouping. Then keyboard navigation customization. Before you know it, you have a component with fifteen props and the person trying to use it has to read documentation for an hour.

This is called prop explosion or API sprawl. The component's interface becomes so large it's harder to use and harder to maintain.

Compound components solve this by breaking the single component into multiple smaller, collaborating components. Instead of passing fifteen props to one thing, you compose pieces. Look at the "after" example on slide 9. `Select.Trigger`, `Select.Menu`, `Select.Option` — each piece is its own component. The consumer controls them individually. You want a custom option? Just write what you want inside `Select.Option`. The parent doesn't need a `renderOption` prop for that.

How does the shared state work? Context. The parent component manages the state and puts it in a Context. The sub-components read from that Context.

Slide 10 walks through a complete implementation with Tabs. Step one: create a Context to hold the shared state — which tab is active and the setter to change it. Step two: the parent `Tabs` component manages `activeTab` in `useState`, wraps everything in the Context Provider. Step three: the sub-components. `Tab` reads `activeTab` from Context to know whether it's active, and calls `setActiveTab` when clicked. `TabPanel` reads `activeTab` and either renders or returns null based on whether its index matches.

The `Tabs.Tab = Tab` and `Tabs.TabPanel = TabPanel` lines at the bottom — that's just attaching the sub-components as static properties. It's purely ergonomic. It means consumers only need to import `Tabs`, and they get all the sub-components along with it. Some teams use named exports instead. Both work perfectly.

Slide 11 shows it in use. Notice how clean the consumer code is. No `tabs` array prop, no `renderTab` prop, no `activeTabIndex` management. The consumer controls what's in each tab and what's in each panel. The Tabs component manages when things show and hide. They have clearly separated responsibilities.

The sub-components can't be used outside the parent — if you render a `Tab` without a `Tabs` ancestor, `useContext(TabsContext)` returns null, and you'll get a clear error. That's actually a feature. It documents the intended usage.

Real-world usage: every major component library uses this. Headless UI, Radix UI, Reach UI — they're all built on compound components. Tabs, accordions, dropdowns, dialog with header/body/footer, menus. When you see a UI that has multiple tightly related pieces and shared state, compound components is the pattern.

---

`[32:00–48:00]`

**Slides 12, 13, 14, 15 — Performance Optimization.**

Alright. Performance. React is fast by default. For most components in most applications, you will never need to think about this at all. But there are specific situations where React's default behavior creates real performance problems, and there are specific tools to address them.

Slide 12 explains when React re-renders. Three cases. One: the component's own state changes. That's expected — state changed, re-render makes sense. Two: the parent re-renders. Three: a context the component subscribes to changes.

Case two is the main source of avoidable re-renders. If you have a component near the top of the tree that updates frequently — a timer, a cursor tracker, a form with lots of state — all of its children re-render with it, even if those children don't use that state at all. In a small app, fine. In an app with deep component trees and expensive child components, this can cause hundreds of unnecessary re-renders per update cycle.

Before I tell you how to fix this, let me be very explicit about something: **measure before you optimize**. In Part 2 we cover the React DevTools Profiler. Use it first. Find the actual slow component. Adding memoization everywhere speculatively is a code smell — it adds complexity and maintenance burden without guaranteed benefit.

With that said — the tools.

Slide 13. `React.memo`. You wrap a component in `React.memo` and React will skip re-rendering it if its props haven't changed. It compares each prop using strict equality — `===`. If all props are equal to what they were on the last render, React bails out and returns the cached output.

The catch that burns everyone the first time: objects and functions. Every time a component renders, any object literal or function defined inside that render creates a brand new reference. `{ theme: 'dark' }` on render 1 and `{ theme: 'dark' }` on render 2 are two different objects even though they look identical. `===` comparison on them returns false. So `React.memo` sees "props changed" and re-renders anyway. You added `React.memo` and it did nothing.

This is why `useCallback` and `useMemo` exist.

Slide 14. `useCallback`. It returns the same function reference between renders — unless the dependencies you list change. In the example, `handleDelete` is stabilized with `useCallback`. It's passed to `MemoizedList` as `onDelete`. Because `handleDelete` has a stable reference, `MemoizedList` doesn't re-render when the parent's counter increments. The memo actually works.

The dependency array for `useCallback` works exactly like `useEffect`'s dependency array. Include any value from the component scope that's used inside the callback. One important note: `useState` setters — the `setItems` function — are guaranteed by React to be stable between renders. You don't need to include them in the dependency array. That's why the examples have empty arrays despite using `setItems` inside.

Slide 15. `useMemo`. Same stabilization mechanism, but for computed values instead of functions. `ProductDashboard` needs to filter and sort a large products array. Without `useMemo`, that filter-plus-sort operation runs on every render — even renders triggered by completely unrelated state changes elsewhere in the app. With `useMemo`, it only runs when `products`, `searchTerm`, or `filters` actually change.

`useMemo` also solves the object reference problem. The `chartConfig` object in the example: if created inline on every render, a memoized `Chart` component receiving it would re-render every single time, even if the data didn't change. With `useMemo`, `chartConfig` is the same object reference until `filteredProducts` changes.

The comparison table at the bottom of slide 15 captures the difference between the two. `useMemo` memoizes a computed value — it calls your function and caches what it returns. `useCallback` memoizes a function itself — it returns the function without calling it. They use the same internal mechanism. `useCallback(fn, deps)` is literally equivalent to `useMemo(() => fn, deps)`.

---

`[48:00–56:00]`

**Slide 16 — When NOT to Optimize.**

I want to spend real time on this slide because it might be the most practically important one in Part 1.

`React.memo`, `useCallback`, and `useMemo` are not free. Every memoized value requires React to store the previous value and compare it on every render. Every dependency array requires evaluation. For tiny operations — multiplying two numbers, rendering a two-element list — the overhead of memoization exceeds the cost of just recomputing the value directly.

The test I use: would you notice if you removed the memoization? If the component renders in under a millisecond, the answer is no. If the component renders large lists or performs expensive calculations, the answer might be yes. Measure to find out.

The bullet points on slide 16 give you a checklist. Memoization helps when you're working with large lists, genuinely expensive computations on large datasets, callbacks passed to memoized children that would otherwise re-render unnecessarily, or functions that are dependencies of `useEffect` — because if those functions recreate on every render, your effect runs on every render, potentially causing infinite loops.

It doesn't help — and actively adds noise to your codebase — when you're memoizing trivial calculations, when the props change on every render anyway (making the comparison work always happen with no benefit), or when the value or function isn't going anywhere that cares about reference stability.

The decision flow at the bottom of the slide is my mental model for approaching a real performance problem. Step one: is there an observable problem? User is noticing lag, the profiler shows a slow render? If no, stop. Don't optimize speculatively. If yes, open the Profiler, find the specific slow component, and then decide what it needs.

One thing worth highlighting before we move on. I said earlier that case two for re-renders — the parent re-rendering — is the main source of avoidable re-renders. But before reaching for `React.memo`, ask a different question: should this state live this high in the tree at all?

If you have a counter that increments every second and it lives in `App`, every component in the entire application re-renders every second. `React.memo` on every component is one fix. But the better fix is to move the counter down to the one component that actually displays it. State that changes frequently should live as close as possible to where it's used. That's not a performance optimization — that's good component design, and it costs nothing.

---

`[56:00–60:00]`

**Slide 17 — Summary.**

Let me bring together the hour.

Three composition patterns. `props.children` for generic containers — the most common, most fundamental, use it constantly. Component injection for multiple layout slots — named props that accept JSX. Render props for sharing behavior that's tied to a DOM element the component owns — understand it to read existing code, prefer custom hooks for new code. Compound components for tightly coupled UI families where multiple pieces share state through Context — tabs, dropdowns, accordions.

Three performance tools. `React.memo` to skip re-renders when props haven't changed — requires object and function props to be stabilized. `useCallback` to give functions stable references — pair with `React.memo` on the receiving component. `useMemo` to cache expensive computations and stabilize object references.

The meta-point that overrides all of it: measure before you optimize. The Profiler, which we'll use in Part 2, tells you what's actually slow. Adding memoization to every component by default is cargo-cult optimization — it looks like performance work but isn't necessarily helping.

In Part 2 we're shifting from writing advanced components to shipping them. Code splitting to reduce your initial load time. Suspense and concurrent React for better loading experiences. React DevTools to find performance issues in practice. And then building for production, setting up environment variables, and deploying to the real internet. Let's take a short break and I'll see you back here in a few minutes.
