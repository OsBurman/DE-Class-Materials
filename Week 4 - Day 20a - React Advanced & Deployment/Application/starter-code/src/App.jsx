import React, { Suspense, lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import StatCard from './components/StatCard.jsx';
import DataTable from './components/DataTable.jsx';
import TabPanel from './components/TabPanel.jsx';

// TODO Task 5: Lazy-load SettingsPage and ReportsPage
// const SettingsPage = lazy(() => import('./pages/SettingsPage.jsx'));
// const ReportsPage  = lazy(() => import('./pages/ReportsPage.jsx'));

// TODO Task 4: Lazy-load ChartWidget (it has an artificial 1.5s delay)
// const ChartWidget = lazy(() => import('./components/ChartWidget.jsx'));

// Sample data — provided
const EMPLOYEES = [
  { id: 1, name: 'Alice Johnson', dept: 'Engineering', salary: 95000, startYear: 2020 },
  { id: 2, name: 'Bob Smith',     dept: 'Marketing',   salary: 72000, startYear: 2019 },
  { id: 3, name: 'Carol White',   dept: 'Engineering', salary: 88000, startYear: 2021 },
  { id: 4, name: 'Dan Brown',     dept: 'HR',          salary: 65000, startYear: 2022 },
  { id: 5, name: 'Eve Davis',     dept: 'Engineering', salary: 102000, startYear: 2018 },
];

export default function App() {
  return (
    <div className="app">
      <header><h1>📊 Analytics Dashboard</h1></header>
      <main>

        {/* TODO Task 5: Wrap Routes in Suspense with a loading fallback */}
        <Routes>
          <Route path="/" element={<DashboardHome employees={EMPLOYEES} />} />
          {/* TODO Task 5: Add lazy routes */}
        </Routes>

      </main>
    </div>
  );
}

function DashboardHome({ employees }) {
  return (
    <>
      {/* TODO Task 6: Use TabPanel compound component */}
      <TabPanel>
        {/* TODO: Add tabs and content panels */}
      </TabPanel>

      {/* TODO Task 1: StatCard should be wrapped in React.memo */}
      <div className="stat-cards">
        <StatCard label="Total Employees" value={employees.length} />
        <StatCard label="Avg Salary" value={`$${Math.round(employees.reduce((s, e) => s + e.salary, 0) / employees.length).toLocaleString()}`} />
      </div>

      {/* DataTable uses useMemo internally */}
      <DataTable data={employees} />

      {/* TODO Task 4: Render ChartWidget inside Suspense with fallback */}
    </>
  );
}
