// Exercise 02 Solution: Props and Data Flow Between Components

// Props are passed as an object to every component function.
// Destructuring in the parameter list is the idiomatic modern style.

// UserCard — receives name, role, isActive via destructured props
function UserCard({ name, role, isActive }) {
  return (
    <div className="user-card">
      <h3>{name}</h3>
      <p>Role: {role}</p>
      <p>Status: {isActive ? "Active" : "Inactive"}</p>
    </div>
  );
}

// Badge — a small reusable display component; receives a single label prop
function Badge({ label }) {
  return <span className="badge">{label}</span>;
}

// ProductCard — demonstrates a default prop value (inStock = true)
// Props flow one way: App passes them down; ProductCard cannot change them.
function ProductCard({ title, price, category, inStock = true }) {
  return (
    <div className="product-card">
      <h3>{title}</h3>
      <p>${price.toFixed(2)}</p>
      <Badge label={category} />
      <p>{inStock ? "In Stock" : "Out of Stock"}</p>
    </div>
  );
}

// App — the parent that owns the data and passes it down
function App() {
  return (
    <div>
      <h1>Props Demo</h1>

      <h2>Users</h2>
      <div className="cards-row">
        {/* Boolean props use curly braces, not quotes */}
        <UserCard name="Alice" role="Admin" isActive={true} />
        <UserCard name="Bob" role="Developer" isActive={false} />
        <UserCard name="Carol" role="Designer" isActive={true} />
      </div>

      <h2>Products</h2>
      <div className="cards-row">
        {/* Explicit inStock={false} overrides the default */}
        <ProductCard title="Widget Pro" price={29.99} category="Electronics" inStock={false} />
        {/* inStock is omitted — defaults to true */}
        <ProductCard title="Basic Kit" price={9.99} category="Tools" />
      </div>
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
