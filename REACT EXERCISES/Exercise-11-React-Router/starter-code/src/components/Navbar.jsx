import { NavLink, Link } from 'react-router-dom'

// TODO 1: Replace the plain <a> tags with <NavLink> components.
//   <NavLink> automatically adds className="active" when the route matches.
//   For the Home link, add end prop to prevent it matching all routes.

// TODO 2: Show a cart badge when cart.length > 0:
//   <span className="badge">{cart.length}</span> next to the Cart link.

// TODO 3: Show user name + logout button when user is logged in.
//   Show a "Login" NavLink when user is null.

function Navbar({ user, cart, onLogout }) {
  return (
    <nav>
      <Link to="/" className="brand">📚 Bookstore</Link>
      {/* TODO: replace these with NavLink */}
      <a href="/">Home</a>
      <a href="/books">Books</a>
      <a href="/cart">Cart ({cart?.length ?? 0})</a>
      {user ? (
        <>
          <a href="/profile">Profile</a>
          <span className="nav-user">👋 {user.name}</span>
          <button onClick={onLogout}>Logout</button>
        </>
      ) : (
        <a href="/login">Login</a>
      )}
    </nav>
  )
}

export default Navbar
