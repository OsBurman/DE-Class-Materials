// ============================================================
// Day 16a — React Fundamentals
// File 3: Component Basics — Functional vs Class Components
// ============================================================
// A component is a self-contained, reusable piece of UI.
// React components are just JavaScript functions (or classes)
// that return JSX.
// ============================================================

import React, { Component } from 'react';

// ─── SECTION 1: WHAT IS A COMPONENT? ─────────────────────────
//
// Think of components like LEGO bricks:
//   - Each brick has a specific shape/purpose
//   - Bricks can be combined to build anything
//   - You can reuse the same brick many times
//
// A React component:
//   - Accepts input data via `props`
//   - Returns JSX describing what to render
//   - Can maintain its own internal state (covered in Day 17a)
//
// Component naming rule: MUST start with a capital letter.
//   <div>   → HTML element (lowercase = built-in DOM element)
//   <MyComponent> → React component (uppercase = custom component)
//

// ─── SECTION 2: FUNCTIONAL COMPONENTS ────────────────────────
//
// A functional component is just a JavaScript function that:
//   1. Has a name starting with a capital letter
//   2. Returns JSX (or null)
//
// This is the MODERN, PREFERRED way to write React components.

// --- 2a. The Simplest Possible Component ---
function Hello() {
  return <h1>Hello, World!</h1>;
}

// --- 2b. Arrow Function Component (also very common) ---
const Goodbye = () => {
  return <h1>Goodbye, World!</h1>;
};

// --- 2c. Arrow Function with Implicit Return ---
// When JSX fits on one line, you can skip `return` and `{}`
const Logo = () => <img src="/logo.png" alt="App logo" />;

// --- 2d. Multi-line implicit return — wrap in () ---
const WelcomeBanner = () => (
  <header className="banner">
    <h1>Welcome to My App</h1>
    <p>Start exploring below.</p>
  </header>
);

// --- 2e. A Realistic Component with Logic ---
function UserGreeting() {
  const currentHour = new Date().getHours();
  let timeOfDay;

  if (currentHour < 12) {
    timeOfDay = 'morning';
  } else if (currentHour < 17) {
    timeOfDay = 'afternoon';
  } else {
    timeOfDay = 'evening';
  }

  return (
    <div className="greeting-card">
      <h2>Good {timeOfDay}! 👋</h2>
      <p>Welcome back to the dashboard.</p>
    </div>
  );
}

// ─── SECTION 3: CLASS COMPONENTS (LEGACY) ────────────────────
//
// Before React 16.8 (2019), you needed class components to use
// state and lifecycle methods. They are still valid today but
// are considered LEGACY — you will see them in older codebases.
//
// Class component rules:
//   - Must extend React.Component
//   - Must have a render() method that returns JSX
//   - Uses `this.props` to access props
//   - Uses `this.state` for state

class ClassCounter extends Component {
  // State is initialized in the constructor
  constructor(props) {
    super(props); // Always call super(props) first
    this.state = {
      count: 0,
    };
    // Event handlers must be bound to `this` in the constructor
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    // setState merges the new value with existing state
    this.setState({ count: this.state.count + 1 });
  }

  render() {
    return (
      <div>
        <p>Count: {this.state.count}</p>
        <button onClick={this.handleClick}>Increment</button>
      </div>
    );
  }
}

// ─── SECTION 4: FUNCTIONAL vs CLASS COMPARISON ───────────────
//
// The SAME counter as a modern functional component:

import { useState } from 'react'; // Hooks enable state in functional components

function FunctionalCounter() {
  const [count, setCount] = useState(0); // Covered fully in Day 17a

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
//  COMPARISON TABLE
// ─────────────────────────────────────────────────────────────
//
//  Feature              Functional Component     Class Component
//  ─────────────────    ─────────────────────    ─────────────────────
//  Syntax               Function / Arrow fn      extends Component
//  State                useState() hook          this.state
//  Side effects         useEffect() hook         componentDidMount etc.
//  Context              useContext() hook         static contextType
//  `this` keyword       Not needed               Needed everywhere
//  Boilerplate          Minimal                  More verbose
//  Performance          Slightly better          Slightly heavier
//  React team stance    ✅ RECOMMENDED            ⚠️ LEGACY (still works)
//  Code readability     High                     Lower (binding issues)
//
// VERDICT: Write ALL new code as functional components with hooks.
//          Know class components so you can READ old code.
//

// ─── SECTION 5: BUILDING A REALISTIC FUNCTIONAL COMPONENT APP ─

// Each piece of a page can be its own component:

// --- Navigation Bar ---
function NavBar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">📚 DevCourse</div>
      <ul className="navbar-links">
        <li><a href="/">Home</a></li>
        <li><a href="/courses">Courses</a></li>
        <li><a href="/about">About</a></li>
      </ul>
    </nav>
  );
}

// --- Course Card ---
function CourseCard() {
  const course = {
    title: 'React Fundamentals',
    instructor: 'Jane Smith',
    duration: '6 hours',
    level: 'Beginner',
    rating: 4.8,
  };

  return (
    <div className="course-card">
      <h3>{course.title}</h3>
      <p>👩‍🏫 {course.instructor}</p>
      <p>⏱ {course.duration}</p>
      <p>📊 {course.level}</p>
      <p>⭐ {course.rating} / 5.0</p>
      <button className="btn-enroll">Enroll Now</button>
    </div>
  );
}

// --- Footer ---
function Footer() {
  const year = new Date().getFullYear();
  return (
    <footer className="footer">
      <p>© {year} DevCourse. All rights reserved.</p>
    </footer>
  );
}

// --- Root App Component: composing all pieces together ---
function App() {
  return (
    <div className="app">
      <NavBar />
      <main>
        <h1>Featured Course</h1>
        <CourseCard />
      </main>
      <Footer />
    </div>
  );
}

// ─── SECTION 6: COMPONENT RULES & BEST PRACTICES ─────────────
//
//  Rule 1: Name starts with a CAPITAL LETTER
//          function myComponent() {} ← React treats as DOM tag
//          function MyComponent()  {} ← React treats as component
//
//  Rule 2: Must return a SINGLE root element (or Fragment)
//
//  Rule 3: Must be a PURE function with respect to props
//          - Same props → same output, every time
//          - Never mutate props or produce side effects in render
//
//  Rule 4: Keep components FOCUSED
//          - One component = one responsibility
//          - If a component gets large, break it into smaller ones
//
//  Rule 5: One component per file (conventional)
//          - File name matches component name: CourseCard.jsx
//

export { Hello, WelcomeBanner, UserGreeting, NavBar, CourseCard, Footer, App };
export default App;
