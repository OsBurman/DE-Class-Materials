// Exercise 02: useEffect with Dependencies and Cleanup
const { useState, useEffect } = React;

// ── TitleUpdater ──────────────────────────────────────────────────────────────
function TitleUpdater() {
  // TODO 1: Declare a 'title' state initialized to 'React Hooks'.

  // TODO 2: Add a useEffect that sets document.title = title whenever title changes.
  //         Dependency array: [title]
  useEffect(() => {
    // TODO: document.title = title;
  }, [/* TODO: add dependency */]);

  return (
    <div className="box">
      <h3>Document Title Updater</h3>
      {/* TODO 3: Add a controlled <input> bound to title via value and onChange */}
      <input placeholder="Enter tab title" />
      <p><em>Watch the browser tab title change as you type!</em></p>
    </div>
  );
}

// ── Timer ─────────────────────────────────────────────────────────────────────
function Timer() {
  // TODO 4: Declare 'seconds' state (number, 0) and 'running' state (boolean, false).

  // TODO 5: Add a useEffect with dependency [running].
  //         If running is true, start a setInterval that increments seconds by 1 every 1000ms.
  //         Return a cleanup function that calls clearInterval to stop the interval
  //         when running changes to false or the component unmounts.
  useEffect(() => {
    // TODO: if (running) { const id = setInterval(...); return () => clearInterval(id); }
  }, [/* TODO: running */]);

  function toggleRunning() {
    // TODO 6: toggle the 'running' state
  }

  function reset() {
    // TODO 7: set running to false and seconds to 0
  }

  return (
    <div className="box">
      <h3>Interval Timer</h3>
      {/* TODO 8: Display "Elapsed: N seconds" using the seconds state */}
      <p>Elapsed: ? seconds</p>
      {/* TODO 9: Button text should be "Stop" when running, "Start" when not */}
      <button onClick={toggleRunning}>Start/Stop</button>
      <button onClick={reset}>Reset</button>
    </div>
  );
}

// ── UserFetcher ───────────────────────────────────────────────────────────────
function UserFetcher() {
  // TODO 10: Declare 'user' state (null) and 'loading' state (true).

  // TODO 11: Add a useEffect with an empty dependency array [] (runs once on mount).
  //          Inside, define an async function that fetches:
  //          https://jsonplaceholder.typicode.com/users/1
  //          then sets user to the JSON response and loading to false.
  //          Call your async function immediately after defining it.
  useEffect(() => {
    // TODO: async function fetchUser() { ... }
    // TODO: fetchUser();
  }, []);

  return (
    <div className="box">
      <h3>Fetch on Mount</h3>
      {/* TODO 12: Show "Loading..." while loading is true,
                   otherwise show user.name and user.email */}
      <p>Loading...</p>
    </div>
  );
}

// ── App ───────────────────────────────────────────────────────────────────────
function App() {
  return (
    <div>
      <h1>useEffect Demo</h1>
      {/* TODO 13: Render TitleUpdater, Timer, and UserFetcher */}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
