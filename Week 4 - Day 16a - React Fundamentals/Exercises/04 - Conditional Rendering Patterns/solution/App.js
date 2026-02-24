// Exercise 04 Solution: Conditional Rendering Patterns

// ─── Pattern 1: && (short-circuit) ────────────────────────────────────────────
// When isNew is false, the && short-circuits and nothing is rendered.
// Never use a number directly on the left of &&: `{count && <span/>}` renders "0"
// when count is 0. Always coerce to boolean: `{count > 0 && <span/>}`.
function ProductBadge({ name, isNew }) {
  return (
    <div className="card">
      <h3>{name}</h3>
      {isNew && <span className="badge new-badge">NEW!</span>}
    </div>
  );
}

// ─── Pattern 2: Ternary ────────────────────────────────────────────────────────
// Ternary works well when you need to choose between exactly two outputs.
function StockStatus({ inStock }) {
  return (
    <div className="card">
      {inStock
        ? <p className="in-stock">✅ In Stock</p>
        : <p className="out-of-stock">❌ Out of Stock</p>}
    </div>
  );
}

// ─── Pattern 3: Early Return ───────────────────────────────────────────────────
// Returning null tells React to render nothing for this component.
// This keeps the JSX in the main return clean — no ternary needed.
function UserProfile({ user }) {
  if (!user) return null;           // early return — renders nothing

  return (
    <div className="profile">
      <h3>{user.name}</h3>
      <p>{user.email}</p>
    </div>
  );
}

// ─── Pattern 4: Function / Switch ─────────────────────────────────────────────
// Extracting the logic into a helper keeps JSX readable.
// A switch (or series of if/else) maps a value to display data.
function getStatusStyle(status) {
  switch (status) {
    case "active":    return { label: "Active",    color: "green"  };
    case "pending":   return { label: "Pending",   color: "orange" };
    case "suspended": return { label: "Suspended", color: "red"    };
    default:          return { label: "Unknown",   color: "gray"   };
  }
}

function StatusBadge({ status }) {
  const { label, color } = getStatusStyle(status);
  return <span style={{ color, marginRight: "1rem", fontWeight: "bold" }}>⬤ {label}</span>;
}

// ─── App ───────────────────────────────────────────────────────────────────────
const alice = { name: "Alice", email: "alice@example.com" };

function App() {
  return (
    <div>
      <h1>Conditional Rendering Patterns</h1>

      <h2>Pattern 1 — && (Show "NEW" badge conditionally)</h2>
      <div className="row">
        <ProductBadge name="Widget Pro" isNew={true} />
        <ProductBadge name="Classic Mug" isNew={false} />
      </div>

      <h2>Pattern 2 — Ternary (In Stock vs Out of Stock)</h2>
      <div className="row">
        <StockStatus inStock={true} />
        <StockStatus inStock={false} />
      </div>

      <h2>Pattern 3 — Early Return (null when no user)</h2>
      <div className="row">
        <UserProfile user={alice} />
        {/* UserProfile with null renders nothing — no empty box */}
        <UserProfile user={null} />
        <p style={{ color: "#888" }}>(The second UserProfile renders nothing — try it!)</p>
      </div>

      <h2>Pattern 4 — Function/Switch (Status Badge)</h2>
      <div className="row">
        <StatusBadge status="active" />
        <StatusBadge status="pending" />
        <StatusBadge status="suspended" />
        <StatusBadge status="unknown" />
      </div>
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
