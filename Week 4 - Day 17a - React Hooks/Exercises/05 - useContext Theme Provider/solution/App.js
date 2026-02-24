// Exercise 05: useContext — Theme Provider — SOLUTION
const { useState, useContext, createContext } = React;

// Create context at module level with a default value of 'light'
const ThemeContext = createContext('light');

// ── ThemeProvider ─────────────────────────────────────────────────────────────
function ThemeProvider({ children }) {
  const [theme, setTheme] = useState('light');

  function toggleTheme() {
    setTheme((t) => (t === 'light' ? 'dark' : 'light'));
  }

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

// ── ThemedButton ──────────────────────────────────────────────────────────────
function ThemedButton() {
  const { theme, toggleTheme } = useContext(ThemeContext);

  return (
    <button onClick={toggleTheme}>
      Switch to {theme === 'light' ? 'Dark' : 'Light'} Mode
    </button>
  );
}

// ── ThemedCard ────────────────────────────────────────────────────────────────
function ThemedCard() {
  const { theme } = useContext(ThemeContext);

  const cardStyle =
    theme === 'dark'
      ? { backgroundColor: '#333', color: 'white' }
      : { backgroundColor: 'white', color: '#333' };

  return (
    <div className="card" style={cardStyle}>
      <h3>Themed Card</h3>
      <p>This card's colors change based on the active theme.</p>
      <p><em>Active theme: <strong>{theme}</strong></em></p>
    </div>
  );
}

// ── ThemeDisplay ──────────────────────────────────────────────────────────────
function ThemeDisplay() {
  const { theme } = useContext(ThemeContext);
  return <p>Current theme: <strong>{theme}</strong></p>;
}

// ── App ───────────────────────────────────────────────────────────────────────
function App() {
  return (
    <ThemeProvider>
      <h1>Context Theme Demo</h1>
      <ThemeDisplay />
      <ThemedButton />
      <ThemedCard />
    </ThemeProvider>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
