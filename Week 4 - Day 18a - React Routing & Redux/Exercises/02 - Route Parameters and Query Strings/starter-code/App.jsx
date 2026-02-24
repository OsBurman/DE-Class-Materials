// src/App.jsx  (starter)
import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import ProductListPage from './pages/ProductListPage';
import ProductDetailPage from './pages/ProductDetailPage';

function App() {
  return (
    <BrowserRouter>
      <nav style={{ padding: '0.5rem', background: '#eee' }}>
        <Link to="/products">Products</Link>
      </nav>

      <Routes>
        {/* TODO 1: Add a route for path="/products" that renders <ProductListPage /> */}
        {/* TODO 2: Add a dynamic route for path="/products/:id" that renders <ProductDetailPage /> */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
