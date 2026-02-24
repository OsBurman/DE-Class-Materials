import React, { useState, Suspense, lazy } from 'react';

// TODO: lazily import HomePage from './HomePage' using React.lazy
//       Add a comment explaining what React.lazy does
const HomePage = null as any; // replace with React.lazy(...)

// TODO: lazily import AboutPage from './AboutPage'
const AboutPage = null as any;

// TODO: lazily import DashboardPage from './DashboardPage'
//       Add a simulated 1500ms delay inside the dynamic import arrow function:
//       async () => { await new Promise(r => setTimeout(r, 1500)); return import('./DashboardPage'); }
const DashboardPage = null as any;

type Page = 'home' | 'about' | 'dashboard';

export default function App() {
  const [activePage, setActivePage] = useState<Page>('home');

  // TODO: create a mapping object from page name → lazy component,
  //       e.g. const pages = { home: HomePage, about: AboutPage, dashboard: DashboardPage }

  // TODO: derive ActiveComponent from the mapping using activePage as the key

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Code Splitting Demo</h1>

      <nav style={{ marginBottom: '1.5rem', display: 'flex', gap: '0.5rem' }}>
        <button onClick={() => setActivePage('home')}>Home</button>
        <button onClick={() => setActivePage('about')}>About</button>
        <button onClick={() => setActivePage('dashboard')}>Dashboard</button>
      </nav>

      {/* TODO: wrap ActiveComponent in <Suspense fallback={<p>Loading...</p>}> */}
      <p>TODO: render the active page here inside Suspense</p>
    </div>
  );
}
