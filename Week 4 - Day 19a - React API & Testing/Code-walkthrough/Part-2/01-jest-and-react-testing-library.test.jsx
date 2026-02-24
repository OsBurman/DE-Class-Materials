// =============================================================
// DAY 19a — Part 2, File 1: Jest & React Testing Library
// =============================================================
// Topics: RTL basics (render, screen, queries), Jest matchers,
//         userEvent, writing component tests, testing hooks
//         with renderHook, testing state changes
// =============================================================
//
// Setup requirements (CRA / Vite with @testing-library):
//   npm install --save-dev @testing-library/react @testing-library/user-event
//   npm install --save-dev @testing-library/jest-dom
//
// CRA (Create React App) includes Jest + RTL out of the box.
// Vite: add vitest or configure Jest separately.
// =============================================================

import React, { useState, useReducer } from 'react';
import {
  render,
  screen,
  waitFor,
  fireEvent,
  within,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderHook, act } from '@testing-library/react';
import '@testing-library/jest-dom'; // extends Jest matchers (toBeInTheDocument, etc.)

// =============================================================
// SECTION 1 — Components Under Test
// =============================================================
// In a real project these would be imported from their own files.
// Defined here so the test file is self-contained for teaching.
// =============================================================

function Greeting({ name }) {
  return <h1>Hello, {name}!</h1>;
}

function CourseCard({ course, onEnroll }) {
  return (
    <article data-testid={`course-card-${course.id}`}>
      <h2>{course.title}</h2>
      <p>{course.description}</p>
      <span className="badge">{course.level}</span>
      <button onClick={() => onEnroll(course.id)}>Enroll</button>
    </article>
  );
}

function EnrollButton({ courseId }) {
  const [enrolled, setEnrolled] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleClick = async () => {
    setLoading(true);
    await new Promise(resolve => setTimeout(resolve, 100)); // simulate async
    setEnrolled(true);
    setLoading(false);
  };

  return (
    <button onClick={handleClick} disabled={loading}>
      {loading ? 'Enrolling...' : enrolled ? 'Enrolled ✓' : 'Enroll Now'}
    </button>
  );
}

function CourseList({ courses }) {
  if (!courses || courses.length === 0) {
    return <p>No courses available.</p>;
  }
  return (
    <ul>
      {courses.map(c => (
        <li key={c.id}>{c.title}</li>
      ))}
    </ul>
  );
}

function SearchBox({ onSearch }) {
  const [query, setQuery] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch(query);
  };

  return (
    <form onSubmit={handleSubmit}>
      <label htmlFor="search">Search courses</label>
      <input
        id="search"
        value={query}
        onChange={e => setQuery(e.target.value)}
        placeholder="Enter a course name"
      />
      <button type="submit">Search</button>
    </form>
  );
}

function Counter() {
  const [count, setCount] = useState(0);
  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(c => c + 1)}>Increment</button>
      <button onClick={() => setCount(c => c - 1)}>Decrement</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  );
}

// =============================================================
// SECTION 2 — Jest Basics: describe, test/it, expect
// =============================================================

// describe — groups related tests under a named block
// test (alias: it) — a single test case
// expect(value).matcher() — assertion

describe('Greeting component', () => {

  // =============================================================
  // SECTION 3 — render() and screen queries
  // =============================================================
  // render(jsx) — mounts the component into a virtual DOM
  // screen     — a global object with query methods to find elements
  //
  // Query types:
  //   getBy*    — finds ONE element; throws if 0 or >1 found
  //   queryBy*  — finds ONE element; returns null if not found (no throw)
  //   findBy*   — async; waits for element to appear; rejects if not found
  //   getAllBy*  — finds ALL matching elements; throws if 0 found
  //   queryAllBy* — finds all; returns empty array if none found
  //   findAllBy*  — async; finds all; rejects if none found
  //
  // Query selectors (priority order — prefer accessible queries):
  //   ByRole         — most preferred; matches ARIA role + accessible name
  //   ByLabelText    — form inputs with an associated <label>
  //   ByPlaceholderText
  //   ByText         — text content visible to users
  //   ByDisplayValue — current value of an input/select
  //   ByAltText      — image alt text
  //   ByTitle        — title attribute
  //   ByTestId       — data-testid attribute (last resort)
  // =============================================================

  test('renders with the provided name', () => {
    // Arrange — render the component
    render(<Greeting name="Alice" />);

    // Act — query the DOM for an element
    // getByRole('heading') + name option checks the text content
    const heading = screen.getByRole('heading', { name: /hello, alice!/i });

    // Assert — verify the element is in the document
    expect(heading).toBeInTheDocument();
    // toBeInTheDocument() is from @testing-library/jest-dom
  });

  test('renders different name when prop changes', () => {
    render(<Greeting name="Bob" />);
    expect(screen.getByText(/hello, bob!/i)).toBeInTheDocument();
  });
});

// =============================================================
// SECTION 4 — Testing with Props and Events
// =============================================================

describe('CourseCard component', () => {
  const mockCourse = {
    id: 1,
    title: 'React Fundamentals',
    description: 'Learn the basics of React',
    level: 'Beginner',
  };

  test('displays course title, description, and level', () => {
    render(<CourseCard course={mockCourse} onEnroll={jest.fn()} />);

    expect(screen.getByRole('heading', { name: /react fundamentals/i })).toBeInTheDocument();
    expect(screen.getByText(/learn the basics of react/i)).toBeInTheDocument();
    expect(screen.getByText('Beginner')).toBeInTheDocument();
  });

  test('calls onEnroll with the course id when Enroll is clicked', async () => {
    // jest.fn() creates a mock function that records calls
    const mockOnEnroll = jest.fn();
    const user = userEvent.setup(); // preferred over fireEvent for realistic events

    render(<CourseCard course={mockCourse} onEnroll={mockOnEnroll} />);

    // userEvent.click simulates a real click (dispatches pointer + mouse + click events)
    await user.click(screen.getByRole('button', { name: /enroll/i }));

    // Verify the mock was called exactly once with the course id
    expect(mockOnEnroll).toHaveBeenCalledTimes(1);
    expect(mockOnEnroll).toHaveBeenCalledWith(1);
  });

  test('renders with data-testid for targeted selection', () => {
    render(<CourseCard course={mockCourse} onEnroll={jest.fn()} />);
    // data-testid as a fallback when no accessible query works
    expect(screen.getByTestId('course-card-1')).toBeInTheDocument();
  });
});

// =============================================================
// SECTION 5 — Testing User Interactions and State Changes
// =============================================================

describe('Counter component', () => {
  test('starts at 0', () => {
    render(<Counter />);
    expect(screen.getByText('Count: 0')).toBeInTheDocument();
  });

  test('increments count when Increment is clicked', async () => {
    const user = userEvent.setup();
    render(<Counter />);

    await user.click(screen.getByRole('button', { name: /increment/i }));
    expect(screen.getByText('Count: 1')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: /increment/i }));
    expect(screen.getByText('Count: 2')).toBeInTheDocument();
  });

  test('decrements count when Decrement is clicked', async () => {
    const user = userEvent.setup();
    render(<Counter />);

    await user.click(screen.getByRole('button', { name: /decrement/i }));
    expect(screen.getByText('Count: -1')).toBeInTheDocument();
  });

  test('resets count to 0 when Reset is clicked', async () => {
    const user = userEvent.setup();
    render(<Counter />);

    await user.click(screen.getByRole('button', { name: /increment/i }));
    await user.click(screen.getByRole('button', { name: /increment/i }));
    await user.click(screen.getByRole('button', { name: /reset/i }));

    expect(screen.getByText('Count: 0')).toBeInTheDocument();
  });
});

// =============================================================
// SECTION 6 — Testing Async State: waitFor
// =============================================================
// When state updates happen asynchronously (setTimeout, Promise),
// use waitFor() to wait for the DOM to reflect the update.
// =============================================================

describe('EnrollButton component', () => {
  test('shows "Enrolling..." while loading, then "Enrolled ✓" after', async () => {
    const user = userEvent.setup();
    render(<EnrollButton courseId={42} />);

    // Initial state
    expect(screen.getByRole('button', { name: /enroll now/i })).toBeInTheDocument();

    // Trigger the async action
    await user.click(screen.getByRole('button'));

    // waitFor polls until the assertion passes (up to 1000ms by default)
    await waitFor(() => {
      expect(screen.getByRole('button', { name: /enrolled ✓/i })).toBeInTheDocument();
    });
  });

  test('button is disabled during enrollment', async () => {
    const user = userEvent.setup();
    render(<EnrollButton courseId={42} />);

    // Click and immediately check disabled — button is synchronously disabled before the await
    const button = screen.getByRole('button');
    user.click(button); // don't await — check mid-flight

    await waitFor(() => {
      expect(screen.getByRole('button')).toBeDisabled();
    });
  });
});

// =============================================================
// SECTION 7 — Testing Conditional Rendering
// =============================================================

describe('CourseList component', () => {
  test('shows "No courses available" when list is empty', () => {
    render(<CourseList courses={[]} />);
    expect(screen.getByText(/no courses available/i)).toBeInTheDocument();
  });

  test('renders a list item for each course', () => {
    const courses = [
      { id: 1, title: 'React Fundamentals' },
      { id: 2, title: 'TypeScript Basics' },
      { id: 3, title: 'Node.js for Beginners' },
    ];
    render(<CourseList courses={courses} />);

    const items = screen.getAllByRole('listitem');
    expect(items).toHaveLength(3);
    expect(screen.getByText('React Fundamentals')).toBeInTheDocument();
    expect(screen.getByText('TypeScript Basics')).toBeInTheDocument();
  });

  test('renders nothing when courses is undefined', () => {
    render(<CourseList />); // no prop passed
    expect(screen.queryByRole('list')).not.toBeInTheDocument();
    expect(screen.getByText(/no courses available/i)).toBeInTheDocument();
  });
});

// =============================================================
// SECTION 8 — Testing Form Inputs with userEvent
// =============================================================

describe('SearchBox component', () => {
  test('calls onSearch with the typed query when form is submitted', async () => {
    const mockOnSearch = jest.fn();
    const user = userEvent.setup();

    render(<SearchBox onSearch={mockOnSearch} />);

    // Find the input by its associated label text (accessible query)
    const input = screen.getByLabelText(/search courses/i);

    // Type into the input — userEvent.type dispatches keydown, keypress, keyup, and change
    await user.type(input, 'TypeScript');

    // Submit the form
    await user.click(screen.getByRole('button', { name: /search/i }));

    expect(mockOnSearch).toHaveBeenCalledWith('TypeScript');
  });

  test('input value updates as the user types', async () => {
    const user = userEvent.setup();
    render(<SearchBox onSearch={jest.fn()} />);

    const input = screen.getByLabelText(/search courses/i);
    await user.type(input, 'React');

    expect(input).toHaveValue('React');
    // toHaveValue() checks the current DOM value of an input
  });
});

// =============================================================
// SECTION 9 — Testing Custom Hooks with renderHook
// =============================================================
// renderHook() lets you test a hook in isolation without
// building a component around it.
// =============================================================

// The hook to test — useCounter
function useCounter(initialValue = 0) {
  const [count, setCount] = useState(initialValue);

  const increment = () => setCount(c => c + 1);
  const decrement = () => setCount(c => c - 1);
  const reset = () => setCount(initialValue);

  return { count, increment, decrement, reset };
}

describe('useCounter hook', () => {
  test('initializes with the provided value', () => {
    const { result } = renderHook(() => useCounter(5));
    // result.current — the current return value of the hook
    expect(result.current.count).toBe(5);
  });

  test('defaults to 0 when no initial value provided', () => {
    const { result } = renderHook(() => useCounter());
    expect(result.current.count).toBe(0);
  });

  test('increments the count', () => {
    const { result } = renderHook(() => useCounter());

    // act() ensures state updates and their effects are flushed before assertions
    act(() => {
      result.current.increment();
    });

    expect(result.current.count).toBe(1);
  });

  test('decrements the count', () => {
    const { result } = renderHook(() => useCounter(10));

    act(() => {
      result.current.decrement();
    });

    expect(result.current.count).toBe(9);
  });

  test('resets to the initial value', () => {
    const { result } = renderHook(() => useCounter(5));

    act(() => {
      result.current.increment();
      result.current.increment();
      result.current.reset();
    });

    expect(result.current.count).toBe(5); // back to initial, not 0
  });
});

// =============================================================
// SECTION 10 — Testing a Hook with useReducer
// =============================================================

function cartReducer(state, action) {
  switch (action.type) {
    case 'ADD_ITEM':
      return { ...state, items: [...state.items, action.payload] };
    case 'REMOVE_ITEM':
      return { ...state, items: state.items.filter(item => item.id !== action.payload) };
    case 'CLEAR':
      return { items: [] };
    default:
      return state;
  }
}

function useCart() {
  const [state, dispatch] = useReducer(cartReducer, { items: [] });

  const addItem = (item) => dispatch({ type: 'ADD_ITEM', payload: item });
  const removeItem = (id) => dispatch({ type: 'REMOVE_ITEM', payload: id });
  const clearCart = () => dispatch({ type: 'CLEAR' });

  return { items: state.items, addItem, removeItem, clearCart };
}

describe('useCart hook', () => {
  test('starts with an empty cart', () => {
    const { result } = renderHook(() => useCart());
    expect(result.current.items).toHaveLength(0);
  });

  test('adds an item to the cart', () => {
    const { result } = renderHook(() => useCart());
    const newItem = { id: 1, title: 'React Course', price: 49 };

    act(() => {
      result.current.addItem(newItem);
    });

    expect(result.current.items).toHaveLength(1);
    expect(result.current.items[0]).toEqual(newItem);
  });

  test('removes an item from the cart', () => {
    const { result } = renderHook(() => useCart());

    act(() => {
      result.current.addItem({ id: 1, title: 'React Course' });
      result.current.addItem({ id: 2, title: 'TypeScript Course' });
      result.current.removeItem(1);
    });

    expect(result.current.items).toHaveLength(1);
    expect(result.current.items[0].id).toBe(2);
  });

  test('clears all items', () => {
    const { result } = renderHook(() => useCart());

    act(() => {
      result.current.addItem({ id: 1 });
      result.current.addItem({ id: 2 });
      result.current.clearCart();
    });

    expect(result.current.items).toHaveLength(0);
  });
});

// =============================================================
// SECTION 11 — Common Jest Matchers Reference
// =============================================================
// Equality:
//   expect(value).toBe(42)              — strict equality (===)
//   expect(value).toEqual({ a: 1 })     — deep equality (objects/arrays)
//   expect(value).not.toBe(null)        — negation with .not
//
// Truthiness:
//   expect(value).toBeTruthy()          — truthy (not false/null/0/'')
//   expect(value).toBeFalsy()           — falsy
//   expect(value).toBeNull()
//   expect(value).toBeUndefined()
//   expect(value).toBeDefined()
//
// Numbers:
//   expect(n).toBeGreaterThan(0)
//   expect(n).toBeLessThanOrEqual(100)
//   expect(n).toBeCloseTo(0.3)          — floating point comparison
//
// Strings:
//   expect(str).toMatch(/regex/)
//   expect(str).toContain('substring')
//
// Arrays:
//   expect(arr).toHaveLength(3)
//   expect(arr).toContain(item)
//   expect(arr).toEqual(expect.arrayContaining([1, 2]))
//
// DOM (@testing-library/jest-dom):
//   expect(el).toBeInTheDocument()
//   expect(el).toBeVisible()
//   expect(el).toBeDisabled()
//   expect(el).toHaveValue('text')
//   expect(el).toHaveClass('active')
//   expect(el).toHaveTextContent(/pattern/)
//   expect(el).toHaveFocus()
//
// Functions/mocks:
//   expect(fn).toHaveBeenCalled()
//   expect(fn).toHaveBeenCalledTimes(2)
//   expect(fn).toHaveBeenCalledWith(arg1, arg2)
//   expect(fn).toHaveBeenLastCalledWith(arg)
// =============================================================
