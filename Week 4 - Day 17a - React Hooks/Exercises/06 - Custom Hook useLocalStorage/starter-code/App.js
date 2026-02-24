// Exercise 06: Custom Hook — useLocalStorage
const { useState, useEffect } = React;

// ── Custom Hook ───────────────────────────────────────────────────────────────
function useLocalStorage(key, initialValue) {
  // TODO 1: Initialize state with a LAZY initializer function (not a direct value).
  //         Inside it: read localStorage.getItem(key).
  //         If found, return JSON.parse(stored). Otherwise return initialValue.
  const [value, setValue] = useState(initialValue); // replace with lazy initializer

  // TODO 2: Add a useEffect that writes value to localStorage using JSON.stringify.
  //         Dependency array: [key, value]
  useEffect(() => {
    // TODO: localStorage.setItem(key, JSON.stringify(value));
  }, [/* TODO: key, value */]);

  // TODO 3: Return [value, setValue] so callers use it exactly like useState.
  return [value, setValue];
}

// ── NamePersister ─────────────────────────────────────────────────────────────
function NamePersister() {
  // TODO 4: Replace the useState below with useLocalStorage('username', '').
  const [name, setName] = useState('');

  return (
    <div className="box">
      <h3>Name Persister</h3>
      {/* TODO 5: Bind input to 'name' via value and onChange */}
      <input
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Enter your name"
      />
      {/* TODO 6: Display "Hello, {name}!" when name is non-empty */}
      <p><em>Try refreshing the page — your name should still be here!</em></p>
    </div>
  );
}

// ── CounterPersister ──────────────────────────────────────────────────────────
function CounterPersister() {
  // TODO 7: Replace the useState below with useLocalStorage('persistCount', 0).
  const [count, setCount] = useState(0);

  return (
    <div className="box">
      <h3>Persistent Counter</h3>
      <p>Count: <strong>{count}</strong></p>
      {/* TODO 8: Wire up the three buttons using setCount */}
      <button onClick={() => setCount(count + 1)}>Increment</button>
      <button onClick={() => setCount(count - 1)}>Decrement</button>
      <button onClick={() => setCount(0)}>Reset</button>
      <p><em>Try refreshing the page — the count should survive!</em></p>
    </div>
  );
}

// ── App ───────────────────────────────────────────────────────────────────────
function App() {
  return (
    <div>
      <h1>Custom Hook — useLocalStorage</h1>
      {/* TODO 9: Render NamePersister and CounterPersister */}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
