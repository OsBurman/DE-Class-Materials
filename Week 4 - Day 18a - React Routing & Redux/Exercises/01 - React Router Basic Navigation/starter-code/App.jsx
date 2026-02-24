// src/App.jsx
import React from 'react';
// TODO 3: Import Routes and Route from 'react-router-dom'.
import Navbar from './Navbar';
import HomePage from './pages/HomePage';
import AboutPage from './pages/AboutPage';
import ContactPage from './pages/ContactPage';
import NotFoundPage from './pages/NotFoundPage';

function App() {
  return (
    <div>
      <Navbar />

      {/* TODO 4: Add a <Routes> block with four <Route> elements:
            - path="/"        → element={<HomePage />}
            - path="/about"   → element={<AboutPage />}
            - path="/contact" → element={<ContactPage />}
            - path="*"        → element={<NotFoundPage />}  (catches all unmatched URLs)
      */}
    </div>
  );
}

export default App;
