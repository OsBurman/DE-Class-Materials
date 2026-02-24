// =============================================================================
// Day 20a — React Advanced & Deployment  |  Part 1
// File: 01-composition-patterns.jsx
//
// Topics covered:
//   1. Component composition patterns
//      a. Children as content (basic composition)
//      b. Specialisation pattern
//      c. Higher-Order Components (HOC)
//   2. Render props pattern
//   3. Compound components pattern
// =============================================================================

import React, { useState, useContext, createContext } from 'react';

// =============================================================================
// SECTION 1 — Component Composition Patterns
// =============================================================================

// -----------------------------------------------------------------------------
// 1a — Children as Content (Basic Composition)
// -----------------------------------------------------------------------------
// The simplest composition: pass children as JSX content into a "container"
// component that owns the layout/style but not the content.
//
// ✅ This avoids prop-drilling for layout concerns.
// ✅ The parent doesn't need to know or care what its children are.

function Card({ title, children }) {
  return (
    <div style={{ border: '1px solid #ddd', borderRadius: 8, padding: 16, margin: 8 }}>
      {title && <h3 style={{ marginBottom: 8 }}>{title}</h3>}
      {children}
    </div>
  );
}

// Usage — Card is reused with completely different content each time
function CourseListPage() {
  return (
    <div>
      <Card title="Angular Mastery">
        <p>Instructor: Jane Dev</p>
        <p>Duration: 20 hours</p>
        <button>Enroll</button>
      </Card>

      <Card title="RxJS Patterns">
        <ul>
          <li>Observables</li>
          <li>Subjects</li>
          <li>Operators</li>
        </ul>
      </Card>

      {/* Card with no title — still works */}
      <Card>
        <p>A card with just a paragraph inside.</p>
      </Card>
    </div>
  );
}

// -----------------------------------------------------------------------------
// 1b — Specialisation Pattern
// -----------------------------------------------------------------------------
// A "general" component that a "specialised" component wraps with preset props.
// The general component contains the logic; the specialised one configures it.

function Button({ label, onClick, variant = 'primary', disabled = false }) {
  const styles = {
    primary:   { backgroundColor: '#0066cc', color: '#fff', border: 'none', padding: '8px 16px', borderRadius: 4, cursor: 'pointer' },
    danger:    { backgroundColor: '#cc0000', color: '#fff', border: 'none', padding: '8px 16px', borderRadius: 4, cursor: 'pointer' },
    secondary: { backgroundColor: '#eee',    color: '#333', border: '1px solid #ccc', padding: '8px 16px', borderRadius: 4, cursor: 'pointer' },
  };

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      style={disabled ? { ...styles[variant], opacity: 0.5, cursor: 'not-allowed' } : styles[variant]}
    >
      {label}
    </button>
  );
}

// Specialised buttons — simple wrappers with pre-set props
function EnrollButton({ courseId, onEnroll }) {
  return <Button label="Enroll Now" onClick={() => onEnroll(courseId)} variant="primary" />;
}

function DeleteCourseButton({ courseId, onDelete }) {
  return <Button label="Delete Course" onClick={() => onDelete(courseId)} variant="danger" />;
}

function CancelButton({ onCancel }) {
  return <Button label="Cancel" onClick={onCancel} variant="secondary" />;
}

// Usage
function CourseAdminRow({ course, onEnroll, onDelete }) {
  return (
    <div style={{ display: 'flex', gap: 8, alignItems: 'center', padding: 8 }}>
      <span>{course.title}</span>
      <EnrollButton courseId={course.id} onEnroll={onEnroll} />
      <DeleteCourseButton courseId={course.id} onDelete={onDelete} />
    </div>
  );
}

// -----------------------------------------------------------------------------
// 1c — Higher-Order Components (HOC)
// -----------------------------------------------------------------------------
// A HOC is a function that takes a component and returns an enhanced component.
// It adds behaviour (cross-cutting concerns) without modifying the original.
//
// ✅ Common uses: auth guards, logging, loading wrappers
// ⚠️  Modern React prefers custom hooks for most HOC use cases, but HOCs are
//     still important to recognise — many libraries (Redux connect, React Router
//     withRouter) use them.

// HOC: wraps any component to show a loading spinner while data loads
function withLoadingSpinner(WrappedComponent) {
  // The returned component has the SAME props as WrappedComponent, plus isLoading
  return function WithSpinner({ isLoading, ...rest }) {
    if (isLoading) {
      return (
        <div style={{ padding: 32, textAlign: 'center' }}>
          <p>⏳ Loading...</p>
        </div>
      );
    }
    // When not loading, render the original component with all its props
    return <WrappedComponent {...rest} />;
  };
}

// HOC: wraps any component to require authentication
function withAuth(WrappedComponent) {
  return function WithAuth(props) {
    // In a real app: read from AuthContext or Redux store
    const isAuthenticated = Boolean(localStorage.getItem('access_token'));

    if (!isAuthenticated) {
      return (
        <div style={{ padding: 16, color: '#cc0000' }}>
          <p>🔒 Please log in to view this content.</p>
          <a href="/login">Go to Login</a>
        </div>
      );
    }

    return <WrappedComponent {...props} />;
  };
}

// Original component — knows nothing about loading or auth
function CourseList({ courses }) {
  return (
    <ul>
      {courses.map((c) => (
        <li key={c.id}>{c.title}</li>
      ))}
    </ul>
  );
}

// Enhanced components created by composing HOCs
const CourseListWithSpinner = withLoadingSpinner(CourseList);
const ProtectedCourseList   = withAuth(CourseList);
const ProtectedWithSpinner  = withAuth(withLoadingSpinner(CourseList));

// Usage
function DashboardPage() {
  const [loading, setLoading] = useState(true);
  const courses = [{ id: 1, title: 'React Advanced' }];

  return (
    <div>
      <CourseListWithSpinner isLoading={loading} courses={courses} />
      <ProtectedCourseList courses={courses} />
      <ProtectedWithSpinner isLoading={loading} courses={courses} />
    </div>
  );
}

// =============================================================================
// SECTION 2 — Render Props Pattern
// =============================================================================
//
// A component with a "render prop" accepts a FUNCTION as a prop.
// It calls that function with internal state/data, letting the consumer
// decide HOW to render it.
//
// ✅ Solves: sharing stateful logic without HOCs or hooks
// ✅ Consumer controls rendering 100%
// ⚠️  Custom hooks are now the preferred approach for most use cases,
//     but render props are still used in libraries like React Router and
//     Formik. Knowing the pattern is essential.

// DataFetcher — encapsulates fetch logic, exposes data via render prop
function DataFetcher({ url, render }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  React.useEffect(() => {
    setLoading(true);
    fetch(url)
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then((json) => { setData(json); setLoading(false); })
      .catch((err) => { setError(err.message); setLoading(false); });
  }, [url]);

  // The consumer decides what to render with this data
  return render({ data, loading, error });
}

// Consumer 1 — renders data as a course list
function CourseListConsumer() {
  return (
    <DataFetcher
      url="/api/courses"
      render={({ data, loading, error }) => {
        if (loading) return <p>Loading courses...</p>;
        if (error)   return <p style={{ color: 'red' }}>Error: {error}</p>;
        return (
          <ul>
            {data?.map((course) => (
              <li key={course.id}>{course.title}</li>
            ))}
          </ul>
        );
      }}
    />
  );
}

// Consumer 2 — renders the SAME DataFetcher but as a count badge
function CourseCountConsumer() {
  return (
    <DataFetcher
      url="/api/courses"
      render={({ data, loading }) => (
        <span>
          {loading ? '...' : `${data?.length ?? 0} courses available`}
        </span>
      )}
    />
  );
}

// Alternative: passing the render function as `children` (children-as-function)
function MouseTracker({ children }) {
  const [position, setPosition] = useState({ x: 0, y: 0 });

  return (
    <div
      onMouseMove={(e) => setPosition({ x: e.clientX, y: e.clientY })}
      style={{ height: 200, border: '1px dashed #ccc' }}
    >
      {/* children is called as a function — this is the "children as render prop" pattern */}
      {children(position)}
    </div>
  );
}

// Usage of children-as-function
function MouseDemo() {
  return (
    <MouseTracker>
      {({ x, y }) => (
        <p>Mouse position: {x}, {y}</p>
      )}
    </MouseTracker>
  );
}

// =============================================================================
// SECTION 3 — Compound Components Pattern
// =============================================================================
//
// Compound components are a set of components designed to work together,
// sharing state through React Context rather than prop drilling.
//
// Examples you already use: HTML <select> + <option>, <table> + <tr> + <td>
//
// ✅ The consumer has full control over layout and composition
// ✅ No prop drilling — each sub-component reads from shared context
// ✅ Clean, expressive API

// 3a — Context for shared state
const TabsContext = createContext(null);

// 3b — Parent component — owns state, provides it via context
function Tabs({ children, defaultTab = 0 }) {
  const [activeTab, setActiveTab] = useState(defaultTab);

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <div>{children}</div>
    </TabsContext.Provider>
  );
}

// 3c — Sub-components — consume context, no props needed for shared state
function TabList({ children }) {
  return (
    <div style={{ display: 'flex', borderBottom: '2px solid #ddd', marginBottom: 16 }}>
      {React.Children.map(children, (child, index) =>
        // Clone each child and inject its index as a prop
        React.cloneElement(child, { index })
      )}
    </div>
  );
}

function Tab({ children, index }) {
  const { activeTab, setActiveTab } = useContext(TabsContext);
  const isActive = activeTab === index;

  return (
    <button
      onClick={() => setActiveTab(index)}
      style={{
        padding: '8px 16px',
        border: 'none',
        background: 'none',
        borderBottom: isActive ? '2px solid #0066cc' : '2px solid transparent',
        color: isActive ? '#0066cc' : '#666',
        cursor: 'pointer',
        fontWeight: isActive ? 'bold' : 'normal',
      }}
    >
      {children}
    </button>
  );
}

function TabPanels({ children }) {
  const { activeTab } = useContext(TabsContext);
  // Only render the panel whose index matches the active tab
  return <div>{React.Children.toArray(children)[activeTab]}</div>;
}

function TabPanel({ children }) {
  return <div style={{ padding: 16 }}>{children}</div>;
}

// Attach sub-components as properties of the parent
Tabs.List   = TabList;
Tabs.Tab    = Tab;
Tabs.Panels = TabPanels;
Tabs.Panel  = TabPanel;

// 3d — Consumer — composable, readable API with no prop drilling
function CourseDetailTabs({ course }) {
  return (
    <Tabs defaultTab={0}>
      <Tabs.List>
        <Tabs.Tab>Overview</Tabs.Tab>
        <Tabs.Tab>Curriculum</Tabs.Tab>
        <Tabs.Tab>Reviews</Tabs.Tab>
      </Tabs.List>

      <Tabs.Panels>
        <Tabs.Panel>
          <h4>About this course</h4>
          <p>{course.description}</p>
        </Tabs.Panel>

        <Tabs.Panel>
          <h4>What you'll learn</h4>
          <ul>{course.topics.map((t, i) => <li key={i}>{t}</li>)}</ul>
        </Tabs.Panel>

        <Tabs.Panel>
          <h4>Student Reviews</h4>
          <p>⭐⭐⭐⭐⭐ "Best React course I've taken!"</p>
        </Tabs.Panel>
      </Tabs.Panels>
    </Tabs>
  );
}

// 3e — Another compound component example: Accordion
const AccordionContext = createContext(null);

function Accordion({ children, allowMultiple = false }) {
  const [openPanels, setOpenPanels] = useState(new Set());

  const toggle = (id) => {
    setOpenPanels((prev) => {
      const next = new Set(allowMultiple ? prev : []);
      if (prev.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  return (
    <AccordionContext.Provider value={{ openPanels, toggle }}>
      <div>{children}</div>
    </AccordionContext.Provider>
  );
}

function AccordionItem({ id, title, children }) {
  const { openPanels, toggle } = useContext(AccordionContext);
  const isOpen = openPanels.has(id);

  return (
    <div style={{ border: '1px solid #ddd', marginBottom: 4 }}>
      <button
        onClick={() => toggle(id)}
        style={{ width: '100%', textAlign: 'left', padding: '12px 16px', background: '#f5f5f5', border: 'none', cursor: 'pointer' }}
      >
        {isOpen ? '▼' : '▶'} {title}
      </button>
      {isOpen && (
        <div style={{ padding: 16 }}>{children}</div>
      )}
    </div>
  );
}

Accordion.Item = AccordionItem;

// Usage
function FAQSection() {
  return (
    <Accordion allowMultiple={false}>
      <Accordion.Item id="q1" title="What is React?">
        <p>A JavaScript library for building user interfaces.</p>
      </Accordion.Item>
      <Accordion.Item id="q2" title="What is JSX?">
        <p>A syntax extension that lets you write HTML-like code in JavaScript.</p>
      </Accordion.Item>
      <Accordion.Item id="q3" title="What are hooks?">
        <p>Functions that let you use React state and lifecycle features in function components.</p>
      </Accordion.Item>
    </Accordion>
  );
}

export {
  Card, CourseListPage,
  Button, EnrollButton, DeleteCourseButton, CancelButton, CourseAdminRow,
  withLoadingSpinner, withAuth, CourseList,
  CourseListWithSpinner, ProtectedCourseList, ProtectedWithSpinner, DashboardPage,
  DataFetcher, CourseListConsumer, CourseCountConsumer,
  MouseTracker, MouseDemo,
  Tabs, CourseDetailTabs,
  Accordion, FAQSection,
};
