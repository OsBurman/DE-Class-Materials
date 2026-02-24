// Exercise 02: useEffect with Dependencies and Cleanup — SOLUTION
const { useState, useEffect } = React;

// ── TitleUpdater ──────────────────────────────────────────────────────────────
function TitleUpdater() {
  const [title, setTitle] = useState("React Hooks");

  // Sync document.title with 'title' state on every change
  useEffect(() => {
    document.title = title;
  }, [title]); // Re-runs only when 'title' changes

  return (
    <div className="box">
      <h3>Document Title Updater</h3>
      <input
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="Enter tab title"
      />
      <p><em>Watch the browser tab title change as you type!</em></p>
    </div>
  );
}

// ── Timer ─────────────────────────────────────────────────────────────────────
function Timer() {
  const [seconds, setSeconds] = useState(0);
  const [running, setRunning] = useState(false);

  // Start/stop the interval based on 'running'; clean up on stop or unmount
  useEffect(() => {
    if (!running) return; // do nothing if stopped

    const id = setInterval(() => {
      setSeconds((s) => s + 1); // functional update avoids stale closure
    }, 1000);

    return () => clearInterval(id); // cleanup: stop interval when running→false
  }, [running]); // Re-runs whenever 'running' changes

  function toggleRunning() {
    setRunning((r) => !r);
  }

  function reset() {
    setRunning(false);
    setSeconds(0);
  }

  return (
    <div className="box">
      <h3>Interval Timer</h3>
      <p>Elapsed: <strong>{seconds}</strong> second{seconds !== 1 ? "s" : ""}</p>
      <button onClick={toggleRunning}>{running ? "Stop" : "Start"}</button>
      <button onClick={reset}>Reset</button>
    </div>
  );
}

// ── UserFetcher ───────────────────────────────────────────────────────────────
function UserFetcher() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Fetch once on mount — empty deps [] means "run once"
  useEffect(() => {
    async function fetchUser() {
      const res = await fetch("https://jsonplaceholder.typicode.com/users/1");
      const data = await res.json();
      setUser(data);
      setLoading(false);
    }
    fetchUser();
  }, []); // Empty array → runs only on mount

  return (
    <div className="box">
      <h3>Fetch on Mount</h3>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <div>
          <p><strong>Name:</strong> {user.name}</p>
          <p><strong>Email:</strong> {user.email}</p>
        </div>
      )}
    </div>
  );
}

// ── App ───────────────────────────────────────────────────────────────────────
function App() {
  return (
    <div>
      <h1>useEffect Demo</h1>
      <TitleUpdater />
      <Timer />
      <UserFetcher />
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
