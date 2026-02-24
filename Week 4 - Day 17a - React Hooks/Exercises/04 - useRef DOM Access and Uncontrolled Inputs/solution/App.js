// Exercise 04: useRef — DOM Access and Uncontrolled Inputs — SOLUTION
const { useState, useRef } = React;

// ── Part A: UncontrolledInput ─────────────────────────────────────────────────
function UncontrolledInput() {
  const inputRef   = useRef(null);
  const [readValue, setReadValue] = useState('');

  function handleRead() {
    setReadValue(inputRef.current.value);
  }

  return (
    <div className="box">
      <h3>Part A — Uncontrolled Input</h3>
      <p><em>React does NOT track this input's value — it lives in the DOM.</em></p>
      <input ref={inputRef} type="text" placeholder="Type something..." />
      <button onClick={handleRead}>Read Value</button>
      {readValue && <p>Captured: <strong>{readValue}</strong></p>}
    </div>
  );
}

// ── Part B: FocusDemo ─────────────────────────────────────────────────────────
function FocusDemo() {
  const focusRef = useRef(null);

  function handleFocus() {
    focusRef.current.focus(); // direct DOM call
  }

  return (
    <div className="box">
      <h3>Part B — Programmatic Focus</h3>
      <input ref={focusRef} type="text" placeholder="Click the button to focus me" />
      <button onClick={handleFocus}>Focus Input</button>
    </div>
  );
}

// ── Part C: RenderCounter ─────────────────────────────────────────────────────
function RenderCounter() {
  const [count, setCount] = useState(0);

  // Ref as a mutable container — incrementing it does NOT cause a re-render
  const renderCountRef = useRef(0);
  renderCountRef.current++; // runs on every render

  return (
    <div className="box">
      <h3>Part C — Render Counter</h3>
      <p>State counter: <strong>{count}</strong></p>
      <p>
        Component has rendered: <strong>{renderCountRef.current}</strong> time(s)
      </p>
      <button onClick={() => setCount(c => c + 1)}>Increment (forces re-render)</button>
      <p><em>Notice: render count increases with each state update.</em></p>
    </div>
  );
}

// ── App ───────────────────────────────────────────────────────────────────────
function App() {
  return (
    <div>
      <h1>useRef Demo</h1>
      <UncontrolledInput />
      <FocusDemo />
      <RenderCounter />
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
