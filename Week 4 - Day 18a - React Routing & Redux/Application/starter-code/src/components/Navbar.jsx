// Navbar.jsx — provided, do not modify
import { NavLink } from 'react-router-dom';

export default function Navbar() {
  return (
    <nav className="navbar">
      <span className="nav-brand">📝 Mini Blog</span>
      <div className="nav-links">
        <NavLink to="/" end className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
          Posts
        </NavLink>
        <NavLink to="/new" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
          + Write
        </NavLink>
      </div>
    </nav>
  );
}
