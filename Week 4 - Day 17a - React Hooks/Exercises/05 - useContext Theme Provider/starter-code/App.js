// Exercise 05: useContext — Theme Provider
const { useState, useContext, createContext } = React;

// TODO 1: Create ThemeContext at the module level using createContext.
//         Use 'light' as the default value.
// const ThemeContext = createContext('light');

// ── ThemeProvider ─────────────────────────────────────────────────────────────
function ThemeProvider({ children }) {
  // TODO 2: Declare 'theme' state initialized to 'light'.

  function toggleTheme() {
    // TODO 3: Toggle theme between 'light' and 'dark'.
  }

  // TODO 4: Wrap children in <ThemeContext.Provider> with value={{ theme, toggleTheme }}.
  return <div>{children}</div>;
}

// ── ThemedButton ──────────────────────────────────────────────────────────────
function ThemedButton() {
  // TODO 5: Use useContext(ThemeContext) to get { theme, toggleTheme }.

  return (
    <button onClick={/* TODO: toggleTheme */ undefined}>
      {/* TODO 6: Show "Switch to Dark Mode" or "Switch to Light Mode" based on theme */}
      Toggle Theme
    </button>
  );
}

// ── ThemedCard ────────────────────────────────────────────────────────────────
function ThemedCard() {
  // TODO 7: Use useContext(ThemeContext) to get { theme }.

  // TODO 8: Define cardStyle — when theme is 'dark': backgroundColor '#333', color 'white';
  //         when 'light': backgroundColor 'white', color '#333'.
  const cardStyle = {};

  return (
    <div className="card" style={cardStyle}>
      <h3>Themed Card</h3>
      <p>This card's colors change based on the active theme.</p>
      {/* TODO 9: Display the current theme value */}
    </div>
  );
}

// ── ThemeDisplay ──────────────────────────────────────────────────────────────
function ThemeDisplay() {
  // TODO 10: Use useContext(ThemeContext) to get { theme } and display it.
  return <p>Current theme: <strong>?</strong></p>;
}

// ── App ───────────────────────────────────────────────────────────────────────
function App() {
  return (
    // TODO 11: Wrap everything in <ThemeProvider>
    <div>
      <h1>Context Theme Demo</h1>
      {/* TODO 12: Render ThemeDisplay, ThemedButton, ThemedCard */}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
