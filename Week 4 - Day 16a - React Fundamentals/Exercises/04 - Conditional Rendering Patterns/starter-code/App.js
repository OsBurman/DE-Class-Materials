// Exercise 04: Conditional Rendering Patterns

// ─── Pattern 1: && (short-circuit) ────────────────────────────────────────────
// TODO 1: Create a ProductBadge component that accepts name (string) and isNew (boolean).
//         Render the product name in an <h3>.
//         Use the && operator to render <span className="badge new-badge">NEW!</span>
//         only when isNew is true.
function ProductBadge({ name, isNew }) {
  return (
    <div className="card">
      <h3>{name}</h3>
      {/* TODO: render the NEW badge only when isNew is true using && */}
    </div>
  );
}

// ─── Pattern 2: Ternary ────────────────────────────────────────────────────────
// TODO 2: Create a StockStatus component that accepts inStock (boolean).
//         Use a ternary to render either:
//           <p className="in-stock">✅ In Stock</p>
//         or:
//           <p className="out-of-stock">❌ Out of Stock</p>
function StockStatus({ inStock }) {
  return (
    <div className="card">
      {/* TODO: use a ternary inside {} to pick the right <p> */}
    </div>
  );
}

// ─── Pattern 3: Early Return ───────────────────────────────────────────────────
// TODO 3: Create a UserProfile component that accepts a user prop (object or null).
//         If user is null or undefined, return null so nothing is rendered.
//         Otherwise render:
//           <div className="profile">
//             <h3>{user.name}</h3>
//             <p>{user.email}</p>
//           </div>
function UserProfile({ user }) {
  // TODO: add early return here

  return (
    <div className="profile">
      <h3>placeholder</h3>
    </div>
  );
}

// ─── Pattern 4: Function / Switch ─────────────────────────────────────────────
// TODO 4a: Implement getStatusStyle(status) to return { label, color }:
//           "active"    → { label: "Active",    color: "green"  }
//           "pending"   → { label: "Pending",   color: "orange" }
//           "suspended" → { label: "Suspended", color: "red"    }
//           default     → { label: "Unknown",   color: "gray"   }
function getStatusStyle(status) {
  // TODO: implement switch or if/else chain
  return { label: "Unknown", color: "gray" };
}

// TODO 4b: Create a StatusBadge component that accepts a status string,
//          calls getStatusStyle(), and renders:
//            <span style={{ color }}>⬤ {label}</span>
function StatusBadge({ status }) {
  // TODO: call getStatusStyle, destructure result, render span
  return <span>status placeholder</span>;
}

// ─── App ───────────────────────────────────────────────────────────────────────
const alice = { name: "Alice", email: "alice@example.com" };

function App() {
  return (
    <div>
      <h1>Conditional Rendering Patterns</h1>

      <h2>Pattern 1 — && (Show "NEW" badge conditionally)</h2>
      <div className="row">
        {/* TODO: render two ProductBadge components — one with isNew={true}, one with isNew={false} */}
      </div>

      <h2>Pattern 2 — Ternary (In Stock vs Out of Stock)</h2>
      <div className="row">
        {/* TODO: render two StockStatus components — one true, one false */}
      </div>

      <h2>Pattern 3 — Early Return (null when no user)</h2>
      <div className="row">
        {/* TODO: render <UserProfile user={alice} /> and <UserProfile user={null} /> */}
        <p>(The second UserProfile renders nothing — try it!)</p>
      </div>

      <h2>Pattern 4 — Function/Switch (Status Badge)</h2>
      <div className="row">
        {/* TODO: render StatusBadge for "active", "pending", "suspended", and "unknown" */}
      </div>
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
