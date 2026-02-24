// src/Navbar.jsx  (solution)
import React from 'react';
import { NavLink } from 'react-router-dom';

// Style for the active link — React Router v6 adds the 'active' class automatically
const linkStyle = {
  textDecoration: 'none',
  color: '#333',
  fontWeight: 'normal',
};

function Navbar() {
  return (
    <nav style={{ display: 'flex', gap: '1rem', padding: '0.75rem', background: '#eee' }}>
      {/* className callback receives { isActive } — apply 'active' class when the link matches */}
      <NavLink
        to="/"
        style={linkStyle}
        className={({ isActive }) => (isActive ? 'active' : '')}
      >
        Home
      </NavLink>
      <NavLink
        to="/about"
        style={linkStyle}
        className={({ isActive }) => (isActive ? 'active' : '')}
      >
        About
      </NavLink>
      <NavLink
        to="/contact"
        style={linkStyle}
        className={({ isActive }) => (isActive ? 'active' : '')}
      >
        Contact
      </NavLink>
    </nav>
  );
}

export default Navbar;
