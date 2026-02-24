// =============================================================================
// 01-controlled-and-uncontrolled-forms.jsx
// Forms, Controlled Components, Uncontrolled Components & useRef
// =============================================================================
// React offers TWO approaches to form inputs:
//
//  CONTROLLED   — React state is the "single source of truth."
//                 Every keystroke updates state; the input's value is driven
//                 by state. You always know the current value.
//
//  UNCONTROLLED — The DOM is the source of truth.
//                 React does NOT track the value on every keystroke.
//                 You read the value when you need it (e.g. on submit)
//                 using a REF.
//
// SECTIONS:
//  1. Controlled components — text, email, select, checkbox, radio, textarea
//  2. Controlled form with validation
//  3. Uncontrolled components with useRef
//  4. useRef for DOM access (not forms)
//  5. useRef as a mutable container (persisting values across renders)
// =============================================================================

import React, { useState, useRef, useEffect } from 'react';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Controlled Components
// ─────────────────────────────────────────────────────────────────────────────
// Every input type follows the same pattern:
//   value={stateVar}          ← React controls what's displayed
//   onChange={e => setState}  ← React updates state on every change
//
// The input never has a "life of its own" — state IS the value.

export function ControlledInputTypes() {
  const [text, setText]         = useState('');
  const [email, setEmail]       = useState('');
  const [course, setCourse]     = useState('react');
  const [agreed, setAgreed]     = useState(false);
  const [level, setLevel]       = useState('beginner');
  const [bio, setBio]           = useState('');

  return (
    <div className="form-section">
      <h2>Controlled Components — All Input Types</h2>

      {/* Text input */}
      <div className="field">
        <label>Name:
          <input
            type="text"
            value={text}
            onChange={e => setText(e.target.value)}
            placeholder="Full name"
          />
        </label>
        <small>Live value: "{text}"</small>
      </div>

      {/* Email input — same pattern, different type */}
      <div className="field">
        <label>Email:
          <input
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            placeholder="you@example.com"
          />
        </label>
      </div>

      {/* Select / dropdown */}
      <div className="field">
        <label>Course:
          <select value={course} onChange={e => setCourse(e.target.value)}>
            <option value="react">React</option>
            <option value="angular">Angular</option>
            <option value="vue">Vue</option>
          </select>
        </label>
        <small>Selected: {course}</small>
      </div>

      {/* Checkbox — use `checked` not `value` */}
      <div className="field">
        <label>
          <input
            type="checkbox"
            checked={agreed}
            onChange={e => setAgreed(e.target.checked)}  // ← e.target.checked, not .value
          />
          I agree to the terms
        </label>
        <small>Agreed: {agreed.toString()}</small>
      </div>

      {/* Radio buttons — compare value to determine which is checked */}
      <div className="field">
        <p>Experience level:</p>
        {['beginner', 'intermediate', 'advanced'].map(lvl => (
          <label key={lvl}>
            <input
              type="radio"
              value={lvl}
              checked={level === lvl}               // ← this radio is "checked" when level matches
              onChange={e => setLevel(e.target.value)}
            />
            {lvl.charAt(0).toUpperCase() + lvl.slice(1)}
          </label>
        ))}
        <small>Level: {level}</small>
      </div>

      {/* Textarea — same pattern as text input */}
      <div className="field">
        <label>Bio:
          <textarea
            value={bio}
            onChange={e => setBio(e.target.value)}
            rows={3}
            placeholder="Tell us about yourself…"
          />
        </label>
        <small>{bio.length} characters</small>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Controlled Form with Validation
// ─────────────────────────────────────────────────────────────────────────────
// Because React controls every value, you can validate in real time (onChange)
// or on submit. Here we show both patterns.

export function RegistrationForm() {
  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [errors, setErrors]       = useState({});
  const [submitted, setSubmitted] = useState(false);

  // Generic field updater — works for all fields
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    // Clear error for this field as the user types
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };

  // Validate the whole form on submit
  const validate = () => {
    const newErrors = {};
    if (!form.name.trim())                     newErrors.name     = 'Name is required';
    if (!form.email.includes('@'))             newErrors.email    = 'Valid email required';
    if (form.password.length < 8)             newErrors.password = 'Password must be 8+ characters';
    if (form.password !== form.confirmPassword) newErrors.confirmPassword = 'Passwords do not match';
    return newErrors;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }
    setSubmitted(true);
  };

  if (submitted) {
    return <p className="success">✅ Welcome, {form.name}! Registration complete.</p>;
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <h2>Controlled Form with Validation</h2>

      {['name', 'email', 'password', 'confirmPassword'].map(field => (
        <div key={field} className="field">
          <label>
            {field.charAt(0).toUpperCase() + field.replace(/([A-Z])/g, ' $1').slice(1)}:
            <input
              type={field.includes('password') || field.includes('Password') ? 'password' : 'text'}
              name={field}
              value={form[field]}
              onChange={handleChange}
              className={errors[field] ? 'input-error' : ''}
            />
          </label>
          {errors[field] && <p className="error-msg">{errors[field]}</p>}
        </div>
      ))}

      <button type="submit">Register</button>
    </form>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Uncontrolled Components with useRef
// ─────────────────────────────────────────────────────────────────────────────
// Sometimes you DON'T need React to track every keystroke.
// For simple forms where you only need the value on submit, uncontrolled
// components are lighter — the DOM manages the value, and you read it
// via a ref when you need it.
//
// useRef() returns a mutable object: { current: null }
// When attached to a DOM element via the `ref` prop, React sets `current`
// to that DOM element after the first render.

export function UncontrolledForm() {
  // Create refs — React populates `.current` after the component renders
  const nameRef     = useRef(null);
  const emailRef    = useRef(null);
  const courseRef   = useRef(null);
  const [result, setResult] = useState(null);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Read values directly from the DOM elements via .current.value
    setResult({
      name:   nameRef.current.value,
      email:  emailRef.current.value,
      course: courseRef.current.value,
    });
  };

  const handleReset = () => {
    // Imperatively clear the inputs — something you CAN do with refs
    nameRef.current.value  = '';
    emailRef.current.value = '';
    setResult(null);
  };

  return (
    <div>
      <h2>Uncontrolled Components — useRef</h2>
      <p>React does NOT track these inputs on every keystroke. Values are read only on submit.</p>

      <form onSubmit={handleSubmit}>
        <div className="field">
          <label>Name: <input ref={nameRef} type="text" defaultValue="Alice" /></label>
        </div>
        <div className="field">
          <label>Email: <input ref={emailRef} type="email" /></label>
        </div>
        <div className="field">
          <label>Course:
            <select ref={courseRef} defaultValue="react">
              <option value="react">React</option>
              <option value="angular">Angular</option>
            </select>
          </label>
        </div>
        <button type="submit">Submit</button>
        <button type="button" onClick={handleReset}>Reset</button>
      </form>

      {result && (
        <div className="result">
          <h4>Submitted:</h4>
          <pre>{JSON.stringify(result, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — useRef for DOM Access (Beyond Forms)
// ─────────────────────────────────────────────────────────────────────────────
// Refs give you a direct "escape hatch" to the DOM — useful for:
//   • Auto-focusing an input
//   • Measuring element dimensions (getBoundingClientRect)
//   • Triggering imperative animations
//   • Integrating with third-party non-React DOM libraries

export function DomAccessDemo() {
  const inputRef     = useRef(null);
  const videoRef     = useRef(null);  // example — no actual video here
  const scrollBoxRef = useRef(null);

  // Auto-focus the input when a button is clicked
  const focusInput = () => {
    inputRef.current.focus();
    inputRef.current.select();   // also select all existing text
  };

  // Measure element dimensions
  const measureBox = () => {
    const rect = scrollBoxRef.current.getBoundingClientRect();
    alert(`Width: ${rect.width}px, Height: ${rect.height}px`);
  };

  // Scroll to the bottom of a scrollable div imperatively
  const scrollToBottom = () => {
    const box = scrollBoxRef.current;
    box.scrollTop = box.scrollHeight;
  };

  return (
    <div>
      <h2>useRef for DOM Access</h2>

      <div className="field">
        <input ref={inputRef} type="text" placeholder="I can be focused programmatically" />
        <button onClick={focusInput}>Focus & Select</button>
      </div>

      <div
        ref={scrollBoxRef}
        style={{ height: '100px', overflowY: 'scroll', border: '1px solid #ccc', padding: '8px' }}
      >
        {Array.from({ length: 20 }, (_, i) => <p key={i}>Line {i + 1}</p>)}
      </div>
      <button onClick={measureBox}>Measure Box</button>
      <button onClick={scrollToBottom}>Scroll to Bottom</button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — useRef as a Mutable Container
// ─────────────────────────────────────────────────────────────────────────────
// useRef is not just for DOM elements. It's a general-purpose container
// for any mutable value that you want to persist across renders WITHOUT
// causing a re-render when it changes.
//
// COMPARE:
//   useState  → persists across renders + triggers re-render on change
//   useRef    → persists across renders + does NOT trigger re-render
//
// COMMON USE CASES:
//   • Storing a timer ID (so you can clear it later)
//   • Keeping the previous value of a prop/state
//   • Tracking whether the component has mounted yet
//   • Storing a WebSocket or subscription reference

export function RenderCounter() {
  const [count, setCount] = useState(0);
  const renderCount = useRef(0);           // persists between renders, no re-render when changed
  const prevCountRef = useRef(undefined);  // track the previous value of count

  // Update the refs on every render (no setter needed — just assign .current)
  renderCount.current += 1;

  // Capture previous count BEFORE the update
  useEffect(() => {
    prevCountRef.current = count;
  }); // no deps = runs after every render — stores previous count

  return (
    <div>
      <h2>useRef as Mutable Container</h2>
      <p>Current count: <strong>{count}</strong></p>
      <p>Previous count: <strong>{prevCountRef.current ?? '—'}</strong></p>
      <p>
        This component has rendered <strong>{renderCount.current}</strong> time(s).
        <br />
        <small>(renderCount is a ref — incrementing it does NOT cause a re-render)</small>
      </p>
      <button onClick={() => setCount(c => c + 1)}>Increment</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Root export
// ─────────────────────────────────────────────────────────────────────────────
export default function FormsAndRefs() {
  return (
    <div>
      <h1>Forms, Controlled & Uncontrolled Components, useRef</h1>
      <ControlledInputTypes />
      <hr />
      <RegistrationForm />
      <hr />
      <UncontrolledForm />
      <hr />
      <DomAccessDemo />
      <hr />
      <RenderCounter />
    </div>
  );
}
