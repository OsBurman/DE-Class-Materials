// Exercise 01 Solution: useState Counter and State Updates
const { useState } = React;

function Counter() {
  const [count, setCount] = useState(0);
  const [step, setStep] = useState(1);

  // Functional update form: guarantees we read the latest state value.
  // Using `prev => prev + step` is safer than `setCount(count + step)` in
  // async / batched scenarios.
  function increment() { setCount(prev => prev + step); }
  function decrement() { setCount(prev => prev - step); }
  function reset()     { setCount(0); }

  return (
    <div className="box">
      <h2>Count: {count}</h2>
      <div>
        <label>Step: </label>
        {/* Controlled input: value comes from state, onChange updates state */}
        <input
          type="number"
          value={step}
          onChange={e => setStep(parseInt(e.target.value) || 1)}
        />
      </div>
      <div>
        <button onClick={increment}>Increment</button>
        <button onClick={decrement}>Decrement</button>
        <button onClick={reset}>Reset</button>
      </div>
    </div>
  );
}

function ClickTracker() {
  const [clicks, setClicks] = useState(0);

  return (
    <div className="box">
      <p>You've clicked {clicks} times</p>
      <button onClick={() => setClicks(c => c + 1)}>Click Me!</button>
      <button onClick={() => setClicks(0)}>Reset</button>
    </div>
  );
}

function App() {
  return (
    <div>
      <h1>useState Demo</h1>
      <Counter />
      <ClickTracker />
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
