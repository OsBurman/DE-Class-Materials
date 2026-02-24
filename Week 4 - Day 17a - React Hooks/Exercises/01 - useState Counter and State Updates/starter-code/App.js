// Exercise 01: useState Counter and State Updates
const { useState } = React;

// ── Counter Component ─────────────────────────────────────────────────────────
function Counter() {
  // TODO 1: Declare a state variable 'count' initialized to 0.
  //         const [count, setCount] = useState(0);

  // TODO 2: Declare a state variable 'step' initialized to 1.
  //         const [step, setStep] = useState(1);

  // TODO 3: Implement increment — call setCount using a functional update: prev => prev + step
  function increment() {
    // TODO: setCount(prev => ...)
  }

  // TODO 4: Implement decrement — call setCount using a functional update: prev => prev - step
  function decrement() {
    // TODO: setCount(prev => ...)
  }

  // TODO 5: Implement reset — set count back to 0
  function reset() {
    // TODO: setCount(...)
  }

  return (
    <div className="box">
      <h2>Count: {/* TODO 6: display count */}</h2>

      <div>
        <label>Step: </label>
        {/* TODO 7: Add <input type="number" value={step} onChange={...}>
                   Parse the input value with parseInt(e.target.value) || 1
                   and call setStep with the parsed number */}
      </div>

      <div>
        {/* TODO 8: Wire up onClick handlers to each button */}
        <button>Increment</button>
        <button>Decrement</button>
        <button>Reset</button>
      </div>
    </div>
  );
}

// ── ClickTracker Component ────────────────────────────────────────────────────
function ClickTracker() {
  // TODO 9: Declare a 'clicks' state variable initialized to 0.

  return (
    <div className="box">
      {/* TODO 10: Display "You've clicked N times" using the clicks state variable */}
      <p>You've clicked ? times</p>
      {/* TODO 11: Wire up the button to increment clicks on each click */}
      <button>Click Me!</button>
      {/* TODO 12: Wire up Reset to set clicks back to 0 */}
      <button>Reset</button>
    </div>
  );
}

// ── App ───────────────────────────────────────────────────────────────────────
function App() {
  return (
    <div>
      <h1>useState Demo</h1>
      {/* TODO 13: Render <Counter /> and <ClickTracker /> */}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
