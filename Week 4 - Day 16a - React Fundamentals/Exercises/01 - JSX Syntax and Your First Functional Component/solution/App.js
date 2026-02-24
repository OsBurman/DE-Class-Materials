// Exercise 01: JSX Syntax and Your First Functional Component — SOLUTION

// Requirement 1: Header component — single <div> root wrapping h1 + p
function Header() {
  return (
    <div>
      <h1>React Fundamentals</h1>
      <p>Building UIs with components and JSX</p>
    </div>
  );
}

// Requirement 2: Welcome component — Fragment (<>) as root to avoid extra div
function Welcome() {
  return (
    <>
      <h2>Welcome to React!</h2>
      <p>React uses a Virtual DOM for efficient updates.</p>
    </>
  );
}

// Requirement 3: InfoBox — className (not class) for CSS, <strong> inline
function InfoBox() {
  return (
    <div className="info-box">
      <strong>Key concept:</strong> JSX must have a single root element.
    </div>
  );
}

// Requirement 4 + 5: App composes the three components
function App() {
  // Requirement 5a: embed a JavaScript value with {}
  const version = 18;

  return (
    <div>
      <Header />
      {/* Requirement 5b: self-closing <hr /> tag */}
      <hr />
      <Welcome />
      <hr />
      <InfoBox />
      {/* JSX expression — curly braces evaluate JS */}
      <p style={{ color: "#888", fontSize: "0.85rem" }}>
        Using React version: {version}
      </p>
    </div>
  );
}

// Requirement 6: Mount the App component into the #root div
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
