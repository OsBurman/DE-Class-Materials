// =============================================================================
// 02-useEffect-and-event-handling.jsx — useEffect & Event Handling
// =============================================================================
// useEffect lets you perform SIDE EFFECTS in a functional component.
// A "side effect" is anything that reaches outside the component's render:
//   • Fetching data from an API
//   • Setting up a timer / interval
//   • Subscribing to a WebSocket or event bus
//   • Directly updating the document title or DOM
//   • Setting up / tearing down event listeners
//
// SECTIONS:
//  1. useEffect with NO dependency array — runs after every render
//  2. useEffect with EMPTY dependency array [] — runs once on mount
//  3. useEffect with SPECIFIC dependencies — runs when deps change
//  4. Effect CLEANUP — preventing memory leaks and stale subscriptions
//  5. Multiple useEffect calls — one per concern
//  6. Event Handling — synthetic events, patterns, common event types
// =============================================================================

import React, { useState, useEffect } from 'react';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — No Dependency Array: Runs After EVERY Render
// ─────────────────────────────────────────────────────────────────────────────
// Omitting the dependency array means React runs this effect after every
// single render — initial AND every update. Use rarely; usually too aggressive.

export function DocumentTitleSync() {
  const [count, setCount] = useState(0);
  const [name, setName]   = useState('Student');

  // Runs after every render — every time count OR name changes
  useEffect(() => {
    document.title = `${name}'s Counter: ${count}`;
    // No return = no cleanup needed for this effect
  }); // ← no [] = runs every render

  return (
    <div>
      <h2>No Dependency Array — Syncs on Every Render</h2>
      <p>Document title updates on every render (check your browser tab).</p>
      <input
        value={name}
        onChange={e => setName(e.target.value)}
        placeholder="Your name"
      />
      <p>Count: {count}</p>
      <button onClick={() => setCount(c => c + 1)}>Increment</button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Empty Dependency Array []: Runs ONCE on Mount
// ─────────────────────────────────────────────────────────────────────────────
// [] means "no dependencies" — this effect never needs to re-run.
// React runs it exactly once: after the first render.
// This is the hook equivalent of componentDidMount.
//
// COMMON USE CASES:
//   • Fetch initial data from an API
//   • Start a timer or interval
//   • Register global event listeners

export function CourseLoader() {
  const [courses, setCourses]   = useState([]);
  const [loading, setLoading]   = useState(true);
  const [error, setError]       = useState(null);

  // Runs ONCE when the component mounts
  useEffect(() => {
    // In a real app: fetch('/api/courses').then(...)
    // We simulate async fetch with setTimeout:
    const timer = setTimeout(() => {
      // Simulate success:
      setCourses([
        { id: 1, title: 'React Fundamentals' },
        { id: 2, title: 'React Hooks' },
        { id: 3, title: 'State Management' },
      ]);
      setLoading(false);

      // To simulate error, uncomment:
      // setError('Failed to load courses');
      // setLoading(false);
    }, 1200);

    // CLEANUP: cancel the timer if the component unmounts before it fires
    return () => clearTimeout(timer);
  }, []); // ← empty array = run once on mount

  if (loading) return <p>⏳ Loading courses…</p>;
  if (error)   return <p className="error">❌ {error}</p>;

  return (
    <div>
      <h2>Empty Deps [] — Fetch on Mount</h2>
      <ul>
        {courses.map(c => <li key={c.id}>{c.title}</li>)}
      </ul>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Specific Dependencies: Runs When a Value Changes
// ─────────────────────────────────────────────────────────────────────────────
// List the values your effect depends on inside the array.
// React re-runs the effect whenever any listed value changes between renders.
// This is equivalent to "watch" in Vue or ngOnChanges in Angular.
//
// ⚠️ ESLINT RULE: react-hooks/exhaustive-deps warns if you miss a dependency.
//    Do NOT silence the warning by omitting dependencies — fix the effect instead.

export function CourseSearch() {
  const [query, setQuery]       = useState('');
  const [results, setResults]   = useState([]);
  const [searching, setSearching] = useState(false);

  const allCourses = [
    'React Hooks', 'React Fundamentals', 'Redux Toolkit',
    'TypeScript', 'Node.js', 'Spring Boot', 'Angular Signals'
  ];

  // Re-runs every time `query` changes — debounced by 400ms
  useEffect(() => {
    if (!query.trim()) {
      setResults([]);
      return;
    }

    setSearching(true);

    // Debounce: wait 400ms after the user stops typing before filtering
    const debounceTimer = setTimeout(() => {
      const filtered = allCourses.filter(c =>
        c.toLowerCase().includes(query.toLowerCase())
      );
      setResults(filtered);
      setSearching(false);
    }, 400);

    // CLEANUP: cancel the previous debounce timer when query changes again
    // This prevents stale results from an earlier (slower) search overwriting
    // the results of a faster, more recent search.
    return () => clearTimeout(debounceTimer);
  }, [query]); // ← only re-run when `query` changes

  return (
    <div>
      <h2>Specific Deps — Live Search (debounced)</h2>
      <input
        value={query}
        onChange={e => setQuery(e.target.value)}
        placeholder="Search courses…"
      />
      {searching && <p>🔍 Searching…</p>}
      {!searching && results.length === 0 && query && <p>No results found.</p>}
      <ul>
        {results.map(r => <li key={r}>{r}</li>)}
      </ul>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Effect Cleanup
// ─────────────────────────────────────────────────────────────────────────────
// Return a function from useEffect to perform cleanup.
// React calls it in two situations:
//   1. Before running the effect again (deps changed between renders)
//   2. When the component unmounts
//
// Without cleanup, effects that add listeners or start intervals will keep
// running after the component is gone → memory leaks and bugs.

export function LiveClock() {
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    // Start an interval that ticks every second
    const intervalId = setInterval(() => {
      setTime(new Date());
    }, 1000);

    console.log('⏱ Clock started — interval id:', intervalId);

    // CLEANUP: clear the interval when the component unmounts
    // Without this, the interval keeps running even after the clock is removed!
    return () => {
      clearInterval(intervalId);
      console.log('🛑 Clock stopped — interval cleared');
    };
  }, []); // [] = set up once, clean up on unmount

  return (
    <div>
      <h2>Cleanup — Live Clock with setInterval</h2>
      <p className="clock">{time.toLocaleTimeString()}</p>
      <small>Unmount this component to see the cleanup log in the console.</small>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Multiple useEffect Calls (Separation of Concerns)
// ─────────────────────────────────────────────────────────────────────────────
// You can (and SHOULD) have multiple useEffect calls in one component.
// Each effect handles ONE concern. This is much cleaner than cramming
// unrelated setup/teardown into a single class lifecycle method.

export function CourseDetail({ courseId }) {
  const [course, setCourse]     = useState(null);
  const [viewCount, setViewCount] = useState(0);

  // Effect 1: Fetch course data when courseId changes
  useEffect(() => {
    if (!courseId) return;
    console.log(`📡 Fetching course ${courseId}…`);
    // Simulate API call
    setTimeout(() => {
      setCourse({ id: courseId, title: `Course #${courseId}`, rating: 4.7 });
    }, 500);
  }, [courseId]); // re-fetch when courseId changes

  // Effect 2: Track view count (completely separate concern)
  useEffect(() => {
    if (!courseId) return;
    setViewCount(v => v + 1);
    document.title = `Viewing Course #${courseId}`;
  }, [courseId]); // also runs when courseId changes, but independently

  // Effect 3: Cleanup document title on unmount
  useEffect(() => {
    return () => {
      document.title = 'Course Platform';
    };
  }, []); // only cleanup, runs once

  if (!courseId) return <p>No course selected.</p>;
  if (!course)   return <p>⏳ Loading course {courseId}…</p>;

  return (
    <div>
      <h2>Multiple Effects — Course Detail</h2>
      <h3>{course.title}</h3>
      <p>Rating: {course.rating} ⭐</p>
      <p>Times viewed: {viewCount}</p>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6 — Event Handling in React
// ─────────────────────────────────────────────────────────────────────────────
// React uses SYNTHETIC EVENTS — wrappers around native browser events that
// normalize behavior across all browsers. The API is identical to native DOM
// events, but the object is pooled and reused (in older React) — so async
// access to event properties needs `event.persist()` or destructuring first.
//
// KEY DIFFERENCES FROM VANILLA JS:
//   • Events are camelCase:       onClick, onChange, onKeyDown, onSubmit
//   • Pass FUNCTIONS, not strings: onClick={handleClick}  NOT onClick="handleClick()"
//   • preventDefault() works the same way

export function EventHandlingDemo() {
  const [log, setLog]       = useState([]);
  const [inputVal, setInputVal] = useState('');
  const [mousePos, setMousePos] = useState({ x: 0, y: 0 });

  const addLog = (entry) =>
    setLog(prev => [`${new Date().toLocaleTimeString()} — ${entry}`, ...prev].slice(0, 8));

  // Standard click handler
  const handleClick = (event) => {
    addLog(`Click on: ${event.currentTarget.textContent}`);
  };

  // Input change — always update state with e.target.value
  const handleChange = (e) => {
    setInputVal(e.target.value);
  };

  // Keyboard events — check e.key for the pressed key
  const handleKeyDown = (e) => {
    if (e.key === 'Enter') addLog(`Enter pressed — value: "${e.target.value}"`);
    if (e.key === 'Escape') { setInputVal(''); addLog('Escape — input cleared'); }
  };

  // Mouse position tracking
  const handleMouseMove = (e) => {
    setMousePos({ x: e.clientX, y: e.clientY });
  };

  // Form submit — ALWAYS call preventDefault() to stop full-page reload
  const handleSubmit = (e) => {
    e.preventDefault();  // ← CRITICAL for form submissions
    addLog(`Form submitted with: "${inputVal}"`);
    setInputVal('');
  };

  return (
    <div onMouseMove={handleMouseMove}>
      <h2>Event Handling — Synthetic Events</h2>

      {/* onClick */}
      <button onClick={handleClick}>Click Me</button>

      {/* Passing extra data to a handler without calling immediately */}
      {/* ✅ Arrow function wrapper: onClick={() => handleAction('enroll')} */}
      {/* ❌ Wrong: onClick={handleAction('enroll')} — calls on render, not click */}
      <button onClick={() => addLog('Enrolled in course!')}>Enroll</button>
      <button onClick={() => addLog('Saved to wishlist')}>Save</button>

      {/* onChange + onKeyDown on input */}
      <form onSubmit={handleSubmit}>
        <input
          value={inputVal}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
          placeholder="Type something (Enter to submit, Esc to clear)"
        />
        <button type="submit">Submit</button>
      </form>

      {/* onFocus / onBlur */}
      <input
        placeholder="Focus / blur demo"
        onFocus={() => addLog('Input focused')}
        onBlur={()  => addLog('Input blurred')}
      />

      {/* Mouse position (from the div's onMouseMove above) */}
      <p>Mouse: ({mousePos.x}, {mousePos.y})</p>

      {/* Event log */}
      <div className="event-log">
        <h4>Event Log (last 8):</h4>
        <ul>
          {log.map((entry, i) => <li key={i}>{entry}</li>)}
        </ul>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Root export
// ─────────────────────────────────────────────────────────────────────────────
export default function UseEffectExamples() {
  const [showClock, setShowClock] = useState(true);
  const [selectedCourse, setSelectedCourse] = useState(1);

  return (
    <div>
      <h1>useEffect & Event Handling</h1>

      <DocumentTitleSync />
      <hr />
      <CourseLoader />
      <hr />
      <CourseSearch />
      <hr />
      <div>
        <button onClick={() => setShowClock(s => !s)}>
          {showClock ? 'Unmount Clock (see cleanup)' : 'Mount Clock'}
        </button>
        {showClock && <LiveClock />}
      </div>
      <hr />
      <div>
        <h3>Select a course to load its detail:</h3>
        {[1, 2, 3].map(id => (
          <button key={id} onClick={() => setSelectedCourse(id)}>
            Course {id}
          </button>
        ))}
        <CourseDetail courseId={selectedCourse} />
      </div>
      <hr />
      <EventHandlingDemo />
    </div>
  );
}
