// =============================================================================
// 03-custom-hooks.jsx — Custom Hooks
// =============================================================================
// A custom hook is any function whose name starts with "use" and that calls
// at least one built-in hook inside it. That's literally all it is.
//
// WHY BUILD CUSTOM HOOKS?
//   • Extract stateful logic from components so it can be REUSED
//   • Keep components clean — they describe UI, hooks handle behavior
//   • Test logic in isolation (no component needed)
//   • Share behavior without sharing JSX (unlike HOCs or render props)
//
// RULES OF HOOKS (apply to custom hooks too):
//   1. Only call hooks at the TOP LEVEL — not inside loops, conditions, callbacks
//   2. Only call hooks from REACT FUNCTIONS — components or custom hooks
//      Not from plain JS functions, class methods, or event handlers.
//
// SECTIONS:
//  1. useLocalStorage — persist state to localStorage
//  2. useFetch — data fetching with loading and error states
//  3. useDebounce — debounce any value
//  4. useForm — generic form state with validation
//  5. Rules of Hooks illustrated
// =============================================================================

import React, { useState, useEffect, useRef, useCallback } from 'react';

// ─────────────────────────────────────────────────────────────────────────────
// CUSTOM HOOK 1 — useLocalStorage
// ─────────────────────────────────────────────────────────────────────────────
// Behaves exactly like useState, but the value is synced to localStorage.
// On first render it reads the saved value; on updates it saves back.
//
// Usage:
//   const [theme, setTheme] = useLocalStorage('theme', 'light');
//   const [cart, setCart]   = useLocalStorage('cart', []);

export function useLocalStorage(key, initialValue) {
  // Lazy initializer: read from localStorage ONCE on mount
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = localStorage.getItem(key);
      // If a value exists, parse it (we serialize to JSON); otherwise use initialValue
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.warn(`useLocalStorage: could not read key "${key}"`, error);
      return initialValue;
    }
  });

  // Wrap the setter: save to localStorage in addition to state
  const setValue = (value) => {
    try {
      // Support functional updates (same API as useState)
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      setStoredValue(valueToStore);
      localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.warn(`useLocalStorage: could not write key "${key}"`, error);
    }
  };

  return [storedValue, setValue];  // same API as useState
}

// Demo component using useLocalStorage
export function ThemeWithPersistence() {
  const [theme, setTheme] = useLocalStorage('app-theme', 'light');

  return (
    <div className={`theme-demo theme-${theme}`}>
      <h2>useLocalStorage — Persistent Theme</h2>
      <p>Theme: <strong>{theme}</strong> (refresh the page — it persists!)</p>
      <button onClick={() => setTheme(t => t === 'light' ? 'dark' : 'light')}>
        Toggle Theme
      </button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// CUSTOM HOOK 2 — useFetch
// ─────────────────────────────────────────────────────────────────────────────
// Encapsulates the common fetch + loading + error pattern so every component
// doesn't have to re-implement it from scratch.
//
// Usage:
//   const { data, loading, error } = useFetch('/api/courses');
//   const { data, refetch }        = useFetch(url);

export function useFetch(url) {
  const [data, setData]       = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);

  // Store the url in a ref so we can compare inside the effect
  // (useful if the caller passes a string literal every render)
  const urlRef = useRef(url);

  useEffect(() => {
    if (!url) return;

    let cancelled = false;  // prevent setting state after unmount

    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const json = await response.json();
        if (!cancelled) setData(json);
      } catch (err) {
        if (!cancelled) setError(err.message);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchData();

    // Cleanup: mark as cancelled if the component unmounts or url changes
    return () => { cancelled = true; };
  }, [url]);

  return { data, loading, error };
}

// Demo component using useFetch
export function CourseListWithFetch() {
  const { data: courses, loading, error } = useFetch(
    'https://jsonplaceholder.typicode.com/todos?_limit=5'  // public test API
  );

  if (loading) return <p>⏳ Fetching courses…</p>;
  if (error)   return <p className="error">❌ Error: {error}</p>;

  return (
    <div>
      <h2>useFetch — Data Fetching Hook</h2>
      <p>Data comes from a public test API (JSONPlaceholder). Notice how clean the component is — all fetch logic lives in the hook.</p>
      <ul>
        {courses?.map(item => (
          <li key={item.id}>
            {item.completed ? '✅' : '○'} {item.title}
          </li>
        ))}
      </ul>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// CUSTOM HOOK 3 — useDebounce
// ─────────────────────────────────────────────────────────────────────────────
// Returns a debounced version of a value — the returned value only updates
// after the specified delay has passed WITHOUT a new value arriving.
//
// Usage:
//   const debouncedSearch = useDebounce(searchQuery, 400);
//
// The component uses `debouncedSearch` to trigger API calls instead of
// `searchQuery` — API is only called when the user pauses typing.

export function useDebounce(value, delay = 400) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    // Cleanup: cancel timer if value changes before delay expires
    return () => clearTimeout(timer);
  }, [value, delay]);

  return debouncedValue;
}

// Demo component using useDebounce
const COURSES = [
  'React Hooks', 'React Fundamentals', 'Redux Toolkit',
  'TypeScript', 'Node.js', 'Spring Boot', 'Angular Signals',
  'GraphQL', 'Docker & Kubernetes', 'AWS Fundamentals'
];

export function DebouncedSearch() {
  const [query, setQuery]     = useState('');
  const debouncedQuery        = useDebounce(query, 400);
  const [callCount, setCallCount] = useState(0);

  // This effect only fires when debouncedQuery changes — not on every keystroke
  useEffect(() => {
    if (debouncedQuery) setCallCount(c => c + 1);
  }, [debouncedQuery]);

  const results = COURSES.filter(c =>
    c.toLowerCase().includes(debouncedQuery.toLowerCase())
  );

  return (
    <div>
      <h2>useDebounce — Debounced Search</h2>
      <input
        value={query}
        onChange={e => setQuery(e.target.value)}
        placeholder="Type to search (watch the search count)…"
      />
      <p>Raw query: "{query}" | Debounced: "{debouncedQuery}"</p>
      <p>Search executed: <strong>{callCount}</strong> time(s) (much less than keystrokes!)</p>
      <ul>
        {debouncedQuery && results.map(r => <li key={r}>{r}</li>)}
      </ul>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// CUSTOM HOOK 4 — useForm
// ─────────────────────────────────────────────────────────────────────────────
// Generic form state management hook.
// Handles values, errors, touched fields, and submit logic.
//
// Usage:
//   const { values, errors, handleChange, handleSubmit, reset } = useForm(
//     initialValues,
//     validationFn
//   );

export function useForm(initialValues, validate) {
  const [values, setValues]   = useState(initialValues);
  const [errors, setErrors]   = useState({});
  const [touched, setTouched] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Called on every input change
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setValues(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  // Called when an input loses focus — show errors only for touched fields
  const handleBlur = (e) => {
    setTouched(prev => ({ ...prev, [e.target.name]: true }));
    if (validate) {
      const validationErrors = validate(values);
      setErrors(validationErrors);
    }
  };

  // Called on form submit — validate all fields, then call the onSubmit callback
  const handleSubmit = (onSubmit) => async (e) => {
    e.preventDefault();
    // Mark all fields as touched on submit attempt
    const allTouched = Object.keys(values).reduce((acc, key) => ({ ...acc, [key]: true }), {});
    setTouched(allTouched);

    if (validate) {
      const validationErrors = validate(values);
      setErrors(validationErrors);
      if (Object.keys(validationErrors).length > 0) return;
    }

    setIsSubmitting(true);
    await onSubmit(values);
    setIsSubmitting(false);
  };

  const reset = () => {
    setValues(initialValues);
    setErrors({});
    setTouched({});
    setIsSubmitting(false);
  };

  return { values, errors, touched, isSubmitting, handleChange, handleBlur, handleSubmit, reset };
}

// Demo component using useForm
export function ContactForm() {
  const [submitted, setSubmitted] = useState(null);

  const validate = (values) => {
    const errors = {};
    if (!values.name.trim())    errors.name    = 'Name is required';
    if (!values.email.includes('@')) errors.email = 'Valid email required';
    if (!values.message.trim()) errors.message = 'Message is required';
    return errors;
  };

  const { values, errors, touched, isSubmitting, handleChange, handleBlur, handleSubmit, reset } =
    useForm({ name: '', email: '', message: '' }, validate);

  const onSubmit = async (data) => {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 800));
    setSubmitted(data);
  };

  if (submitted) {
    return (
      <div>
        <h2>useForm — Contact Form</h2>
        <p className="success">✅ Message sent from {submitted.name}!</p>
        <button onClick={() => { reset(); setSubmitted(null); }}>Send Another</button>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <h2>useForm — Reusable Form Hook</h2>

      {[
        { name: 'name',    label: 'Name',    type: 'text' },
        { name: 'email',   label: 'Email',   type: 'email' },
        { name: 'message', label: 'Message', type: 'textarea' },
      ].map(({ name, label, type }) => (
        <div key={name} className="field">
          <label>{label}:
            {type === 'textarea'
              ? <textarea name={name} value={values[name]} onChange={handleChange} onBlur={handleBlur} />
              : <input type={type} name={name} value={values[name]} onChange={handleChange} onBlur={handleBlur} />
            }
          </label>
          {touched[name] && errors[name] && <p className="error-msg">{errors[name]}</p>}
        </div>
      ))}

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Sending…' : 'Send Message'}
      </button>
    </form>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Rules of Hooks Illustrated
// ─────────────────────────────────────────────────────────────────────────────

export function RulesOfHooks() {
  const [showWarnings, setShowWarnings] = useState(false);

  return (
    <div className="rules-section">
      <h2>Rules of Hooks</h2>

      <h4>Rule 1: Only call hooks at the top level</h4>
      <pre>{`
// ❌ WRONG — hook inside a condition
function MyComponent({ isLoggedIn }) {
  if (isLoggedIn) {
    const [data, setData] = useState(null);  // breaks hook ordering!
  }
}

// ✅ CORRECT — hook always called, condition inside
function MyComponent({ isLoggedIn }) {
  const [data, setData] = useState(null);  // always called
  if (!isLoggedIn) return null;            // condition here is fine
}
      `}</pre>

      <h4>Rule 2: Only call hooks from React functions</h4>
      <pre>{`
// ❌ WRONG — hook in a plain JS function
function fetchUser(id) {
  const [user, setUser] = useState(null);  // not a component or custom hook!
}

// ✅ CORRECT — hook in a component
function UserCard({ id }) {
  const [user, setUser] = useState(null);  // component function ✓
}

// ✅ CORRECT — hook in a custom hook
function useUser(id) {         // name starts with "use" ✓
  const [user, setUser] = useState(null);
  return user;
}
      `}</pre>

      <h4>Why these rules?</h4>
      <p>
        React relies on the <strong>order</strong> in which hooks are called to associate
        state with the correct hook between renders. If you call hooks conditionally,
        the order can change between renders and React assigns state to the wrong hook.
        The ESLint plugin <code>eslint-plugin-react-hooks</code> enforces these rules automatically.
      </p>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Root export
// ─────────────────────────────────────────────────────────────────────────────
export default function CustomHooksDemo() {
  return (
    <div>
      <h1>Custom Hooks</h1>
      <ThemeWithPersistence />
      <hr />
      <CourseListWithFetch />
      <hr />
      <DebouncedSearch />
      <hr />
      <ContactForm />
      <hr />
      <RulesOfHooks />
    </div>
  );
}
