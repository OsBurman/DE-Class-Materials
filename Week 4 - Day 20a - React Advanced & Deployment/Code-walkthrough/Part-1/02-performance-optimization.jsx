// =============================================================================
// Day 20a — React Advanced & Deployment  |  Part 1
// File: 02-performance-optimization.jsx
//
// Topics covered:
//   1. React.memo — prevent unnecessary re-renders of child components
//   2. useMemo    — memoize expensive computed values
//   3. useCallback — memoize callback functions (stable references)
//   4. When to use each (and when NOT to)
//   5. Identifying performance problems before optimising
// =============================================================================

import React, {
  useState,
  useMemo,
  useCallback,
  useEffect,
  useRef,
} from 'react';

// =============================================================================
// SECTION 0 — Why Performance Optimisation Matters (And When NOT to Do It)
// =============================================================================
//
// React is fast by default. DO NOT optimise blindly.
//
// Golden rule: "Premature optimisation is the root of all evil" (Knuth)
//
// When SHOULD you optimise?
//   ✅ You've measured a slowdown with React DevTools Profiler
//   ✅ A component renders frequently and its render is expensive
//   ✅ A callback is passed to a child that itself uses React.memo
//
// When NOT to:
//   ❌ "It might be slow someday" — don't guess, measure first
//   ❌ Wrapping every component in React.memo by default
//   ❌ Wrapping every function in useCallback by default
//   ❌ Over-memoising simple string/number computations
//
// Measure first. Optimise only when you have evidence of a bottleneck.

// =============================================================================
// SECTION 1 — React.memo
// =============================================================================
//
// React.memo is a Higher-Order Component that memoises a COMPONENT.
// If a parent re-renders but the memoised child's props haven't changed,
// React SKIPS re-rendering the child entirely.
//
// Default comparison: shallow equality (Object.is for each prop)
// Custom comparison: pass a second argument comparator function

// Simulate a slow component to make the problem visible
function simulateExpensiveRender(label) {
  // Artificially slow — in real life this would be a heavy calculation
  // or a very deep component tree
  const start = performance.now();
  while (performance.now() - start < 50) {} // burn 50ms
  console.log(`🐢 ${label} rendered (expensive!)`);
}

// WITHOUT React.memo — re-renders every time the parent re-renders
function CourseCardUnmemoised({ course, onEnroll }) {
  simulateExpensiveRender('CourseCardUnmemoised');
  return (
    <div style={{ border: '1px solid #ddd', padding: 16, margin: 8 }}>
      <h3>{course.title}</h3>
      <p>by {course.instructor}</p>
      <button onClick={() => onEnroll(course.id)}>Enroll</button>
    </div>
  );
}

// WITH React.memo — only re-renders if course or onEnroll props change
const CourseCard = React.memo(function CourseCard({ course, onEnroll }) {
  console.log(`✅ CourseCard rendered: ${course.title}`);
  return (
    <div style={{ border: '1px solid #ddd', padding: 16, margin: 8 }}>
      <h3>{course.title}</h3>
      <p>by {course.instructor}</p>
      <button onClick={() => onEnroll(course.id)}>Enroll</button>
    </div>
  );
});

// Custom comparator — only re-render if the course ID changes
// (ignores changes to other fields like enrollmentCount)
const CourseCardCustomCompare = React.memo(
  function CourseCardCustomCompare({ course, onEnroll }) {
    console.log(`🔍 CourseCardCustomCompare rendered: ${course.title}`);
    return (
      <div style={{ padding: 16 }}>
        <h3>{course.title}</h3>
        <p>Enrolled: {course.enrollmentCount}</p>
        <button onClick={() => onEnroll(course.id)}>Enroll</button>
      </div>
    );
  },
  // comparator(prevProps, nextProps) → true means "skip re-render"
  (prevProps, nextProps) => prevProps.course.id === nextProps.course.id
);

// Parent component — the source of unnecessary re-renders
function CourseListParent() {
  const [count, setCount] = useState(0);
  const courses = [
    { id: 1, title: 'React Mastery',  instructor: 'Jane Dev', enrollmentCount: 142 },
    { id: 2, title: 'TypeScript 101', instructor: 'Bob Type', enrollmentCount: 89 },
  ];

  // ⚠️  Problem: every render creates a NEW function reference for handleEnroll.
  // Even though CourseCard is memo-wrapped, passing a new function breaks memoisation!
  // (We'll fix this in Section 3 — useCallback)
  const handleEnrollBroken = (id) => {
    console.log('Enrolling in course', id);
  };

  return (
    <div>
      <p>Counter: {count}</p>
      <button onClick={() => setCount((c) => c + 1)}>
        Re-render parent (without changing courses)
      </button>

      {/* Click the button — unmemoised card re-renders every time */}
      {courses.map((course) => (
        <CourseCardUnmemoised
          key={course.id}
          course={course}
          onEnroll={handleEnrollBroken}
        />
      ))}

      {/* Memo-wrapped — BUT still re-renders because handleEnrollBroken is recreated each render */}
      {courses.map((course) => (
        <CourseCard
          key={course.id}
          course={course}
          onEnroll={handleEnrollBroken}
        />
      ))}
    </div>
  );
}

// =============================================================================
// SECTION 2 — useMemo
// =============================================================================
//
// useMemo memoises a COMPUTED VALUE.
// It only recomputes when one of the listed dependencies changes.
//
// ✅ Use when:
//   - A calculation is genuinely expensive (e.g. sorting/filtering large arrays)
//   - You need referential stability for an object/array passed as a prop to
//     a memo-wrapped child
//
// ❌ Don't use for:
//   - Simple computations (a + b, string concatenation, boolean checks)
//   - Values that change on every render anyway

function CourseFilter({ courses }) {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedLevel, setSelectedLevel] = useState('all');
  const [unrelatedCount, setUnrelatedCount] = useState(0);

  // ❌ Without useMemo — this filter runs on EVERY render,
  //    even when unrelatedCount changes and courses/filters haven't.
  const filteredWithoutMemo = courses
    .filter((c) =>
      selectedLevel === 'all' ? true : c.level === selectedLevel
    )
    .filter((c) =>
      c.title.toLowerCase().includes(searchTerm.toLowerCase())
    )
    .sort((a, b) => a.title.localeCompare(b.title));

  // ✅ With useMemo — only recalculates when courses, searchTerm, or selectedLevel change.
  //    Changing unrelatedCount does NOT trigger a recalculation.
  const filteredCourses = useMemo(() => {
    console.log('🔄 Computing filtered courses...');
    return courses
      .filter((c) =>
        selectedLevel === 'all' ? true : c.level === selectedLevel
      )
      .filter((c) =>
        c.title.toLowerCase().includes(searchTerm.toLowerCase())
      )
      .sort((a, b) => a.title.localeCompare(b.title));
  }, [courses, searchTerm, selectedLevel]); // ← dependencies

  // useMemo for a derived statistic (expensive aggregation)
  const courseStats = useMemo(() => {
    console.log('📊 Computing stats...');
    const total = courses.length;
    const byLevel = courses.reduce((acc, c) => {
      acc[c.level] = (acc[c.level] ?? 0) + 1;
      return acc;
    }, {});
    const avgDuration =
      courses.reduce((sum, c) => sum + c.duration, 0) / (total || 1);
    return { total, byLevel, avgDuration: Math.round(avgDuration) };
  }, [courses]);

  return (
    <div>
      <input
        placeholder="Search courses..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />
      <select value={selectedLevel} onChange={(e) => setSelectedLevel(e.target.value)}>
        <option value="all">All Levels</option>
        <option value="beginner">Beginner</option>
        <option value="intermediate">Intermediate</option>
        <option value="advanced">Advanced</option>
      </select>

      {/* Changing this counter re-renders the component but does NOT
          recompute filteredCourses or courseStats */}
      <button onClick={() => setUnrelatedCount((c) => c + 1)}>
        Unrelated update (count: {unrelatedCount})
      </button>

      <p>Stats: {courseStats.total} total, avg {courseStats.avgDuration}h</p>

      <ul>
        {filteredCourses.map((c) => (
          <li key={c.id}>{c.title} — {c.level}</li>
        ))}
      </ul>
    </div>
  );
}

// =============================================================================
// SECTION 3 — useCallback
// =============================================================================
//
// useCallback memoises a FUNCTION (returns a stable reference).
// It only creates a new function when dependencies change.
//
// ✅ Use when:
//   - Passing a callback to a React.memo child (breaks memoisation without it)
//   - Passing a callback to a useEffect dependency array
//   - The function is expensive to create AND passed frequently
//
// ❌ Don't use for:
//   - Callbacks that aren't passed to children or dependency arrays
//   - Callbacks with no dependencies (define outside the component instead)

function CourseListOptimised({ courses }) {
  const [selectedCourseId, setSelectedCourseId] = useState(null);
  const [notification, setNotification] = useState('');

  // ❌ WITHOUT useCallback — new function every render → breaks React.memo on CourseCard
  // const handleEnroll = (id) => { setSelectedCourseId(id); };

  // ✅ WITH useCallback — same function reference across renders (no dependencies)
  const handleEnroll = useCallback((id) => {
    setSelectedCourseId(id);
    setNotification(`Enrolled in course #${id}`);
  }, []); // Empty array = never recreated

  // useCallback with dependencies — recreated only when selectedCourseId changes
  const handleUnenroll = useCallback(
    (id) => {
      if (selectedCourseId === id) {
        setSelectedCourseId(null);
        setNotification(`Unenrolled from course #${id}`);
      }
    },
    [selectedCourseId] // Only recreated when selectedCourseId changes
  );

  // useCallback for a fetch function used in useEffect
  const fetchCourseDetails = useCallback(
    async (id) => {
      try {
        const res = await fetch(`/api/courses/${id}`);
        const data = await res.json();
        console.log('Course details:', data);
      } catch (err) {
        console.error('Failed to fetch:', err);
      }
    },
    [] // No dependencies — the fetch URL pattern never changes
  );

  // Safe to include fetchCourseDetails in the dependency array because
  // useCallback gives it a stable reference
  useEffect(() => {
    if (selectedCourseId) {
      fetchCourseDetails(selectedCourseId);
    }
  }, [selectedCourseId, fetchCourseDetails]);

  return (
    <div>
      {notification && <p style={{ color: 'green' }}>{notification}</p>}
      {courses.map((course) => (
        // CourseCard is React.memo — will only re-render if course or handleEnroll changes
        <CourseCard
          key={course.id}
          course={course}
          onEnroll={handleEnroll}
        />
      ))}
    </div>
  );
}

// =============================================================================
// SECTION 4 — Bringing It All Together: The Optimised Dashboard
// =============================================================================
//
// This component demonstrates all three optimisations working together:
//   - React.memo on CourseCard (skip child re-renders)
//   - useMemo for expensive filtering/sorting
//   - useCallback for stable callback references

function OptimisedCourseDashboard() {
  const [courses] = useState([
    { id: 1, title: 'React Mastery',     instructor: 'Jane Dev',  level: 'advanced',     duration: 20 },
    { id: 2, title: 'TypeScript 101',    instructor: 'Bob Type',  level: 'beginner',     duration: 10 },
    { id: 3, title: 'Node.js Basics',    instructor: 'Alice API', level: 'beginner',     duration: 12 },
    { id: 4, title: 'Spring Boot Deep',  instructor: 'Carlos J',  level: 'intermediate', duration: 25 },
    { id: 5, title: 'Docker for Devs',   instructor: 'Sara Ops',  level: 'intermediate', duration: 8  },
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedLevel, setSelectedLevel] = useState('all');
  const [enrolledIds, setEnrolledIds] = useState(new Set());
  const [theme, setTheme] = useState('light'); // Unrelated state — triggers re-render

  // ✅ useMemo — expensive filter + sort
  const filteredCourses = useMemo(() => {
    return courses
      .filter((c) => (selectedLevel === 'all' ? true : c.level === selectedLevel))
      .filter((c) => c.title.toLowerCase().includes(searchTerm.toLowerCase()))
      .sort((a, b) => a.title.localeCompare(b.title));
  }, [courses, searchTerm, selectedLevel]);

  // ✅ useCallback — stable reference, won't break React.memo on CourseCard
  const handleEnroll = useCallback((id) => {
    setEnrolledIds((prev) => new Set([...prev, id]));
  }, []);

  // ✅ useCallback with dependency
  const handleUnenroll = useCallback(
    (id) => setEnrolledIds((prev) => { const next = new Set(prev); next.delete(id); return next; }),
    []
  );

  return (
    <div style={{ backgroundColor: theme === 'dark' ? '#222' : '#fff', color: theme === 'dark' ? '#fff' : '#000', padding: 16 }}>
      <button onClick={() => setTheme((t) => t === 'light' ? 'dark' : 'light')}>
        Toggle Theme (re-renders parent — watch the cards)
      </button>

      <input
        placeholder="Search..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        style={{ margin: '0 8px' }}
      />

      <select value={selectedLevel} onChange={(e) => setSelectedLevel(e.target.value)}>
        <option value="all">All Levels</option>
        <option value="beginner">Beginner</option>
        <option value="intermediate">Intermediate</option>
        <option value="advanced">Advanced</option>
      </select>

      {filteredCourses.map((course) => (
        <CourseCard
          key={course.id}
          course={course}
          onEnroll={handleEnroll}
        />
      ))}
    </div>
  );
}

// =============================================================================
// SECTION 5 — Quick Reference: When to Use What
// =============================================================================
//
//  Tool           | Memoises    | Use when
//  ───────────────┼─────────────┼──────────────────────────────────────────────
//  React.memo     | Component   | Child renders frequently; parent re-renders
//                 |             | often due to unrelated state changes
//  useMemo        | Value       | Expensive computation (filter/sort/reduce of
//                 |             | large arrays); referential stability for props
//  useCallback    | Function    | Callback passed to a React.memo child;
//                 |             | Function in a useEffect dependency array
//
//  Common mistake: using all three everywhere "just in case" — this adds
//  memory overhead and makes code harder to read without any benefit.
//
//  Profile FIRST → optimise ONLY where measurements show a problem.

export {
  CourseCardUnmemoised, CourseCard, CourseCardCustomCompare,
  CourseListParent, CourseFilter, CourseListOptimised,
  OptimisedCourseDashboard,
};
