// =============================================================================
// Day 20a — Part 2 | File 2: Concurrent Features & React DevTools
// =============================================================================
// Topics covered:
//   1. useTransition — mark state updates as non-urgent
//   2. useDeferredValue — defer expensive derived state
//   3. Suspense for data fetching (React 18+ concept)
//   4. React DevTools — Components tab & Profiler tab tips
// =============================================================================

import React, {
  useState,
  useTransition,
  useDeferredValue,
  useMemo,
  Suspense,
} from "react";

// =============================================================================
// SECTION 1 — useTransition
// =============================================================================
// useTransition lets you mark a state update as "non-urgent" so React can
// keep the UI responsive while working on the expensive update in the
// background. It returns [isPending, startTransition].
// =============================================================================

// Simulated course data — 10,000 items to force a slow render
const ALL_COURSES = Array.from({ length: 10_000 }, (_, i) => ({
  id: i + 1,
  title: `Course ${i + 1}`,
  category: i % 3 === 0 ? "Java" : i % 3 === 1 ? "React" : "DevOps",
}));

function SlowCourseList({ filter }) {
  // This component deliberately does expensive work on every render
  const filtered = ALL_COURSES.filter(
    (c) =>
      filter === "" || c.title.toLowerCase().includes(filter.toLowerCase())
  );

  return (
    <ul style={{ maxHeight: 200, overflow: "auto" }}>
      {filtered.map((c) => (
        <li key={c.id}>{c.title}</li>
      ))}
    </ul>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// WITHOUT useTransition — typing feels laggy because every keystroke
// triggers the expensive SlowCourseList render synchronously
// ─────────────────────────────────────────────────────────────────────────────
function CourseSearchWithoutTransition() {
  const [filter, setFilter] = useState("");

  return (
    <div>
      <input
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
        placeholder="Search courses (no transition)..."
      />
      <SlowCourseList filter={filter} />
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// WITH useTransition — the input updates immediately (urgent);
// the heavy list re-render is deferred (non-urgent).
// isPending lets us show a visual "thinking" indicator.
// ─────────────────────────────────────────────────────────────────────────────
function CourseSearchWithTransition() {
  const [inputValue, setInputValue] = useState("");
  const [filter, setFilter] = useState("");

  // isPending is true while the non-urgent update is still in flight
  const [isPending, startTransition] = useTransition();

  function handleChange(e) {
    const value = e.target.value;

    // URGENT — update the input instantly so it feels responsive
    setInputValue(value);

    // NON-URGENT — React may defer and batch this update
    startTransition(() => {
      setFilter(value);
    });
  }

  return (
    <div>
      <input
        value={inputValue}
        onChange={handleChange}
        placeholder="Search courses (with transition)..."
        style={{ borderColor: isPending ? "orange" : "black" }}
      />
      {/* Show a subtle indicator while the list is being re-rendered */}
      {isPending && <span style={{ color: "gray" }}> ⏳ Updating…</span>}
      <SlowCourseList filter={filter} />
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// INSTRUCTOR NOTE: When to use useTransition vs useCallback/useMemo
//
// • useCallback / useMemo         → avoid UNNECESSARY re-renders
// • useTransition / startTransition → keep the UI responsive during
//                                     a render that is genuinely expensive
//
// These solve different problems. Use profiling to decide which you need.
// ─────────────────────────────────────────────────────────────────────────────

export function TransitionDemo() {
  const [mode, setMode] = useState("with");
  return (
    <div style={{ padding: 16 }}>
      <h2>useTransition Demo</h2>
      <label>
        <input
          type="radio"
          value="without"
          checked={mode === "without"}
          onChange={() => setMode("without")}
        />{" "}
        Without Transition
      </label>{" "}
      <label>
        <input
          type="radio"
          value="with"
          checked={mode === "with"}
          onChange={() => setMode("with")}
        />{" "}
        With Transition
      </label>
      <hr />
      {mode === "without" ? (
        <CourseSearchWithoutTransition />
      ) : (
        <CourseSearchWithTransition />
      )}
    </div>
  );
}

// =============================================================================
// SECTION 2 — useDeferredValue
// =============================================================================
// useDeferredValue returns a "stale" version of a value that React will
// update in the background. Unlike useTransition you don't need access to
// the setter — it works on VALUES, not state update calls.
//
// Great for: derived/computed UI that depends on a prop or state you don't
// control (e.g., a search term passed down from a parent).
// =============================================================================

// HeavyList simulates an expensive render
const HeavyList = React.memo(function HeavyList({ query }) {
  const items = useMemo(() => {
    // Simulate heavy work
    return ALL_COURSES.filter((c) =>
      c.title.toLowerCase().includes(query.toLowerCase())
    );
  }, [query]);

  return (
    <ul style={{ maxHeight: 200, overflow: "auto" }}>
      {items.map((c) => (
        <li key={c.id}>{c.title}</li>
      ))}
    </ul>
  );
});

export function DeferredValueDemo() {
  const [query, setQuery] = useState("");

  // deferredQuery lags behind query — React may show a stale list
  // while computing the new one, keeping the input responsive.
  const deferredQuery = useDeferredValue(query);

  // When deferredQuery !== query, we know React is still catching up
  const isStale = deferredQuery !== query;

  return (
    <div style={{ padding: 16 }}>
      <h2>useDeferredValue Demo</h2>
      <input
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search courses…"
      />
      {/* Dim the list while stale to signal it's updating */}
      <div style={{ opacity: isStale ? 0.5 : 1 }}>
        <HeavyList query={deferredQuery} />
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// useTransition vs useDeferredValue — quick comparison
//
// useTransition:
//   + You control the state setter that causes the slow render
//   + You get isPending to show a loading indicator
//   - Requires changes at the point where state is set
//
// useDeferredValue:
//   + Works when you receive a value via props (don't own the setter)
//   + Simple — just wrap the value
//   - No built-in pending flag (derive it: value !== deferred)
// ─────────────────────────────────────────────────────────────────────────────

// =============================================================================
// SECTION 3 — Suspense for Data Fetching (React 18 pattern)
// =============================================================================
// React 18 officially supports Suspense for data fetching via frameworks
// (Next.js App Router, Remix) or libraries (React Query, SWR).
//
// The core idea: a component "suspends" (throws a Promise) while data is
// loading; React shows the nearest <Suspense> fallback until it resolves.
//
// Here we simulate this with a simple "suspense-compatible" resource pattern
// so students understand the concept before using a real library.
// =============================================================================

// ── Minimal "resource" helper (NOT production code — for teaching only) ──────
function createResource(promise) {
  let status = "pending";
  let result;
  const suspender = promise.then(
    (data) => {
      status = "success";
      result = data;
    },
    (error) => {
      status = "error";
      result = error;
    }
  );

  return {
    read() {
      if (status === "pending") throw suspender; // React catches this
      if (status === "error") throw result;
      return result;
    },
  };
}

// ── Simulate an API call ──────────────────────────────────────────────────────
function fetchFeaturedCourse() {
  return new Promise((resolve) =>
    setTimeout(
      () => resolve({ id: 1, title: "Advanced React Patterns", rating: 4.9 }),
      1500
    )
  );
}

// Created ONCE outside the component so it isn't re-created on every render
const featuredCourseResource = createResource(fetchFeaturedCourse());

// ── Component that "suspends" ─────────────────────────────────────────────────
function FeaturedCourse() {
  // .read() throws a Promise if data isn't ready yet — React suspends this
  // component and shows the <Suspense fallback> above it in the tree.
  const course = featuredCourseResource.read();

  return (
    <div style={{ border: "1px solid #ccc", padding: 12, borderRadius: 4 }}>
      <h3>⭐ Featured: {course.title}</h3>
      <p>Rating: {course.rating}</p>
    </div>
  );
}

export function SuspenseDataDemo() {
  return (
    <div style={{ padding: 16 }}>
      <h2>Suspense for Data Fetching</h2>
      <p>
        The course card below suspends while fetching. React shows the fallback
        until data is ready — no <code>isLoading</code> state required.
      </p>
      <Suspense fallback={<div>⏳ Loading featured course…</div>}>
        <FeaturedCourse />
      </Suspense>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// INSTRUCTOR NOTE: In real apps you would use:
//   • React Query  → useQuery() / useSuspenseQuery()
//   • SWR          → useSWR() with suspense: true option
//   • Next.js 13+  → Server Components + fetch() with caching
//
// The resource pattern above is only for teaching the concept. Don't ship it.
// ─────────────────────────────────────────────────────────────────────────────

// =============================================================================
// SECTION 4 — React DevTools Walkthrough (narrative / comment-based)
// =============================================================================
// React DevTools is a browser extension (Chrome / Firefox / Edge).
// Install: https://react.dev/learn/react-developer-tools
//
// ── Components Tab ──────────────────────────────────────────────────────────
//
// • Shows your component tree on the left, exactly mirroring JSX structure.
// • Click a component to inspect:
//     - Props  — current values passed in
//     - State  — useState values (can be edited live!)
//     - Hooks  — all hooks with their current values
//     - Source — the file/line where the component is defined
//
// • "Highlight updates" toggle (⚙ → "Highlight updates when components render")
//     - Flash = that component re-rendered. Use this to spot unnecessary renders.
//
// • Search bar — type a component name to filter the tree.
//
// ── Profiler Tab ─────────────────────────────────────────────────────────────
//
// • Click ● (record), interact with the app, click ■ (stop).
// • Flamegraph — horizontal bars show every component that rendered in a
//   commit. Width = time spent. Hover a bar for exact duration.
//   Grey = did not render in this commit.
//
// • Ranked chart — sorts by render time so slowest components are at the top.
//   Great for finding low-hanging fruit.
//
// • "Why did this render?" — in the Components tab, select a component and
//   look at the right panel after a profiler recording. React will tell you:
//     "Rendered because: hooks changed (useState) / parent re-rendered / etc."
//
// • Record "why did each component render" setting must be enabled in ⚙ first.
//
// ── Practical Workflow ───────────────────────────────────────────────────────
//
// 1. Enable "Highlight updates when components render"
// 2. Perform the action you suspect is slow
// 3. Watch which components flash — unexpected flashes = wasted renders
// 4. For deeper analysis → switch to Profiler, record, inspect flamegraph
// 5. Use React.memo / useMemo / useCallback to fix the identified bottlenecks
// 6. Re-profile to confirm improvement
//
// INSTRUCTOR TIP: Live-demo the DevTools in the browser during the walkthrough.
// Compose the TransitionDemo or DeferredValueDemo above and profile them.
// =============================================================================

// Placeholder component so this file has a default export
export default function DevtoolsAndConcurrentFeatures() {
  return (
    <div style={{ padding: 24, fontFamily: "sans-serif" }}>
      <h1>Day 20a — Concurrent Features & DevTools</h1>
      <TransitionDemo />
      <hr />
      <DeferredValueDemo />
      <hr />
      <SuspenseDataDemo />
      <hr />
      <p>
        <strong>React DevTools</strong> walkthrough is in the instructor
        speaking notes. Open DevTools → Components tab → enable "Highlight
        updates" → interact with the demos above.
      </p>
    </div>
  );
}
