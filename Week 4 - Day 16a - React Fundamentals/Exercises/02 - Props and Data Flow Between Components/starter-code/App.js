// Exercise 02: Props and Data Flow Between Components

// TODO 1: Create a UserCard component that accepts name, role, and isActive props.
//         Render a <div className="user-card"> containing:
//           - <h3> with the user's name
//           - <p> showing "Role: " + role
//           - <p> showing "Status: Active" or "Status: Inactive" based on isActive
//         Use destructured props: function UserCard({ name, role, isActive }) { ... }
function UserCard() {
  // Replace this placeholder with your component implementation
  return <div className="user-card"><p>UserCard placeholder</p></div>;
}

// TODO 2: Create a Badge component that accepts a label prop.
//         Render a <span className="badge"> with the label text inside.
function Badge() {
  // Replace this placeholder with your component implementation
  return <span className="badge">Badge placeholder</span>;
}

// TODO 3: Create a ProductCard component that accepts title, price, category,
//         and inStock props (inStock should default to true).
//         Render:
//           - <h3> with title
//           - <p> with "$" + price.toFixed(2)
//           - <Badge label={category} />
//           - <p> with "In Stock" or "Out of Stock" based on inStock
function ProductCard() {
  // Replace this placeholder with your component implementation
  return <div className="product-card"><p>ProductCard placeholder</p></div>;
}

// TODO 4: Create an App component that renders:
//           - A <h2>Users</h2> heading followed by a <div className="cards-row">
//             containing at least three <UserCard /> instances with different props
//           - A <h2>Products</h2> heading followed by a <div className="cards-row">
//             containing two <ProductCard /> instances
//         Try passing booleans: isActive={true}, isActive={false}
//         Try omitting inStock on one ProductCard to use the default value
function App() {
  return (
    <div>
      <h1>Props Demo</h1>

      <h2>Users</h2>
      <div className="cards-row">
        {/* TODO: Add UserCard components here */}
      </div>

      <h2>Products</h2>
      <div className="cards-row">
        {/* TODO: Add ProductCard components here */}
      </div>
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
