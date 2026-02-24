// ============================================================
// Day 16a — React Fundamentals
// File: Lists and Keys
// ============================================================
// In React, you render lists using JavaScript's .map() method.
// Each list item must have a unique `key` prop to help React
// track which items changed, were added, or were removed.
// ============================================================

import React from 'react';

// ─── SECTION 1: RENDERING SIMPLE LISTS WITH .map() ───────────
//
// JSX can contain arrays of elements — React will render them all.
// .map() transforms an array of data into an array of JSX elements.

const fruits = ['Apple', 'Banana', 'Mango', 'Orange', 'Pineapple'];

function FruitList() {
  return (
    <ul>
      {fruits.map((fruit) => (
        <li key={fruit}>{fruit}</li>
        // ↑ key is required — more on why below
      ))}
    </ul>
  );
}

// ─── SECTION 2: WHY KEYS MATTER — RECONCILIATION ────────────
//
// Keys help React's reconciliation algorithm identify which items
// have changed, been added, or been removed.
//
// Without keys, React re-renders the ENTIRE list on every change.
// With keys, React surgically updates only the changed items.
//
// SCENARIO: A list has 1000 items. You add one new item to the FRONT.
//
// Without keys:
//   React sees: old item at index 0 ≠ new item at index 0
//   React thinks: ALL items changed → re-renders all 1000 + 1 = ❌ slow
//
// With keys:
//   React sees: new key at front, all other keys match the previous list
//   React thinks: one new item added → renders only that 1 item = ✅ fast
//

// ─── SECTION 3: RULES FOR KEYS ───────────────────────────────
//
//  Rule 1: Keys must be UNIQUE among siblings (not globally)
//          Two different lists can have items with the same key.
//
//  Rule 2: Keys must be STABLE — don't use the array index as key
//          when the list can be reordered, filtered, or have items
//          added/removed from the middle.
//
//  Rule 3: Keys must be STRINGS or NUMBERS
//
//  Rule 4: Keys are NOT accessible as props inside the component
//          (don't try to do: props.key — it won't work)
//
//  Best practice: Use a unique ID from your data (database ID, UUID)
//

// ─── SECTION 4: BAD KEY — array index (and when it's ok) ─────

const staticNavLinks = ['Home', 'About', 'Contact'];

// ✅ Index as key is OK ONLY when the list:
//    - is static (never reordered, never filtered, never changes)
//    - has no stable IDs
//    - items have no state
function StaticNavMenu() {
  return (
    <nav>
      {staticNavLinks.map((link, index) => (
        <a key={index} href={`/${link.toLowerCase()}`}>
          {link}
        </a>
      ))}
    </nav>
  );
}

// ─── SECTION 5: GOOD KEY — using a unique ID from data ───────

const students = [
  { id: 'stu-001', name: 'Alice Chen',  grade: 'A', gpa: 3.9 },
  { id: 'stu-002', name: 'Bob Martinez', grade: 'B+', gpa: 3.4 },
  { id: 'stu-003', name: 'Carol White', grade: 'A-', gpa: 3.7 },
  { id: 'stu-004', name: 'Dave Kim',    grade: 'B',  gpa: 3.2 },
];

// ✅ Database ID as key — stable, unique, survives reordering
function StudentTable() {
  return (
    <table>
      <thead>
        <tr>
          <th>Name</th>
          <th>Grade</th>
          <th>GPA</th>
        </tr>
      </thead>
      <tbody>
        {students.map((student) => (
          <tr key={student.id}>
            <td>{student.name}</td>
            <td>{student.grade}</td>
            <td>{student.gpa}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

// ─── SECTION 6: LISTS OF COMPONENTS ─────────────────────────
//
// The key goes on the outermost element returned from .map()
// (i.e., on the component tag itself, not inside the component)

function ProductCard({ product }) {
  return (
    <div className="product-card">
      <h3>{product.name}</h3>
      <p>${product.price.toFixed(2)}</p>
      <p>{product.category}</p>
      <button>Add to Cart</button>
    </div>
  );
}

const products = [
  { id: 'p-1', name: 'JavaScript: The Good Parts', price: 29.99, category: 'Books' },
  { id: 'p-2', name: 'Mechanical Keyboard',        price: 149.00, category: 'Hardware' },
  { id: 'p-3', name: 'VS Code Pro Theme',           price: 4.99,  category: 'Software' },
  { id: 'p-4', name: 'React Sticker Pack',          price: 9.99,  category: 'Merch' },
];

function ProductGrid() {
  return (
    <div className="product-grid">
      {products.map((product) => (
        // ✅ key on the component element, NOT inside ProductCard
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  );
}

// ─── SECTION 7: FILTERING BEFORE RENDERING ───────────────────
//
// .filter() + .map() is a very common pattern for conditional list rendering.

const tasks = [
  { id: 1, title: 'Build React app',       completed: true  },
  { id: 2, title: 'Write unit tests',      completed: false },
  { id: 3, title: 'Deploy to Vercel',      completed: false },
  { id: 4, title: 'Review pull requests',  completed: true  },
  { id: 5, title: 'Update documentation',  completed: false },
];

function TaskList({ showCompleted }) {
  const filteredTasks = showCompleted
    ? tasks
    : tasks.filter((task) => !task.completed);

  return (
    <div>
      <h3>{showCompleted ? 'All Tasks' : 'Pending Tasks'} ({filteredTasks.length})</h3>
      <ul>
        {filteredTasks.map((task) => (
          <li
            key={task.id}
            style={{ textDecoration: task.completed ? 'line-through' : 'none' }}
          >
            {task.completed ? '✅' : '⬜'} {task.title}
          </li>
        ))}
      </ul>
    </div>
  );
}

// ─── SECTION 8: SORTING BEFORE RENDERING ─────────────────────

const leaderboard = [
  { id: 'u1', username: 'react_ninja',   score: 2840 },
  { id: 'u2', username: 'js_wizard',     score: 3100 },
  { id: 'u3', username: 'codemaster99', score: 2650 },
  { id: 'u4', username: 'devqueen',      score: 3500 },
];

function Leaderboard() {
  // Sort by score descending — create a copy first with [...spread]
  // NEVER mutate the original array — .sort() mutates in place!
  const ranked = [...leaderboard].sort((a, b) => b.score - a.score);

  return (
    <ol className="leaderboard">
      {ranked.map((player, index) => (
        <li key={player.id}>
          <span className="rank">#{index + 1}</span>
          <span className="username">{player.username}</span>
          <span className="score">{player.score.toLocaleString()} pts</span>
        </li>
      ))}
    </ol>
  );
}

// ─── SECTION 9: NESTED LISTS ─────────────────────────────────

const curriculum = [
  {
    week: 1,
    title: 'Java Fundamentals',
    days: ['Day 1: Variables & Types', 'Day 2: Control Flow', 'Day 3: OOP Basics'],
  },
  {
    week: 2,
    title: 'Collections & Streams',
    days: ['Day 6: Lists & Maps', 'Day 7: Exception Handling', 'Day 8: Streams'],
  },
  {
    week: 3,
    title: 'Web Fundamentals',
    days: ['Day 11: HTML & CSS', 'Day 12: JavaScript', 'Day 13: DOM Events'],
  },
];

function CurriculumOutline() {
  return (
    <div className="curriculum">
      {curriculum.map((module) => (
        // Outer list key: module's week number
        <div key={module.week} className="module">
          <h3>Week {module.week}: {module.title}</h3>
          <ul>
            {module.days.map((day) => (
              // Inner list key: the day string itself (unique within siblings)
              <li key={day}>{day}</li>
            ))}
          </ul>
        </div>
      ))}
    </div>
  );
}

export { FruitList, StudentTable, ProductGrid, TaskList, Leaderboard, CurriculumOutline };
