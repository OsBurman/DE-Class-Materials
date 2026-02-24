// src/layouts/DashboardLayout.jsx  (solution)
import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';

// Returns 'active' when the NavLink matches the current URL
const navLinkClass = ({ isActive }) => (isActive ? 'active' : '');

function DashboardLayout() {
  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* Sidebar — stays mounted while child routes swap */}
      <aside style={{ width: '200px', background: '#f0f0f0', padding: '1rem', borderRight: '1px solid #ccc' }}>
        <h2>Dashboard</h2>
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          {/* `end` prevents /dashboard from matching /dashboard/analytics */}
          <NavLink to="/dashboard" end className={navLinkClass}>Overview</NavLink>
          <NavLink to="/dashboard/analytics" className={navLinkClass}>Analytics</NavLink>
          <NavLink to="/dashboard/settings" className={navLinkClass}>Settings</NavLink>
        </nav>
      </aside>

      {/* <Outlet /> renders the matched child route's element */}
      <main style={{ flex: 1, padding: '1rem' }}>
        <Outlet />
      </main>
    </div>
  );
}

export default DashboardLayout;
