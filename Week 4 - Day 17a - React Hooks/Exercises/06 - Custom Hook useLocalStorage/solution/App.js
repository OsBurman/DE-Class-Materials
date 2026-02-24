// Exercise 06: Custom Hook — useLocalStorage — SOLUTION
const { useState, useEffect } = React;

// ── Custom Hook ───────────────────────────────────────────────────────────────
function useLocalStorage(key, initialValue) {
  // Lazy initializer: reads localStorage ONCE on mount
  const [value, setValue] = useState(() => {
    const stored = localStorage.getItem(key);
    return stored !== null ? JSON.parse(stored) : initialValue;
  });

  // Sync to localStorage whenever key or value changes
  useEffect(() => {
    localStorage.setItem(key, JSON.stringify(value));
  }, [key, value]);

  return [value, setValue]; // same API as useState
}

// ── NamePersister ─────────────────────────────────────────────────────────────
function NamePersister() {
  const [name, setName] = useLocalStorage('username', '');

  return (
    <div className="box">
      <h3>Name Persister</h3>
      <input
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Enter your name"
      />
      {name && <p>Hello, <strong>{name}</strong>! 👋</p>}
      <p><em>Try refreshing the page — your name should still be here!</em></p>
    </div>
  );
}

// ── CounterPersister ──────────────────────────────────────────────────────────
function CounterPersister() {
  const [count, setCount] = useLocalStorage('persistCount', 0);

  return (
    <div className="box">
      <h3>Persistent Counter</h3>
      <p>Count: <strong>{count}</strong></p>
      <button onClick={() => setCount((c) => c + 1)}>Increment</button>
      <button onClick={() => setCount((c) => c - 1)}>Decrement</button>
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
      <NamePersister />
      <CounterPersister />
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
