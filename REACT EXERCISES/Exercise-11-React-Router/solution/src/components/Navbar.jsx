import { NavLink, Link } from 'react-router-dom'

function Navbar({ user, cart, onLogout }) {
  return (
    <nav>
      <Link to="/" className="brand">📚 Bookstore</Link>

      <NavLink to="/" end>Home</NavLink>
      <NavLink to="/books">Books</NavLink>
      <NavLink to="/cart">
        Cart
        {cart.length > 0 && <span className="badge">{cart.length}</span>}
      </NavLink>

      {user ? (
        <>
          <NavLink to="/profile" className="nav-user">👤 {user.name}</NavLink>
          <button onClick={onLogout}>Logout</button>
        </>
      ) : (
        <NavLink to="/login">Login</NavLink>
      )}
    </nav>
  )
}

export default Navbar
