// src/App.jsx  (starter)
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import DashboardLayout from './layouts/DashboardLayout';
import OverviewPage from './pages/OverviewPage';
import AnalyticsPage from './pages/AnalyticsPage';
import SettingsPage from './pages/SettingsPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* TODO 1: Create a parent <Route path="/dashboard" element={<DashboardLayout />}>.
              Inside it, nest three child routes:
              - An index route (no path) that renders <OverviewPage />
              - path="analytics" that renders <AnalyticsPage />
              - path="settings"  that renders <SettingsPage />
        */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
