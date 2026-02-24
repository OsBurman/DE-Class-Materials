import React, { useState, Suspense, lazy } from 'react';

// React.lazy takes a factory function that calls a dynamic import().
// The bundler creates a separate chunk for each lazily imported module.
const HomePage = lazy(() => import('./HomePage'));
const AboutPage = lazy(() => import('./AboutPage'));

// DashboardPage has a simulated 1.5 s delay to make the Suspense fallback visible.
const DashboardPage = lazy(async () => {
  await new Promise(r => setTimeout(r, 1500)); // simulated network latency
  return import('./DashboardPage');
});

type Page = 'home' | 'about' | 'dashboard';

// Map page names to their lazy-loaded components.
const pages: Record<Page, React.LazyExoticComponent<() => React.ReactElement>> = {
  home:      HomePage,
  about:     AboutPage,
  dashboard: DashboardPage,
};

export default function App() {
  const [activePage, setActivePage] = useState<Page>('home');

  // Pick the component that matches the current route.
  const ActiveComponent = pages[activePage];

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Code Splitting Demo</h1>

      <nav style={{ marginBottom: '1.5rem', display: 'flex', gap: '0.5rem' }}>
        <button onClick={() => setActivePage('home')}>Home</button>
        <button onClick={() => setActivePage('about')}>About</button>
        <button onClick={() => setActivePage('dashboard')}>Dashboard</button>
      </nav>

      {/*
        Suspense shows the fallback UI while the lazy chunk is downloading.
        One Suspense boundary can wrap multiple lazy components.
      */}
      <Suspense fallback={<p>Loading...</p>}>
        <ActiveComponent />
      </Suspense>
    </div>
  );
}
