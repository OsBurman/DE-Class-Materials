// =============================================================================
// 01-useState-hook.jsx — State Management with useState
// =============================================================================
// useState is the most fundamental React hook. It lets a functional component
// "remember" data between renders. Every time state changes, React re-renders
// the component and the UI updates automatically.
//
// SECTIONS:
//  1. Basic useState — primitive values (number, string, boolean)
//  2. Object state — updating objects correctly (spread operator)
//  3. Array state — adding, removing, and updating items
//  4. Functional state updates — when the new state depends on the old state
//  5. Lazy initializer — expensive initial state computation
// =============================================================================

import React, { useState } from 'react';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Basic useState: Primitive Values
// ─────────────────────────────────────────────────────────────────────────────
// useState returns a tuple: [currentValue, setterFunction]
// Convention: name the pair [thing, setThing]
//
// When you call the setter, React schedules a re-render with the new value.
// The variable `count` is immutable within a single render — you CANNOT do
// count++ and expect the UI to update. You MUST call setCount().

export function Counter() {
  // Destructure the tuple right away — this is the idiomatic pattern
  const [count, setCount] = useState(0);       // initial value = 0
  const [message, setMessage] = useState('');  // initial value = empty string

  const increment = () => setCount(count + 1);
  const decrement = () => setCount(count - 1);
  const reset     = () => {
    setCount(0);
    setMessage('Counter has been reset!');
  };

  return (
    <div className="counter">
      <h2>Basic useState — Counter</h2>
      <p>Count: <strong>{count}</strong></p>
      {/* Inline handlers are fine for simple one-liners */}
      <button onClick={increment}>+ Increment</button>
      <button onClick={decrement}>- Decrement</button>
      <button onClick={reset}>Reset</button>
      {/* Conditional rendering based on state */}
      {message && <p className="message">{message}</p>}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Object State
// ─────────────────────────────────────────────────────────────────────────────
// ⚠️ COMMON MISTAKE: Mutating the object directly does NOT trigger a re-render.
//   profile.name = 'Bob'  ← React never sees this change!
//
// CORRECT APPROACH: Always create a NEW object using the spread operator.
//   setProfile({ ...profile, name: 'Bob' })
//       ↑ copy all existing fields, then override the ones that changed

export function UserProfile() {
  const [profile, setProfile] = useState({
    name: 'Alice Johnson',
    email: 'alice@example.com',
    role: 'Student',
    isActive: true
  });

  const [isEditing, setIsEditing] = useState(false);

  // Spread operator copies all existing fields, then overrides `name`
  const updateName = (newName) =>
    setProfile({ ...profile, name: newName });

  const toggleActive = () =>
    setProfile({ ...profile, isActive: !profile.isActive });

  return (
    <div className="user-profile">
      <h2>Object State — User Profile</h2>

      {isEditing ? (
        // Controlled input updates state on every keystroke
        <input
          value={profile.name}
          onChange={(e) => updateName(e.target.value)}
          onBlur={() => setIsEditing(false)}
          autoFocus
        />
      ) : (
        <h3 onDoubleClick={() => setIsEditing(true)}>
          {profile.name} <small>(double-click to edit)</small>
        </h3>
      )}

      <p>Email: {profile.email}</p>
      <p>Role: {profile.role}</p>
      <p>Status: <span className={profile.isActive ? 'active' : 'inactive'}>
        {profile.isActive ? '✅ Active' : '❌ Inactive'}
      </span></p>

      <button onClick={toggleActive}>Toggle Active Status</button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Array State
// ─────────────────────────────────────────────────────────────────────────────
// Same principle as objects: never mutate the array directly.
// Array methods that MUTATE (don't use!): push, pop, splice, sort, reverse
// Array methods that RETURN A NEW ARRAY (use these!): map, filter, concat, spread

export function CourseList() {
  const [courses, setCourses] = useState([
    { id: 1, title: 'React Fundamentals', completed: true  },
    { id: 2, title: 'React Hooks',         completed: false },
    { id: 3, title: 'State Management',    completed: false },
  ]);
  const [newCourseTitle, setNewCourseTitle] = useState('');

  // ADD — spread existing array, append new item
  const addCourse = () => {
    if (!newCourseTitle.trim()) return;
    const newCourse = {
      id: Date.now(),   // simple unique ID
      title: newCourseTitle,
      completed: false
    };
    setCourses([...courses, newCourse]);  // spread + new item
    setNewCourseTitle('');
  };

  // REMOVE — filter out the item with the matching id
  const removeCourse = (id) =>
    setCourses(courses.filter(course => course.id !== id));

  // UPDATE — map over array, replace only the matching item
  const toggleCompleted = (id) =>
    setCourses(courses.map(course =>
      course.id === id
        ? { ...course, completed: !course.completed }  // new object for the changed item
        : course                                         // unchanged items returned as-is
    ));

  return (
    <div className="course-list">
      <h2>Array State — Course List</h2>

      <div className="add-course">
        <input
          value={newCourseTitle}
          onChange={(e) => setNewCourseTitle(e.target.value)}
          placeholder="New course title…"
          onKeyDown={(e) => e.key === 'Enter' && addCourse()}
        />
        <button onClick={addCourse}>Add Course</button>
      </div>

      <ul>
        {courses.map(course => (
          <li key={course.id} className={course.completed ? 'done' : ''}>
            <input
              type="checkbox"
              checked={course.completed}
              onChange={() => toggleCompleted(course.id)}
            />
            <span>{course.title}</span>
            <button onClick={() => removeCourse(course.id)}>✕</button>
          </li>
        ))}
      </ul>

      <p>{courses.filter(c => c.completed).length} / {courses.length} completed</p>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Functional State Updates
// ─────────────────────────────────────────────────────────────────────────────
// When the new state value depends on the PREVIOUS state value, always use
// the functional form of the setter: setState(prevState => newState)
//
// WHY THIS MATTERS: React may batch multiple state updates together.
// If you call setCount(count + 1) three times in a row, count is still the
// same stale value all three times → you only get +1 total.
// With the functional form, each update receives the latest value.

export function BatchCounter() {
  const [count, setCount] = useState(0);

  // ⚠️ BUG — these three calls all read the SAME stale `count`
  const buggyTripleIncrement = () => {
    setCount(count + 1);  // reads count = 0, sets to 1
    setCount(count + 1);  // reads count = 0 AGAIN, sets to 1 again
    setCount(count + 1);  // reads count = 0 AGAIN, sets to 1 again
    // Result: count becomes 1, not 3
  };

  // ✅ CORRECT — functional form receives the latest queued value each time
  const correctTripleIncrement = () => {
    setCount(prev => prev + 1);  // prev=0, returns 1
    setCount(prev => prev + 1);  // prev=1, returns 2
    setCount(prev => prev + 1);  // prev=2, returns 3
    // Result: count becomes 3 ✓
  };

  return (
    <div className="batch-counter">
      <h2>Functional Updates — Batching</h2>
      <p>Count: <strong>{count}</strong></p>
      <button onClick={buggyTripleIncrement}>
        ⚠️ Buggy +3 (try me)
      </button>
      <button onClick={correctTripleIncrement}>
        ✅ Correct +3
      </button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Lazy Initializer
// ─────────────────────────────────────────────────────────────────────────────
// useState(initialValue) runs on EVERY render — even though React only uses
// the initial value the first time. If computing the initial value is expensive,
// pass a FUNCTION instead: useState(() => expensiveComputation())
// React calls this function only once, on the very first render.

function loadSavedTheme() {
  // Simulates reading from localStorage (or a large JSON file, heavy parse, etc.)
  console.log('🔧 loadSavedTheme() called — this should only happen once!');
  return localStorage.getItem('theme') || 'light';
}

export function ThemeToggle() {
  // ❌ Without lazy init — loadSavedTheme() runs on EVERY render
  // const [theme, setTheme] = useState(loadSavedTheme());

  // ✅ With lazy init — loadSavedTheme() runs only on the FIRST render
  const [theme, setTheme] = useState(() => loadSavedTheme());

  const toggle = () => {
    const next = theme === 'light' ? 'dark' : 'light';
    setTheme(next);
    localStorage.setItem('theme', next);
  };

  return (
    <div className={`theme-toggle theme-${theme}`}>
      <h2>Lazy Initializer — Theme</h2>
      <p>Current theme: <strong>{theme}</strong></p>
      <button onClick={toggle}>Toggle Theme</button>
      <small>Open the console — loadSavedTheme() only logs once.</small>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Root App combining all examples
// ─────────────────────────────────────────────────────────────────────────────
export default function UseStateExamples() {
  return (
    <div>
      <h1>useState Hook — All Patterns</h1>
      <Counter />
      <hr />
      <UserProfile />
      <hr />
      <CourseList />
      <hr />
      <BatchCounter />
      <hr />
      <ThemeToggle />
    </div>
  );
}
