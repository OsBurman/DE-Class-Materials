// src/layouts/DashboardLayout.jsx  (starter)
import React from 'react';
// TODO 2: Import NavLink and Outlet from 'react-router-dom'.

function DashboardLayout() {
  const navLinkClass = ({ isActive }) => (isActive ? 'active' : '');

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* Sidebar */}
      <aside style={{ width: '200px', background: '#f0f0f0', padding: '1rem' }}>
        <h2>Dashboard</h2>
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          {/* TODO 3: Add three <NavLink> elements with className={navLinkClass}:
                - "Overview"  → to="/dashboard"    (add end prop to prevent matching /dashboard/*)
                - "Analytics" → to="/dashboard/analytics"
                - "Settings"  → to="/dashboard/settings"
          */}
        </nav>
      </aside>

      {/* Main content area */}
      <main style={{ flex: 1, padding: '1rem' }}>
        {/* TODO 4: Render <Outlet /> here so child route content appears in this area */}
      </main>
    </div>
  );
}

export default DashboardLayout;
