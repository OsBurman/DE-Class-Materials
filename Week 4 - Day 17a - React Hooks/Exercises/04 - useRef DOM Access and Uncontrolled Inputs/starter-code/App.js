// Exercise 04: useRef — DOM Access and Uncontrolled Inputs
const { useState, useRef } = React;

// ── Part A: UncontrolledInput ─────────────────────────────────────────────────
function UncontrolledInput() {
  // TODO 1: Create a ref called inputRef using useRef(null).

  // TODO 2: Declare a 'readValue' state (string) to display the captured value.

  function handleRead() {
    // TODO 3: Read inputRef.current.value and store it in readValue state.
  }

  return (
    <div className="box">
      <h3>Part A — Uncontrolled Input</h3>
      {/*
        TODO 4: Add a text <input> with:
          - NO value prop (uncontrolled)
          - NO onChange prop
          - ref={inputRef}
          - placeholder="Type something..."
      */}
      <button onClick={handleRead}>Read Value</button>
      {/* TODO 5: Display "Captured: {readValue}" when readValue is non-empty */}
    </div>
  );
}

// ── Part B: FocusDemo ─────────────────────────────────────────────────────────
function FocusDemo() {
  // TODO 6: Create a ref called focusRef using useRef(null).

  function handleFocus() {
    // TODO 7: Call .focus() on focusRef.current to programmatically focus the input.
  }

  return (
    <div className="box">
      <h3>Part B — Programmatic Focus</h3>
      {/*
        TODO 8: Add a text <input> with ref={focusRef} and placeholder="Click the button to focus me"
      */}
      <button onClick={handleFocus}>Focus Input</button>
    </div>
  );
}

// ── Part C: RenderCounter ─────────────────────────────────────────────────────
function RenderCounter() {
  const [count, setCount] = useState(0);

  // TODO 9: Create a ref called renderCountRef initialized to 0.

  // TODO 10: Increment renderCountRef.current here (at render time, NOT in useEffect).
  //          This runs on every render but does NOT cause another render.

  return (
    <div className="box">
      <h3>Part C — Render Counter</h3>
      <p>State counter: <strong>{count}</strong></p>
      {/* TODO 11: Display renderCountRef.current as the render count */}
      <p>Component has rendered: <strong>?</strong> time(s)</p>
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
      {/* TODO 12: Render UncontrolledInput, FocusDemo, and RenderCounter */}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
