// src/pages/NotFoundPage.jsx  (solution)
import React from 'react';
import { Link } from 'react-router-dom';

function NotFoundPage() {
  return (
    <main style={{ padding: '1rem' }}>
      <h1>404 - Page Not Found</h1>
      <p>The page you are looking for does not exist.</p>
      {/* Link navigates without a full page reload */}
      <Link to="/">Go Home</Link>
    </main>
  );
}

export default NotFoundPage;
