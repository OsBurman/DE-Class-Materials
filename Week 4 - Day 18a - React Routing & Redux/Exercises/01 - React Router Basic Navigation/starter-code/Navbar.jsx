// src/Navbar.jsx
import React from 'react';
// TODO 5: Import NavLink from 'react-router-dom'.

function Navbar() {
  return (
    <nav style={{ display: 'flex', gap: '1rem', padding: '0.75rem', background: '#eee' }}>
      {/* TODO 6: Replace each <a> below with a <NavLink>.
            - Set the `to` prop to the correct path.
            - Set className={({ isActive }) => (isActive ? 'active' : '')}
            so the active link gets the 'active' CSS class.
      */}
      <a href="/">Home</a>
      <a href="/about">About</a>
      <a href="/contact">Contact</a>
    </nav>
  );
}

export default Navbar;
