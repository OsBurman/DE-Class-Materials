// src/index.jsx  (solution)
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom'; // BrowserRouter provides the routing context
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));

// Wrap the entire app so every component can access routing hooks and <Link>
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
