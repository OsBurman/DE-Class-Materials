// src/index.jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// TODO 1: Import BrowserRouter from 'react-router-dom'.

const root = ReactDOM.createRoot(document.getElementById('root'));

// TODO 2: Wrap <App /> in <BrowserRouter> so the entire app has access to routing.
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
